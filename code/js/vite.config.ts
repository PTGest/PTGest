import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'
import {vuePort} from "./src/services/utils/envUtils";
// https://vitejs.dev/config/
export default defineConfig({
    plugins: [vue()],
    server: {
        port: vuePort ,
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
