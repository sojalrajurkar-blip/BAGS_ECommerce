# ============================================================================
# Stage 1: Build JAR using Maven with Eclipse Temurin 21 JDK
# ============================================================================
FROM maven:3.9.9-eclipse-temurin-21-alpine AS builder

WORKDIR /build

# Cache Maven dependencies layer
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Build application
COPY src ./src
RUN mvn clean package -DskipTests -B

# ============================================================================
# Stage 2: Minimal Production Runtime with Eclipse Temurin 21 JRE
# ============================================================================
FROM eclipse-temurin:21-jre-alpine AS runner

# Security: Create non-privileged system group and user
RUN addgroup -S rora && adduser -S rora -G rora

WORKDIR /app

# Copy compiled JAR from builder stage
COPY --from=builder /build/target/rora-backend-*.jar /app/app.jar

# Set ownership
RUN chown -R rora:rora /app

# Switch to non-root user
USER rora:rora

# Environment configuration
ENV SPRING_PROFILES_ACTIVE=prod \
    PORT=8080 \
    JAVA_OPTS="-XX:+UseG1GC -XX:MaxRAMPercentage=75.0 -XX:InitialRAMPercentage=50.0 -Djava.security.egd=file:/dev/./urandom"

EXPOSE 8080

# Health check probe against Actuator health endpoint
HEALTHCHECK --interval=30s --timeout=5s --start-period=45s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
