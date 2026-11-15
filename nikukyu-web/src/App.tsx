import { useEffect } from 'react'
import { QueryClientProvider } from '@tanstack/react-query'
import { BrowserRouter, Link, Route, Routes } from 'react-router'
import { Toaster } from 'sonner'
import { Brand } from '@/components/common'
import { ProtectedLayout } from '@/components/layout'
import { Button } from '@/components/ui/button'
import { queryClient } from '@/lib/api'
import { useTheme } from '@/lib/theme'
import { AuthPage } from '@/pages/auth'
import { AuthorizePage } from '@/pages/authorize'
import { GrantsPage } from '@/pages/grants'
import { HomePage } from '@/pages/home'
import { ProfilePage } from '@/pages/profile'
import { SecurityPage } from '@/pages/security'

function AppRoutes() {
  const theme = useTheme((state) => state.theme)
  useEffect(() => {
    document.documentElement.classList.toggle('dark', theme === 'dark')
  }, [theme])
  return (
    <>
      <Routes>
        <Route path="/login" element={<AuthPage />} />
        <Route path="/register" element={<AuthPage />} />
        <Route path="/authorize" element={<AuthorizePage />} />
        <Route element={<ProtectedLayout />}>
          <Route index element={<HomePage />} />
          <Route path="/settings/profile" element={<ProfilePage />} />
          <Route path="/settings/security" element={<SecurityPage />} />
          <Route path="/settings/authorizations" element={<GrantsPage />} />
        </Route>
        <Route
          path="*"
          element={
            <div className="mx-auto max-w-lg px-6 py-16">
              <Brand />
              <p className="mt-16 text-sm font-semibold text-primary">404</p>
              <h1 className="mt-3 text-3xl font-semibold">这里没有找到页面</h1>
              <p className="mt-3 text-muted-foreground">
                这个地址可能已经不存在。
              </p>
              <Button className="mt-6" asChild>
                <Link to="/">返回首页</Link>
              </Button>
            </div>
          }
        />
      </Routes>
      <Toaster theme={theme} richColors position="top-right" />
    </>
  )
}

export default function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <BrowserRouter>
        <AppRoutes />
      </BrowserRouter>
    </QueryClientProvider>
  )
}
