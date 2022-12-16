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
    // TODO Uncomment (and remove the hardcoded dependencies) once the project dependency management is migrated to toml
    // implementation(libs.plugin.kotlin.gradle)
    // implementation(libs.plugin.kotlin.gradle.api)
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin-api:1.7.20")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.20")
}

gradlePlugin {
    plugins.register("MultiplatformResources") {
        id = "multiplatform-resources"
        implementationClass = "co.touchlab.droidcon.gradle.MultiplatformResourcesPlugin"
    }
}
