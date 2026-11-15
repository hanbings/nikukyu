import {
  ArrowRight,
  KeyRound,
  ShieldCheck,
  Sparkles,
  UserRound,
} from 'lucide-react'
import { Link } from 'react-router'
import { PageHeading, PersonAvatar } from '@/components/common'
import { Button } from '@/components/ui/button'
import { Card, CardContent } from '@/components/ui/card'
import { useSession } from '@/lib/api'

const sections = [
  {
    icon: UserRound,
    title: '个人资料',
    description: '让大家更好地认识你。',
    to: '/settings/profile',
    color: 'bg-[#f5e6dc] text-[#b77555] dark:bg-[#443128]',
  },
  {
    icon: ShieldCheck,
    title: '账号安全',
    description: '管理你的登录方式。',
    to: '/settings/security',
    color: 'bg-[#e6eee5] text-[#718a6b] dark:bg-[#303f32]',
  },
  {
    icon: KeyRound,
    title: '已授权应用',
    description: '查看并管理应用权限。',
    to: '/settings/authorizations',
    color: 'bg-[#eee9f5] text-[#8a78a4] dark:bg-[#3c3446]',
  },
]

export function HomePage() {
  const { data } = useSession()
  if (!data) return null
  const { account } = data
  const name = account.nickname || account.username
  return (
    <div>
      <PageHeading
        eyebrow="Your account"
        title={`你好，${name}`}
        description="欢迎回到你的账号中心，一切都在你的掌握之中。"
      />
      <div className="relative mb-10 overflow-hidden rounded-3xl bg-[#e6ece1] p-7 dark:bg-[#2c3a2e] sm:p-10">
        <div className="relative z-10 max-w-lg">
          <span className="inline-flex items-center gap-1.5 rounded-full bg-white/65 px-3 py-1 text-xs font-medium text-[#667d62] dark:bg-white/10 dark:text-[#c2d9bc]">
            <Sparkles className="size-3.5" />
            属于你的小空间
          </span>
          <h2 className="mt-6 text-2xl font-semibold tracking-tight sm:text-3xl">
            让连接更简单，
            <br />
            让选择留给自己。
          </h2>
          <p className="mt-4 max-w-sm text-sm leading-7 text-[#687669] dark:text-[#c3d1be]">
            你的资料、登录方式和应用授权都在这里。随时查看，也随时调整。
          </p>
          <Button asChild className="mt-6">
            <Link to="/settings/profile">
              编辑个人资料 <ArrowRight />
            </Link>
          </Button>
        </div>
        <div
          aria-hidden="true"
          className="absolute -right-4 -bottom-16 hidden size-64 items-center justify-center rounded-full border border-white/55 bg-white/25 dark:border-white/10 dark:bg-white/5 sm:flex"
        >
          <PersonAvatar
            name={name}
            image={account.avatar}
            className="size-28 border-4 border-white/70 text-3xl"
          />
        </div>
      </div>
      <div className="mb-4 flex items-end justify-between">
        <div>
          <p className="text-xs font-semibold tracking-[0.14em] text-primary uppercase">
            Explore
          </p>
          <h2 className="mt-2 text-xl font-semibold tracking-tight">
            管理你的账号
          </h2>
        </div>
        <span className="text-xs text-muted-foreground">一切从这里开始</span>
      </div>
      <div className="grid gap-4 md:grid-cols-3">
        {sections.map(({ icon: Icon, title, description, to, color }) => (
          <Link
            key={to}
            to={to}
            className="group rounded-2xl focus-visible:outline-2"
          >
            <Card className="h-full gap-0 rounded-2xl transition-shadow group-hover:shadow-md">
              <CardContent>
                <div
                  className={`mb-7 flex size-11 items-center justify-center rounded-2xl ${color}`}
                >
                  <Icon className="size-5" />
                </div>
                <div className="flex items-center justify-between gap-2">
                  <h3 className="font-semibold">{title}</h3>
                  <ArrowRight className="size-4 text-muted-foreground transition-transform group-hover:translate-x-1" />
                </div>
                <p className="mt-2 text-sm text-muted-foreground">
                  {description}
                </p>
              </CardContent>
            </Card>
          </Link>
        ))}
      </div>
    </div>
  )
}
