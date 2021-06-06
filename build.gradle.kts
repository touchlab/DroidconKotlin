buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven(url = "https://maven.fabric.io/public")
    }

    dependencies {
        classpath(Deps.Android.plugin)
        classpath(Deps.Kotlin.plugin)
        classpath(Deps.Serialization.plugin)
        classpath(Deps.Android.google_services)
        classpath(Deps.SqlDelight.plugin)
        classpath(Deps.xcodesync)
        classpath(Deps.fabric_plugin)
        classpath(Deps.Android.Navigation.plugin)
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://dl.bintray.com/russhwolf/multiplatform-settings")
    }
}
