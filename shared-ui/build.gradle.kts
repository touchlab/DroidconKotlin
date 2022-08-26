plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("com.android.library")

    id("org.jetbrains.compose") version "1.2.0-alpha01-dev770"
}

android {
    val androidMinSdk: String by project
    val androidCompileSdk: String by project
    val androidTargetSdk: String by project

    compileSdk = androidCompileSdk.toInt()
    defaultConfig {
        minSdk = androidMinSdk.toInt()
        targetSdk = androidTargetSdk.toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    lint {
        isWarningsAsErrors = true
        isAbortOnError = true
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
    android {
        useAndroidX = true
        androidxVersion = "1.2.1"
    }
}

kotlin {
    android()
    ios()
    iosSimulatorArm64()

    version = "1.0"

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":shared"))

                api(libs.kermit)
                api(libs.kermit.crashlytics)
                api(libs.kotlinx.coroutines.core)
                api(libs.kotlinx.datetime)
                api(libs.multiplatformSettings.core)
                api(libs.uuid)

                implementation(libs.bundles.ktor.common)
                implementation(libs.bundles.sqldelight.common)

                implementation(libs.stately.common)
                implementation(libs.koin.core)

                implementation(compose.ui)
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(compose.runtime)

                implementation(libs.hyperdrive.multiplatformx.api)
                // implementation(libs.hyperdrive.multiplatformx.compose)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.multiplatformSettings.test)
                implementation(libs.kotlin.test.common)
                implementation(libs.koin.test)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(libs.accompanist.coil)
            }
        }
        val androidTest by getting {
            dependencies {
                implementation(libs.test.junit)
                implementation(libs.test.junitKtx)
                implementation(libs.test.coroutines)
            }
        }
        val iosMain by getting {
            dependencies {
                implementation(libs.imageLoader)
            }
        }
        val iosTest by getting {}

        // TODO: Uncomment when image-loader dependency adds support for iosSimulatorArm64
        // sourceSets["iosSimulatorArm64Main"].dependsOn(iosMain)
        // sourceSets["iosSimulatorArm64Test"].dependsOn(iosTest)
    }

    sourceSets.all {
        languageSettings.apply {
            optIn("kotlin.RequiresOptIn")
            optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
        }
    }

    sourceSets.matching { it.name.endsWith("Test") }.configureEach {
        languageSettings.optIn("kotlin.time.ExperimentalTime")
    }

    // Enable concurrent sweep phase in new native memory manager. (This will be enabled by default in 1.7.0)
    // https://kotlinlang.org/docs/whatsnew1620.html#concurrent-implementation-for-the-sweep-phase-in-new-memory-manager
    // (This might not be necessary here since the other module creates the framework, but it's here as well just in case it matters for
    //  test binaries)
    targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget> {
        binaries.all {
            freeCompilerArgs += "-Xgc=cms"
        }
    }
}
