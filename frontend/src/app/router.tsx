/* eslint-disable react-refresh/only-export-components */
import { createBrowserRouter, Navigate } from 'react-router-dom'
import { lazy, Suspense } from 'react'

// Lazy load pour code splitting
const LandingPage = lazy(() => import('@/features/landing/pages/LandingPage'))
const LoginPage = lazy(() => import('@/features/auth/pages/LoginPage'))
const RegisterPage = lazy(() => import('@/features/auth/pages/RegisterPage'))
const VerifyEmailPage = lazy(() => import('@/features/auth/pages/VerifyEmailPage'))
const DashboardPage = lazy(() => import('@/features/dashboard/pages/DashboardPage'))
const TasksPage = lazy(() => import('@/features/tasks/pages/TasksPage'))
const CategoriesPage = lazy(() => import('@/features/categories/pages/CategoriesPage'))
const NotificationsPage = lazy(() => import('@/features/notifications/pages/NotificationsPage'))
const ProfilePage = lazy(() => import('@/features/profile/pages/ProfilePage'))

function LoadingFallback() {
  return (
    <div className="flex h-screen items-center justify-center">
      <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary" />
    </div>
  )
}

export const router = createBrowserRouter([
  {
    path: '/',
    element: <Suspense fallback={<LoadingFallback />}><LandingPage /></Suspense>,
  },
  {
    path: '/login',
    element: <Suspense fallback={<LoadingFallback />}><LoginPage /></Suspense>,
  },
  {
    path: '/register',
    element: <Suspense fallback={<LoadingFallback />}><RegisterPage /></Suspense>,
  },
  {
    path: '/verify-email',
    element: <Suspense fallback={<LoadingFallback />}><VerifyEmailPage /></Suspense>,
  },
  {
    path: '/app',
    children: [
      { index: true, element: <Navigate to="/app/dashboard" replace /> },
      {
        path: 'dashboard',
        element: <Suspense fallback={<LoadingFallback />}><DashboardPage /></Suspense>,
      },
      {
        path: 'tasks',
        element: <Suspense fallback={<LoadingFallback />}><TasksPage /></Suspense>,
      },
      {
        path: 'categories',
        element: <Suspense fallback={<LoadingFallback />}><CategoriesPage /></Suspense>,
      },
      {
        path: 'notifications',
        element: <Suspense fallback={<LoadingFallback />}><NotificationsPage /></Suspense>,
      },
      {
        path: 'profile',
        element: <Suspense fallback={<LoadingFallback />}><ProfilePage /></Suspense>,
      },
    ],
  },
  {
    path: '*',
    element: (
      <div className="flex h-screen items-center justify-center text-muted-foreground">
        404 — Page not found
      </div>
    ),
  },
])