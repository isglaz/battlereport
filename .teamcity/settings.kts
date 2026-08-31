package _Self

import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildSteps.script
import jetbrains.buildServer.configs.kotlin.triggers.vcs

/*
 * Конфигурация сборки в TeamCity (versioned settings, Kotlin DSL).
 *
 * Чтобы TeamCity подхватил этот файл, в проекте нужно один раз включить
 * Versioned Settings -> Synchronization enabled, format Kotlin, указав этот репозиторий.
 *
 * Пайплайн из трёх шагов, порядок жёсткий:
 *   Build -> Migrate -> Deploy
 * Миграции применяются ДО подъёма нового кода: иначе новый сервер стартует на старой схеме.
 *
 * Параметры проекта, которые нужно задать в UI:
 *   REGISTRY      — хост registry, куда пушатся образы
 *   DEPLOY_HOST   — user@host прод-сервера
 *   DEPLOY_PATH   — путь до репозитория на прод-сервере
 *   DB_JDBC_URL   — JDBC-координаты прод-БД
 *   DB_USER
 *   DB_PASSWORD   — тип password, маскируется в логах
 */

version = "2025.03"

project {
    description = "BattleReport: сборка образов, миграции БД, деплой на прод"

    params {
        param("REGISTRY", "")
        param("DEPLOY_HOST", "")
        param("DEPLOY_PATH", "/opt/battlereport")
        param("DB_JDBC_URL", "")
        param("DB_USER", "battlereport")
        password("DB_PASSWORD", "credentialsJSON:db-password", display = ParameterDisplay.HIDDEN)
    }

    buildType(Build)
    buildType(Migrate)
    buildType(Deploy)

    sequential {
        buildType(Build)
        buildType(Migrate)
        buildType(Deploy)
    }
}

object Build : BuildType({
    name = "Build"
    description = "Собирает образы server и site и пушит их в registry"

    vcs { root(DslContext.settingsRoot) }

    steps {
        script {
            name = "Build and push images"
            scriptContent = """
                set -eu
                docker build -f server/Dockerfile -t %REGISTRY%/battlereport-server:%build.number% .
                docker build -f site/Dockerfile   -t %REGISTRY%/battlereport-site:%build.number% .
                docker push %REGISTRY%/battlereport-server:%build.number%
                docker push %REGISTRY%/battlereport-site:%build.number%
            """.trimIndent()
        }
    }

    triggers { vcs {} }
})

object Migrate : BuildType({
    name = "Migrate"
    description = "Накатывает миграции на прод-БД до деплоя нового кода"

    vcs { root(DslContext.settingsRoot) }

    steps {
        script {
            name = "liquibase update"
            // migrate.sh читает координаты из окружения; пароль приходит password-параметром
            // и маскируется в логах TeamCity.
            scriptContent = """
                set -eu
                export DB_JDBC_URL='%DB_JDBC_URL%'
                export DB_USER='%DB_USER%'
                export DB_PASSWORD='%DB_PASSWORD%'
                ./deploy/migrate.sh update
            """.trimIndent()
        }
    }
})

object Deploy : BuildType({
    name = "Deploy"
    description = "Обновляет compose-стек на прод-сервере"

    vcs { root(DslContext.settingsRoot) }

    steps {
        script {
            name = "deploy over ssh"
            // На сервере лежит .env с REGISTRY, TAG и координатами БД — вне репозитория.
            scriptContent = """
                set -eu
                ssh %DEPLOY_HOST% "cd %DEPLOY_PATH% \
                    && git fetch --all && git checkout --force %build.vcs.number% \
                    && sed -i 's|^TAG=.*|TAG=%build.number%|' .env \
                    && ./deploy/deploy.sh"
            """.trimIndent()
        }
    }
})
