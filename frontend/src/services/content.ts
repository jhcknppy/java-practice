import { request } from '@umijs/max';

export interface ChapterVO {
  id: number;
  name: string;
  description?: string;
  passRate: number;
  locked: boolean;
  completed: boolean;
  answered: number;
  total: number;
  correct: number;
}

export interface ModuleVO {
  id: number;
  name: string;
  description?: string;
  chapters: ChapterVO[];
}

export interface KnowledgePointItem {
  id: number;
  title: string;
  content: string;
}

export interface OptionVO {
  label: string;
  content: string;
}

export interface QuestionItem {
  id: number;
  type: string;
  title: string;
  difficulty: number;
  knowledgePointTitle?: string;
  options: OptionVO[];
}

interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
}

export async function getModules() {
  const res = await request<ApiResponse<ModuleVO[]>>('/api/modules', { method: 'GET' });
  return res.data;
}

export async function getChapter(id: number) {
  const res = await request<ApiResponse<{ id: number; name: string; description?: string; passRate: number }>>(
    `/api/chapters/${id}`,
    { method: 'GET' },
  );
  return res.data;
}

export async function getKnowledgePoints(chapterId: number) {
  const res = await request<ApiResponse<KnowledgePointItem[]>>(`/api/chapters/${chapterId}/knowledge-points`, {
    method: 'GET',
  });
  return res.data;
}

export async function getQuestions(chapterId: number) {
  const res = await request<ApiResponse<QuestionItem[]>>(`/api/chapters/${chapterId}/questions`, { method: 'GET' });
  return res.data;
}
