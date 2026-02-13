import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    js(IR) {
        browser()
        binaries.executable()
    }

    /*
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.executable()
    }*/

    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation(projects.shared)
                implementation(projects.sharedUi)
                implementation(libs.koin.core)
                implementation(compose.ui)
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(libs.multiplatformSettings.core)
                implementation(libs.multiplatform.settings.make.observable)
                implementation(libs.koin.compose)
                implementation(libs.hyperdrive.multiplatformx.api)

            }
        }
        val jsTest by getting {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-test:2.2.21")
                //implementation(libs.kotlin.test)
                implementation(libs.koin.test)
            }
        }
    }
}
