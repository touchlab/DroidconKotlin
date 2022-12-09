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
    plugins {
        kotlin("multiplatform") version kotlinVersion
        kotlin("android") version kotlinVersion
        kotlin("plugin.serialization") version kotlinVersion
        kotlin("native.cocoapods") version kotlinVersion
        id("com.squareup.sqldelight") version sqldelightVersion
        id("org.jlleitschuh.gradle.ktlint") version "11.0.0"
        id("org.jetbrains.compose") version "1.2.0"
        id("de.undercouch.download") version "5.3.0"
        id("com.github.gmazzo.buildconfig") version "3.1.0"
    }

    resolutionStrategy {
        eachPlugin {
            if (requested.id.namespace == "com.android" || requested.id.id == "android-gradle") {
                useModule("com.android.tools.build:gradle:7.2.1")
            }

            if (requested.id.id == "com.google.gms.google-services") {
                useModule("com.google.gms:google-services:4.3.14")
            }

            if (requested.id.id == "com.google.firebase.crashlytics") {
                useModule("com.google.firebase:firebase-crashlytics-gradle:2.9.2")
            }
        }
    }
}

enableFeaturePreview("VERSION_CATALOGS")

dependencyResolutionManagement {
    val kotlinVersion: String by settings
    val sqldelightVersion: String by settings

    versionCatalogs {
        create("libs") {
            val kotlinRef = version("kotlin", kotlinVersion)
            val sqldelightRef = version("sqldelight", sqldelightVersion)
            val composeRef = version("compose", "1.3.0")
            val composeCompilerRef = version("compose-compiler", "1.3.2")
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
            val hyperdriveRef = version("hyperdrive", "0.1.139")
            val accompanistCoilRef = version("accompanistCoil", "0.15.0")
            val accompanistNavigationAnimationRef = version("accompanistNavigationAnimation", "0.27.0")
            val coreRef = version("androidx-core", "1.9.0")
            val firebaseAnalyticsRef = version("firebase-analytics", "21.2.0")
            val firebaseCrashlyticsRef = version("firebase-crashlytics", "18.3.1")
            val uuidRef = version("uuid", "0.5.0")
            val junitRef = version("junit", "4.13.2")
            val junitKtxRef = version("junitKtx", "1.1.3")
            val coroutinesTestRef = version("coroutinesTest", "1.6.0-native-mt")
            val imageLoaderRef = version("imageLoader", "1.2.2.1")
            val korioRef = version("korio", "3.3.1")

            alias("kotlin-test-common").to("org.jetbrains.kotlin", "kotlin-test-common").versionRef(kotlinRef)
            alias("android-desugar").to("com.android.tools", "desugar_jdk_libs").version("1.1.5")
            alias("atomicFu").to("org.jetbrains.kotlinx", "atomicfu").version("0.18.3")

            alias("androidx-compose-ui-core").to("androidx.compose.ui", "ui").versionRef(composeRef)
            alias("androidx-compose-ui-tooling").to("androidx.compose.ui", "ui-tooling").versionRef(composeRef)
            alias("androidx-compose-foundation").to("androidx.compose.foundation", "foundation").versionRef(composeRef)
            alias("androidx-compose-material").to("androidx.compose.material", "material").versionRef(composeRef)
            alias("androidx-compose-activity").to("androidx.activity", "activity-compose").versionRef(composeActivityRef)
            alias("androidx-compose-navigation").to("androidx.navigation", "navigation-compose").versionRef(composeNavigationRef)

            alias("androidx-core-splashscreen").to("androidx.core", "core-splashscreen").versionRef(splashcreenRef)

            alias("kotlinx-coroutines-core").to("org.jetbrains.kotlinx", "kotlinx-coroutines-core").versionRef(coroutinesRef)
            alias("kotlinx-coroutines-android").to("org.jetbrains.kotlinx", "kotlinx-coroutines-android").versionRef(coroutinesRef)
            alias("kotlinx-datetime").to("org.jetbrains.kotlinx", "kotlinx-datetime").versionRef(datetimeRef)

            alias("koin-core").to("io.insert-koin", "koin-core").versionRef(koinRef)
            alias("koin-android").to("io.insert-koin", "koin-android").versionRef(koinRef)
            alias("koin-test").to("io.insert-koin", "koin-test").versionRef(koinRef)

            alias("kermit").to("co.touchlab", "kermit").versionRef(kermitRef)
            alias("kermit-crashlytics").to("co.touchlab", "kermit-crashlytics").versionRef(kermitRef)
            alias("stately-common").to("co.touchlab", "stately-common").versionRef(statelyRef)

            alias("sqldelight-runtime").to("com.squareup.sqldelight", "runtime").versionRef(sqldelightRef)
            alias("sqldelight-coroutines").to("com.squareup.sqldelight", "coroutines-extensions").versionRef(sqldelightRef)
            alias("sqldelight-driver-ios").to("com.squareup.sqldelight", "native-driver").versionRef(sqldelightRef)
            alias("sqldelight-driver-android").to("com.squareup.sqldelight", "android-driver").versionRef(sqldelightRef)

            alias("ktor-client-core").to("io.ktor", "ktor-client-core").versionRef(ktorRef)
            alias("ktor-client-json").to("io.ktor", "ktor-client-json").versionRef(ktorRef)
            alias("ktor-client-logging").to("io.ktor", "ktor-client-logging").versionRef(ktorRef)
            alias("ktor-client-serialization").to("io.ktor", "ktor-client-serialization").versionRef(ktorRef)
            alias("ktor-client-okhttp").to("io.ktor", "ktor-client-okhttp").versionRef(ktorRef)
            alias("ktor-client-ios").to("io.ktor", "ktor-client-ios").versionRef(ktorRef)

            alias("multiplatformSettings-core").to("com.russhwolf", "multiplatform-settings").versionRef(multiplatformSettingsRef)
            alias("multiplatformSettings-test").to("com.russhwolf", "multiplatform-settings-test").versionRef(multiplatformSettingsRef)
            alias("accompanist-coil").to("com.google.accompanist", "accompanist-coil").versionRef(accompanistCoilRef)
            alias("accompanist-navigationAnimation").to("com.google.accompanist", "accompanist-navigation-animation").versionRef(accompanistNavigationAnimationRef)

            alias("hyperdrive-multiplatformx-api").to("org.brightify.hyperdrive", "multiplatformx-api").versionRef(hyperdriveRef)
            alias("hyperdrive-multiplatformx-compose").to("org.brightify.hyperdrive", "multiplatformx-compose").versionRef(hyperdriveRef)

            alias("androidx-core").to("androidx.core", "core-ktx").versionRef(coreRef)

            alias("firebase-analytics").to("com.google.firebase", "firebase-analytics-ktx").versionRef(firebaseAnalyticsRef)
            alias("firebase-crashlytics").to("com.google.firebase", "firebase-crashlytics-ktx").versionRef(firebaseCrashlyticsRef)

            alias("uuid").to("com.benasher44", "uuid").versionRef(uuidRef)

            alias("imageLoader").to("io.github.qdsfdhvh", "image-loader").versionRef(imageLoaderRef)

            alias("korio").to("com.soywiz.korlibs.korio", "korio").versionRef(korioRef)

            alias("test-junit").to("junit", "junit").versionRef(junitRef)
            alias("test-junitKtx").to("androidx.test.ext", "junit-ktx").versionRef(junitKtxRef)
            alias("test-coroutines").to("org.jetbrains.kotlinx", "kotlinx-coroutines-test").versionRef(coroutinesTestRef)

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
