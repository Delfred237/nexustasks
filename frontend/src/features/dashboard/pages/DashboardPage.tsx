import {
  CheckSquare,
  Clock,
  AlertTriangle,
  TrendingUp,
  Loader2,
} from "lucide-react";
import { StatCard } from "@/features/dashboard/components/StatCard";
import { useAuth } from "@/features/auth/hooks/useAuth";
import { useDashboardStats } from "@/features/dashboard/hooks/useDashboardStats";
import { PageTransition } from "@/components/PageTransition";

export default function DashboardPage() {
  const { user } = useAuth();
  const { data: stats, isLoading } = useDashboardStats();

  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-64">
        <Loader2 className="h-8 w-8 animate-spin text-muted-foreground" />
      </div>
    );
  }

  return (
    <PageTransition>
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl sm:text-3xl font-bold">
          Welcome back, {user?.firstName}! 👋
        </h1>
        <p className="mt-1 text-muted-foreground">
          Here&apos;s what&apos;s happening with your tasks today.
        </p>
      </div>

      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <StatCard
          title="Total Tasks"
          value={stats?.totalTasks ?? 0}
          icon={CheckSquare}
        />
        <StatCard
          title="Completed"
          value={stats?.completedTasks ?? 0}
          icon={TrendingUp}
        />
        <StatCard
          title="In Progress"
          value={stats?.inProgressTasks ?? 0}
          icon={Clock}
        />
        <StatCard
          title="Due Soon (3 days)"
          value={stats?.dueSoonCount ?? 0}
          icon={AlertTriangle}
        />
      </div>

      {/* Quick actions */}
      <div className="rounded-lg border p-6">
        <h2 className="text-lg font-semibold mb-2">Quick Actions</h2>
        <p className="text-sm text-muted-foreground">
          Navigate to Tasks to create, filter, and manage your tasks. Use
          Categories to organize them, and check Notifications for updates.
        </p>
      </div>
    </div>
    </PageTransition>
  );
}
