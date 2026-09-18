# =====================================================
# STAGE 1 : Build du bundle optimisé avec Vite
# =====================================================
FROM node:20-alpine AS build

WORKDIR /app

COPY package*.json ./
COPY .npmrc ./
RUN npm ci

COPY . .
ENV VITE_API_URL=/api
RUN npm run build

# =====================================================
# STAGE 2 : Serveur nginx NON-ROOT
# =====================================================
FROM nginxinc/nginx-unprivileged:1.27-alpine AS runtime

# Copier le bundle optimisé
COPY --from=build --chown=nginx:nginx /app/dist /usr/share/nginx/html

# Copier la config nginx
COPY --chown=nginx:nginx nginx.conf /etc/nginx/conf.d/default.conf

# Exposer 8080 (port non-privilégié)
EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=5s --retries=3 \
    CMD wget -qO- http://localhost:8080/ >/dev/null || exit 1

CMD ["nginx", "-g", "daemon off;"]