// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("android-gradle") apply false
    id("com.google.firebase.crashlytics") apply false
    kotlin("multiplatform") apply false
    id("org.jlleitschuh.gradle.ktlint")
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://oss.sonatype.org/content/repositories/snapshots/")
        maven("https://androidx.dev/storage/compose-compiler/repository/") // TODO Remove this once we're back on release compose version
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
