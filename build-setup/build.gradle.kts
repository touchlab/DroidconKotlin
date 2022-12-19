plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(gradleKotlinDsl())
    implementation(libs.gradle.plugin)
    implementation(libs.gradle.plugin.api)
}

gradlePlugin {
    plugins.register("MultiplatformResources") {
        id = "multiplatform-resources"
        implementationClass = "co.touchlab.droidcon.gradle.MultiplatformResourcesPlugin"
    }
}
