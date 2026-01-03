import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import Components from 'unplugin-vue-components/vite'
import { VantResolver } from '@vant/auto-import-resolver'

export default defineConfig({
    plugins: [
        vue(),
        Components({
            resolvers: [VantResolver()]
        })
    ],
    resolve: {
        alias: {
            '@': '/src'
        }
    },
    server: {
        port: 5174,
        proxy: {
            '/api': {
                target: 'http://111.230.110.203:8080',
                changeOrigin: true,
                timeout: 30000
            }
        }
    }
})
