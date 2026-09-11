package _Self

import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildSteps.script
import jetbrains.buildServer.configs.kotlin.triggers.vcs

/*
 * TeamCity build configuration (versioned settings, Kotlin DSL).
 *
 * For TeamCity to pick this file up, the project needs Versioned Settings -> Synchronization
 * enabled once, format Kotlin, pointing at this repository.
 *
 * A three-step pipeline, the order is strict:
 *   Build -> Migrate -> Deploy
 * Migrations are applied BEFORE the new code comes up: otherwise the new server starts against the old schema.
 *
 * Project parameters that must be set in the UI:
 *   REGISTRY      - registry host the images are pushed to
 *   DEPLOY_HOST   - user@host of the production server
 *   DEPLOY_PATH   - path to the repository on the production server
 *   DB_JDBC_URL   - JDBC coordinates of the production database
 *   DB_USER
 *   DB_PASSWORD   - password type, masked in the logs
 */

version = "2025.03"

project {
    description = "BattleReport: image builds, database migrations, production deployment"

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
    description = "Builds the server and site images and pushes them to the registry"

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
    description = "Applies migrations to the production database before the new code is deployed"

    vcs { root(DslContext.settingsRoot) }

    steps {
        script {
            name = "liquibase update"
            // migrate.sh reads the coordinates from the environment; the password arrives as a
            // password parameter and is masked in the TeamCity logs.
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
    description = "Updates the compose stack on the production server"

    vcs { root(DslContext.settingsRoot) }

    steps {
        script {
            name = "deploy over ssh"
            // The server holds an .env with REGISTRY, TAG and the database coordinates - outside the repository.
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
