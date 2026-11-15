import { zodResolver } from '@hookform/resolvers/zod'
import { useMutation } from '@tanstack/react-query'
import { Image, Palette, Save } from 'lucide-react'
import { useForm, useWatch } from 'react-hook-form'
import { toast } from 'sonner'
import { z } from 'zod'
import { PageHeading, PersonAvatar } from '@/components/common'
import { Button } from '@/components/ui/button'
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/components/ui/card'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { api, messageFor, queryClient, sessionKey, useSession } from '@/lib/api'
import { safeUrl } from '@/lib/presentation'
import type { Account, Session } from '@/lib/types'

const shortText = (limit: number) =>
  z
    .string()
    .refine(
      (value) => Array.from(value).length <= limit,
      `不能超过 ${limit} 个字符。`,
    )
const imageUrl = shortText(2048).refine(
  (value) => !value || !!safeUrl(value),
  '请输入完整的 HTTP 或 HTTPS 地址。',
)
const schema = z.object({
  nickname: shortText(128),
  avatar: imageUrl,
  background: imageUrl,
  theme_color: shortText(32),
})
type Values = z.infer<typeof schema>

function ProfileForm({ account }: { account: Account }) {
  const form = useForm<Values>({
    resolver: zodResolver(schema),
    defaultValues: {
      nickname: account.nickname || '',
      avatar: account.avatar || '',
      background: account.background || '',
      theme_color: account.theme_color || '',
    },
  })
  const save = useMutation({
    mutationFn: async (values: Values) =>
      (
        await api.patch<Account>(
          '/accounts/me',
          Object.fromEntries(
            Object.entries(values).map(([key, value]) => [
              key,
              value.trim() || null,
            ]),
          ),
        )
      ).data,
    onSuccess: (account) => {
      queryClient.setQueryData<Session | null>(sessionKey, (old) =>
        old ? { ...old, account } : old,
      )
      form.reset({
        nickname: account.nickname || '',
        avatar: account.avatar || '',
        background: account.background || '',
        theme_color: account.theme_color || '',
      })
      toast.success('资料已保存。')
    },
    onError: (error) => toast.error(messageFor(error)),
  })
  const nickname = useWatch({ control: form.control, name: 'nickname' })
  const avatar = useWatch({ control: form.control, name: 'avatar' })
  const fields = [
    { name: 'nickname' as const, label: '昵称', placeholder: '给自己起个名字' },
    {
      name: 'avatar' as const,
      label: '头像地址',
      placeholder: 'https://example.com/avatar.png',
    },
    {
      name: 'background' as const,
      label: '背景图片地址',
      placeholder: 'https://example.com/background.png',
    },
    {
      name: 'theme_color' as const,
      label: '个人主题色',
      placeholder: '例如 #bd6042',
    },
  ]
  return (
    <div className="grid items-start gap-6 lg:grid-cols-[minmax(0,1fr)_300px]">
      <Card className="rounded-2xl">
        <CardHeader>
          <CardTitle>基本资料</CardTitle>
          <CardDescription>
            这些信息可能会在你授权的应用中展示。
          </CardDescription>
        </CardHeader>
        <CardContent>
          <form
            className="space-y-5"
            onSubmit={form.handleSubmit((values) => save.mutate(values))}
          >
            <div className="space-y-2">
              <Label>用户名</Label>
              <Input value={account.username} disabled className="h-11" />
              <p className="text-xs text-muted-foreground">
                用户名目前不可修改。
              </p>
            </div>
            {fields.map((field) => (
              <div className="space-y-2" key={field.name}>
                <Label htmlFor={field.name}>{field.label}</Label>
                <Input
                  id={field.name}
                  className="h-11"
                  placeholder={field.placeholder}
                  aria-invalid={!!form.formState.errors[field.name]}
                  {...form.register(field.name)}
                />
                {form.formState.errors[field.name] && (
                  <p className="text-xs text-destructive">
                    {form.formState.errors[field.name]?.message}
                  </p>
                )}
              </div>
            ))}
            <p className="text-xs leading-5 text-muted-foreground">
              头像和背景请填写图片地址，暂不支持直接上传。
            </p>
            <Button
              type="submit"
              disabled={save.isPending || !form.formState.isDirty}
            >
              <Save />
              {save.isPending ? '保存中…' : '保存修改'}
            </Button>
          </form>
        </CardContent>
      </Card>
      <Card className="overflow-hidden rounded-2xl pt-0">
        <div className="h-24 bg-[#e6ece1] dark:bg-[#2d3a30]" />
        <CardContent className="-mt-11">
          <PersonAvatar
            className="size-20 border-4 border-card"
            name={nickname || account.username}
            image={avatar}
          />
          <h2 className="mt-4 font-semibold">{nickname || account.username}</h2>
          <p className="mt-1 text-sm text-muted-foreground">
            @{account.username}
          </p>
          <div className="mt-6 space-y-3 border-t pt-5 text-xs leading-5 text-muted-foreground">
            <p className="flex gap-2">
              <Image className="size-4 shrink-0" />
              头像填写可公开访问的图片地址。
            </p>
            <p className="flex gap-2">
              <Palette className="size-4 shrink-0" />
              个人主题色可用于支持它的应用。
            </p>
          </div>
        </CardContent>
      </Card>
    </div>
  )
}

export function ProfilePage() {
  const session = useSession()
  if (!session.data) return null
  return (
    <div>
      <PageHeading
        eyebrow="Profile"
        title="个人资料"
        description="编辑属于你的名字与个人风格。"
      />
      <ProfileForm
        key={session.data.account.id}
        account={session.data.account}
      />
    </div>
  )
}
