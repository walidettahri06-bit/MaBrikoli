import { Category } from './category.model';

export interface AvailabilityInfo {
  id: number;
  dayOfWeek: string;
  startTime: string;
  endTime: string;
  available: boolean;
}

/**
 * Standard Artisan Profile representation matching the backend response.
 */
export interface ArtisanProfileResponse {
  id: number;
  userId: number;
  firstName: string;
  lastName: string;
  profilePhoto: string;
  bio: string;
  yearsOfExperience: number;
  city: string;
  address: string;
  hourlyPrice: number;
  averageRating: number;
  totalReviews: number;
  available: boolean;
  verified: boolean;
  categories: Category[];
  availabilities: AvailabilityInfo[];
}
