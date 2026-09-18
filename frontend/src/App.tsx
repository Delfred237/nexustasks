import { useEffect } from "react";
import { RouterProvider } from "react-router-dom";
import { Toaster } from "react-hot-toast";
import { QueryProvider } from "@/app/providers/QueryProvider";
import { ThemeProvider } from "@/app/providers/ThemeProvider";
import { useAuth } from "@/features/auth/hooks/useAuth";
import { router } from "@/app/router";

function App() {
  const { fetchCurrentUser } = useAuth();

  useEffect(() => {
    // Vérifier au démarrage si l'utilisateur a une session active
    fetchCurrentUser();
  }, [fetchCurrentUser]);

  return (
    <ThemeProvider defaultTheme="system" storageKey="nexustasks-theme">
      <QueryProvider>
        <RouterProvider router={router} />
        <Toaster
          position="top-right"
          toastOptions={{
            duration: 4000,
          }}
        />
      </QueryProvider>
    </ThemeProvider>
  );
}

export default App;
