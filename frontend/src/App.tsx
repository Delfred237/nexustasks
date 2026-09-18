import { RouterProvider } from 'react-router-dom'
import { Toaster } from 'react-hot-toast'
import { QueryProvider } from '@/app/providers/QueryProvider'
import { ThemeProvider } from '@/app/providers/ThemeProvider'
import { router } from '@/app/router'

function App() {
  return (
    <ThemeProvider defaultTheme="system" storageKey="nexustasks-theme">
      <QueryProvider>
        <RouterProvider router={router} />
        <Toaster
          position="top-right"
          toastOptions={{
            duration: 4000,
          }}
        />
      </QueryProvider>
    </ThemeProvider>
  )
}

export default App