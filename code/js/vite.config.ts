import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'


// https://vitejs.dev/config/
export default defineConfig({
    plugins: [vue()],
    server: {
        port: Number.parseInt(process.env.VITE_VUE_PORT!) || 5173,
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
