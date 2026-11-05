import { PageContainer } from '@ant-design/pro-components';
import { Button, Card, Empty, Radio, Result, Space, Tag, Typography } from 'antd';
import { history, useLocation, useParams } from '@umijs/max';
import { useEffect, useState } from 'react';
import { getQuestions, type QuestionItem } from '@/services/content';
import { getReinforcements, settleChapter, submitAnswer, type AnswerResult } from '@/services/practice';

export default function PracticePage() {
  const { chapterId } = useParams<{ chapterId: string }>();
  const cid = Number(chapterId);
  const location = useLocation();
  const mode = new URLSearchParams(location.search).get('mode') === 'reinforce' ? 'reinforce' : 'normal';

  const [questions, setQuestions] = useState<QuestionItem[]>([]);
  const [index, setIndex] = useState(0);
  const [selected, setSelected] = useState<string>();
  const [feedback, setFeedback] = useState<AnswerResult>();
  const [correctCount, setCorrectCount] = useState(0);
  const [finished, setFinished] = useState(false);
  const [settle, setSettle] = useState<{ completed: boolean; leveledUp: boolean; level: number }>();

  useEffect(() => {
    const load = mode === 'reinforce' ? getReinforcements(cid) : getQuestions(cid);
    load.then(setQuestions).catch(() => {});
  }, [cid, mode]);

  const q = questions[index];

  const submit = async () => {
    if (!q || !selected || feedback) return;
    const r = await submitAnswer(q.id, selected);
    setFeedback(r);
    if (r.correct) setCorrectCount((c) => c + 1);
  };

  const next = () => {
    if (index + 1 < questions.length) {
      setIndex(index + 1);
      setSelected(undefined);
      setFeedback(undefined);
    } else {
      setFinished(true);
    }
  };

  if (finished) {
    const rate = questions.length ? correctCount / questions.length : 0;
    return (
      <PageContainer title="练习完成">
        <Result
          status={rate >= 0.8 ? 'success' : 'info'}
          title={`答对 ${correctCount} / ${questions.length}`}
          extra={[
            settle ? (
              <div key="result">
                <Tag color="gold">开发等级 Lv.{settle.level}</Tag>
                {settle.leveledUp && <Tag color="green">升级成功！</Tag>}
                {settle.completed ? <Tag color="green">章节已通关</Tag> : <Tag color="orange">正确率未达标</Tag>}
              </div>
            ) : (
              <Button key="settle" type="primary" onClick={async () => setSettle(await settleChapter(cid))}>
                结算章节
              </Button>
            ),
            <Button key="back" onClick={() => history.push('/home')}>
              返回首页
            </Button>,
          ]}
        />
      </PageContainer>
    );
  }

  if (!q) {
    return (
      <PageContainer title="练习">
        <Empty description={mode === 'reinforce' ? '没有薄弱题目，继续加油' : '暂无题目'} />
      </PageContainer>
    );
  }

  return (
    <PageContainer title={`练习${mode === 'reinforce' ? '（加强）' : ''}`} onBack={() => history.push(`/chapter/${cid}`)}>
      <Card>
        <div style={{ marginBottom: 12 }}>
          <Tag>{q.type === 'TRUE_FALSE' ? '判断题' : q.type === 'CODE' ? '代码题' : '选择题'}</Tag>
          {q.knowledgePointTitle && <Tag color="blue">{q.knowledgePointTitle}</Tag>}
          <span style={{ float: 'right', color: '#999' }}>
            {index + 1} / {questions.length}
          </span>
        </div>
        <Typography.Paragraph strong style={{ whiteSpace: 'pre-wrap' }}>
          {q.title}
        </Typography.Paragraph>
        <Radio.Group value={selected} onChange={(e) => setSelected(e.target.value)} disabled={Boolean(feedback)}>
          <Space direction="vertical">
            {q.type === 'TRUE_FALSE'
              ? [
                  <Radio key="true" value="true">正确</Radio>,
                  <Radio key="false" value="false">错误</Radio>,
                ]
              : q.options.map((o) => (
                  <Radio key={o.label} value={o.label}>
                    {o.label}. {o.content}
                  </Radio>
                ))}
          </Space>
        </Radio.Group>

        {feedback && (
          <div
            style={{
              marginTop: 16,
              padding: 12,
              background: feedback.correct ? '#f6ffed' : '#fff2f0',
              border: `1px solid ${feedback.correct ? '#b7eb8f' : '#ffccc7'}`,
              borderRadius: 6,
            }}
          >
            <div style={{ fontWeight: 600, color: feedback.correct ? '#389e0d' : '#cf1322' }}>
              {feedback.correct ? '回答正确' : `回答错误，正确答案：${feedback.correctAnswer}`}
            </div>
            <Typography.Paragraph style={{ whiteSpace: 'pre-wrap', marginTop: 8, marginBottom: 0 }}>
              {feedback.analysis}
            </Typography.Paragraph>
          </div>
        )}

        <div style={{ marginTop: 20 }}>
          {!feedback ? (
            <Button type="primary" disabled={!selected} onClick={submit}>
              提交
            </Button>
          ) : (
            <Button type="primary" onClick={next}>
              {index + 1 < questions.length ? '下一题' : '完成'}
            </Button>
          )}
        </div>
      </Card>
    </PageContainer>
  );
}
