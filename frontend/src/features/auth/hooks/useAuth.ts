import { create } from 'zustand'
import { authService } from '../services/auth.service'
import type { LoginRequest, RegisterRequest, User } from '../types/auth.types'

interface AuthState {
  user: User | null
  isAuthenticated: boolean
  isLoading: boolean
  error: string | null

  // Actions
  login: (data: LoginRequest) => Promise<void>
  register: (data: RegisterRequest) => Promise<User>
  logout: () => Promise<void>
  fetchCurrentUser: () => Promise<void>
  setUser: (user: User | null) => void
  clearError: () => void
}

export const useAuth = create<AuthState>((set) => ({
  user: null,
  isAuthenticated: false,
  isLoading: false,
  error: null,

  login: async (data) => {
    set({ isLoading: true, error: null })
    try {
      const response = await authService.login(data)

      // Stocker les tokens dans localStorage
      localStorage.setItem('accessToken', response.accessToken)
      localStorage.setItem('refreshToken', response.refreshToken)

      // Récupérer le profil utilisateur
      const user = await authService.getCurrentUser()

      set({
        user,
        isAuthenticated: true,
        isLoading: false,
      })
    } catch (error: unknown) {
      const message = extractErrorMessage(error)
      set({ error: message, isLoading: false, isAuthenticated: false })
      throw error
    }
  },

  register: async (data) => {
    set({ isLoading: true, error: null })
    try {
      const user = await authService.register(data)
      set({ isLoading: false })
      return user
    } catch (error: unknown) {
      const message = extractErrorMessage(error)
      set({ error: message, isLoading: false })
      throw error
    }
  },

  logout: async () => {
    try {
      await authService.logout()
    } catch {
      // Ignorer les erreurs de logout (le token est peut-être déjà expiré)
    } finally {
      localStorage.removeItem('accessToken')
      localStorage.removeItem('refreshToken')
      set({ user: null, isAuthenticated: false })
    }
  },

  fetchCurrentUser: async () => {
    const token = localStorage.getItem('accessToken')
    if (!token) {
      set({ isAuthenticated: false, user: null })
      return
    }

    set({ isLoading: true })
    try {
      const user = await authService.getCurrentUser()
      set({ user, isAuthenticated: true, isLoading: false })
    } catch {
      localStorage.removeItem('accessToken')
      localStorage.removeItem('refreshToken')
      set({ user: null, isAuthenticated: false, isLoading: false })
    }
  },

  setUser: (user) => set({ user, isAuthenticated: !!user }),

  clearError: () => set({ error: null }),
}))

// Helper pour extraire le message d'erreur du format RFC 7807
function extractErrorMessage(error: unknown): string {
  if (error && typeof error === 'object' && 'response' in error) {
    const axiosError = error as {
      response?: {
        data?: {
          detail?: string
          title?: string
          message?: string
          errors?: Array<{ message: string }>
        }
      }
    }

    const data = axiosError.response?.data

    if (data?.errors && data.errors.length > 0) {
      return data.errors.map((e) => e.message).join(', ')
    }

    return data?.detail || data?.title || data?.message || 'An error occurred'
  }

  return 'An unexpected error occurred'
}