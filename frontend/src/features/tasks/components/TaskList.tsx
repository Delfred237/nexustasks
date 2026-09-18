import { Button } from "@/components/ui/button";
import type { Page } from "@/types";
import type { Task } from "@/features/tasks/types/task.types";
import { TaskCard } from "./TaskCard";

interface TaskListProps {
  data: Page<Task>;
  onEdit: (task: Task) => void;
  onPageChange: (page: number) => void;
}

export function TaskList({ data, onEdit, onPageChange }: Readonly<TaskListProps>) {
  if (data.content.length === 0) {
    return null;
  }

  return (
    <div className="space-y-3">
      {/* Task cards */}
      <div className="space-y-3">
        {data.content.map((task) => (
          <TaskCard key={task.publicId} task={task} onEdit={onEdit} />
        ))}
      </div>

      {/* Pagination */}
      {data.totalPages > 1 && (
        <div className="flex items-center justify-between pt-4">
          <p className="text-sm text-muted-foreground">
            Page {data.number + 1} of {data.totalPages} ({data.totalElements}{" "}
            tasks)
          </p>
          <div className="flex gap-2">
            <Button
              variant="outline"
              size="sm"
              onClick={() => onPageChange(data.number - 1)}
              disabled={data.first}
            >
              Previous
            </Button>
            <Button
              variant="outline"
              size="sm"
              onClick={() => onPageChange(data.number + 1)}
              disabled={data.last}
            >
              Next
            </Button>
          </div>
        </div>
      )}
    </div>
  );
}
