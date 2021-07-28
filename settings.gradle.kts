@file:Suppress("UnstableApiUsage")

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }

    val kotlinVersion: String by settings
    val sqldelightVersion: String by settings
    plugins {
        kotlin("multiplatform") version kotlinVersion
        kotlin("android") version kotlinVersion
        kotlin("plugin.serialization") version kotlinVersion
        kotlin("native.cocoapods") version kotlinVersion
        id("com.squareup.sqldelight") version sqldelightVersion
        id("org.jlleitschuh.gradle.ktlint") version "10.1.0"
    }

    resolutionStrategy {
        eachPlugin {
            if (requested.id.namespace == "com.android" || requested.id.id == "android-gradle") {
                useModule("com.android.tools.build:gradle:7.0.0-rc01")
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
            val composeRef = version("compose", "1.0.0-rc02")
            val composeActivityRef = version("composeActivity", "1.3.0-rc01")
            val coroutinesRef = version("kotlinx-coroutines", "1.5.0-native-mt")
            val datetimeRef = version("kotlinx-datetime", "0.2.1")
            val serializationRef = version("kotlinx-serialization", "1.2.1")
            val koinRef = version("koin", "3.0.2")
            val kermitRef = version("kermit", "0.1.9")
            val statelyRef = version("stately", "1.1.7")
            val ktorRef = version("ktor", "1.6.0")
            val multiplatformSettingsRef = version("multiplatformSettings", "0.7.7")

            alias("kotlin-test-common").to("org.jetbrains.kotlin", "kotlin-test-common").versionRef(kotlinRef)

            alias("androidx-compose-ui-core").to("androidx.compose.ui", "ui").versionRef(composeRef)
            alias("androidx-compose-ui-tooling").to("androidx.compose.ui", "ui-tooling").versionRef(composeRef)
            alias("androidx-compose-foundation").to("androidx.compose.foundation", "foundation").versionRef(composeRef)
            alias("androidx-compose-material").to("androidx.compose.material", "material").versionRef(composeRef)
            alias("androidx-compose-activity").to("androidx.activity", "activity-compose").versionRef(composeActivityRef)

            alias("kotlinx-coroutines-core").to("org.jetbrains.kotlinx", "kotlinx-coroutines-core").versionRef(coroutinesRef)
            alias("kotlinx-coroutines-android").to("org.jetbrains.kotlinx", "kotlinx-coroutines-android").versionRef(coroutinesRef)
            alias("kotlinx-datetime").to("org.jetbrains.kotlinx", "kotlinx-datetime").versionRef(datetimeRef)

            alias("koin-core").to("io.insert-koin", "koin-core").versionRef(koinRef)
            alias("koin-android").to("io.insert-koin", "koin-android").versionRef(koinRef)
            alias("koin-test").to("io.insert-koin", "koin-test").versionRef(koinRef)

            alias("kermit").to("co.touchlab", "kermit").versionRef(kermitRef)
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

            bundle("androidx-compose", listOf(
                "androidx-compose-ui-core",
                "androidx-compose-ui-tooling",
                "androidx-compose-foundation",
                "androidx-compose-material",
                "androidx-compose-activity",
            ))
            bundle("ktor-common", listOf(
                "ktor-client-core",
                "ktor-client-json",
                "ktor-client-logging",
                "ktor-client-serialization",
            ))
            bundle("sqldelight-common", listOf(
                "sqldelight-runtime",
                "sqldelight-coroutines",
            ))
        }
    }
}

include(":shared", ":android", ":ios")
rootProject.name = "Droidcon"