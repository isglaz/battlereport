import com.varabyte.kobweb.gradle.application.util.configAsKobwebApplication
import kotlinx.html.link

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kobweb.application)
    alias(libs.plugins.kobwebx.markdown)
    alias(libs.plugins.kotlin.serialization)
}

group = "org.iglaz.battlereport"
version = "1.0.0"


kobweb {
    app {
        index {
            description.set("Battle reports for tabletop wargamers")
            head.add {
                link(rel = "preconnect", href = "https://fonts.googleapis.com")
                link(rel = "preconnect", href = "https://fonts.gstatic.com") {
                    attributes["crossorigin"] = ""
                }
                link(
                    rel = "stylesheet",
                    href = "https://fonts.googleapis.com/css2?family=Source+Serif+4:ital,opsz,wght@" +
                            "0,8..60,400;0,8..60,500;0,8..60,600;0,8..60,700;1,8..60,400&display=swap"
                )
            }
        }
    }
}

kotlin {
    configAsKobwebApplication("battlereport")

    sourceSets {
        jsMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.html.core)
            implementation(libs.kobweb.core)
            implementation(libs.kobweb.silk)
            implementation(libs.silk.icons.fa)
            implementation(libs.kobwebx.markdown)
            implementation(libs.kobwebx.serialization.kotlinx)   // уже есть в каталоге
            implementation(libs.kotlinx.serialization.json)
            implementation(npm("marked", "12.0.2"))
        }
    }
}
