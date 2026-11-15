import { useMutation, useQuery } from '@tanstack/react-query'
import { Check, LockKeyhole, ShieldCheck } from 'lucide-react'
import { Link } from 'react-router'
import { ErrorState, Loading, PageHeading } from '@/components/common'
import { GithubIcon } from '@/components/github-icon'
import { Button } from '@/components/ui/button'
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/components/ui/card'
import { api, messageFor, usePublicConfig } from '@/lib/api'
import { displayDate } from '@/lib/presentation'
import type { Identity, List } from '@/lib/types'

export function SecurityPage() {
  const config = usePublicConfig()
  const identities = useQuery({
    queryKey: ['identities'],
    queryFn: async () =>
      (await api.get<List<Identity>>('/accounts/me/authorizations')).data.items,
  })
  const bind = useMutation({
    mutationFn: async () => {
      const { authorization_url } = (
        await api.post<{ authorization_url: string }>(
          '/accounts/me/authorizations/github',
        )
      ).data
      const url = new URL(authorization_url)
      if (url.origin !== 'https://github.com')
        throw new Error('Unexpected GitHub authorization URL')
      return url.href
    },
    onSuccess: (url) => window.location.assign(url),
  })
  const github = identities.data?.find((item) => item.provider === 'github')
  return (
    <div>
      <PageHeading
        eyebrow="Security"
        title="账号安全"
        description="管理你的登录方式，让每次登录都安心。"
      />
      <div className="grid items-start gap-6 lg:grid-cols-[minmax(0,1fr)_300px]">
        <Card className="rounded-2xl">
          <CardHeader>
            <CardTitle>登录方式</CardTitle>
            <CardDescription>选择适合你的方式登录 Nikukyu。</CardDescription>
          </CardHeader>
          <CardContent>
            <div className="flex items-start gap-4 py-3">
              <span className="flex size-11 shrink-0 items-center justify-center rounded-2xl bg-accent text-primary">
                <LockKeyhole className="size-5" />
              </span>
              <div className="min-w-0 flex-1">
                <p className="font-medium">账号与密码</p>
                <p className="mt-1 text-sm text-muted-foreground">
                  如果你为账号设置了密码，可使用用户名和密码登录。
                </p>
              </div>
            </div>
            <div className="my-4 h-px bg-border" />
            {identities.isPending ? (
              <Loading />
            ) : identities.isError ? (
              <ErrorState
                message={messageFor(identities.error)}
                retry={() => void identities.refetch()}
              />
            ) : (
              <div className="flex flex-wrap items-center gap-4 py-2">
                <span className="flex size-11 shrink-0 items-center justify-center rounded-2xl bg-muted">
                  <GithubIcon className="size-5" />
                </span>
                <div className="min-w-0 flex-1">
                  <p className="font-medium">GitHub</p>
                  <p className="mt-1 text-sm text-muted-foreground">
                    {github
                      ? `已绑定 · GitHub ID ${github.openid} · ${displayDate(github.created_at)}`
                      : config.data?.github_enabled
                        ? '绑定后可直接使用 GitHub 登录。'
                        : '当前未启用 GitHub 登录。'}
                  </p>
                </div>
                {github ? (
                  <span className="inline-flex items-center gap-1.5 rounded-full bg-[#e9f0e6] px-3 py-1.5 text-xs font-medium text-[#66835e] dark:bg-[#344638] dark:text-[#b1ceac]">
                    <Check className="size-3.5" />
                    已绑定
                  </span>
                ) : (
                  <Button
                    variant="outline"
                    disabled={!config.data?.github_enabled || bind.isPending}
                    onClick={() => bind.mutate()}
                  >
                    {bind.isPending ? '正在跳转…' : '绑定 GitHub'}
                  </Button>
                )}
              </div>
            )}
            {bind.isError && (
              <div
                role="alert"
                className="mt-4 rounded-xl bg-destructive/10 p-3 text-sm text-destructive"
              >
                <p>{messageFor(bind.error)}</p>
                <Link
                  className="mt-2 inline-block font-medium underline"
                  to="/login?next=%2Fsettings%2Fsecurity"
                >
                  重新登录
                </Link>
              </div>
            )}
          </CardContent>
        </Card>
        <Card className="rounded-2xl bg-[#edf2e9] dark:bg-[#2c392d]">
          <CardContent>
            <ShieldCheck className="mb-4 size-6 text-[#718a6b]" />
            <h3 className="font-semibold">保护好你的账号</h3>
            <p className="mt-2 text-sm leading-6 text-muted-foreground">
              绑定登录方式前，系统会要求你在最近五分钟内登录。
            </p>
          </CardContent>
        </Card>
      </div>
    </div>
  )
}
