import { Search, X } from "lucide-react";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import type {
  TaskFilters as TaskFiltersType,
  TaskPriority,
  TaskStatus,
} from "../types/task.types";

interface TaskFiltersProps {
  filters: TaskFiltersType;
  onFiltersChange: (filters: Partial<TaskFiltersType>) => void;
  searchValue: string;
  onSearchChange: (value: string) => void;
}

const statusOptions: Array<{ value: TaskStatus; label: string }> = [
  { value: "TODO", label: "To Do" },
  { value: "IN_PROGRESS", label: "In Progress" },
  { value: "COMPLETED", label: "Completed" },
];

const priorityOptions: Array<{ value: TaskPriority; label: string }> = [
  { value: "LOW", label: "Low" },
  { value: "MEDIUM", label: "Medium" },
  { value: "HIGH", label: "High" },
  { value: "URGENT", label: "Urgent" },
];

export function TaskFilters({
  filters,
  onFiltersChange,
  searchValue,
  onSearchChange,
}: Readonly<TaskFiltersProps>) {
  const hasActiveFilters = filters.status || filters.priority || filters.search;

  const clearFilters = () => {
    onFiltersChange({
      status: undefined,
      priority: undefined,
      search: undefined,
      page: 0,
    });
    onSearchChange("");
  };

  return (
    <div className="space-y-3">
      {/* Search input */}
      <div className="relative">
        <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
        <Input
          type="text"
          placeholder="Search tasks..."
          value={searchValue}
          onChange={(e) => onSearchChange(e.target.value)}
          className="pl-9"
          aria-label="Search tasks"
        />
      </div>

      {/* Filter selects - responsive : stack sur mobile, row sur desktop */}
      <div className="flex flex-col sm:flex-row gap-2">
        {/* Status filter */}
        <select
          value={filters.status || ""}
          onChange={(e) =>
            onFiltersChange({
              status: (e.target.value || undefined) as TaskStatus | undefined,
              page: 0,
            })
          }
          className="flex h-9 w-full sm:w-40 rounded-md border border-input bg-transparent px-3 py-1 text-sm shadow-sm"
          aria-label="Filter by status"
        >
          <option value="">All Statuses</option>
          {statusOptions.map((opt) => (
            <option key={opt.value} value={opt.value}>
              {opt.label}
            </option>
          ))}
        </select>

        {/* Priority filter */}
        <select
          value={filters.priority || ""}
          onChange={(e) =>
            onFiltersChange({
              priority: (e.target.value || undefined) as
                | TaskPriority
                | undefined,
              page: 0,
            })
          }
          className="flex h-9 w-full sm:w-40 rounded-md border border-input bg-transparent px-3 py-1 text-sm shadow-sm"
          aria-label="Filter by priority"
        >
          <option value="">All Priorities</option>
          {priorityOptions.map((opt) => (
            <option key={opt.value} value={opt.value}>
              {opt.label}
            </option>
          ))}
        </select>

        {/* Archived toggle */}
        <label className="flex items-center gap-2 text-sm">
          <input
            type="checkbox"
            checked={filters.includeArchived || false}
            onChange={(e) =>
              onFiltersChange({ includeArchived: e.target.checked, page: 0 })
            }
            className="rounded border-input"
          />
          Show archived
        </label>

        {/* Clear filters button */}
        {hasActiveFilters && (
          <Button
            variant="outline"
            size="sm"
            onClick={clearFilters}
            className="gap-1"
          >
            <X className="h-3 w-3" />
            Clear
          </Button>
        )}
      </div>
    </div>
  );
}
