// Top-level build file where you can add configuration options common to all sub-projects/modules.
@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    kotlin("multiplatform") version libs.versions.kotlin.get() apply false
    kotlin("plugin.serialization") version (libs.versions.kotlin.get()) apply false
    id("android-gradle") apply false
    id("com.google.firebase.crashlytics") apply false
    id("org.jlleitschuh.gradle.ktlint") version libs.versions.ktlint.get()
    id("org.jetbrains.compose") version (libs.versions.jetbrainsCompose.get()) apply false
    id("app.cash.sqldelight") version (libs.versions.sqldelight.get()) apply false
    id("com.github.gmazzo.buildconfig") version (libs.versions.buildConfig.get()) apply false
    id("de.undercouch.download") version (libs.versions.download.get()) apply false
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://oss.sonatype.org/content/repositories/snapshots/")
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://androidx.dev/storage/compose-compiler/repository/")
    }
}

subprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    ktlint {
        version.set("0.37.2")
        enableExperimentalRules.set(true)
        verbose.set(true)
        filter {
            exclude { it.file.path.contains("build/") }
        }
    }

    afterEvaluate {
        tasks.named("check") {
            dependsOn(tasks.getByName("ktlintCheck"))
        }
    }
}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}
