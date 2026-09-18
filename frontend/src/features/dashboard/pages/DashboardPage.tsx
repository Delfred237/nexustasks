import {
  CheckSquare,
  Clock,
  AlertTriangle,
  TrendingUp,
  Loader2,
} from "lucide-react";
import { StatCard } from "@/features/dashboard/components/StatCard";
import { TaskActivityChart } from "@/features/dashboard/components/TaskActivityChart";
import { StatusDonutChart } from "@/features/dashboard/components/StatusDonuChart";
import { RecentTasks } from "@/features/dashboard/components/RecentTasks";
import { UpcomingDeadlines } from "@/features/dashboard/components/UpcomingDeadlines";
import { useAuth } from "@/features/auth/hooks/useAuth";
import { useDashboardStats } from "@/features/dashboard/hooks/useDashboardStats";

export default function DashboardPage() {
  const { user } = useAuth();
  const { data: stats, isLoading } = useDashboardStats();

  if (isLoading || !stats) {
    return (
      <div className="flex items-center justify-center h-64">
        <Loader2 className="h-8 w-8 animate-spin text-muted-foreground" />
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Welcome header */}
      <div>
        <h1 className="text-2xl sm:text-3xl font-bold">
          Welcome back, {user?.firstName}! 👋
        </h1>
        <p className="mt-1 text-muted-foreground">
          Here&apos;s an overview of your work.
        </p>
      </div>

      {/* Stats cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <StatCard
          title="Total Tasks"
          value={stats.totalTasks}
          icon={CheckSquare}
        />
        <StatCard
          title="Completed"
          value={stats.completedTasks}
          icon={TrendingUp}
        />
        <StatCard
          title="In Progress"
          value={stats.inProgressTasks}
          icon={Clock}
        />
        <StatCard
          title="Due Soon (3 days)"
          value={stats.dueSoonCount}
          icon={AlertTriangle}
        />
      </div>

      {/* Charts row : Activity (2/3) + Status donut (1/3) */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-4">
        <TaskActivityChart data={stats.activityData} />
        <StatusDonutChart data={stats.statusData} />
      </div>

      {/* Lists row : Recent + Upcoming */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-4">
        <RecentTasks tasks={stats.recentTasks} />
        <UpcomingDeadlines tasks={stats.upcomingTasks} />
      </div>
    </div>
  );
}
