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
        commonMain.dependencies {
            implementation(projects.shared)
            implementation(projects.sharedUi)
            implementation(libs.koin.core)
            implementation(compose.ui)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(libs.multiplatformSettings.core)
            implementation(libs.multiplatform.settings.make.observable)
            implementation(libs.koin.compose)
            implementation("androidx.lifecycle:lifecycle-viewmodel: 2.10.0")
        }
        commonTest.dependencies {
            implementation("org.jetbrains.kotlin:kotlin-test:2.2.21")
            //implementation(libs.kotlin.test)
            implementation(libs.koin.test)
        }
    }
}
