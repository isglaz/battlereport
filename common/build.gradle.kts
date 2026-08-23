plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
}

group = "org.isglaz.battlereport"
version = "1.0.0"

kotlin {
    jvm()
    js(IR) {
        browser()
    }

    jvmToolchain(21)

    sourceSets {
        commonMain.dependencies {
            api(libs.kotlinx.serialization.json)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}
