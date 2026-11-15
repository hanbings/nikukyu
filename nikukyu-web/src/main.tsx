import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import App from './App'
import { captureAuthorizationTicket } from './lib/authorization'
import './index.css'

captureAuthorizationTicket()
createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <App />
  </StrictMode>,
)
