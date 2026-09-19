# ADR-001 — Choix du tech stack full-stack

- **Statut** : Accepté
- **Date** : 2026-09
- **Décideurs** : équipe projet

## Contexte

Le produit cible trois surfaces : une API centrale, un client web et un client mobile
Android, avec des exigences de sécurité (multi-sessions, tokens) et de qualité
(tests, CI) élevées pour un projet portfolio.

## Décision

- **Backend** : Spring Boot 3.5 / Java 21, Spring Data JPA + MySQL, Flyway, Spring Security.
- **Web** : React 18 + TypeScript + Vite, Tailwind v4, shadcn/ui, TanStack Query, Zustand.
- **Mobile** : Flutter (Android), Riverpod 3, GoRouter, Dio.
- **Infra** : Docker Compose local, GitHub Actions, déploiement GCP free tier prévu.

## Alternatives considérées

| Option | Écartée car |
|---|---|
| Node/NestJS backend | Moins de valeur démonstrative sur le volet sécurité/transactions |
| Next.js (SSR) | SEO inutile pour un dashboard authentifié ; complexité d'hydratation |
| React Native | Flutter offre une parité web/mobile plus simple à maintenir seul |
| Redux (web) | Overkill ; TanStack Query + Zustand couvrent server/client state |
| BLoC (mobile) | Riverpod 3 plus ergonomique pour une équipe d'une personne |

## Conséquences

- ✅ Un seul contrat d'API (OpenAPI) consommé par deux clients
- ✅ Tests réalistes côté backend (Testcontainers = vrai MySQL)
- ⚠️ Trois écosystèmes à maintenir → CI séparée par couche pour contenir les coûts
- ⚠️ Design system dupliqué web/mobile → tokens de couleurs partagés manuellement
