# Контекст сборки — корень репозитория: :server зависит от :common, нужен весь Gradle-проект.
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /src

# Сначала только файлы сборки: слой с зависимостями переиспользуется,
# пока не изменились сами build-файлы.
COPY gradlew ./
COPY gradle gradle
COPY settings.gradle.kts build.gradle.kts gradle.properties ./
COPY common/build.gradle.kts common/
COPY server/build.gradle.kts server/
# Gradle настраивает все модули из settings.gradle.kts, даже когда собирается только
# :server, поэтому :site тоже должен конфигурироваться. Kobweb-плагин при apply требует
# .kobweb/conf.yaml и без него падает с "This project is not a Kobweb project".
COPY site/build.gradle.kts site/
COPY site/.kobweb/conf.yaml site/.kobweb/

# gradlew в репозитории лежит с CRLF-окончаниями: на Linux shebang с \r не исполняется
# и запуск падает с exit 127. Правим внутри образа, файл в репозитории не трогаем.
RUN tr -d '\r' < gradlew > /tmp/gradlew && mv /tmp/gradlew gradlew && chmod +x gradlew

COPY common common
COPY server server

# Кеш BuildKit переживает пересборки: без него каждая заново качает дистрибутив
# Gradle (~130 МБ) и все зависимости, и сборка растягивается на десятки минут.
RUN --mount=type=cache,target=/root/.gradle \
    ./gradlew --no-daemon :server:buildFatJar

FROM eclipse-temurin:21-jre-alpine
WORKDIR /opt/app
COPY --from=build /src/server/build/libs/*-all.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
