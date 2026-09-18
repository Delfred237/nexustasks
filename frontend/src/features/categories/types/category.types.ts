export interface Category {
  publicId: string;
  name: string;
  slug: string;
  description: string | null;
  color: string;
  createdAt: string;
  updatedAt: string;
}

export interface CreateCategoryRequest {
  name: string;
  description?: string;
  color: string;
}

export interface UpdateCategoryRequest {
  name: string;
  description?: string;
  color: string;
}
