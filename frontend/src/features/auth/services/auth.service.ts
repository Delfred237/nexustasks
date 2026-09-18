import { apiClient } from '@/lib/api/client'
import { ENDPOINTS } from '@/lib/api/endpoints'
import type {
  AuthResponse,
  LoginRequest,
  RegisterRequest,
  ResendVerificationRequest,
  User,
  VerifyEmailRequest,
} from '../types/auth.types'

export const authService = {
  async register(data: RegisterRequest): Promise<User> {
    const response = await apiClient.post<User>(ENDPOINTS.auth.register, data)
    return response.data
  },

  async verifyEmail(data: VerifyEmailRequest): Promise<void> {
    await apiClient.post(ENDPOINTS.auth.verifyEmail, data)
  },

  async resendVerification(data: ResendVerificationRequest): Promise<void> {
    await apiClient.post(ENDPOINTS.auth.resendVerification, data)
  },

  async login(data: LoginRequest): Promise<AuthResponse> {
    const response = await apiClient.post<AuthResponse>(ENDPOINTS.auth.login, data)
    return response.data
  },

  async logout(): Promise<void> {
    await apiClient.post(ENDPOINTS.auth.logout)
  },

  async getCurrentUser(): Promise<User> {
    const response = await apiClient.get<User>(ENDPOINTS.users.me)
    return response.data
  },
}