# ===== BUILD =====
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app
COPY . .

RUN mvn clean package -DskipTests

# ===== RUN =====
FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

COPY --from=build /app/target/safewalk-1.0.0.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]