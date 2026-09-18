import { apiClient } from "@/lib/api/client";
import { ENDPOINTS } from "@/lib/api/endpoints";
import type { Page } from "@/types";
import type {
  Category,
  CreateCategoryRequest,
  UpdateCategoryRequest,
} from "../types/category.types";

export const categoryService = {
  async getCategories(page = 0, size = 20): Promise<Page<Category>> {
    const response = await apiClient.get<Page<Category>>(
      `${ENDPOINTS.categories.getAll}?page=${page}&size=${size}&sort=name,asc`,
    );
    return response.data;
  },

  async getCategory(publicId: string): Promise<Category> {
    const response = await apiClient.get<Category>(
      ENDPOINTS.categories.getById(publicId),
    );
    return response.data;
  },

  async createCategory(data: CreateCategoryRequest): Promise<Category> {
    const response = await apiClient.post<Category>(
      ENDPOINTS.categories.create,
      data,
    );
    return response.data;
  },

  async updateCategory(
    publicId: string,
    data: UpdateCategoryRequest,
  ): Promise<Category> {
    const response = await apiClient.put<Category>(
      ENDPOINTS.categories.update(publicId),
      data,
    );
    return response.data;
  },

  async deleteCategory(publicId: string): Promise<void> {
    await apiClient.delete(ENDPOINTS.categories.delete(publicId));
  },
};
