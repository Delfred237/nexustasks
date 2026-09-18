export type TaskStatus = "TODO" | "IN_PROGRESS" | "COMPLETED" | "ARCHIVED";
export type TaskPriority = "LOW" | "MEDIUM" | "HIGH" | "URGENT";

export interface Task {
  publicId: string;
  title: string;
  slug: string;
  description: string | null;
  status: TaskStatus;
  priority: TaskPriority;
  dueDate: string | null;
  completedAt: string | null;
  archived: boolean;
  archivedAt: string | null;
  category: CategorySummary | null;
  createdAt: string;
  updatedAt: string;
}

export interface CategorySummary {
  publicId: string;
  name: string;
  slug: string;
  color: string;
}

export interface CreateTaskRequest {
  title: string;
  description?: string;
  status: TaskStatus;
  priority: TaskPriority;
  dueDate?: string | null;
  categoryPublicId?: string | null;
}

export interface UpdateTaskRequest {
  title: string;
  description?: string;
  status: TaskStatus;
  priority: TaskPriority;
  dueDate?: string | null;
  categoryPublicId?: string | null;
}

export interface TaskFilters {
  page: number;
  size: number;
  sort?: string;
  status?: TaskStatus;
  priority?: TaskPriority;
  category?: string;
  search?: string;
  includeArchived?: boolean;
}

// Labels pour affichage
export const TASK_STATUS_LABELS: Record<TaskStatus, string> = {
  TODO: "To Do",
  IN_PROGRESS: "In Progress",
  COMPLETED: "Completed",
  ARCHIVED: "Archived",
};

export const TASK_PRIORITY_LABELS: Record<TaskPriority, string> = {
  LOW: "Low",
  MEDIUM: "Medium",
  HIGH: "High",
  URGENT: "Urgent",
};

export const TASK_PRIORITY_COLORS: Record<TaskPriority, string> = {
  LOW: "bg-gray-100 text-gray-800",
  MEDIUM: "bg-blue-100 text-blue-800",
  HIGH: "bg-orange-100 text-orange-800",
  URGENT: "bg-red-100 text-red-800",
};

export const TASK_STATUS_COLORS: Record<TaskStatus, string> = {
  TODO: "bg-slate-100 text-slate-800",
  IN_PROGRESS: "bg-yellow-100 text-yellow-800",
  COMPLETED: "bg-green-100 text-green-800",
  ARCHIVED: "bg-purple-100 text-purple-800",
};
