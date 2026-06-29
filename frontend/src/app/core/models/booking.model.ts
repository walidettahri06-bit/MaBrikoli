export type BookingStatus = 'PENDING' | 'CONFIRMED' | 'REJECTED' | 'CANCELLED' | 'COMPLETED';

export interface BookingResponse {
  id: number;
  clientId: number;
  clientName: string;
  artisanId: number;
  artisanName: string;
  categoryId: number;
  categoryName: string;
  description: string;
  address: string;
  city: string;
  bookingDate: string; // ISO date 'YYYY-MM-DD'
  preferredTime: string; // 'HH:mm:ss'
  status: BookingStatus;
  estimatedPrice: number;
  finalPrice?: number;
  clientNotes?: string;
  artisanNotes?: string;
  cancellationReason?: string;
  completedAt?: string;
  cancelledAt?: string;
  createdAt: string;
  updatedAt: string;
}

export interface BookingRequest {
  artisanId: number;
  categoryId: number;
  description: string;
  address: string;
  city: string;
  bookingDate: string; // 'YYYY-MM-DD'
  preferredTime?: string; // 'HH:mm'
  clientNotes?: string;
}
