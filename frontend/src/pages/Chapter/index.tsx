import { PageContainer } from '@ant-design/pro-components';
import { Button, Card, Collapse, Empty, List, Tag } from 'antd';
import { history, useParams } from '@umijs/max';
import { useEffect, useState } from 'react';
import ReactMarkdown from 'react-markdown';
import remarkGfm from 'remark-gfm';
import { getChapter, getKnowledgePoints, type KnowledgePointItem } from '@/services/content';
import { getChapterReview, type KnowledgePointVO } from '@/services/practice';

export default function ChapterPage() {
  const { id } = useParams<{ id: string }>();
  const chapterId = Number(id);
  const [chapter, setChapter] = useState<{ name?: string; description?: string }>();
  const [kps, setKps] = useState<KnowledgePointItem[]>([]);
  const [review, setReview] = useState<KnowledgePointVO[]>([]);

  useEffect(() => {
    getChapter(chapterId).then(setChapter).catch(() => {});
    getKnowledgePoints(chapterId).then(setKps).catch(() => {});
    getChapterReview(chapterId).then(setReview).catch(() => {});
  }, [chapterId]);

  const weakCount = review.filter((r) => (r.wrongCount ?? 0) > 0 || (r.mastery != null && r.mastery < 0.8)).length;

  const renderMarkdown = (text?: string) => (
    <div className="kp-markdown">
      <ReactMarkdown remarkPlugins={[remarkGfm]}>{text || ''}</ReactMarkdown>
    </div>
  );

  return (
    <PageContainer
      title={chapter?.name}
      subTitle={chapter?.description}
      onBack={() => history.push('/home')}
      extra={[
        <Button key="p" type="primary" onClick={() => history.push(`/practice/${chapterId}`)}>
          开始练习
        </Button>,
        <Button
          key="r"
          onClick={() => history.push(`/practice/${chapterId}?mode=reinforce`)}
          disabled={weakCount === 0}
        >
          加强练习
        </Button>,
      ]}
    >
      <Card title="知识点讲解" style={{ marginBottom: 16 }}>
        <Collapse
          items={kps.map((k) => ({
            key: String(k.id),
            label: k.title,
            children: renderMarkdown(k.content),
          }))}
        />
      </Card>

      <Card title="章节复盘">
        {review.length === 0 ? (
          <Empty description="先完成练习后再来复盘" />
        ) : (
          <List
            dataSource={review}
            renderItem={(r) => {
              const weak = (r.wrongCount ?? 0) > 0 || (r.mastery != null && r.mastery < 0.8);
              const mastery = r.mastery == null ? '未练习' : Math.round(r.mastery * 100) + '%';
              return (
                <List.Item>
                  <div style={{ width: '100%' }}>
                    <div>
                      {r.title}{' '}
                      <Tag color={weak ? 'red' : 'green'}>{weak ? '薄弱' : '已掌握'}</Tag>
                      <span style={{ color: '#999' }}>
                        {mastery}（对 {r.correctCount ?? 0} / 错 {r.wrongCount ?? 0}）
                      </span>
                    </div>
                    {weak && (
                      <div style={{ marginTop: 10 }}>{renderMarkdown(r.content)}</div>
                    )}
                  </div>
                </List.Item>
              );
            }}
          />
        )}
      </Card>
    </PageContainer>
  );
}
