import org.jetbrains.kotlin.gradle.plugin.mpp.Framework
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    kotlin("plugin.serialization")
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

    version = "1.0"

    sourceSets {
        all {
            languageSettings.apply {
                useExperimentalAnnotation("kotlin.RequiresOptIn")
                useExperimentalAnnotation("kotlinx.coroutines.ExperimentalCoroutinesApi")
            }
        }
    }

    sourceSets["commonMain"].dependencies {


//        implementation(Deps.SqlDelight.runtime)
//        implementation(Deps.SqlDelight.coroutinesExtensions)
//        implementation(Deps.Ktor.commonCore)
//        implementation(Deps.Ktor.commonJson)
//        implementation(Deps.Ktor.commonLogging)
//        implementation(Deps.Coroutines.common)
//        implementation(Deps.stately)
//        implementation(Deps.multiplatformSettings)
//        implementation(Deps.koinCore)
//        implementation(Deps.Ktor.commonSerialization)
//        implementation(Deps.kotlinxDateTime)
//        api(Deps.kermit)
    }

    sourceSets["commonTest"].dependencies {
//        implementation(Deps.multiplatformSettingsTest)
//        implementation(Deps.KotlinTest.common)
//        implementation(Deps.KotlinTest.annotations)
//        implementation(Deps.koinTest)
//        implementation(Deps.turbine)
    }

    sourceSets.matching { it.name.endsWith("Test") }
        .configureEach {
            languageSettings.useExperimentalAnnotation("kotlin.time.ExperimentalTime")
        }

    sourceSets["iosMain"].dependencies {
        api(project(":shared"))
        api(libs.kermit)
//        implementation(Deps.SqlDelight.driverIos)
//        implementation(Deps.Ktor.ios)
//
//        implementation(libs.kotlinx.coroutines.core) {
//            version {
//                strictly(libs.versions.kotlinx.coroutines.get())
//            }
//        }
    }

    cocoapods {
        summary = "Common library for the Droidcon app"
        homepage = "https://github.com/touchlab/DroidconKotlin"
        frameworkName = "DroidconKit"
        noPodspec()
    }

    // Configure the framework which is generated internally by cocoapods plugin
    targets.withType<KotlinNativeTarget> {
        binaries.withType<Framework> {
            isStatic = true
            linkerOpts.add("-lsqlite3")
            export(libs.kermit)
            export(project(":shared"))
            transitiveExport = true
        }
    }
}

