@file:Suppress("UnstableApiUsage")

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }

    includeBuild("build-setup")

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

include(":shared", ":shared-ui", ":android", ":ios")

rootProject.name = "Droidcon"
