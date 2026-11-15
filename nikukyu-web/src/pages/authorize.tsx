import { useMutation, useQuery } from '@tanstack/react-query'
import {
  ArrowLeftRight,
  ArrowRight,
  ExternalLink,
  ShieldCheck,
} from 'lucide-react'
import { Link, Navigate } from 'react-router'
import {
  ApplicationAvatar,
  Brand,
  ErrorState,
  Loading,
  PermissionList,
  PersonAvatar,
  ThemeToggle,
} from '@/components/common'
import { Button } from '@/components/ui/button'
import { Card, CardContent } from '@/components/ui/card'
import { api, messageFor, useSession } from '@/lib/api'
import {
  authorizationTicket,
  clearAuthorizationTicket,
} from '@/lib/authorization'
import { safeUrl } from '@/lib/presentation'
import type { AuthorizationContext, PublicClient } from '@/lib/types'

export function AuthorizePage() {
  const ticket = authorizationTicket()
  const session = useSession()
  const context = useQuery({
    queryKey: ['authorization-context', ticket],
    queryFn: async () =>
      (
        await api.post<AuthorizationContext>('/oidc/authorization-contexts', {
          request_token: ticket,
        })
      ).data,
    enabled: !!ticket,
    retry: false,
  })
  const client = useQuery({
    queryKey: ['public-client', context.data?.client_id],
    queryFn: async () =>
      (await api.get<PublicClient>(`/oauth-clients/${context.data!.client_id}`))
        .data,
    enabled: !!context.data?.client_id,
    retry: false,
  })
  const decision = useMutation({
    mutationFn: async (choice: 'allow' | 'deny') =>
      (
        await api.post<{ redirect_uri: string }>('/oidc/authorizations', {
          request_token: ticket,
          decision: choice,
        })
      ).data,
    onSuccess: ({ redirect_uri }) => {
      clearAuthorizationTicket()
      window.location.replace(redirect_uri)
    },
  })

  let content: React.ReactNode
  if (!ticket) {
    content = (
      <ErrorState message="授权请求不存在，或浏览器未能保存请求。请返回原应用重新发起授权，并允许此网站使用会话存储。" />
    )
  } else if (context.isError) {
    content = (
      <ErrorState
        message={`无法继续授权：${messageFor(context.error)}。请返回原应用重新发起授权。`}
      />
    )
  } else if (context.isPending || context.isFetching || session.isPending) {
    content = <Loading label="正在获取授权信息…" />
  } else if (session.isError) {
    content = (
      <ErrorState
        message={messageFor(session.error)}
        retry={() => void session.refetch()}
      />
    )
  } else if (context.data.login_required || !session.data) {
    return <Navigate to="/login?next=%2Fauthorize" replace />
  } else if (client.isPending) {
    content = <Loading label="正在获取应用信息…" />
  } else if (client.isError) {
    content = (
      <ErrorState
        message={messageFor(client.error)}
        retry={() => void client.refetch()}
      />
    )
  } else {
    const app = client.data
    const name = session.data.account.nickname || session.data.account.username
    content = (
      <Card className="gap-0 rounded-3xl py-0 shadow-sm">
        <CardContent className="p-6 sm:p-8">
          <div className="flex items-center gap-4">
            <ApplicationAvatar
              name={app.name}
              image={app.avatar}
              className="size-16"
            />
            <div className="flex size-8 items-center justify-center rounded-full bg-muted text-muted-foreground">
              <ArrowRight className="size-4" />
            </div>
            <PersonAvatar
              name={name}
              image={session.data.account.avatar}
              className="size-14"
            />
          </div>
          <p className="mt-7 text-xs font-semibold tracking-[0.16em] text-primary uppercase">
            Authorization request
          </p>
          <h1 className="mt-2 text-2xl leading-tight font-semibold tracking-tight sm:text-3xl">
            <span className="break-words">{app.name}</span> 想要连接你的账号
          </h1>
          <p className="mt-3 text-sm leading-6 text-muted-foreground">
            {app.description ||
              '此应用希望使用你的 Nikukyu 账号。请确认以下权限。'}
          </p>
          <div className="mt-6 flex flex-wrap items-center gap-3 rounded-xl border p-3">
            <PersonAvatar name={name} image={session.data.account.avatar} />
            <div className="min-w-0 flex-1">
              <p className="truncate text-sm font-medium">{name}</p>
              <p className="truncate text-xs text-muted-foreground">
                @{session.data.account.username}
              </p>
            </div>
            <Button variant="ghost" size="sm" asChild>
              <Link to="/login?next=%2Fauthorize">
                <ArrowLeftRight />
                切换账号
              </Link>
            </Button>
          </div>
          {context.data.select_account && (
            <p className="mt-3 text-xs text-muted-foreground">
              请确认要使用的账号，或选择切换账号。
            </p>
          )}
          <div className="mt-6 rounded-xl bg-muted/70 p-5">
            <p className="mb-4 text-sm font-semibold">允许此应用：</p>
            <PermissionList scopes={context.data.scope} />
          </div>
          <p className="mt-5 flex items-start gap-2 text-xs leading-5 text-muted-foreground">
            <ShieldCheck className="mt-0.5 size-4 shrink-0" />
            授权后，你可以随时在账号中心查看和撤销应用权限。
          </p>
          {decision.isError && (
            <div
              role="alert"
              className="mt-4 rounded-xl bg-destructive/10 p-3 text-sm text-destructive"
            >
              {messageFor(decision.error)}
            </div>
          )}
          <div className="mt-6 flex flex-col-reverse gap-3 sm:flex-row">
            <Button
              variant="outline"
              className="h-11 flex-1"
              disabled={decision.isPending}
              onClick={() => decision.mutate('deny')}
            >
              拒绝
            </Button>
            <Button
              className="h-11 flex-1"
              disabled={decision.isPending}
              onClick={() => decision.mutate('allow')}
            >
              {decision.isPending ? '正在处理…' : '允许并继续'}
              <ArrowRight />
            </Button>
          </div>
          {(safeUrl(app.privacy_policy) || safeUrl(app.terms_of_service)) && (
            <div className="mt-5 flex flex-wrap justify-center gap-4 text-xs text-muted-foreground">
              {safeUrl(app.privacy_policy) && (
                <a
                  className="inline-flex items-center gap-1 hover:text-primary"
                  href={safeUrl(app.privacy_policy)}
                  target="_blank"
                  rel="noopener noreferrer"
                >
                  隐私政策 <ExternalLink className="size-3" />
                </a>
              )}
              {safeUrl(app.terms_of_service) && (
                <a
                  className="inline-flex items-center gap-1 hover:text-primary"
                  href={safeUrl(app.terms_of_service)}
                  target="_blank"
                  rel="noopener noreferrer"
                >
                  服务条款 <ExternalLink className="size-3" />
                </a>
              )}
            </div>
          )}
        </CardContent>
      </Card>
    )
  }
  return (
    <div className="min-h-screen bg-background">
      <header className="mx-auto flex max-w-5xl items-center justify-between px-6 py-7">
        <Brand />
        <ThemeToggle />
      </header>
      <main className="mx-auto w-full max-w-xl px-5 py-10 sm:py-16">
        {content}
      </main>
    </div>
  )
}
