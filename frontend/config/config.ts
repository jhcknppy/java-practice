import { defineConfig } from '@umijs/max';

export default defineConfig({
  antd: {},
  model: {},
  initialState: {},
  request: {},
  layout: {
    title: 'Java 练习系统',
    locale: false,
  },
  routes: [
    { path: '/', redirect: '/home' },
    { path: '/login', component: './Login', layout: false },
    { path: '/register', component: './Register', layout: false },
    { name: '首页', path: '/home', icon: 'HomeOutlined', component: './Home', wrappers: ['@/wrappers/Auth'] },
    { name: '错题本', path: '/wrong', icon: 'BookOutlined', component: './WrongBook', wrappers: ['@/wrappers/Auth'] },
    { name: '章节学习', path: '/chapter/:id', component: './Chapter', hideInMenu: true, wrappers: ['@/wrappers/Auth'] },
    { name: '练习', path: '/practice/:chapterId', component: './Practice', hideInMenu: true, wrappers: ['@/wrappers/Auth'] },
  ],
  npmClient: 'npm',
  proxy: {
    '/api': {
      target: 'http://127.0.0.1:8082',
      changeOrigin: true,
    },
  },
});
