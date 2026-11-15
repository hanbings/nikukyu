import { useMutation, useQuery } from '@tanstack/react-query'
import { CalendarDays, ExternalLink, KeyRound, ShieldCheck } from 'lucide-react'
import { toast } from 'sonner'
import {
  ApplicationAvatar,
  ErrorState,
  Loading,
  PageHeading,
  PermissionList,
} from '@/components/common'
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
  AlertDialogTrigger,
} from '@/components/ui/alert-dialog'
import { Badge } from '@/components/ui/badge'
import { Button } from '@/components/ui/button'
import { Card, CardContent } from '@/components/ui/card'
import { api, messageFor, queryClient } from '@/lib/api'
import { displayDate, safeUrl } from '@/lib/presentation'
import type { Grant, List } from '@/lib/types'

export function GrantsPage() {
  const grants = useQuery({
    queryKey: ['grants'],
    queryFn: async () =>
      (await api.get<List<Grant>>('/accounts/me/grants')).data.items,
  })
  const revoke = useMutation({
    mutationFn: (id: string) => api.delete(`/accounts/me/grants/${id}`),
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ['grants'] })
      toast.success('授权已撤销。')
    },
    onError: (error) => toast.error(messageFor(error)),
  })
  return (
    <div>
      <PageHeading
        eyebrow="Connected apps"
        title="已授权应用"
        description="查看哪些应用可以访问你的信息，也可以随时撤销授权。"
      />
      {grants.isPending ? (
        <Loading />
      ) : grants.isError ? (
        <ErrorState
          message={messageFor(grants.error)}
          retry={() => void grants.refetch()}
        />
      ) : grants.data.length === 0 ? (
        <div className="rounded-2xl border bg-card px-6 py-16 text-center">
          <div className="mx-auto flex size-14 items-center justify-center rounded-2xl bg-accent text-primary">
            <KeyRound className="size-6" />
          </div>
          <h2 className="mt-5 font-semibold">还没有授权应用</h2>
          <p className="mt-2 text-sm text-muted-foreground">
            当你授权应用使用账号时，它会出现在这里。
          </p>
        </div>
      ) : (
        <div className="grid gap-4">
          {grants.data.map((grant) => {
            const app = grant.application
            const name = app?.name || '未知应用'
            return (
              <Card className="gap-0 rounded-2xl" key={grant.id}>
                <CardContent>
                  <div className="flex flex-wrap items-start gap-4">
                    <ApplicationAvatar
                      name={name}
                      image={app?.avatar}
                      className="size-14"
                    />
                    <div className="min-w-0 flex-1">
                      <div className="flex flex-wrap items-center gap-2">
                        <h2 className="font-semibold">{name}</h2>
                        {app?.is_deleted && (
                          <Badge variant="secondary">应用已停用</Badge>
                        )}
                      </div>
                      <p className="mt-1 text-sm leading-6 text-muted-foreground">
                        {app?.description ||
                          (app
                            ? '此应用未提供描述。'
                            : `应用 ID：${grant.oauth_id}`)}
                      </p>
                      <p className="mt-2 flex items-center gap-1.5 text-xs text-muted-foreground">
                        <CalendarDays className="size-3.5" />
                        授权于 {displayDate(grant.created_at)}
                      </p>
                    </div>
                    <AlertDialog>
                      <AlertDialogTrigger asChild>
                        <Button
                          variant="outline"
                          size="sm"
                          disabled={revoke.isPending}
                        >
                          撤销授权
                        </Button>
                      </AlertDialogTrigger>
                      <AlertDialogContent>
                        <AlertDialogHeader>
                          <AlertDialogTitle>
                            撤销对 {name} 的授权？
                          </AlertDialogTitle>
                          <AlertDialogDescription>
                            撤销后，该应用将无法继续使用这项授权访问你的信息。你可以之后重新授权。
                          </AlertDialogDescription>
                        </AlertDialogHeader>
                        <AlertDialogFooter>
                          <AlertDialogCancel>取消</AlertDialogCancel>
                          <AlertDialogAction
                            variant="destructive"
                            onClick={() => revoke.mutate(grant.id)}
                          >
                            确认撤销
                          </AlertDialogAction>
                        </AlertDialogFooter>
                      </AlertDialogContent>
                    </AlertDialog>
                  </div>
                  <div className="mt-6 rounded-xl bg-muted/65 p-4">
                    <p className="mb-3 flex items-center gap-2 text-xs font-semibold text-muted-foreground">
                      <ShieldCheck className="size-4" />
                      已授予的权限
                    </p>
                    <PermissionList scopes={grant.scope} />
                  </div>
                  {(safeUrl(app?.privacy_policy) ||
                    safeUrl(app?.terms_of_service)) && (
                    <div className="mt-4 flex gap-4 text-xs text-muted-foreground">
                      {safeUrl(app?.privacy_policy) && (
                        <a
                          href={safeUrl(app?.privacy_policy)}
                          target="_blank"
                          rel="noopener noreferrer"
                          className="inline-flex items-center gap-1 hover:text-primary"
                        >
                          隐私政策 <ExternalLink className="size-3" />
                        </a>
                      )}
                      {safeUrl(app?.terms_of_service) && (
                        <a
                          href={safeUrl(app?.terms_of_service)}
                          target="_blank"
                          rel="noopener noreferrer"
                          className="inline-flex items-center gap-1 hover:text-primary"
                        >
                          服务条款 <ExternalLink className="size-3" />
                        </a>
                      )}
                    </div>
                  )}
                </CardContent>
              </Card>
            )
          })}
        </div>
      )}
    </div>
  )
}
