import { useQuery } from "@tanstack/react-query";
import { apiClient } from "@/lib/api/client";
import { ENDPOINTS } from "@/lib/api/endpoints";
import type { Page } from "@/types";
import type { Task } from "@/features/tasks/types/task.types";

export interface DashboardStats {
  totalTasks: number;
  completedTasks: number;
  inProgressTasks: number;
  todoTasks: number;
  dueSoonCount: number;
  // Données pour les graphiques
  activityData: Array<{ day: string; created: number; completed: number }>;
  statusData: Array<{ name: string; value: number; color: string }>;
  recentTasks: Task[];
  upcomingTasks: Task[];
}

export function useDashboardStats() {
  return useQuery({
    queryKey: ["dashboard", "stats"],
    queryFn: async (): Promise<DashboardStats> => {
      const response = await apiClient.get<Page<Task>>(
        `${ENDPOINTS.tasks.getAll}?page=0&size=1000&includeArchived=false`,
      );
      const tasks = response.data.content;
      const now = new Date();

      // --- Statistiques de base ---
      const threeDaysFromNow = new Date(
        now.getTime() + 3 * 24 * 60 * 60 * 1000,
      );

      // --- Données d'activité (7 derniers jours) ---
      const activityData: Array<{
        day: string;
        created: number;
        completed: number;
      }> = [];
      for (let i = 6; i >= 0; i--) {
        const dayStart = new Date(now);
        dayStart.setDate(now.getDate() - i);
        dayStart.setHours(0, 0, 0, 0);

        const dayEnd = new Date(dayStart);
        dayEnd.setDate(dayStart.getDate() + 1);

        const created = tasks.filter((t) => {
          const createdDate = new Date(t.createdAt);
          return createdDate >= dayStart && createdDate < dayEnd;
        }).length;

        const completed = tasks.filter((t) => {
          if (!t.completedAt) return false;
          const completedDate = new Date(t.completedAt);
          return completedDate >= dayStart && completedDate < dayEnd;
        }).length;

        activityData.push({
          day: dayStart.toLocaleDateString("en-US", { weekday: "short" }),
          created,
          completed,
        });
      }

      // --- Répartition par statut ---
      const statusCounts = {
        TODO: tasks.filter((t) => t.status === "TODO").length,
        IN_PROGRESS: tasks.filter((t) => t.status === "IN_PROGRESS").length,
        COMPLETED: tasks.filter((t) => t.status === "COMPLETED").length,
      };

      const statusData = [
        { name: "To Do", value: statusCounts.TODO, color: "#94a3b8" },
        {
          name: "In Progress",
          value: statusCounts.IN_PROGRESS,
          color: "#f59e0b",
        },
        { name: "Completed", value: statusCounts.COMPLETED, color: "#10b981" },
      ].filter((s) => s.value > 0);

      // --- Tâches récentes (5 dernières modifiées) ---
      const recentTasks = [...tasks]
        .sort(
          (a, b) =>
            new Date(b.updatedAt).getTime() - new Date(a.updatedAt).getTime(),
        )
        .slice(0, 5);

      // --- Prochaines échéances ---
      const upcomingTasks = tasks
        .filter(
          (t) =>
            t.dueDate && t.status !== "COMPLETED" && new Date(t.dueDate) >= now,
        )
        .sort(
          (a, b) =>
            new Date(a.dueDate!).getTime() - new Date(b.dueDate!).getTime(),
        )
        .slice(0, 5);

      return {
        totalTasks: response.data.totalElements,
        completedTasks: statusCounts.COMPLETED,
        inProgressTasks: statusCounts.IN_PROGRESS,
        todoTasks: statusCounts.TODO,
        dueSoonCount: tasks.filter((t) => {
          if (!t.dueDate || t.status === "COMPLETED") return false;
          const due = new Date(t.dueDate);
          return due >= now && due <= threeDaysFromNow;
        }).length,
        activityData,
        statusData,
        recentTasks,
        upcomingTasks,
      };
    },
    staleTime: 60000,
  });
}
