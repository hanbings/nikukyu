const ticketKey = 'nikukyu.authorization.request'

// The backend sends the continuation in the fragment so it never reaches a server.
export function captureAuthorizationTicket() {
  if (window.location.pathname !== '/authorize') return
  const fragment = new URLSearchParams(window.location.hash.slice(1))
  const ticket = fragment.get('request_token')
  if (ticket) {
    try {
      window.sessionStorage.setItem(ticketKey, ticket)
    } catch {
      // The authorization page will show a helpful error if storage is disabled.
    } finally {
      window.history.replaceState(
        window.history.state,
        '',
        window.location.pathname + window.location.search,
      )
    }
  }
}

export function authorizationTicket(): string | null {
  try {
    return window.sessionStorage.getItem(ticketKey)
  } catch {
    return null
  }
}

export function clearAuthorizationTicket() {
  try {
    window.sessionStorage.removeItem(ticketKey)
  } catch {
    /* Storage is unavailable. */
  }
}

export function nextPath(value: string | null): string {
  return value === '/authorize' || value === '/settings/security' ? value : '/'
}
