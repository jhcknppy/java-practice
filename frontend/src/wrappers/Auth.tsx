import { Spin } from 'antd';
import { Navigate, Outlet, useModel } from '@umijs/max';

export default function Auth() {
  const { initialState, loading } = useModel('@@initialState');

  if (loading) {
    return (
      <div style={{ display: 'flex', justifyContent: 'center', paddingTop: 200 }}>
        <Spin />
      </div>
    );
  }

  if (!initialState?.currentUser) {
    return <Navigate to="/login" replace />;
  }

  return <Outlet />;
}
