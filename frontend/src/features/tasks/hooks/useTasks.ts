import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { toast } from "react-hot-toast";
import { taskService } from "../services/task.service";
import type {
  CreateTaskRequest,
  TaskFilters,
  UpdateTaskRequest,
} from "../types/task.types";

// Query keys centralisés
export const taskKeys = {
  all: ["tasks"] as const,
  lists: () => [...taskKeys.all, "list"] as const,
  list: (filters: TaskFilters) => [...taskKeys.lists(), filters] as const,
  details: () => [...taskKeys.all, "detail"] as const,
  detail: (id: string) => [...taskKeys.details(), id] as const,
};

// Hook pour lister les tâches avec filtres
export function useTasks(filters: TaskFilters) {
  return useQuery({
    queryKey: taskKeys.list(filters),
    queryFn: () => taskService.getTasks(filters),
    placeholderData: (previousData) => previousData, // Évite le flash lors de la pagination
  });
}

// Hook pour une tâche individuelle
export function useTask(publicId: string) {
  return useQuery({
    queryKey: taskKeys.detail(publicId),
    queryFn: () => taskService.getTask(publicId),
    enabled: !!publicId,
  });
}

// Hook pour créer une tâche
export function useCreateTask() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (data: CreateTaskRequest) => taskService.createTask(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: taskKeys.lists() });
      toast.success("Task created successfully");
    },
    onError: (error: unknown) => {
      const message = extractErrorMessage(error);
      toast.error(message);
    },
  });
}

// Hook pour mettre à jour une tâche
export function useUpdateTask() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({
      publicId,
      data,
    }: {
      publicId: string;
      data: UpdateTaskRequest;
    }) => taskService.updateTask(publicId, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: taskKeys.lists() });
      toast.success("Task updated successfully");
    },
    onError: (error: unknown) => {
      const message = extractErrorMessage(error);
      toast.error(message);
    },
  });
}

// Hook pour archiver une tâche
export function useArchiveTask() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (publicId: string) => taskService.archiveTask(publicId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: taskKeys.lists() });
      toast.success("Task archived");
    },
    onError: (error: unknown) => {
      const message = extractErrorMessage(error);
      toast.error(message);
    },
  });
}

// Hook pour restaurer une tâche
export function useRestoreTask() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (publicId: string) => taskService.restoreTask(publicId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: taskKeys.lists() });
      toast.success("Task restored");
    },
    onError: (error: unknown) => {
      const message = extractErrorMessage(error);
      toast.error(message);
    },
  });
}

// Hook pour supprimer une tâche
export function useDeleteTask() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (publicId: string) => taskService.deleteTask(publicId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: taskKeys.lists() });
      toast.success("Task deleted");
    },
    onError: (error: unknown) => {
      const message = extractErrorMessage(error);
      toast.error(message);
    },
  });
}

// Helper pour extraire les messages d'erreur
function extractErrorMessage(error: unknown): string {
  if (error && typeof error === "object" && "response" in error) {
    const axiosError = error as {
      response?: { data?: { detail?: string; title?: string } };
    };
    return (
      axiosError.response?.data?.detail ||
      axiosError.response?.data?.title ||
      "An error occurred"
    );
  }
  return "An unexpected error occurred";
}
