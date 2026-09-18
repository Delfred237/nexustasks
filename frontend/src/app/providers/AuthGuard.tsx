import { useEffect } from "react";
import { Navigate, Outlet, useLocation } from "react-router-dom";
import { useAuth } from "@/features/auth/hooks/useAuth";

export function AuthGuard() {
  const { isAuthenticated, isLoading, fetchCurrentUser } = useAuth();
  const location = useLocation();

  useEffect(() => {
    // Au premier chargement, vérifier si l'utilisateur a un token valide
    fetchCurrentUser();
  }, [fetchCurrentUser]);

  if (isLoading) {
    return (
      <div className="flex h-screen items-center justify-center">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary" />
      </div>
    );
  }

  if (!isAuthenticated) {
    // Rediriger vers login en gardant l'URL de redirection
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  return <Outlet />;
}
