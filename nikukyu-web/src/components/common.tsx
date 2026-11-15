import { Link } from 'react-router'
import {
  AlertCircle,
  Check,
  LoaderCircle,
  Moon,
  PawPrint,
  Sun,
} from 'lucide-react'
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar'
import { Button } from '@/components/ui/button'
import { safeUrl, scopeDetails } from '@/lib/presentation'
import { useTheme } from '@/lib/theme'
import { cn } from '@/lib/utils'

export function Brand({ compact = false }: { compact?: boolean }) {
  return (
    <Link
      to="/"
      className="inline-flex items-center gap-2.5 font-bold tracking-[-0.04em] text-foreground"
      aria-label="Nikukyu 首页"
    >
      <span className="flex size-9 items-center justify-center rounded-xl bg-primary text-primary-foreground">
        <PawPrint className="size-5" strokeWidth={2.3} />
      </span>
      {!compact && (
        <span className="text-xl">
          nikukyu<span className="text-primary">.</span>
        </span>
      )}
    </Link>
  )
}

export function ThemeToggle() {
  const { theme, toggleTheme } = useTheme()
  return (
    <Button
      variant="ghost"
      size="icon"
      onClick={toggleTheme}
      aria-label={theme === 'light' ? '切换到深色模式' : '切换到浅色模式'}
    >
      {theme === 'light' ? <Moon /> : <Sun />}
    </Button>
  )
}

export function PersonAvatar({
  name,
  image,
  className,
}: {
  name: string
  image?: string | null
  className?: string
}) {
  return (
    <Avatar className={cn('size-10 border border-border', className)}>
      {safeUrl(image) && (
        <AvatarImage src={safeUrl(image)} alt="" referrerPolicy="no-referrer" />
      )}
      <AvatarFallback className="bg-[#e9eee5] font-semibold text-[#6a8064] dark:bg-[#354237] dark:text-[#c5d7c0]">
        {name.slice(0, 1).toUpperCase() || 'N'}
      </AvatarFallback>
    </Avatar>
  )
}

export function ApplicationAvatar({
  name,
  image,
  className,
}: {
  name: string
  image?: string | null
  className?: string
}) {
  return (
    <Avatar
      className={cn('size-12 rounded-2xl border border-border', className)}
    >
      {safeUrl(image) && (
        <AvatarImage
          className="object-cover"
          src={safeUrl(image)}
          alt=""
          referrerPolicy="no-referrer"
        />
      )}
      <AvatarFallback className="rounded-2xl bg-accent text-lg font-bold text-primary">
        {name.slice(0, 1).toUpperCase() || 'A'}
      </AvatarFallback>
    </Avatar>
  )
}

export function PageHeading({
  eyebrow,
  title,
  description,
}: {
  eyebrow: string
  title: string
  description: string
}) {
  return (
    <div className="mb-8">
      <p className="mb-2 text-xs font-semibold tracking-[0.16em] text-primary uppercase">
        {eyebrow}
      </p>
      <h1 className="text-3xl font-semibold tracking-tight sm:text-4xl">
        {title}
      </h1>
      <p className="mt-3 text-sm leading-6 text-muted-foreground sm:text-base">
        {description}
      </p>
    </div>
  )
}

export function PermissionList({
  scopes,
  compact = false,
}: {
  scopes: string[]
  compact?: boolean
}) {
  return (
    <ul className={cn('space-y-3', compact && 'space-y-2')}>
      {scopes.map((scope) => {
        const info = scopeDetails[scope] || {
          title: scope,
          description: '应用请求的其他权限。',
        }
        return (
          <li key={scope} className="flex items-start gap-3">
            <span className="mt-0.5 flex size-5 shrink-0 items-center justify-center rounded-full bg-[#e9f0e6] text-[#66835e] dark:bg-[#344638] dark:text-[#b1ceac]">
              <Check className="size-3" strokeWidth={2.5} />
            </span>
            <span>
              <span className="block text-sm font-medium">{info.title}</span>
              {!compact && (
                <span className="mt-0.5 block text-xs leading-5 text-muted-foreground">
                  {info.description}
                </span>
              )}
            </span>
          </li>
        )
      })}
    </ul>
  )
}

export function Loading({ label = '正在加载…' }: { label?: string }) {
  return (
    <div
      className="flex min-h-40 items-center justify-center gap-2 text-sm text-muted-foreground"
      role="status"
    >
      <LoaderCircle className="size-4 animate-spin" />
      {label}
    </div>
  )
}

export function ErrorState({
  message,
  retry,
}: {
  message: string
  retry?: () => void
}) {
  return (
    <div
      className="rounded-2xl border border-destructive/20 bg-card p-6 text-sm"
      role="alert"
    >
      <div className="flex gap-3">
        <AlertCircle className="size-5 shrink-0 text-destructive" />
        <div>
          <p className="font-medium">出了点问题</p>
          <p className="mt-1 leading-6 text-muted-foreground">{message}</p>
          {retry && (
            <Button
              variant="outline"
              size="sm"
              className="mt-4"
              onClick={retry}
            >
              重试
            </Button>
          )}
        </div>
      </div>
    </div>
  )
}
