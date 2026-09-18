import { render } from "@testing-library/react";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { MemoryRouter } from "react-router-dom";
import type { ReactElement } from "react";

/**
 * QueryClient de test : pas de retry, pas de refetch automatique.
 * Évite les requêtes réseau parasites et les timeouts en test.
 */
export function createTestQueryClient() {
  return new QueryClient({
    defaultOptions: {
      queries: { retry: false, gcTime: Infinity },
      mutations: { retry: false },
    },
  });
}

interface RenderWithProvidersOptions {
  route?: string;
}

/**
 * Render un composant avec tous les providers nécessaires
 * (QueryClient + Router), comme dans l'application réelle.
 */
export function renderWithProviders(
  ui: ReactElement,
  { route = "/" }: RenderWithProvidersOptions = {},
) {
  const queryClient = createTestQueryClient();

  return render(
    <QueryClientProvider client={queryClient}>
      <MemoryRouter initialEntries={[route]}>{ui}</MemoryRouter>
    </QueryClientProvider>,
  );
}
