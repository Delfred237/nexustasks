import { Menu, Bell } from "lucide-react";
import { Button } from "@/components/ui/button";
import { useSidebar } from "@/hooks/useSidebar";
import { useIsDesktop } from "@/hooks/useMediaQuery";
import { UserDropdown } from "./UserDropdown";
import { ThemeToggle } from "./ThemeToggle";

export function Header() {
  const { toggle } = useSidebar();
  const isDesktop = useIsDesktop();

  return (
    <header className="sticky top-0 z-30 flex h-16 items-center justify-between border-b bg-background/95 backdrop-blur supports-backdrop-filter:bg-background/60 px-4">
      <div className="flex items-center gap-2">
        {/* Hamburger menu - visible uniquement sur mobile */}
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

        {/* Titre de la page (optionnel, peut être dynamique) */}
        <h1 className="text-lg font-semibold hidden sm:block">NexusTasks</h1>
      </div>

      <div className="flex items-center gap-1">
        {/* Notification bell */}
        <Button
          variant="ghost"
          size="icon"
          aria-label="Notifications"
          className="relative"
        >
          <Bell className="h-5 w-5" />
          {/* Badge pour les notifications non lues */}
          <span className="absolute top-1 right-1 h-2 w-2 rounded-full bg-destructive" />
        </Button>

        {/* Theme toggle */}
        <ThemeToggle />

        {/* User dropdown */}
        <UserDropdown />
      </div>
    </header>
  );
}
