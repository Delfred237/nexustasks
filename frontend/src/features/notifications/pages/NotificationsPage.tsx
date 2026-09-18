import { Bell, CheckCheck } from "lucide-react";
import { Button } from "@/components/ui/button";
import {
  useNotifications,
  useMarkAsRead,
  useMarkAllAsRead,
  useUnreadCount,
} from "../hooks/useNotifications";
import { format } from "date-fns";
import type { NotificationType } from "../types/notification.types";

const typeIcons: Record<NotificationType, string> = {
  TASK_CREATED: "📝",
  TASK_COMPLETED: "✅",
  TASK_ARCHIVED: "📦",
  TASK_RESTORED: "♻️",
  TASK_DELETED: "🗑️",
  SECURITY_EVENT: "🔒",
};

export default function NotificationsPage() {
  const { data, isLoading } = useNotifications();
  const { data: unreadCount } = useUnreadCount();
  const markAsRead = useMarkAsRead();
  const markAllAsRead = useMarkAllAsRead();

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold">Notifications</h1>
          <p className="text-muted-foreground">
            {unreadCount
              ? `${unreadCount} unread notification${unreadCount > 1 ? "s" : ""}`
              : "All caught up!"}
          </p>
        </div>
        {unreadCount && unreadCount > 0 && (
          <Button
            variant="outline"
            onClick={() => markAllAsRead.mutate()}
            disabled={markAllAsRead.isPending}
            className="gap-2"
          >
            <CheckCheck className="h-4 w-4" />
            Mark all as read
          </Button>
        )}
      </div>

      {isLoading ? (
        <div className="space-y-3">
          {Array.from({ length: 3 }).map((_, i) => (
            <div key={i} className="rounded-lg border p-4 animate-pulse">
              <div className="h-4 bg-muted rounded w-1/3 mb-2" />
              <div className="h-3 bg-muted rounded w-2/3" />
            </div>
          ))}
        </div>
      ) : data && data.content.length > 0 ? (
        <div className="space-y-2">
          {data.content.map((notification) => (
            <div
              key={notification.publicId}
              className={`rounded-lg border p-4 transition-colors ${
                notification.read ? "bg-card" : "bg-primary/5 border-primary/20"
              }`}
            >
              <div className="flex items-start gap-3">
                <span className="text-xl" aria-hidden="true">
                  {typeIcons[notification.type] || "🔔"}
                </span>
                <div className="flex-1 min-w-0">
                  <p className="font-medium text-sm">{notification.title}</p>
                  {notification.message && (
                    <p className="text-sm text-muted-foreground mt-1">
                      {notification.message}
                    </p>
                  )}
                  <p className="text-xs text-muted-foreground mt-1">
                    {format(
                      new Date(notification.createdAt),
                      "MMM d, yyyy HH:mm",
                    )}
                  </p>
                </div>
                {!notification.read && (
                  <Button
                    variant="ghost"
                    size="sm"
                    onClick={() => markAsRead.mutate(notification.publicId)}
                    disabled={markAsRead.isPending}
                  >
                    Mark read
                  </Button>
                )}
              </div>
            </div>
          ))}
        </div>
      ) : (
        <div className="text-center py-12">
          <Bell className="h-12 w-12 text-muted-foreground mx-auto mb-4" />
          <p className="text-muted-foreground">No notifications yet.</p>
        </div>
      )}
    </div>
  );
}
