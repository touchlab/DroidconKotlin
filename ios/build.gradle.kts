import org.jetbrains.kotlin.gradle.plugin.mpp.Framework
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    kotlin("plugin.serialization")
    id("co.touchlab.faktory") version "0.8.13"
}

faktory {
    faktoryReadKey.set("285E7757D9384C5EA5CC175816")
    cocoapods()
}

version = "1.0"

kotlin {
    // Revert to just ios() when gradle plugin can properly resolve it
    val onPhone = System.getenv("SDK_NAME")?.startsWith("iphoneos") ?: false
    if (onPhone) {
        iosArm64("ios")
    } else {
        iosX64("ios")
    }

    sourceSets {
        all {
            languageSettings.apply {
                useExperimentalAnnotation("kotlin.RequiresOptIn")
                useExperimentalAnnotation("kotlinx.coroutines.ExperimentalCoroutinesApi")
            }
        }
    }

    sourceSets.matching { it.name.endsWith("Test") }
        .configureEach {
            languageSettings.useExperimentalAnnotation("kotlin.time.ExperimentalTime")
        }

    sourceSets["iosMain"].dependencies {
        api(project(":shared"))
        api(libs.kermit)
        api(libs.hyperdrive.multiplatformx.api)
    }

    cocoapods {
        summary = "Common library for the Droidcon app"
        homepage = "https://github.com/touchlab/DroidconKotlin"
        // TODO we can't set name here until 1.6.20, and Faktory seems to be pulling us down to 1.6.10
        // name = "DroidconKit"
        framework {
            isStatic = true
        }
    }

    // Configure the framework which is generated internally by cocoapods plugin
    targets.withType<KotlinNativeTarget> {
        binaries.withType<Framework> {
            linkerOpts.add("-lsqlite3")
            export(libs.kermit)
            export(libs.hyperdrive.multiplatformx.api)
            export(project(":shared"))
        }
    }

    // Enable concurrent sweep phase in new native memory manager. (This will be enabled by default in 1.7.0)
    // https://kotlinlang.org/docs/whatsnew1620.html#concurrent-implementation-for-the-sweep-phase-in-new-memory-manager
    targets.withType<KotlinNativeTarget> {
        binaries.all {
            freeCompilerArgs += "-Xgc=cms"
        }
    }
}

