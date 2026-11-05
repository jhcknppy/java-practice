import { request } from '@umijs/max';

export interface UserItem {
  id: number;
  username: string;
  nickname: string;
  level: number;
  createdAt?: string;
}

interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
}

export async function login(data: { username: string; password: string }) {
  const res = await request<ApiResponse<{ token: string; user: UserItem }>>('/api/auth/login', {
    method: 'POST',
    data,
  });
  return res.data;
}

export async function register(data: { username: string; nickname: string; password: string }) {
  const res = await request<ApiResponse<UserItem>>('/api/auth/register', {
    method: 'POST',
    data,
  });
  return res.data;
}

export async function getCurrentUser() {
  const res = await request<ApiResponse<UserItem>>('/api/auth/me', { method: 'GET' });
  return res.data;
}
