import org.jetbrains.kotlin.gradle.plugin.mpp.BitcodeEmbeddingMode
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.serialization)
    alias(libs.plugins.jetbrains.compose)
}

version = "1.0"

kotlin {
    val targets = listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    )

    applyDefaultHierarchyTemplate()

    sourceSets {
        all {
            languageSettings.apply {
                optIn("kotlin.RequiresOptIn")
                optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
            }
        }
        iosMain.dependencies {
            implementation(compose.ui)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.runtime)

            api(project(":shared"))
            api(project(":shared-ui"))
            api(libs.kermit)
            api(libs.kermit.simple)
            api(libs.hyperdrive.multiplatformx.api)
        }

        matching { it.name.endsWith("Test") }
            .configureEach {
                languageSettings.optIn("kotlin.time.ExperimentalTime")
            }
    }

    targets.forEach { iosTarget ->
        iosTarget.binaries {
            framework {
                baseName = "DroidconKit"
                isStatic = true
                embedBitcodeMode = BitcodeEmbeddingMode.DISABLE

                freeCompilerArgs += listOf(
                    "-linker-option", "-framework", "-linker-option", "Metal",
                    "-linker-option", "-framework", "-linker-option", "CoreText",
                    "-linker-option", "-framework", "-linker-option", "CoreGraphics",
                    "-Xdisable-phases=VerifyBitcode"
                )
                linkerOpts.add("-lsqlite3")
                export(libs.kermit)
                export(libs.kermit.simple)
                export(libs.hyperdrive.multiplatformx.api)
                export(project(":shared"))
                export(project(":shared-ui"))
            }
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
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinNativeCompile> {
        (compilerPluginClasspath as? Configuration)?.isTransitive = true
    }
}
