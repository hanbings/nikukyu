import { useMutation } from '@tanstack/react-query'
import {
  KeyRound,
  LayoutGrid,
  LogOut,
  ShieldCheck,
  UserRound,
  ChevronRight,
} from 'lucide-react'
import { Link, Navigate, NavLink, Outlet, useNavigate } from 'react-router'
import { toast } from 'sonner'
import {
  Brand,
  ErrorState,
  Loading,
  PersonAvatar,
  ThemeToggle,
} from '@/components/common'
import { Button } from '@/components/ui/button'
import {
  api,
  clearUserData,
  messageFor,
  queryClient,
  useSession,
} from '@/lib/api'
import { cn } from '@/lib/utils'

const navigation = [
  { to: '/', label: '概览', icon: LayoutGrid, end: true },
  { to: '/settings/profile', label: '个人资料', icon: UserRound },
  { to: '/settings/security', label: '账号安全', icon: ShieldCheck },
  { to: '/settings/authorizations', label: '已授权应用', icon: KeyRound },
]

export function ProtectedLayout() {
  const session = useSession()
  const navigate = useNavigate()
  const logout = useMutation({
    mutationFn: () => api.delete('/sessions/current'),
    onSuccess: async () => {
      await queryClient.cancelQueries()
      clearUserData()
      navigate('/login', { replace: true })
    },
    onError: (error) => toast.error(messageFor(error)),
  })

  if (session.isPending) return <Loading label="正在确认登录状态…" />
  if (session.isError)
    return (
      <div className="mx-auto max-w-lg p-8">
        <ErrorState
          message={messageFor(session.error)}
          retry={() => void session.refetch()}
        />
      </div>
    )
  if (!session.data) return <Navigate to="/login" replace />
  const account = session.data.account
  const name = account.nickname || account.username

  return (
    <div className="min-h-screen lg:flex">
      <aside className="border-b bg-card lg:fixed lg:inset-y-0 lg:flex lg:w-64 lg:flex-col lg:border-r lg:border-b-0">
        <div className="flex items-center justify-between px-5 py-5 lg:px-7 lg:pt-8">
          <Brand />
          <div className="lg:hidden">
            <ThemeToggle />
          </div>
        </div>
        <div className="px-3 pb-3 lg:px-4 lg:pt-10">
          <p className="hidden px-3 pb-3 text-[10px] font-bold tracking-[0.14em] text-muted-foreground uppercase lg:block">
            你的空间
          </p>
          <nav
            aria-label="账号导航"
            className="grid grid-cols-4 gap-1 lg:flex lg:flex-col"
          >
            {navigation.map(({ to, label, icon: Icon, end }) => (
              <NavLink
                key={to}
                to={to}
                end={end}
                className={({ isActive }) =>
                  cn(
                    'flex min-w-0 flex-col items-center justify-center gap-1 rounded-xl px-1 py-2 text-[11px] font-medium transition-colors sm:flex-row sm:gap-2 sm:px-3.5 sm:py-3 sm:text-sm lg:justify-start lg:gap-3',
                    isActive
                      ? 'bg-accent text-accent-foreground'
                      : 'text-muted-foreground hover:bg-muted hover:text-foreground',
                  )
                }
              >
                <Icon className="size-4 shrink-0 lg:size-[18px]" />
                {label}
              </NavLink>
            ))}
          </nav>
        </div>
        <div className="mt-auto hidden px-6 pb-7 lg:block">
          <div className="rounded-2xl border bg-background/70 p-4">
            <p className="text-sm font-medium">好好保护你的账号</p>
            <p className="mt-1 text-xs leading-5 text-muted-foreground">
              定期检查你的授权，安心使用每个应用。
            </p>
          </div>
        </div>
      </aside>
      <div className="min-w-0 flex-1 lg:ml-64">
        <header className="hidden h-20 items-center justify-between border-b bg-card/75 px-8 lg:flex xl:px-12">
          <div className="text-sm text-muted-foreground">
            账号中心 <span className="mx-2 text-border">/</span>{' '}
            <span className="text-foreground">个人空间</span>
          </div>
          <div className="flex items-center gap-4">
            <ThemeToggle />
            <div className="h-6 w-px bg-border" />
            <Link to="/settings/profile" className="flex items-center gap-2.5">
              <PersonAvatar
                name={name}
                image={account.avatar}
                className="size-9"
              />
              <span className="max-w-32 truncate text-sm font-medium">
                {name}
              </span>
              <ChevronRight className="size-4 text-muted-foreground" />
            </Link>
            <Button
              variant="ghost"
              size="icon"
              title="退出登录"
              aria-label="退出登录"
              disabled={logout.isPending}
              onClick={() => logout.mutate()}
            >
              <LogOut />
            </Button>
          </div>
        </header>
        <main className="mx-auto w-full max-w-6xl px-5 py-8 sm:px-8 sm:py-12 xl:px-12">
          <Outlet />
        </main>
        <div className="px-5 pb-8 lg:hidden">
          <Button
            variant="ghost"
            className="text-muted-foreground"
            disabled={logout.isPending}
            onClick={() => logout.mutate()}
          >
            <LogOut />
            退出登录
          </Button>
        </div>
      </div>
    </div>
  )
}
