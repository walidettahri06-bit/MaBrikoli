/**
 * Represents a service category (e.g. Plumber, Electrician).
 */
export interface Category {
  id: number;
  name: string;
  description: string;
  icon?: string;
  artisanCount?: number;
}
