import { Outlet } from "react-router-dom";
import { Sidebar } from "@/features/dashboard/components/Sidebar";
import { Header } from "@/features/dashboard/components/Header";

export default function AuthenticatedLayout() {
  return (
    <div className="min-h-screen bg-background">
      <Sidebar />

      <div className="lg:pl-64">
        <Header />

        <main id="main-content" className="p-4 sm:p-6" tabIndex={-1}>
          <Outlet />
        </main>
      </div>
    </div>
  );
}
