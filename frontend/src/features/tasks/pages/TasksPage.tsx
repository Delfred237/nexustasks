import { PageTransition } from "@/components/PageTransition";
import { useState } from "react";
import { Plus } from "lucide-react";
import { Button } from "@/components/ui/button";
import { useDebounce } from "@/hooks/useDebounce";
import { useTasks } from "../hooks/useTasks";
import { TaskList } from "../components/TaskList";
import { TaskFilters } from "../components/TaskFilters";
import { TaskForm } from "../components/TaskForm";
import { TaskSkeleton } from "../components/TaskSkeleton";
import { TaskEmptyState } from "../components/TaskEmptyState";
import type { Task, TaskFilters as TaskFiltersType } from "../types/task.types";

export default function TasksPage() {
  // État des filtres
  const [filters, setFilters] = useState<TaskFiltersType>({
    page: 0,
    size: 10,
    sort: "createdAt,desc",
  });

  // État de la recherche (avec debounce)
  const [searchInput, setSearchInput] = useState("");
  const debouncedSearch = useDebounce(searchInput, 300);

  // Appliquer la recherche débouncée aux filtres
  const effectiveFilters: TaskFiltersType = {
    ...filters,
    search: debouncedSearch || undefined,
  };

  // État du dialog (create/edit)
  const [isDialogOpen, setIsDialogOpen] = useState(false);
  const [editingTask, setEditingTask] = useState<Task | null>(null);

  // Fetch des tâches
  const { data, isLoading, isError, error } = useTasks(effectiveFilters);

  // Handlers
  const handleFiltersChange = (newFilters: Partial<TaskFiltersType>) => {
    setFilters((prev) => ({ ...prev, ...newFilters }));
  };

  const handleSearchChange = (value: string) => {
    setSearchInput(value);
    // Reset la page quand la recherche change
    setFilters((prev) => ({ ...prev, page: 0 }));
  };

  const handlePageChange = (page: number) => {
    setFilters((prev) => ({ ...prev, page }));
  };

  const handleCreateTask = () => {
    setEditingTask(null);
    setIsDialogOpen(true);
  };

  const handleEditTask = (task: Task) => {
    setEditingTask(task);
    setIsDialogOpen(true);
  };

  const handleCloseDialog = () => {
    setIsDialogOpen(false);
    setEditingTask(null);
  };

  const hasFilters = !!(filters.status || filters.priority || debouncedSearch);

  return (
    <PageTransition>
      <div className="space-y-6">
        {/* Header */}
        <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
          <div>
            <h1 className="text-2xl font-bold">Tasks</h1>
            <p className="text-muted-foreground">
              Manage and organize your tasks
            </p>
          </div>
          <Button onClick={handleCreateTask} className="gap-2">
            <Plus className="h-4 w-4" />
            New Task
          </Button>
        </div>

        {/* Filters */}
        <TaskFilters
          filters={effectiveFilters}
          onFiltersChange={handleFiltersChange}
          searchValue={searchInput}
          onSearchChange={handleSearchChange}
        />

        {/* Content */}
        {isLoading ? (
          <TaskSkeleton />
        ) : isError ? (
          <div className="rounded-lg border border-destructive p-6 text-center">
            <p className="text-destructive">
              Failed to load tasks:{" "}
              {error instanceof Error ? error.message : "Unknown error"}
            </p>
            <Button
              variant="outline"
              className="mt-2"
              onClick={() => window.location.reload()}
            >
              Retry
            </Button>
          </div>
        ) : data && data.content.length > 0 ? (
          <TaskList
            data={data}
            onEdit={handleEditTask}
            onPageChange={handlePageChange}
          />
        ) : (
          <TaskEmptyState
            hasFilters={hasFilters}
            onClearFilters={() => {
              setFilters({ page: 0, size: 10, sort: "createdAt,desc" });
              setSearchInput("");
            }}
            onCreateTask={handleCreateTask}
          />
        )}

        {/* Create/Edit Dialog */}
        {isDialogOpen && (
          <div className="fixed inset-0 z-50 flex items-center justify-center">
            {/* Overlay */}
            <div
              className="absolute inset-0 bg-black/50"
              onClick={handleCloseDialog}
              aria-hidden="true"
            />

            {/* Dialog content */}
            <div className="relative z-10 w-full max-w-lg rounded-lg border bg-background p-6 shadow-lg mx-4 max-h-[90vh] overflow-y-auto">
              <h2 className="text-lg font-semibold mb-4">
                {editingTask ? "Edit Task" : "Create New Task"}
              </h2>
              <TaskForm task={editingTask} onClose={handleCloseDialog} />
            </div>
          </div>
        )}
      </div>
    </PageTransition>
  );
}
