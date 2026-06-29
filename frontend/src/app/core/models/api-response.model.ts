/**
 * Standard API wrapper envelope matching the Spring Boot backend structure.
 */
export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp: string;
}
