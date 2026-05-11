FROM gradle:8.10.2-jdk21 AS build
WORKDIR /workspace
COPY --chown=gradle:gradle . .
RUN gradle --no-daemon installDist

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /workspace/build/install/company/ ./
EXPOSE 8080
ENTRYPOINT ["./bin/company"]
