# The build context is the repository root: :site depends on :common.
#
# kobwebExport produces a static export and needs a browser for prerendering, which makes
# the build stage heavy. The result is plain static content, served by nginx.
FROM eclipse-temurin:21-jdk AS build
WORKDIR /src

# Playwright downloads the browser itself, but it needs the system libraries.
RUN apt-get update \
 && apt-get install -y --no-install-recommends \
      libnss3 libnspr4 libatk1.0-0 libatk-bridge2.0-0 libcups2 libdrm2 \
      libxkbcommon0 libxcomposite1 libxdamage1 libxfixes3 libxrandr2 \
      libgbm1 libpango-1.0-0 libcairo2 libasound2 \
 && rm -rf /var/lib/apt/lists/*

COPY gradlew ./
COPY gradle gradle
COPY settings.gradle.kts build.gradle.kts gradle.properties ./
COPY common/build.gradle.kts common/
COPY server/build.gradle.kts server/
COPY site/build.gradle.kts site/

# gradlew is stored with CRLF endings: on Linux a shebang with \r is not executable.
RUN tr -d '\r' < gradlew > /tmp/gradlew && mv /tmp/gradlew gradlew && chmod +x gradlew

COPY common common
COPY site site

# The BuildKit cache survives rebuilds: without it every rebuild re-downloads the Gradle
# distribution (~130 MB), the dependencies and the prerendering browser.
RUN --mount=type=cache,target=/root/.gradle \
    ./gradlew --no-daemon :site:kobwebExport

FROM nginx:alpine
COPY --from=build /src/site/.kobweb/site /usr/share/nginx/html
COPY site/nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
