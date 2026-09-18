import { Link } from "react-router-dom";
import { formatDistanceToNow } from "date-fns";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import type { Task, TaskStatus } from "@/features/tasks/types/task.types";

const statusDotColors: Record<TaskStatus, string> = {
  TODO: "bg-slate-400",
  IN_PROGRESS: "bg-amber-500",
  COMPLETED: "bg-emerald-500",
  ARCHIVED: "bg-purple-500",
};

interface RecentTasksProps {
  tasks: Task[];
}

export function RecentTasks({ tasks }: Readonly<RecentTasksProps>) {
  return (
    <Card>
      <CardHeader>
        <CardTitle>Recent activity</CardTitle>
        <CardDescription>Your most recently updated tasks</CardDescription>
      </CardHeader>
      <CardContent>
        {tasks.length === 0 ? (
          <p className="text-sm text-muted-foreground py-8 text-center">
            No recent tasks
          </p>
        ) : (
          <div className="space-y-3">
            {tasks.map((task) => (
              <Link
                key={task.publicId}
                to={`/app/tasks`}
                className="flex items-center gap-3 rounded-lg border p-3 transition-colors hover:bg-accent"
              >
                <span
                  className={`h-2 w-2 shrink-0 rounded-full ${statusDotColors[task.status]}`}
                  aria-hidden="true"
                />
                <div className="flex-1 min-w-0">
                  <p className="text-sm font-medium truncate">{task.title}</p>
                  <p className="text-xs text-muted-foreground">
                    Updated{" "}
                    {formatDistanceToNow(new Date(task.updatedAt), {
                      addSuffix: true,
                    })}
                  </p>
                </div>
              </Link>
            ))}
          </div>
        )}
      </CardContent>
    </Card>
  );
}
