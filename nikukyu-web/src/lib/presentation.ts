export const scopeDetails: Record<
  string,
  { title: string; description: string }
> = {
  openid: {
    title: '确认你的身份',
    description: '允许应用识别你的 Nikukyu 账号。',
  },
  profile: {
    title: '读取基本资料',
    description: '允许应用读取你的用户名、昵称和头像等资料。',
  },
  email: {
    title: '读取邮箱地址',
    description: '允许应用读取你的主邮箱及其验证状态（如有）。',
  },
  offline_access: {
    title: '保持访问',
    description: '允许应用在你离开后继续访问已授权的信息。',
  },
}

export function safeUrl(value: string | null | undefined): string | undefined {
  if (!value) return undefined
  try {
    const url = new URL(value)
    return url.protocol === 'https:' || url.protocol === 'http:'
      ? url.href
      : undefined
  } catch {
    return undefined
  }
}

export function displayDate(value: string): string {
  const date = new Date(value)
  return Number.isNaN(date.getTime())
    ? '日期未知'
    : new Intl.DateTimeFormat('zh-CN', { dateStyle: 'long' }).format(date)
}
