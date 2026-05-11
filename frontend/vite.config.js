import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/companies': 'http://localhost:8080',
      '/employees': 'http://localhost:8080',
    },
  },
});
