import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
    plugins: [vue()],
    resolve: {
        alias: {
            '@': '/src'
        }
    },
    server: {
        port: 5173,
        proxy: {
            '/api': {
                target: 'http://111.230.110.203:8080',
                changeOrigin: true,
                timeout: 30000, // 30秒超时
                proxyTimeout: 30000
            }
        }
    }
})
