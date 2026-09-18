export type NotificationType =
  | "TASK_CREATED"
  | "TASK_COMPLETED"
  | "TASK_ARCHIVED"
  | "TASK_RESTORED"
  | "TASK_DELETED"
  | "SECURITY_EVENT";

export interface Notification {
  publicId: string;
  type: NotificationType;
  title: string;
  message: string | null;
  read: boolean;
  readAt: string | null;
  resourcePublicId: string | null;
  createdAt: string;
}
