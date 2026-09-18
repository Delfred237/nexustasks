import { useSyncExternalStore } from "react";

export function useMediaQuery(query: string): boolean {
  return useSyncExternalStore(
    // 1. Fonction d'abonnement aux changements
    (callback) => {
      const media = window.matchMedia(query);
      media.addEventListener("change", callback);
      return () => media.removeEventListener("change", callback);
    },
    // 2. Fonction pour récupérer la valeur côté client
    () => window.matchMedia(query).matches,
    // 3. Valeur par défaut pour le rendu côté serveur (SSR) - optionnel
    () => false,
  );
}

// Hooks pratiques
export function useIsMobile() {
  return useMediaQuery("(max-width: 767px)");
}

export function useIsTablet() {
  return useMediaQuery("(min-width: 768px) and (max-width: 1023px)");
}

export function useIsDesktop() {
  return useMediaQuery("(min-width: 1024px)");
}
