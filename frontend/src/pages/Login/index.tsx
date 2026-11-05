import { LockOutlined, UserOutlined } from '@ant-design/icons';
import { Button, Card, Form, Input, message } from 'antd';
import { history, useModel } from '@umijs/max';
import { useState } from 'react';
import { login } from '@/services/auth';
import { setToken } from '@/utils/auth';

export default function LoginPage() {
  const { setInitialState } = useModel('@@initialState');
  const [loading, setLoading] = useState(false);

  const onFinish = async (values: { username: string; password: string }) => {
    setLoading(true);
    try {
      const r = await login(values);
      setToken(r.token);
      setInitialState((s: any) => ({ ...s, currentUser: r.user }));
      message.success('登录成功');
      history.push('/home');
    } catch {
      // 全局错误处理已提示
    } finally {
      setLoading(false);
    }
  };

  return (
    <div
      style={{
        minHeight: '100vh',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        background: '#f0f2f5',
      }}
    >
      <Card title="Java 练习系统" style={{ width: 380 }}>
        <Form onFinish={onFinish}>
          <Form.Item name="username" rules={[{ required: true, message: '请输入用户名' }]}>
            <Input prefix={<UserOutlined />} placeholder="用户名" />
          </Form.Item>
          <Form.Item name="password" rules={[{ required: true, message: '请输入密码' }]}>
            <Input.Password prefix={<LockOutlined />} placeholder="密码" />
          </Form.Item>
          <Form.Item>
            <Button type="primary" htmlType="submit" block loading={loading}>
              登录
            </Button>
          </Form.Item>
        </Form>
        <div style={{ textAlign: 'center', color: '#999' }}>
          没有账号？<a onClick={() => history.push('/register')}>立即注册</a>
        </div>
      </Card>
    </div>
  );
}
