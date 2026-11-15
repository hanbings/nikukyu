import { zodResolver } from '@hookform/resolvers/zod'
import { useMutation } from '@tanstack/react-query'
import {
  ArrowRight,
  LockKeyhole,
  PawPrint,
  ShieldCheck,
  Sparkles,
} from 'lucide-react'
import { useForm } from 'react-hook-form'
import { Link, useLocation, useNavigate, useSearchParams } from 'react-router'
import { toast } from 'sonner'
import { z } from 'zod'
import { Brand, ThemeToggle } from '@/components/common'
import { GithubIcon } from '@/components/github-icon'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import {
  api,
  messageFor,
  queryClient,
  sessionKey,
  usePublicConfig,
} from '@/lib/api'
import { nextPath } from '@/lib/authorization'
import type { Session } from '@/lib/types'

const loginSchema = z.object({
  username: z.string().min(1, '请输入用户名。'),
  password: z.string().min(1, '请输入密码。'),
  nickname: z.string().optional(),
})
const registrationSchema = z.object({
  username: z
    .string()
    .regex(
      /^[a-zA-Z0-9_-]{3,64}$/,
      '用户名需为 3–64 位字母、数字、下划线或连字符。',
    ),
  password: z
    .string()
    .refine(
      (value) =>
        Array.from(value).length >= 12 &&
        Array.from(value).length <= 128 &&
        new TextEncoder().encode(value).length <= 512,
      '密码需为 12–128 个字符。',
    ),
  nickname: z.string().max(128, '昵称不能超过 128 个字符。').optional(),
})
type FormValues = z.infer<typeof loginSchema>

export function AuthPage() {
  const isRegister = useLocation().pathname === '/register'
  const [search] = useSearchParams()
  const next = nextPath(search.get('next'))
  const suffix = next === '/' ? '' : `?next=${encodeURIComponent(next)}`
  const config = usePublicConfig()
  const navigate = useNavigate()
  const form = useForm<FormValues>({
    resolver: zodResolver(isRegister ? registrationSchema : loginSchema),
    defaultValues: { username: '', password: '', nickname: '' },
  })
  const submit = useMutation({
    mutationFn: async (values: FormValues) => {
      if (isRegister) {
        await api.post('/accounts', {
          username: values.username,
          password: values.password,
          nickname: values.nickname?.trim() || null,
        })
        return null
      }
      await queryClient.cancelQueries({ queryKey: sessionKey })
      return (
        await api.post<Session>('/sessions', {
          username: values.username,
          password: values.password,
        })
      ).data
    },
    onSuccess: async (session) => {
      if (!session) {
        toast.success('注册成功，请登录你的账号。')
        navigate(`/login${suffix}`, { replace: true })
        return
      }
      await queryClient.cancelQueries({ queryKey: sessionKey })
      queryClient.setQueryData(sessionKey, session)
      queryClient.removeQueries({ queryKey: ['authorization-context'] })
      navigate(next, { replace: true })
    },
  })
  const githubReturn = next === '/authorize' ? '/authorize' : '/'

  return (
    <div className="min-h-screen bg-background lg:grid lg:grid-cols-[minmax(0,0.95fr)_minmax(520px,1.05fr)]">
      <div className="relative hidden overflow-hidden bg-[#eae9df] p-12 dark:bg-[#252b25] lg:flex lg:flex-col">
        <Brand />
        <div className="relative z-10 my-auto max-w-lg py-20">
          <span className="mb-7 inline-flex items-center gap-2 rounded-full border border-[#cbd4c5] bg-white/60 px-3 py-1.5 text-xs font-medium text-[#60745d] dark:border-[#566354] dark:bg-[#344034] dark:text-[#c2d2bc]">
            <Sparkles className="size-3.5" />
            一个属于你的账号空间
          </span>
          <h1 className="text-5xl leading-[1.25] font-semibold tracking-tight">
            简单连接，
            <br />
            <span className="text-primary">安心做自己。</span>
          </h1>
          <p className="mt-6 max-w-sm text-base leading-8 text-muted-foreground">
            在这里管理你的身份、资料和应用授权，让每一次连接都由你决定。
          </p>
        </div>
        <div
          aria-hidden="true"
          className="absolute -right-24 bottom-[-100px] flex size-[420px] items-center justify-center rounded-full border border-white/50 bg-white/25 dark:border-white/5 dark:bg-white/5"
        >
          <div className="flex size-[300px] items-center justify-center rounded-full border border-white/60 bg-white/30 dark:border-white/5 dark:bg-white/5">
            <PawPrint
              className="size-28 rotate-[-15deg] text-[#c4cfbd] dark:text-[#4c5b4d]"
              strokeWidth={1.1}
            />
          </div>
        </div>
        <p className="relative z-10 text-xs tracking-wide text-muted-foreground">
          A LITTLE SPACE FOR YOUR DIGITAL SELF
        </p>
      </div>
      <div className="flex min-h-screen flex-col px-6 py-7 sm:px-10">
        <div className="flex justify-between lg:justify-end">
          <div className="lg:hidden">
            <Brand />
          </div>
          <ThemeToggle />
        </div>
        <div className="mx-auto flex w-full max-w-sm flex-1 flex-col justify-center py-12">
          <div className="mb-8">
            <div className="mb-5 flex size-12 items-center justify-center rounded-2xl bg-accent text-primary">
              <LockKeyhole className="size-5" />
            </div>
            <p className="mb-2 text-xs font-semibold tracking-[0.16em] text-primary uppercase">
              {isRegister ? 'Join Nikukyu' : 'Welcome back'}
            </p>
            <h2 className="text-3xl font-semibold tracking-tight">
              {isRegister ? '创建你的账号' : '欢迎回来'}
            </h2>
            <p className="mt-3 text-sm leading-6 text-muted-foreground">
              {isRegister
                ? '从这里开始，拥有自己的账号空间。'
                : '登录 Nikukyu，继续你的旅程。'}
            </p>
          </div>
          {isRegister && config.data && !config.data.registration_enabled ? (
            <div className="rounded-xl border bg-card p-4 text-sm text-muted-foreground">
              当前暂未开放注册。
            </div>
          ) : (
            <form
              className="space-y-5"
              onSubmit={form.handleSubmit((values) => submit.mutate(values))}
            >
              <div className="space-y-2">
                <Label htmlFor="username">用户名</Label>
                <Input
                  id="username"
                  className="h-11 bg-card"
                  placeholder="你的用户名"
                  autoComplete="username"
                  aria-invalid={!!form.formState.errors.username}
                  {...form.register('username')}
                />
                {form.formState.errors.username && (
                  <p className="text-xs text-destructive">
                    {form.formState.errors.username.message}
                  </p>
                )}
              </div>
              {isRegister && (
                <div className="space-y-2">
                  <Label htmlFor="nickname">
                    昵称{' '}
                    <span className="font-normal text-muted-foreground">
                      （可选）
                    </span>
                  </Label>
                  <Input
                    id="nickname"
                    className="h-11 bg-card"
                    placeholder="希望大家怎么称呼你"
                    autoComplete="nickname"
                    {...form.register('nickname')}
                  />
                  {form.formState.errors.nickname && (
                    <p className="text-xs text-destructive">
                      {form.formState.errors.nickname.message}
                    </p>
                  )}
                </div>
              )}
              <div className="space-y-2">
                <Label htmlFor="password">密码</Label>
                <Input
                  id="password"
                  className="h-11 bg-card"
                  type="password"
                  placeholder={isRegister ? '至少 12 个字符' : '输入你的密码'}
                  autoComplete={
                    isRegister ? 'new-password' : 'current-password'
                  }
                  aria-invalid={!!form.formState.errors.password}
                  {...form.register('password')}
                />
                {form.formState.errors.password && (
                  <p className="text-xs text-destructive">
                    {form.formState.errors.password.message}
                  </p>
                )}
              </div>
              {submit.isError && (
                <p
                  role="alert"
                  className="rounded-xl bg-destructive/10 p-3 text-sm text-destructive"
                >
                  {messageFor(submit.error)}
                </p>
              )}
              <Button
                className="h-11 w-full"
                disabled={
                  submit.isPending ||
                  (isRegister && !config.data?.registration_enabled)
                }
                type="submit"
              >
                {submit.isPending
                  ? '请稍候…'
                  : isRegister
                    ? '创建账号'
                    : '登录'}
                <ArrowRight />
              </Button>
            </form>
          )}
          {!isRegister && config.data?.github_enabled && (
            <>
              <div className="my-6 flex items-center gap-3 text-xs text-muted-foreground">
                <span className="h-px flex-1 bg-border" />
                或者
                <span className="h-px flex-1 bg-border" />
              </div>
              <Button variant="outline" className="h-11 w-full bg-card" asChild>
                <a
                  href={`/api/v1/sessions/github?return_to=${encodeURIComponent(githubReturn)}`}
                >
                  <GithubIcon className="size-4" />
                  使用 GitHub 登录
                </a>
              </Button>
            </>
          )}
          {config.isError && (
            <p className="mt-4 text-xs text-muted-foreground">
              无法获取登录方式，请刷新页面重试。
            </p>
          )}
          <p className="mt-8 text-center text-sm text-muted-foreground">
            {isRegister ? '已经有账号？' : '还没有账号？'}{' '}
            {!isRegister && !config.data?.registration_enabled ? (
              <span>注册暂不可用</span>
            ) : (
              <Link
                className="font-semibold text-primary hover:underline"
                to={`${isRegister ? '/login' : '/register'}${suffix}`}
              >
                {isRegister ? '登录' : '创建账号'}
              </Link>
            )}
          </p>
          <div className="mt-12 flex items-center justify-center gap-2 text-xs text-muted-foreground">
            <ShieldCheck className="size-4" />
            你的账号，由你掌控
          </div>
        </div>
      </div>
    </div>
  )
}
