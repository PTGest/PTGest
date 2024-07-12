import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'
// https://vitejs.dev/config/
export default defineConfig({
    plugins: [vue()],
    build: {
        rollupOptions: {
            external: ['node:inspector'],
        },
    },
    server: {
        proxy: {
          '/api': {
            target: 'http://localhost:8080',
            changeOrigin: true,
          }
        },
    },
    resolve: {
        alias: {
          '@': path.resolve(__dirname, './src')
        },
    }
})
