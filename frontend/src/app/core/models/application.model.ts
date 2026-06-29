import { Category } from './category.model';

export type ApplicationStatus = 'PENDING' | 'APPROVED' | 'REJECTED';
export type DocumentType = 'ID_CARD' | 'DIPLOMA' | 'CRIMINAL_RECORD' | 'OTHER';

export interface DocumentInfo {
  id: number;
  documentType: DocumentType;
  documentUrl: string;
  fileName: string;
  fileSize: number;
}

export interface ArtisanApplicationResponse {
  id: number;
  userId: number;
  status: ApplicationStatus;
  firstName: string;
  lastName: string;
  phoneNumber: string;
  city: string;
  yearsOfExperience: number;
  description: string;
  category: Category;
  personalPhotoUrl: string;
  adminNotes?: string;
  reviewedById?: number;
  reviewedAt?: string;
  createdAt: string;
  updatedAt: string;
  documents: DocumentInfo[];
}

export interface ArtisanApplicationRequest {
  firstName: string;
  lastName: string;
  phoneNumber: string;
  city: string;
  yearsOfExperience: number;
  description: string;
  categoryId: number;
  diplomaFileUrl: string;
  nationalIdFileUrl: string;
  personalPhotoUrl: string;
}
