# Etapa de construcción
FROM eclipse-temurin:21-jdk as builder
WORKDIR /app
COPY . .
RUN ./gradlew bootJar --no-daemon

# Imagen final
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
