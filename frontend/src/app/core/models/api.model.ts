export interface ProblemDetail {
  title: string; detail: string; status: number;
  errorCode?: string; errors?: Record<string, string>; timestamp?: string;
}
export interface PagedResult<T> {
  content: T[]; pageNumber: number; pageSize: number;
  totalElements: number; totalPages: number;
}
