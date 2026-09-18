import { apiClient } from "@/lib/api/client";
import { ENDPOINTS } from "@/lib/api/endpoints";
import type { Page } from "@/types";
import type { Notification } from "../types/notification.types";

export const notificationService = {
  async getNotifications(page = 0, size = 20): Promise<Page<Notification>> {
    const response = await apiClient.get<Page<Notification>>(
      `${ENDPOINTS.notifications.getAll}?page=${page}&size=${size}&sort=createdAt,desc`,
    );
    return response.data;
  },

  async getUnreadCount(): Promise<number> {
    const response = await apiClient.get<number>(
      ENDPOINTS.notifications.getUnreadCount,
    );
    return response.data;
  },

  async markAsRead(publicId: string): Promise<Notification> {
    const response = await apiClient.patch<Notification>(
      ENDPOINTS.notifications.markAsRead(publicId),
    );
    return response.data;
  },

  async markAllAsRead(): Promise<void> {
    await apiClient.patch(ENDPOINTS.notifications.markAllAsRead);
  },
};
