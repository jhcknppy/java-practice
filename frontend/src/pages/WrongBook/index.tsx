import { PageContainer } from '@ant-design/pro-components';
import { Card, Empty, List, Tag, Typography } from 'antd';
import { useEffect, useState } from 'react';
import { getWrongQuestions, type WrongQuestionItem } from '@/services/practice';

export default function WrongBookPage() {
  const [list, setList] = useState<WrongQuestionItem[]>([]);

  useEffect(() => {
    getWrongQuestions().then(setList).catch(() => {});
  }, []);

  return (
    <PageContainer title="错题本">
      {list.length === 0 ? (
        <Empty description="暂无错题" />
      ) : (
        <List
          dataSource={list}
          renderItem={(w) => (
            <List.Item>
              <Card size="small" style={{ width: '100%' }}>
                <div>
                  <Tag>{w.type === 'TRUE_FALSE' ? '判断题' : w.type === 'CODE' ? '代码题' : '选择题'}</Tag>
                  {w.knowledgePointTitle && <Tag color="blue">{w.knowledgePointTitle}</Tag>}
                  <Tag color="red">错 {w.wrongCount} 次</Tag>
                </div>
                <Typography.Paragraph strong style={{ whiteSpace: 'pre-wrap' }}>
                  {w.title}
                </Typography.Paragraph>
                <div>正确答案：{w.correctAnswer}</div>
                <Typography.Paragraph type="secondary" style={{ whiteSpace: 'pre-wrap', marginBottom: 0 }}>
                  {w.analysis}
                </Typography.Paragraph>
              </Card>
            </List.Item>
          )}
        />
      )}
    </PageContainer>
  );
}
