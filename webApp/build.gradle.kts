plugins {
    kotlin("js")
    id("org.jetbrains.compose")
    alias(libs.plugins.composeCompiler)
    id("de.comahe.i18n4k") version "0.10.0"
}

version = "1.0"

kotlin {

    js(IR) {
        moduleName = "droidcon"
        browser {
            commonWebpackConfig {
                outputFileName = "droidcon.js"
            }
        }
        binaries.executable()
        nodejs()
    }

    version = "1.0"
}
dependencies {
    api(projects.shared)
    api(projects.sharedUi)
    implementation(libs.multiplatformSettings.makeobservable)
    implementation(libs.koin.core)
    implementation(compose.runtime)
    implementation(compose.foundation)
    implementation(compose.material)
    implementation(compose.ui)
    implementation(libs.firebase.auth)
    implementation(libs.gitlive.firebase.analytics)
    implementation(libs.i18n4k.core.js)
}

i18n4k {
    sourceCodeLocales = listOf("en")
}
