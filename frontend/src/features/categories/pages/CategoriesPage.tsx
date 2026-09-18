import { useState } from "react";
import { Plus } from "lucide-react";
import { Button } from "@/components/ui/button";
import { useCategories } from "../hooks/useCategories";
import { CategoryList } from "../components/CategoryList";
import { CategoryForm } from "../components/CategoryForm";
import type { Category } from "../types/category.types";
import { PageTransition } from "@/components/PageTransition";

export default function CategoriesPage() {
  const [isDialogOpen, setIsDialogOpen] = useState(false);
  const [editingCategory, setEditingCategory] = useState<Category | null>(null);
  const { data, isLoading } = useCategories();

  const handleCreate = () => {
    setEditingCategory(null);
    setIsDialogOpen(true);
  };

  const handleEdit = (category: Category) => {
    setEditingCategory(category);
    setIsDialogOpen(true);
  };

  const handleClose = () => {
    setIsDialogOpen(false);
    setEditingCategory(null);
  };

  return (
    <PageTransition>
      <div className="space-y-6">
        <div className="flex items-center justify-between">
          <div>
            <h1 className="text-2xl font-bold">Categories</h1>
            <p className="text-muted-foreground">
              Organize your tasks with categories
            </p>
          </div>
          <Button onClick={handleCreate} className="gap-2">
            <Plus className="h-4 w-4" />
            New Category
          </Button>
        </div>

        {isLoading ? (
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
            {Array.from({ length: 3 }).map((_, i) => (
              <div key={i} className="rounded-lg border p-4 animate-pulse">
                <div className="h-4 bg-muted rounded w-1/2 mb-2" />
                <div className="h-3 bg-muted rounded w-full" />
              </div>
            ))}
          </div>
        ) : data && data.content.length > 0 ? (
          <CategoryList categories={data.content} onEdit={handleEdit} />
        ) : (
          <div className="text-center py-12">
            <p className="text-muted-foreground">
              No categories yet. Create one to get started.
            </p>
          </div>
        )}

        {isDialogOpen && (
          <div className="fixed inset-0 z-50 flex items-center justify-center">
            <div
              className="absolute inset-0 bg-black/50"
              role="presentation"
              onClick={handleClose}
            />
            <div className="relative z-10 w-full max-w-md rounded-lg border bg-background p-6 shadow-lg mx-4">
              <h2 className="text-lg font-semibold mb-4">
                {editingCategory ? "Edit Category" : "New Category"}
              </h2>
              <CategoryForm category={editingCategory} onClose={handleClose} />
            </div>
          </div>
        )}
      </div>
    </PageTransition>
  );
}
