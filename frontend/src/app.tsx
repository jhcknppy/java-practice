import type { RequestConfig, RunTimeLayoutConfig } from '@umijs/max';
import { message } from 'antd';
import AvatarDropdown from '@/components/AvatarDropdown';
import { getCurrentUser } from '@/services/auth';
import { clearToken, getToken } from '@/utils/auth';

export async function getInitialState() {
  const token = getToken();
  if (!token) {
    return { currentUser: undefined };
  }
  try {
    const user = await getCurrentUser();
    return { currentUser: user };
  } catch {
    clearToken();
    return { currentUser: undefined };
  }
}

export const request: RequestConfig = {
  timeout: 10000,
  requestInterceptors: [
    (url: string, options: any) => {
      const token = getToken();
      if (token) {
        options.headers = {
          ...(options.headers || {}),
          Authorization: `Bearer ${token}`,
        };
      }
      return { url, options };
    },
  ],
  errorConfig: {
    errorHandler(error: any, opts: any) {
      if (opts?.skipErrorHandler) throw error;
      if (error?.response?.status === 401) {
        clearToken();
        if (window.location.pathname !== '/login') {
          message.error('登录已过期，请重新登录');
          window.location.href = '/login';
        }
        throw error;
      }
      const msg = error?.data?.message || error?.message || '请求失败，请稍后重试';
      message.error(msg);
      throw error;
    },
  },
};

export const layout: RunTimeLayoutConfig = () => ({
  layout: 'mix',
  rightContentRender: () => <AvatarDropdown />,
});
