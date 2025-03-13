# Стадия 1: Сборка приложения
FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /app

# Копируем pom.xml
COPY pom.xml /app/

# Копируем исходный код
COPY src /app/src/

# Копируем application.properties.example и переименовываем в application.properties
COPY src/main/resources/application.properties.example /app/src/main/resources/application.properties

# Собираем только task-tracker-scheduler из корневого pom.xml
RUN mvn -f /app/pom.xml clean package -DskipTests

# Стадия 2: Создание итогового образа
FROM eclipse-temurin:21-jre-alpine-3.21

WORKDIR /app

# Копируем собранный JAR
COPY --from=builder /app/target/*.jar /app/app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]