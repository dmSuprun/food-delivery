FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
# Run as an unprivileged user instead of root
RUN addgroup -S app && adduser -S app -G app
# JAR is built by Maven before the image (CI stage "Package" or `mvn package`)
COPY target/notification-service.jar app.jar
USER app
ENV PORT=8086
EXPOSE 8086
HEALTHCHECK --interval=10s --timeout=3s --start-period=10s --retries=3 \
  CMD wget -qO- http://127.0.0.1:8086/health || exit 1
ENTRYPOINT ["java", "-jar", "app.jar"]
