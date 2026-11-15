import { defineConfig } from '@rsbuild/core'
import { pluginReact } from '@rsbuild/plugin-react'
import { pluginTailwindcss } from '@rsbuild/plugin-tailwindcss'

const apiTarget = process.env.NIKUKYU_API_TARGET || 'http://127.0.0.1:8080'

export default defineConfig({
  plugins: [pluginReact({ reactCompiler: true }), pluginTailwindcss()],
  source: { entry: { index: './src/main.tsx' } },
  html: { template: './index.html' },
  server: {
    port: 5173,
    proxy: {
      '/api': apiTarget,
      '/oidc': apiTarget,
      '/.well-known': apiTarget,
    },
  },
})
