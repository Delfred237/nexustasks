import { NavLink, useLocation } from "react-router-dom";
import {
  LayoutDashboard,
  CheckSquare,
  FolderOpen,
  Bell,
  User,
  X,
} from "lucide-react";
import { cn } from "@/lib/utils";
import { useSidebar } from "@/hooks/useSidebar";
import { useIsDesktop } from "@/hooks/useMediaQuery";
import { Button } from "@/components/ui/button";

const navItems = [
  { to: "/app/dashboard", label: "Dashboard", icon: LayoutDashboard },
  { to: "/app/tasks", label: "Tasks", icon: CheckSquare },
  { to: "/app/categories", label: "Categories", icon: FolderOpen },
  { to: "/app/notifications", label: "Notifications", icon: Bell },
  { to: "/app/profile", label: "Profile", icon: User },
];

export function Sidebar() {
  const { isOpen, close } = useSidebar();
  const isDesktop = useIsDesktop();
  const location = useLocation();

  // Fermer la sidebar automatiquement sur mobile quand on navigue
  const handleNavClick = () => {
    if (!isDesktop) {
      close();
    }
  };

  return (
    <>
      {/* Overlay mobile (visible uniquement quand la sidebar est ouverte) */}
      {!isDesktop && isOpen && (
        <div
          className="fixed inset-0 z-40 bg-black/50 lg:hidden"
          onClick={close}
          aria-hidden="true"
        />
      )}

      {/* Sidebar */}
      <aside
        className={cn(
          "fixed inset-y-0 left-0 z-50 w-64 transform bg-card border-r border-border transition-transform duration-200 ease-in-out",
          "lg:translate-x-0", // Toujours visible sur desktop
          isOpen ? "translate-x-0" : "-translate-x-full", // Animé sur mobile
        )}
      >
        {/* Header sidebar */}
        <div className="flex h-16 items-center justify-between px-4 border-b">
          <span className="text-xl font-bold">NexusTasks</span>
          {!isDesktop && (
            <Button
              variant="ghost"
              size="icon"
              onClick={close}
              aria-label="Close sidebar"
            >
              <X className="h-5 w-5" />
            </Button>
          )}
        </div>

        {/* Navigation */}
        <nav className="mt-4 px-3 space-y-1" aria-label="Main navigation">
          {navItems.map((item) => {
            const isActive =
              location.pathname === item.to ||
              location.pathname.startsWith(item.to + "/");

            return (
              <NavLink
                key={item.to}
                to={item.to}
                onClick={handleNavClick}
                className={cn(
                  "flex items-center gap-3 rounded-md px-3 py-2 text-sm font-medium transition-colors",
                  "focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring",
                  isActive
                    ? "bg-primary text-primary-foreground"
                    : "text-muted-foreground hover:bg-accent hover:text-accent-foreground",
                )}
                aria-current={isActive ? "page" : undefined}
              >
                <item.icon className="h-4 w-4 shrink-0" aria-hidden="true" />
                {item.label}
              </NavLink>
            );
          })}
        </nav>
      </aside>
    </>
  );
}
