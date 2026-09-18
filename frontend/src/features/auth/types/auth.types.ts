export interface User {
  publicId: string
  firstName: string
  lastName: string
  email: string
  role: 'USER' | 'ADMIN'
  emailVerified: boolean
  avatarUrl: string | null
}

export interface AuthResponse {
  accessToken: string
  refreshToken: string
  publicId: string
  role: string
}

export interface LoginRequest {
  email: string
  password: string
}

export interface RegisterRequest {
  firstName: string
  lastName: string
  email: string
  password: string
}

export interface VerifyEmailRequest {
  email: string
  code: string
}

export interface ResendVerificationRequest {
  email: string
}