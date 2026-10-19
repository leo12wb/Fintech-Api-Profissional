# Build stage
FROM maven:3.9.9-eclipse-temurin-21-alpine AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn -B dependency:go-offline -q
COPY src ./src
RUN mvn -B package -DskipTests -q

# Runtime stage
FROM eclipse-temurin:21-jre-alpine AS runtime
RUN addgroup -S nexbank && adduser -S nexbank -G nexbank
WORKDIR /app
COPY --from=builder /app/target/nexbank-backend-*.jar app.jar
RUN chown nexbank:nexbank app.jar
USER nexbank
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "-Djava.security.egd=file:/dev/./urandom", "app.jar"]
