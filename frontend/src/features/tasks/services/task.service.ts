import { apiClient } from "@/lib/api/client";
import { ENDPOINTS } from "@/lib/api/endpoints";
import type { Page } from "@/types";
import type {
  CreateTaskRequest,
  Task,
  TaskFilters,
  UpdateTaskRequest,
} from "../types/task.types";

export const taskService = {
  async getTasks(filters: TaskFilters): Promise<Page<Task>> {
    const params = new URLSearchParams();

    if (filters.page !== undefined) params.set("page", String(filters.page));
    if (filters.size !== undefined) params.set("size", String(filters.size));
    if (filters.sort) params.set("sort", filters.sort);
    if (filters.status) params.set("status", filters.status);
    if (filters.priority) params.set("priority", filters.priority);
    if (filters.category) params.set("category", filters.category);
    if (filters.search) params.set("search", filters.search);
    if (filters.includeArchived !== undefined)
      params.set("includeArchived", String(filters.includeArchived));

    const response = await apiClient.get<Page<Task>>(
      `${ENDPOINTS.tasks.getAll}?${params.toString()}`,
    );
    return response.data;
  },

  async getTask(publicId: string): Promise<Task> {
    const response = await apiClient.get<Task>(
      ENDPOINTS.tasks.getById(publicId),
    );
    return response.data;
  },

  async createTask(data: CreateTaskRequest): Promise<Task> {
    const response = await apiClient.post<Task>(ENDPOINTS.tasks.create, data);
    return response.data;
  },

  async updateTask(publicId: string, data: UpdateTaskRequest): Promise<Task> {
    const response = await apiClient.put<Task>(
      ENDPOINTS.tasks.update(publicId),
      data,
    );
    return response.data;
  },

  async deleteTask(publicId: string): Promise<void> {
    await apiClient.delete(ENDPOINTS.tasks.delete(publicId));
  },

  async archiveTask(publicId: string): Promise<Task> {
    const response = await apiClient.post<Task>(
      ENDPOINTS.tasks.archive(publicId),
    );
    return response.data;
  },

  async restoreTask(publicId: string): Promise<Task> {
    const response = await apiClient.post<Task>(
      ENDPOINTS.tasks.restore(publicId),
    );
    return response.data;
  },
};
