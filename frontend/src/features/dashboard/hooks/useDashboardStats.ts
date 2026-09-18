import { useQuery } from "@tanstack/react-query";
import { apiClient } from "@/lib/api/client";
import { ENDPOINTS } from "@/lib/api/endpoints";
import type { Page } from "@/types";
import type { Task } from "@/features/tasks/types/task.types";

interface DashboardStats {
  totalTasks: number;
  completedTasks: number;
  inProgressTasks: number;
  todoTasks: number;
  dueSoonCount: number;
}

export function useDashboardStats() {
  return useQuery({
    queryKey: ["dashboard", "stats"],
    queryFn: async (): Promise<DashboardStats> => {
      // Récupérer toutes les tâches (page large pour compter)
      const response = await apiClient.get<Page<Task>>(
        `${ENDPOINTS.tasks.getAll}?page=0&size=1000`,
      );
      const tasks = response.data.content;

      const now = new Date();
      const threeDaysFromNow = new Date(
        now.getTime() + 3 * 24 * 60 * 60 * 1000,
      );

      return {
        totalTasks: response.data.totalElements,
        completedTasks: tasks.filter((t) => t.status === "COMPLETED").length,
        inProgressTasks: tasks.filter((t) => t.status === "IN_PROGRESS").length,
        todoTasks: tasks.filter((t) => t.status === "TODO").length,
        dueSoonCount: tasks.filter((t) => {
          if (!t.dueDate || t.status === "COMPLETED") return false;
          const due = new Date(t.dueDate);
          return due >= now && due <= threeDaysFromNow;
        }).length,
      };
    },
    staleTime: 60000, // 1 minute
  });
}
