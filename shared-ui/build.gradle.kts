import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetTree

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.serialization)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrains.compose)
}

android {
    namespace = "co.touchlab.droidcon.sharedui"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    lint {
        targetSdk = libs.versions.targetSdk.get().toInt()
        warningsAsErrors = false
        abortOnError = true
    }

    sourceSets {
        val main by getting
        main.java.setSrcDirs(listOf("src/androidMain/kotlin"))
        main.res.setSrcDirs(listOf("src/androidMain/res"))
        main.resources.setSrcDirs(
            listOf(
                "src/androidMain/resources",
                "src/commonMain/resources",
            )
        )
        main.manifest.srcFile("src/androidMain/AndroidManifest.xml")
    }
}

version = "1.0"

android {
    configurations {
        create("androidTestApi")
        create("androidTestDebugApi")
        create("androidTestReleaseApi")
        create("testApi")
        create("testDebugApi")
        create("testReleaseApi")
    }
}

compose {
    /*android {
        useAndroidX = true
        androidxVersion = "1.3.0"
    }*/
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        unitTestVariant.sourceSetTree.set(KotlinSourceSetTree.test)
    }
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    version = "1.0"

    sourceSets {
        commonMain.dependencies {
            implementation(project(":shared"))

            api(libs.kermit)
            api(libs.kotlinx.coroutines.core)
            api(libs.kotlinx.datetime)
            api(libs.multiplatformSettings.core)
            api(libs.uuid)
            implementation(libs.coil.compose)
            implementation(libs.coil.network)

            implementation(libs.bundles.ktor.common)
            implementation(libs.bundles.sqldelight.common)

            implementation(libs.stately.common)
            implementation(libs.koin.core)

            implementation(compose.ui)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            // Moved from implementation to api due to below issue
            // https://issuetracker.google.com/issues/294869453
            // https://github.com/JetBrains/compose-multiplatform/issues/3927
            api(compose.runtime)

            implementation(libs.hyperdrive.multiplatformx.api)
            // implementation(libs.hyperdrive.multiplatformx.compose)
        }
        iosMain.dependencies {
            implementation(libs.imageLoader)
        }
        all {
            languageSettings.apply {
                optIn("kotlin.RequiresOptIn")
                optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
            }
        }
        matching { it.name.endsWith("Test") }.configureEach {
            languageSettings.optIn("kotlin.time.ExperimentalTime")
        }
    }
}
