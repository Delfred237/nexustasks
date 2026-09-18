import { describe, it, expect, vi } from "vitest";
import { screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { renderWithProviders } from "@/test/test-utils";
import { TaskCard } from "./TaskCard";
import type { Task } from "../types/task.types";

const taskFixture: Task = {
  publicId: "task-1",
  title: "Finaliser le rapport Q3",
  slug: "finaliser-le-rapport-q3",
  description: "Inclure les graphiques de ventes",
  status: "IN_PROGRESS",
  priority: "HIGH",
  dueDate: "2026-09-30T17:00:00Z",
  completedAt: null,
  archived: false,
  archivedAt: null,
  category: {
    publicId: "cat-1",
    name: "Work",
    slug: "work",
    color: "#4f46e5",
  },
  createdAt: "2026-09-01T10:00:00Z",
  updatedAt: "2026-09-02T10:00:00Z",
};

describe("TaskCard", () => {
  it("affiche le titre, le statut, la priorité et la catégorie", () => {
    renderWithProviders(<TaskCard task={taskFixture} onEdit={vi.fn()} />);

    expect(screen.getByText("Finaliser le rapport Q3")).toBeInTheDocument();
    expect(screen.getByText("In Progress")).toBeInTheDocument();
    expect(screen.getByText("High")).toBeInTheDocument();
    expect(screen.getByText("Work")).toBeInTheDocument();
  });

  it("affiche le bouton archive pour une tâche non archivée", () => {
    renderWithProviders(<TaskCard task={taskFixture} onEdit={vi.fn()} />);
    expect(
      screen.getByLabelText(/archive finaliser le rapport q3/i),
    ).toBeInTheDocument();
    expect(screen.queryByLabelText(/restore/i)).not.toBeInTheDocument();
  });

  it("affiche le bouton restore pour une tâche archivée", () => {
    const archivedTask = { ...taskFixture, archived: true };
    renderWithProviders(<TaskCard task={archivedTask} onEdit={vi.fn()} />);
    expect(
      screen.getByLabelText(/restore finaliser le rapport q3/i),
    ).toBeInTheDocument();
  });

  it("appelle onEdit avec la tâche au clic sur le crayon", async () => {
    const onEdit = vi.fn();
    const user = userEvent.setup();

    renderWithProviders(<TaskCard task={taskFixture} onEdit={onEdit} />);
    await user.click(screen.getByLabelText(/edit finaliser le rapport q3/i));

    expect(onEdit).toHaveBeenCalledWith(taskFixture);
  });
});
