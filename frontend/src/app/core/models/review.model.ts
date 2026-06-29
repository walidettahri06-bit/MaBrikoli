export interface ReviewResponse {
  id: number;
  bookingId: number;
  clientId: number;
  clientName: string;
  artisanId: number;
  rating: number;
  comment: string;
  createdAt: string;
  updatedAt: string;
}

export interface ReviewRequest {
  bookingId: number;
  rating: number;
  comment: string;
}
