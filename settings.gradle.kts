@file:Suppress("UnstableApiUsage")

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }

    val kotlinVersion: String by settings
    val sqldelightVersion: String by settings
    val composeVersion: String by settings
    plugins {
        kotlin("multiplatform") version kotlinVersion
        kotlin("android") version kotlinVersion
        kotlin("plugin.serialization") version kotlinVersion
        kotlin("native.cocoapods") version kotlinVersion
        id("com.android.application") version "8.1.0"
        id("com.android.library") version "8.1.0"
        id("com.squareup.sqldelight") version sqldelightVersion
        id("org.jlleitschuh.gradle.ktlint") version "11.0.0"
        id("org.jetbrains.compose") version composeVersion
    }

    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "com.google.gms.google-services") {
                useModule("com.google.gms:google-services:4.3.14")
            }

            if (requested.id.id == "com.google.firebase.crashlytics") {
                useModule("com.google.firebase:firebase-crashlytics-gradle:2.9.2")
            }
        }
    }
}

dependencyResolutionManagement {
    val kotlinVersion: String by settings
    val sqldelightVersion: String by settings
    val composeVersion: String by settings

    versionCatalogs {
        create("libs") {
            val kotlinRef = version("kotlin", kotlinVersion)
            val sqldelightRef = version("sqldelight", sqldelightVersion)
            val composeRef = version("compose", composeVersion)
            val composeCompilerRef = version("compose-compiler", composeVersion)
            val composeActivityRef = version("composeActivity", "1.6.1")
            val composeNavigationRef = version("composeNavigation", "2.5.3")
            val splashcreenRef = version("splashscreen", "1.0.0")
            val coroutinesRef = version("kotlinx-coroutines", "1.6.4")
            val datetimeRef = version("kotlinx-datetime", "0.4.0")
            val serializationRef = version("kotlinx-serialization", "1.4.1")
            val koinRef = version("koin", "3.2.0")
            val kermitRef = version("kermit", "1.1.3")
            val statelyRef = version("stately", "1.2.2")
            val ktorRef = version("ktor", "2.1.3")
            val multiplatformSettingsRef = version("multiplatformSettings", "0.9")
            val hyperdriveRef = version("hyperdrive", "0.1.148")
            val accompanistCoilRef = version("accompanistCoil", "0.15.0")
            val accompanistNavigationAnimationRef = version("accompanistNavigationAnimation", "0.27.0")
            val coreRef = version("androidx-core", "1.9.0")
            val firebase = version("firebase", "32.3.1")
            val firebaseAnalyticsRef = version("firebase-analytics", "21.2.0")
            val firebaseCrashlyticsRef = version("firebase-crashlytics", "18.3.1")
            val uuidRef = version("uuid", "0.5.0")
            val junitRef = version("junit", "4.13.2")
            val junitKtxRef = version("junitKtx", "1.1.3")
            val coroutinesTestRef = version("coroutinesTest", "1.6.0-native-mt")
            val imageLoaderRef = version("imageLoader", "1.2.2.1")
            val korioRef = version("korio", "3.3.1")
            val javaRef = version("java", "18")

            library("kotlin-test-common", "org.jetbrains.kotlin", "kotlin-test-common").versionRef(kotlinRef)
            library("android-desugar", "com.android.tools", "desugar_jdk_libs").version("1.1.5")
            library("atomicFu", "org.jetbrains.kotlinx", "atomicfu").version("0.18.3")

            library("androidx-compose-ui-core", "androidx.compose.ui", "ui").versionRef(composeRef)
            library("androidx-compose-ui-tooling", "androidx.compose.ui", "ui-tooling").versionRef(composeRef)
            library("androidx-compose-foundation", "androidx.compose.foundation", "foundation").versionRef(composeRef)
            library("androidx-compose-material", "androidx.compose.material", "material").versionRef(composeRef)
            library("androidx-compose-activity", "androidx.activity", "activity-compose").versionRef(composeActivityRef)
            library("androidx-compose-navigation", "androidx.navigation", "navigation-compose").versionRef(composeNavigationRef)

            library("androidx-core-splashscreen", "androidx.core", "core-splashscreen").versionRef(splashcreenRef)

            library("kotlinx-coroutines-core", "org.jetbrains.kotlinx", "kotlinx-coroutines-core").versionRef(coroutinesRef)
            library("kotlinx-coroutines-android", "org.jetbrains.kotlinx", "kotlinx-coroutines-android").versionRef(coroutinesRef)
            library("kotlinx-datetime", "org.jetbrains.kotlinx", "kotlinx-datetime").versionRef(datetimeRef)

            library("koin-core", "io.insert-koin", "koin-core").versionRef(koinRef)
            library("koin-android", "io.insert-koin", "koin-android").versionRef(koinRef)
            library("koin-test", "io.insert-koin", "koin-test").versionRef(koinRef)

            library("kermit", "co.touchlab", "kermit").versionRef(kermitRef)
            library("kermit-crashlytics", "co.touchlab", "kermit-crashlytics").versionRef(kermitRef)
            library("stately-common", "co.touchlab", "stately-common").versionRef(statelyRef)

            library("sqldelight-runtime", "com.squareup.sqldelight", "runtime").versionRef(sqldelightRef)
            library("sqldelight-coroutines", "com.squareup.sqldelight", "coroutines-extensions").versionRef(sqldelightRef)
            library("sqldelight-driver-ios", "com.squareup.sqldelight", "native-driver").versionRef(sqldelightRef)
            library("sqldelight-driver-android", "com.squareup.sqldelight", "android-driver").versionRef(sqldelightRef)

            library("ktor-client-core", "io.ktor", "ktor-client-core").versionRef(ktorRef)
            library("ktor-client-json", "io.ktor", "ktor-client-json").versionRef(ktorRef)
            library("ktor-client-logging", "io.ktor", "ktor-client-logging").versionRef(ktorRef)
            library("ktor-client-serialization", "io.ktor", "ktor-client-serialization").versionRef(ktorRef)
            library("ktor-client-okhttp", "io.ktor", "ktor-client-okhttp").versionRef(ktorRef)
            library("ktor-client-ios", "io.ktor", "ktor-client-ios").versionRef(ktorRef)

            library("multiplatformSettings-core", "com.russhwolf", "multiplatform-settings").versionRef(multiplatformSettingsRef)
            library("multiplatformSettings-test", "com.russhwolf", "multiplatform-settings-test").versionRef(multiplatformSettingsRef)
            library("accompanist-coil", "com.google.accompanist", "accompanist-coil").versionRef(accompanistCoilRef)
            library("accompanist-navigationAnimation", "com.google.accompanist", "accompanist-navigation-animation").versionRef(accompanistNavigationAnimationRef)

            library("hyperdrive-multiplatformx-api", "org.brightify.hyperdrive", "multiplatformx-api").versionRef(hyperdriveRef)
            library("hyperdrive-multiplatformx-compose", "org.brightify.hyperdrive", "multiplatformx-compose").versionRef(hyperdriveRef)

            library("androidx-core", "androidx.core", "core-ktx").versionRef(coreRef)

            library("firebase-bom", "com.google.firebase", "firebase-bom").versionRef(firebase)
            library("firebase-analytics", "com.google.firebase", "firebase-analytics-ktx").version("_")
            library("firebase-crashlytics", "com.google.firebase", "firebase-crashlytics-ktx").version("_")

            library("uuid", "com.benasher44", "uuid").versionRef(uuidRef)

            library("imageLoader", "io.github.qdsfdhvh", "image-loader").versionRef(imageLoaderRef)

            library("korio", "com.soywiz.korlibs.korio", "korio").versionRef(korioRef)

            library("test-junit", "junit", "junit").versionRef(junitRef)
            library("test-junitKtx", "androidx.test.ext", "junit-ktx").versionRef(junitKtxRef)
            library("test-coroutines", "org.jetbrains.kotlinx", "kotlinx-coroutines-test").versionRef(coroutinesTestRef)

            bundle(
                "androidx-compose",
                listOf(
                    "androidx-compose-ui-core",
                    "androidx-compose-ui-tooling",
                    "androidx-compose-foundation",
                    "androidx-compose-material",
                    "androidx-compose-activity",
                    "androidx-compose-navigation"
                )
            )
            bundle(
                "ktor-common",
                listOf(
                    "ktor-client-core",
                    "ktor-client-json",
                    "ktor-client-logging",
                    "ktor-client-serialization"
                )
            )
            bundle(
                "sqldelight-common",
                listOf(
                    "sqldelight-runtime",
                    "sqldelight-coroutines"
                )
            )
        }
    }
}

include(":shared", ":shared-ui", ":android", ":ios")

rootProject.name = "Droidcon"
