import { LogoutOutlined, UserOutlined } from '@ant-design/icons';
import { Avatar, Dropdown } from 'antd';
import { useModel } from '@umijs/max';
import { clearToken } from '@/utils/auth';

export default function AvatarDropdown() {
  const { initialState } = useModel('@@initialState');
  const user = initialState?.currentUser;
  const name = user?.nickname || user?.username || '用户';

  return (
    <Dropdown
      placement="bottomRight"
      menu={{
        items: [{ key: 'logout', icon: <LogoutOutlined />, label: '退出登录' }],
        onClick: ({ key }) => {
          if (key === 'logout') {
            clearToken();
            window.location.href = '/login';
          }
        },
      }}
    >
      <span style={{ cursor: 'pointer', display: 'inline-flex', alignItems: 'center', gap: 8 }}>
        <Avatar style={{ background: '#1677ff' }} icon={<UserOutlined />} />
        <span>{name}</span>
      </span>
    </Dropdown>
  );
}
