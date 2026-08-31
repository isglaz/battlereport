# Контекст сборки — корень репозитория: :site зависит от :common.
#
# kobwebExport делает статический экспорт, для предрендера ему нужен браузер,
# поэтому стадия сборки тяжёлая. Результат — обычная статика, её отдаёт nginx.
FROM eclipse-temurin:21-jdk AS build
WORKDIR /src

# Playwright скачивает браузер сам, но ему нужны системные библиотеки.
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

# gradlew в репозитории лежит с CRLF-окончаниями: на Linux shebang с \r не исполняется.
RUN tr -d '\r' < gradlew > /tmp/gradlew && mv /tmp/gradlew gradlew && chmod +x gradlew

COPY common common
COPY site site

# Кеш BuildKit переживает пересборки: без него каждая заново качает дистрибутив
# Gradle (~130 МБ), зависимости и браузер для предрендера.
RUN --mount=type=cache,target=/root/.gradle \
    ./gradlew --no-daemon :site:kobwebExport

FROM nginx:alpine
COPY --from=build /src/site/.kobweb/site /usr/share/nginx/html
COPY site/nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
