import { Pencil, Trash2 } from "lucide-react";
import { Button } from "@/components/ui/button";
import type { Category } from "../types/category.types";
import { useDeleteCategory } from "../hooks/useCategories";

interface CategoryListProps {
  categories: Category[];
  onEdit: (category: Category) => void;
}

export function CategoryList({ categories, onEdit }: Readonly<CategoryListProps>) {
  const deleteCategory = useDeleteCategory();

  const handleDelete = (category: Category) => {
    if (window.confirm(`Delete category "${category.name}"?`)) {
      deleteCategory.mutate(category.publicId);
    }
  };

  return (
    <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
      {categories.map((category) => (
        <div key={category.publicId} className="rounded-lg border bg-card p-4">
          <div className="flex items-start justify-between">
            <div className="flex items-center gap-2">
              <div
                className="h-4 w-4 rounded-full"
                style={{ backgroundColor: category.color }}
              />
              <h3 className="font-medium">{category.name}</h3>
            </div>
            <div className="flex gap-1">
              <Button
                variant="ghost"
                size="icon"
                onClick={() => onEdit(category)}
                aria-label={`Edit ${category.name}`}
              >
                <Pencil className="h-4 w-4" />
              </Button>
              <Button
                variant="ghost"
                size="icon"
                onClick={() => handleDelete(category)}
                disabled={deleteCategory.isPending}
                aria-label={`Delete ${category.name}`}
                className="text-destructive"
              >
                <Trash2 className="h-4 w-4" />
              </Button>
            </div>
          </div>
          {category.description && (
            <p className="mt-2 text-sm text-muted-foreground">
              {category.description}
            </p>
          )}
        </div>
      ))}
    </div>
  );
}
