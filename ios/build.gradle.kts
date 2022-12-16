import org.jetbrains.kotlin.gradle.plugin.mpp.BitcodeEmbeddingMode
import org.jetbrains.kotlin.gradle.plugin.mpp.Framework
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    kotlin("plugin.serialization")
    id("org.jetbrains.compose")
    id("co.touchlab.crashkios.crashlyticslink") version "0.8.1"
    id("multiplatform-resources")
}

version = "1.0"

kotlin {
    ios()
    iosSimulatorArm64()
    sourceSets {
        all {
            languageSettings.apply {
                optIn("kotlin.RequiresOptIn")
                optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
            }
        }
    }

    sourceSets.matching { it.name.endsWith("Test") }
        .configureEach {
            languageSettings.optIn("kotlin.time.ExperimentalTime")
        }

    sourceSets["iosMain"].dependencies {
        implementation(compose.ui)
        implementation(compose.foundation)
        implementation(compose.material)
        implementation(compose.runtime)

        api(project(":shared"))
        api(project(":shared-ui"))
        api(libs.kermit)
    }
    sourceSets["iosSimulatorArm64Main"].dependsOn(sourceSets["iosMain"])
    sourceSets["iosSimulatorArm64Test"].dependsOn(sourceSets["iosTest"])

    cocoapods {
        summary = "Common library for the Droidcon app"
        homepage = "https://github.com/touchlab/DroidconKotlin"
        name = "DroidconKit"
        framework {
            baseName = "DroidconKit"
            isStatic = false
            embedBitcode = BitcodeEmbeddingMode.DISABLE

            freeCompilerArgs += listOf(
                "-linker-option", "-framework", "-linker-option", "Metal",
                "-linker-option", "-framework", "-linker-option", "CoreText",
                "-linker-option", "-framework", "-linker-option", "CoreGraphics",
                "-Xdisable-phases=VerifyBitcode"
            )
        }
    }

    // Configure the framework which is generated internally by cocoapods plugin
    targets.withType<KotlinNativeTarget> {
        binaries.withType<Framework> {
            linkerOpts.add("-lsqlite3")
            export(libs.kermit)
            export(libs.hyperdrive.multiplatformx.api)
            export(project(":shared"))
            export(project(":shared-ui"))
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

kotlin {
    targets.withType<KotlinNativeTarget> {
        binaries.all {
            // TODO: the current compose binary surprises LLVM, so disable checks for now.
            freeCompilerArgs += "-Xdisable-phases=VerifyBitcode"
        }
    }
}

afterEvaluate {
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinNativeCompile>() {
        (compilerPluginClasspath as? Configuration)?.isTransitive = true
    }
}

tasks.register("extractKotlinResourcesForXcode", Copy::class) {
    from(tasks.named("extractResourcesPodDebugFrameworkIosArm64"))
    into(layout.buildDirectory.dir("KotlinResourcesForXcode"))
}
