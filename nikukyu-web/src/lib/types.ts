export interface Account {
  id: string
  username: string
  nickname: string | null
  avatar: string | null
  background: string | null
  theme_color: string | null
}

export interface Session {
  id: string
  account: Account
  expires_at: string
  csrf_token: string
}

export interface PublicConfig {
  registration_enabled: boolean
  github_enabled: boolean
}

export interface Identity {
  id: string
  provider: string
  openid: string
  created_at: string
}

export interface Application {
  name: string
  description: string | null
  avatar: string | null
  background: string | null
  theme_color: string | null
  terms_of_service: string | null
  privacy_policy: string | null
  is_deleted: boolean
}

export interface Grant {
  id: string
  oauth_id: string
  application: Application | null
  scope: string[]
  created_at: string
}

export interface PublicClient {
  client_id: string
  oauth_id: string
  name: string
  description: string | null
  avatar: string | null
  terms_of_service: string | null
  privacy_policy: string | null
}

export interface AuthorizationContext {
  client_id: string
  scope: string[]
  login_required: boolean
  select_account: boolean
  expires_at: number
}

export interface List<T> {
  items: T[]
}
