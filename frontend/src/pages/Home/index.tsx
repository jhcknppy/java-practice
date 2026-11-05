import { CheckCircleFilled, LockOutlined } from '@ant-design/icons';
import { PageContainer } from '@ant-design/pro-components';
import { Card, Col, Progress, Row, Tag, Typography } from 'antd';
import { history, useModel } from '@umijs/max';
import { useEffect, useState } from 'react';
import { getModules, type ChapterVO, type ModuleVO } from '@/services/content';

export default function HomePage() {
  const { initialState } = useModel('@@initialState');
  const [modules, setModules] = useState<ModuleVO[]>([]);

  useEffect(() => {
    getModules().then(setModules).catch(() => {});
  }, []);

  const goChapter = (c: ChapterVO) => {
    if (!c.locked) history.push(`/chapter/${c.id}`);
  };

  return (
    <PageContainer
      title="Java 练习系统"
      extra={<Tag color="gold">开发等级 Lv.{initialState?.currentUser?.level ?? 0}</Tag>}
    >
      {modules.map((m) => (
        <Card key={m.id} title={m.name} style={{ marginBottom: 16 }}>
          <Typography.Paragraph type="secondary">{m.description}</Typography.Paragraph>
          <Row gutter={[16, 16]}>
            {m.chapters.map((c) => (
              <Col key={c.id} xs={24} sm={12} md={8}>
                <Card
                  size="small"
                  hoverable={!c.locked}
                  style={{ opacity: c.locked ? 0.55 : 1 }}
                  onClick={() => goChapter(c)}
                >
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                    <span style={{ fontWeight: 600 }}>{c.name}</span>
                    {c.locked ? <LockOutlined /> : c.completed ? <CheckCircleFilled style={{ color: '#52c41a' }} /> : null}
                  </div>
                  <Progress
                    percent={c.total ? Math.round((c.answered / c.total) * 100) : 0}
                    size="small"
                    style={{ marginTop: 8 }}
                  />
                  <div style={{ fontSize: 12, color: '#999' }}>
                    已答 {c.answered}/{c.total} · 正确 {c.correct}
                  </div>
                </Card>
              </Col>
            ))}
          </Row>
        </Card>
      ))}
    </PageContainer>
  );
}
