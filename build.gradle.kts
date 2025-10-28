import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.crashlytics).apply(false)
    alias(libs.plugins.googleServices).apply(false)
    alias(libs.plugins.kotlinMultiplatform).apply(false)
    alias(libs.plugins.androidLibrary).apply(false)
    alias(libs.plugins.sqlDelight).apply(false)
    alias(libs.plugins.jetbrainsCompose).apply(false)
    alias(libs.plugins.composeCompiler).apply(false)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.serialization).apply(false)
    alias(libs.plugins.skie).apply(false)
}

subprojects {
    apply(plugin = rootProject.libs.plugins.ktlint.get().pluginId)

    afterEvaluate {
        tasks.named("check") {
            dependsOn(tasks.getByName("ktlintCheck"))
        }
    }

    tasks.withType(KotlinCompile::class).all {
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }
    configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
        version.set("1.4.0")
        enableExperimentalRules.set(true)
        verbose.set(true)
        filter {
            exclude { it.file.path.contains("build/") }
        }
    }
}

tasks.register<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}
