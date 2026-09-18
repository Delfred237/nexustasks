import { CheckSquare, Clock, AlertTriangle, TrendingUp } from "lucide-react";
import { StatCard } from "@/features/dashboard/components/StatCard";
import { useAuth } from "@/features/auth/hooks/useAuth";

export default function DashboardPage() {
  const { user } = useAuth();

  // Données placeholder - seront remplacées par des appels API dans Phase 17b
  const stats = {
    totalTasks: 24,
    completedToday: 5,
    dueSoon: 3,
    inProgress: 8,
  };

  return (
    <div className="space-y-6 p-8">
      {/* Welcome header */}
      <div>
        <h1 className="text-2xl sm:text-3xl font-bold">
          Welcome back, {user?.firstName}! 👋
        </h1>
        <p className="mt-1 text-muted-foreground">
          Here&apos;s what&apos;s happening with your tasks today.
        </p>
      </div>

      {/* Stats grid - responsive : 1 col mobile, 2 col tablette, 4 col desktop */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <StatCard
          title="Total Tasks"
          value={stats.totalTasks}
          icon={CheckSquare}
        />
        <StatCard
          title="Completed Today"
          value={stats.completedToday}
          icon={TrendingUp}
          trend="+2 from yesterday"
          trendUp={true}
        />
        <StatCard title="Due Soon" value={stats.dueSoon} icon={Clock} />
        <StatCard
          title="In Progress"
          value={stats.inProgress}
          icon={AlertTriangle}
        />
      </div>

      {/* Recent tasks - placeholder */}
      <div className="rounded-lg border p-6">
        <h2 className="text-lg font-semibold mb-4">Recent Tasks</h2>
        <p className="text-muted-foreground">
          Your recent tasks will be displayed here. This section will be built
          in Phase 17b.
        </p>
      </div>
    </div>
  );
}
