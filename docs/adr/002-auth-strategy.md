# ADR-002 — Stratégie d'authentification : JWT courts + refresh rotatifs

- **Statut** : Accepté
- **Date** : 2026-09

## Contexte

Deux clients aux contraintes opposées : le web (XSS → tokens volables en localStorage)
et le mobile (pas de cookies partagés, besoin de Bearer). Il faut aussi gérer
le multi-sessions et la révocation.

## Décision

- **Access token JWT 15 min** (stateless, vérifié sans DB).
- **Refresh token opaque 7 j** stocké en base, **rotation à chaque usage** :
  l'ancien est marqué `replacedBy` ; **rejouer un token rotaté = vol détecté →
  révocation de toutes les sessions de l'utilisateur**.
- **Web** : les deux tokens en **cookies HttpOnly + SameSite** (insensibles au XSS).
- **Mobile** : tokens en **SecureStorage** (Keystore), envoyés en header Bearer.
- **Inscription** : compte désactivé tant que l'email n'est pas vérifié par **OTP 6 chiffres**
  (hash SHA-256 en base, expiration 15 min, max 5 tentatives, cooldown de renvoi).

## Alternatives considérées

| Option | Écartée car |
|---|---|
| Sessions serveur classiques | Stateful, moins adapté à un client mobile natif |
| JWT longue durée sans refresh | Révocation impossible avant expiration |
| localStorage seul (web) | Exposés au XSS |
| OAuth2 complet (Keycloak…) | Surdimensionné pour le périmètre |

## Conséquences

- ✅ Détection de vol de session sans infrastructure supplémentaire
- ✅ Logout = suppression des refresh tokens en base (révocation immédiate)
- ⚠️ Le refresh nécessite un endpoint stateful (DB) : assumé, c'est le point de contrôle
- ⚠️ Deux modes de transport (cookie / Bearer) → intercepteurs dédiés par client
