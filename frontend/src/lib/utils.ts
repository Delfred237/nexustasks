export { cn } from "cn"

const API_BASE = import.meta.env.VITE_API_URL || '/api'

/**
 * Normalise une URL d'avatar.
 * Gère trois cas :
 * - URL absolue (http...) → telle quelle
 * - Chemin public déjà complet (/api/files/...) → tel quel
 * - Chemin de stockage brut (avatars/x.png) → préfixé par l'API
 */
export function resolveAvatarUrl(avatarUrl?: string | null): string | null {
  if (!avatarUrl) return null
  if (avatarUrl.startsWith('http')) return avatarUrl
  if (avatarUrl.startsWith('/')) return avatarUrl
  return `${API_BASE}/files/${avatarUrl}`
}