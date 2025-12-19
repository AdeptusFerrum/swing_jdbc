

# Dockerfile
FROM maven:3.9-eclipse-temurin-11 AS build

WORKDIR /app

# Копируем исходный код
COPY pom.xml .
COPY src ./src

# Скачиваем зависимости
RUN mvn dependency:go-offline

# Собираем проект
RUN mvn clean package -DskipTests

# Создаем директорию для отчетов
RUN mkdir -p /app/reports

# Финальный образ
FROM eclipse-temurin:11-jre

WORKDIR /app

# Копируем собранный JAR
COPY --from=build /app/target/*.jar app.jar

# Устанавливаем необходимые утилиты
RUN apt-get update && apt-get install -y \
    curl \
    wget \
    unzip \
    && rm -rf /var/lib/apt/lists/*

# Копируем отчеты если есть
COPY --from=build /app/reports ./reports

# Копируем БД SQLite если нужно
COPY crud_app.db ./crud_app.db

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]