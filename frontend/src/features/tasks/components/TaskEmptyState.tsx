import { ClipboardList } from "lucide-react";
import { Button } from "@/components/ui/button";

interface TaskEmptyStateProps {
  hasFilters: boolean;
  onClearFilters: () => void;
  onCreateTask: () => void;
}

export function TaskEmptyState({
  hasFilters,
  onClearFilters,
  onCreateTask,
}: Readonly<TaskEmptyStateProps>) {
  return (
    <div className="flex flex-col items-center justify-center py-12 text-center">
      <ClipboardList className="h-12 w-12 text-muted-foreground mb-4" />
      <h3 className="text-lg font-medium mb-1">No tasks found</h3>
      <p className="text-sm text-muted-foreground mb-4">
        {hasFilters
          ? "No tasks match your current filters."
          : "Get started by creating your first task."}
      </p>
      {hasFilters ? (
        <Button variant="outline" onClick={onClearFilters}>
          Clear filters
        </Button>
      ) : (
        <Button onClick={onCreateTask}>Create your first task</Button>
      )}
    </div>
  );
}
