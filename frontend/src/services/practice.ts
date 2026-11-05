import { request } from '@umijs/max';
import type { OptionVO } from './content';

export interface AnswerResult {
  correct: boolean;
  correctAnswer: string;
  analysis: string;
  knowledgePointTitle: string;
}

export interface WrongQuestionItem {
  id: number;
  questionId: number;
  type: string;
  title: string;
  correctAnswer: string;
  analysis: string;
  knowledgePointTitle?: string;
  wrongCount: number;
  options: OptionVO[];
}

export interface KnowledgePointVO {
  id: number;
  title: string;
  content: string;
  mastery?: number | null;
  correctCount?: number;
  wrongCount?: number;
}

export interface QuestionItem {
  id: number;
  type: string;
  title: string;
  difficulty: number;
  knowledgePointTitle?: string;
  options: OptionVO[];
}

export interface SettleResult {
  completed: boolean;
  leveledUp: boolean;
  level: number;
  correct: number;
  total: number;
}

interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
}

export async function submitAnswer(questionId: number, answer: string) {
  const res = await request<ApiResponse<AnswerResult>>('/api/answers', {
    method: 'POST',
    data: { questionId, answer },
  });
  return res.data;
}

export async function getWrongQuestions() {
  const res = await request<ApiResponse<WrongQuestionItem[]>>('/api/wrong-questions', { method: 'GET' });
  return res.data;
}

export async function getChapterReview(chapterId: number) {
  const res = await request<ApiResponse<KnowledgePointVO[]>>(`/api/chapters/${chapterId}/review`, { method: 'GET' });
  return res.data;
}

export async function getReinforcements(chapterId: number) {
  const res = await request<ApiResponse<QuestionItem[]>>(`/api/chapters/${chapterId}/reinforcements`, {
    method: 'GET',
  });
  return res.data;
}

export async function settleChapter(chapterId: number) {
  const res = await request<ApiResponse<SettleResult>>(`/api/chapters/${chapterId}/settle`, { method: 'POST' });
  return res.data;
}
