# Build stage
FROM node:20-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm ci --legacy-peer-deps --silent
COPY . .
RUN npm run build:prod

# Runtime stage
FROM nginx:alpine AS runtime
COPY --from=builder /app/dist/nexbank-frontend/browser /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
