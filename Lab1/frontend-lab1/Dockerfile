# Этап 1: Сборка приложения с Vite
FROM node:18 AS build

# Устанавливаем рабочую директорию в контейнере
WORKDIR /app

# Копируем package.json и package-lock.json для установки зависимостей
COPY package*.json ./

# Устанавливаем зависимости проекта
RUN npm install

# Копируем весь проект в рабочую директорию контейнера
COPY . .

# Строим проект
RUN npm run build

# Этап 2: Настройка Nginx для обслуживания статики
FROM nginx:alpine

# Копируем сгенерированные файлы из папки dist в директорию Nginx для статики
COPY --from=build /app/dist /usr/share/nginx/html

# Копируем кастомный конфиг для Nginx (если есть)
# COPY nginx.conf /etc/nginx/nginx.conf

# Копируем кастомный конфиг для Nginx
COPY nginx.conf /etc/nginx/nginx.conf

# Открываем порт 80 для доступа к приложению
EXPOSE 80

# Запускаем Nginx
CMD ["nginx", "-g", "daemon off;"]
