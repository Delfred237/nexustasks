# NexusTasks — Frontend Web

SPA React 18 + TypeScript + Vite + Tailwind CSS v4 + shadcn/ui.

## Prérequis

- Node 22+

## Démarrage

```bash
npm install
npm run dev          # http://localhost:5173 (proxy /api → localhost:8080)
```

Le proxy Vite rend l'API same-origin en dev : aucun problème CORS.

## Commandes

| Commande | Rôle |
|---|---|
| `npm run dev` | Dev server HMR |
| `npm run build` | Type-check + build production (`dist/`) |
| `npm test` | Tests Vitest (run unique) |
| `npm run test:watch` | Tests en mode watch |
| `npm run test:coverage` | Tests + rapport de couverture |

## Structure

```
src/
├── app/          # providers (Query, Theme, guards), router, layouts
├── components/   # UI réutilisable (shadcn/ui) + ErrorBoundary, Avatar…
├── features/     # auth, tasks, categories, notifications, profile, landing, dashboard
├── hooks/        # useDebounce, useMediaQuery, useSidebar
├── lib/          # client Axios (intercepteurs auth/refresh), endpoints, utils
└── types/        # types partagés (Page<T>, ApiError RFC 7807)
```

## État serveur vs état client

- **TanStack Query** : données API (cache, invalidation, polling notifications)
- **Zustand** : session utilisateur, thème, sidebar

## Docker

```bash
docker build -t nexustasks-frontend .
docker run -p 5173:8080 nexustasks-frontend
```

Image nginx **non-root** : SPA fallback + proxy `/api` vers le service backend
(same-origin, zéro CORS en production conteneurisée).
