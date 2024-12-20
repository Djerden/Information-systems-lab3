# Этап сборки (build stage)
FROM openjdk:17-jdk-slim AS build

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем файлы проекта в контейнер (включая gradle wrapper)
COPY . .

# Даем права на выполнение скрипта gradlew (если это необходимо)
RUN chmod +x gradlew

# Сборка проекта с использованием gradle wrapper
RUN ./gradlew build --no-daemon

# Этап финальной сборки (final stage)
FROM openjdk:17-jdk-slim

# Устанавливаем рабочую директорию для финального контейнера
WORKDIR /app

# Копируем скомпилированный JAR файл из первого этапа (уточните путь, если их два)
COPY --from=build /app/build/libs/backend-lab1-0.0.1-SNAPSHOT.jar app.jar

# Открываем порт для приложения
EXPOSE 8080

# Запуск приложения
ENTRYPOINT ["java", "-jar", "app.jar"]
