# The build context is the repository root: :server depends on :common, the whole Gradle project is needed.
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /src

# Build files first: the dependency layer is reused for as long as the build files themselves are unchanged.
COPY gradlew ./
COPY gradle gradle
COPY settings.gradle.kts build.gradle.kts gradle.properties ./
COPY common/build.gradle.kts common/
COPY server/build.gradle.kts server/
# Gradle configures every module from settings.gradle.kts even when only :server is built,
# so :site must be configurable too. On apply the Kobweb plugin requires .kobweb/conf.yaml
# and without it fails with "This project is not a Kobweb project".
COPY site/build.gradle.kts site/
COPY site/.kobweb/conf.yaml site/.kobweb/

# gradlew is stored with CRLF endings: on Linux a shebang with \r is not executable
# and the launch fails with exit 127. Fix it inside the image, leave the repository file untouched.
RUN tr -d '\r' < gradlew > /tmp/gradlew && mv /tmp/gradlew gradlew && chmod +x gradlew

COPY common common
COPY server server

# The BuildKit cache survives rebuilds: without it every rebuild re-downloads the Gradle
# distribution (~130 MB) and all dependencies, stretching the build to tens of minutes.
RUN --mount=type=cache,target=/root/.gradle \
    ./gradlew --no-daemon :server:buildFatJar

FROM eclipse-temurin:21-jre-alpine
WORKDIR /opt/app
COPY --from=build /src/server/build/libs/*-all.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
