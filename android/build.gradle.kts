import java.util.Properties

plugins {
    id("com.android.application")
    kotlin("android")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}
val releaseEnabled = file("./release.jks").exists()

val properties = Properties()
try {

    properties.load(project.rootProject.file("local.properties").bufferedReader())
} catch (e: Exception) {
}

val releasePassword = properties.getProperty("releasePassword", "")

android {
    val androidMinSdk = libs.versions.minSdk.get()
    val androidCompileSdk = libs.versions.compileSdk.get()
    val androidTargetSdk = libs.versions.targetSdk.get()

    namespace = "co.touchlab.droidcon"
    compileSdk = androidCompileSdk.toInt()

    defaultConfig {
        applicationId = "co.touchlab.droidcon.london"
        minSdk = androidMinSdk.toInt()
        targetSdk = androidTargetSdk.toInt()
        versionCode = 20201
        versionName = "2.2.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    packagingOptions {
        resources.excludes.add("META-INF/*.kotlin_module")
    }
    if (releaseEnabled) {
        signingConfigs {
            create("release") {
                keyAlias = "key0"
                keyPassword = releasePassword
                storeFile = file("./release.jks")
                storePassword = releasePassword
            }
        }
    }
    buildTypes {
        if (releaseEnabled) {
            getByName("release") {
                isMinifyEnabled = false
                proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
                signingConfig = signingConfigs.getByName("release")
            }
        }
    }
    compileOptions {
        // Flag to enable support for the new language APIs
        isCoreLibraryDesugaringEnabled = true

        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    lint {
        warningsAsErrors = true
        abortOnError = true
    }

    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }

    packagingOptions {
        resources.excludes.add("META-INF/*.kotlin_module")
    }
}

kotlin {
    jvmToolchain(libs.versions.java.get().toInt())
}

dependencies {
    implementation(project(":shared"))
    implementation(project(":shared-ui"))

    implementation(libs.androidx.core.splashscreen)
    implementation(libs.koin.core)
    implementation(libs.koin.android)
    implementation(libs.kotlinx.datetime)
    implementation(libs.accompanist.coil)
    implementation(libs.accompanist.navigationAnimation)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)

    implementation(libs.hyperdrive.multiplatformx.api)

    implementation(libs.bundles.androidx.compose)

    coreLibraryDesugaring(libs.android.desugar)
}
