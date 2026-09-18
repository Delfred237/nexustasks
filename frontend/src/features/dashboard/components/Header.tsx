import { useNavigate } from "react-router-dom";
import { Menu, Bell } from "lucide-react";
import { Button } from "@/components/ui/button";
import { useSidebar } from "@/hooks/useSidebar";
import { useIsDesktop } from "@/hooks/useMediaQuery";
import { useUnreadCount } from "@/features/notifications/hooks/useNotifications";
import { UserDropdown } from "./UserDropdown";
import { ThemeToggle } from "./ThemeToggle";

export function Header() {
  const { toggle } = useSidebar();
  const isDesktop = useIsDesktop();
  const navigate = useNavigate();
  const { data: unreadCount } = useUnreadCount();

  return (
    <header className="sticky top-0 z-30 flex h-16 items-center justify-between border-b bg-background/95 backdrop-blur supports-backdrop-filter:bg-background/60 px-4">
      <div className="flex items-center gap-2">
        {!isDesktop && (
          <Button
            variant="ghost"
            size="icon"
            onClick={toggle}
            aria-label="Open sidebar"
          >
            <Menu className="h-5 w-5" />
          </Button>
        )}

        <h1 className="text-lg font-semibold hidden sm:block">NexusTasks</h1>
      </div>

      <div className="flex items-center gap-1">
        {/* Cloche : navigue vers les notifications + vrai compteur */}
        <Button
          variant="ghost"
          size="icon"
          onClick={() => navigate("/app/notifications")}
          aria-label={
            unreadCount && unreadCount > 0
              ? `Notifications (${unreadCount} unread)`
              : "Notifications"
          }
          className="relative"
        >
          <Bell className="h-5 w-5" />
          {unreadCount && unreadCount > 0 && (
            <span className="absolute top-1 right-1 flex h-4 min-w-4 items-center justify-center rounded-full bg-destructive px-1 text-[10px] font-bold text-destructive-foreground">
              {unreadCount > 9 ? "9+" : unreadCount}
            </span>
          )}
        </Button>

        <ThemeToggle />
        <UserDropdown />
      </div>
    </header>
  );
}
