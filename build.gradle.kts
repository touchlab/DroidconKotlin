// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.google.firebase.crashlytics") version libs.versions.firebase.crashlytics.gradle.get() apply false
    id("com.google.gms.google-services") version libs.versions.gms.google.services.get() apply false
    id("com.android.library") version libs.versions.android.gradle.plugin.get() apply false

    val kotlinVersion = libs.versions.kotlin.get()

    kotlin("multiplatform") version kotlinVersion apply false
    kotlin("android") version kotlinVersion apply false
    kotlin("plugin.serialization") version kotlinVersion apply false
    kotlin("native.cocoapods") version kotlinVersion apply false
    id("app.cash.sqldelight") version libs.versions.sqlDelight.get() apply false
    id("org.jlleitschuh.gradle.ktlint") version "11.0.0" apply false
    id("org.jetbrains.compose") version libs.versions.compose.jb.get() apply false
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://oss.sonatype.org/content/repositories/snapshots/")
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

subprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    // ktlint {
    //     version.set("0.37.2")
    //     enableExperimentalRules.set(true)
    //     verbose.set(true)
    //     filter {
    //         exclude { it.file.path.contains("build/") }
    //     }
    // }

    afterEvaluate {
        tasks.named("check") {
            dependsOn(tasks.getByName("ktlintCheck"))
        }
    }
}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}
