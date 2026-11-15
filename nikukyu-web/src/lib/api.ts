import axios from 'axios'
import { QueryClient, useQuery } from '@tanstack/react-query'
import type { PublicConfig, Session } from './types'

export const sessionKey = ['session'] as const
export const queryClient = new QueryClient({
  defaultOptions: {
    queries: { retry: 1, refetchOnWindowFocus: true },
    mutations: { retry: false },
  },
})

export const api = axios.create({
  baseURL: '/api/v1',
  withCredentials: true,
  timeout: 15_000,
})

api.interceptors.request.use((config) => {
  const method = config.method?.toUpperCase()
  const csrf = queryClient.getQueryData<Session | null>(sessionKey)?.csrf_token
  if (csrf && method && !['GET', 'HEAD', 'OPTIONS'].includes(method)) {
    config.headers.set('X-CSRF-Token', csrf)
  }
  return config
})

api.interceptors.response.use(undefined, (error: unknown) => {
  if (axios.isAxiosError(error) && error.response?.status === 401) {
    const path = error.config?.url || ''
    if (
      path.startsWith('/accounts/me') ||
      path === '/sessions/current' ||
      path === '/oidc/authorizations'
    ) {
      queryClient.removeQueries({
        predicate: (query) =>
          ['identities', 'grants'].includes(String(query.queryKey[0])),
      })
      queryClient.setQueryData(sessionKey, null)
    }
  }
  return Promise.reject(error)
})

export function useSession() {
  return useQuery({
    queryKey: sessionKey,
    queryFn: async ({ signal }) => {
      try {
        return (await api.get<Session>('/sessions/current', { signal })).data
      } catch (error) {
        if (axios.isAxiosError(error) && error.response?.status === 401)
          return null
        throw error
      }
    },
    retry: false,
    staleTime: 30_000,
  })
}

export function usePublicConfig() {
  return useQuery({
    queryKey: ['config'],
    queryFn: async () => (await api.get<PublicConfig>('/config')).data,
    staleTime: 60_000,
  })
}

export function messageFor(error: unknown): string {
  if (!axios.isAxiosError(error)) return '操作未能完成，请稍后重试。'
  if (!error.response) return '无法连接服务器，请检查网络后重试。'
  const body = error.response.data as
    { error?: string; error_description?: string } | undefined
  if (body?.error === 'invalid_credentials') return '用户名或密码不正确。'
  if (error.response.status === 429) return '尝试次数过多，请稍后再试。'
  if (error.response.status === 409)
    return error.config?.url === '/accounts'
      ? '该用户名已被使用。'
      : '操作与已有记录冲突。'
  if (body?.error_description === 'Registration is disabled')
    return '当前暂未开放注册。'
  if (
    body?.error_description ===
    'Sign in again before changing authentication methods'
  )
    return '请先重新登录，再绑定 GitHub。'
  if (body?.error_description === 'Invalid CSRF token')
    return '会话已更新，请刷新页面后重试。'
  if (error.response.status >= 500) return '服务器暂时不可用，请稍后重试。'
  return body?.error_description || '操作未能完成，请稍后重试。'
}

export function clearUserData() {
  queryClient.removeQueries({
    predicate: (query) =>
      ['session', 'identities', 'grants', 'authorization-context'].includes(
        String(query.queryKey[0]),
      ),
  })
  queryClient.setQueryData(sessionKey, null)
}
