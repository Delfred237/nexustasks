import { Outlet } from "react-router-dom";
import { Sidebar } from "@/features/dashboard/components/Sidebar";
import { Header } from "@/features/dashboard/components/Header";

export default function AuthenticatedLayout() {
  return (
    <div className="min-h-screen bg-background">
      <Sidebar />

      {/* Content area - décalé à droite pour laisser la place à la sidebar sur desktop */}
      <div className="lg:pl-64">
        <Header />

        <main className="p-4 sm:p-6">
          <Outlet />
        </main>
      </div>
    </div>
  );
}
