import { Archive, ArchiveRestore, Trash2, Pencil } from "lucide-react";
import { Button } from "@/components/ui/button";
import {
  type Task,
  TASK_PRIORITY_COLORS,
  TASK_PRIORITY_LABELS,
  TASK_STATUS_COLORS,
  TASK_STATUS_LABELS,
} from "../types/task.types";
import {
  useArchiveTask,
  useRestoreTask,
  useDeleteTask,
} from "../hooks/useTasks";
import { format } from "date-fns";

interface TaskCardProps {
  task: Task;
  onEdit: (task: Task) => void;
}

export function TaskCard({ task, onEdit }: Readonly<TaskCardProps>) {
  const archiveTask = useArchiveTask();
  const restoreTask = useRestoreTask();
  const deleteTask = useDeleteTask();

  const handleArchive = () => {
    archiveTask.mutate(task.publicId);
  };

  const handleRestore = () => {
    restoreTask.mutate(task.publicId);
  };

  const handleDelete = () => {
    if (window.confirm("Are you sure you want to delete this task?")) {
      deleteTask.mutate(task.publicId);
    }
  };

  return (
    <div className="rounded-lg border bg-card p-4 shadow-sm transition-shadow hover:shadow-md">
      <div className="flex items-start justify-between gap-2">
        <div className="flex-1 min-w-0">
          {/* Title + Category */}
          <div className="flex items-center gap-2 flex-wrap">
            <h3 className="font-medium text-sm sm:text-base truncate">
              {task.title}
            </h3>
            {task.category && (
              <span
                className="inline-flex items-center rounded-full px-2 py-0.5 text-xs font-medium"
                style={{
                  backgroundColor: `${task.category.color}20`,
                  color: task.category.color,
                }}
              >
                {task.category.name}
              </span>
            )}
          </div>

          {/* Description */}
          {task.description && (
            <p className="mt-1 text-sm text-muted-foreground line-clamp-2">
              {task.description}
            </p>
          )}

          {/* Meta info */}
          <div className="mt-2 flex items-center gap-2 flex-wrap">
            {/* Status badge */}
            <span
              className={`inline-flex items-center rounded-full px-2 py-0.5 text-xs font-medium ${TASK_STATUS_COLORS[task.status]}`}
            >
              {TASK_STATUS_LABELS[task.status]}
            </span>

            {/* Priority badge */}
            <span
              className={`inline-flex items-center rounded-full px-2 py-0.5 text-xs font-medium ${TASK_PRIORITY_COLORS[task.priority]}`}
            >
              {TASK_PRIORITY_LABELS[task.priority]}
            </span>

            {/* Due date */}
            {task.dueDate && (
              <span className="text-xs text-muted-foreground">
                Due: {format(new Date(task.dueDate), "MMM d, yyyy")}
              </span>
            )}
          </div>
        </div>

        {/* Actions */}
        <div className="flex items-center gap-1 shrink-0">
          <Button
            variant="ghost"
            size="icon"
            onClick={() => onEdit(task)}
            aria-label={`Edit ${task.title}`}
          >
            <Pencil className="h-4 w-4" />
          </Button>

          {task.archived ? (
            <Button
              variant="ghost"
              size="icon"
              onClick={handleRestore}
              disabled={restoreTask.isPending}
              aria-label={`Restore ${task.title}`}
            >
              <ArchiveRestore className="h-4 w-4" />
            </Button>
          ) : (
            <Button
              variant="ghost"
              size="icon"
              onClick={handleArchive}
              disabled={archiveTask.isPending}
              aria-label={`Archive ${task.title}`}
            >
              <Archive className="h-4 w-4" />
            </Button>
          )}

          <Button
            variant="ghost"
            size="icon"
            onClick={handleDelete}
            disabled={deleteTask.isPending}
            aria-label={`Delete ${task.title}`}
            className="text-destructive hover:text-destructive"
          >
            <Trash2 className="h-4 w-4" />
          </Button>
        </div>
      </div>
    </div>
  );
}
