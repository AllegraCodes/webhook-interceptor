# build
FROM eclipse-temurin:21 AS builder

WORKDIR /workspace
COPY . .
RUN chmod +x ./gradlew

RUN ./gradlew bootJar --no-daemon

# run
FROM eclipse-temurin:21-jre-alpine

LABEL org.opencontainers.image.source=https://github.com/AllegraCodes/webhook-interceptor
LABEL org.opencontainers.image.description="Modifies HTTP requests"
LABEL org.opencontainers.image.licenses=MIT

USER guest

WORKDIR /app

COPY --from=builder /workspace/build/libs/*.jar app.jar

EXPOSE 8080

HEALTHCHECK CMD wget -qO- http://localhost:8080/actuator/health || exit 1

CMD ["java", "-jar", "app.jar"]
