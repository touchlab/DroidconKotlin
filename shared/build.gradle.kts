plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("com.android.library")
    id("com.squareup.sqldelight")
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

kotlin {
    android()
    ios()
    iosSimulatorArm64()

    version = "1.0"

    sourceSets {
        val commonMain by getting {
            dependencies {
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
                implementation(libs.sqldelight.driver.android)
                implementation(libs.kotlinx.coroutines.android)
                implementation(libs.ktor.client.okhttp)
                implementation(libs.androidx.core)
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
                implementation(libs.sqldelight.driver.ios)
                implementation(libs.ktor.client.ios)
            }
        }
        val iosTest by getting {}

        sourceSets["iosSimulatorArm64Main"].dependsOn(iosMain)
        sourceSets["iosSimulatorArm64Test"].dependsOn(iosTest)
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

sqldelight {
    database("DroidconDatabase") {
        packageName = "co.touchlab.droidcon.db"
    }
}
