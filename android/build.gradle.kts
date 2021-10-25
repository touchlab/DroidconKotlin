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
}
catch(e:Exception) {

}

val releasePassword = properties.getProperty("releasePassword", "")

android {
    val androidMinSdk: String by project
    val androidCompileSdk: String by project
    val androidTargetSdk: String by project

    compileSdk = androidCompileSdk.toInt()
    defaultConfig {
        applicationId = "co.touchlab.droidcon.london"
        minSdk = androidMinSdk.toInt()
        targetSdk = androidTargetSdk.toInt()
        versionCode = 20001
        versionName = "2.0.1"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    packagingOptions {
        resources.excludes.add("META-INF/*.kotlin_module")
    }
    if(releaseEnabled) {
        signingConfigs {
            create("release") {
                keyAlias = "key0"
                keyPassword = "$releasePassword"
                storeFile = file("./release.jks")
                storePassword = "$releasePassword"
            }
        }
    }
    buildTypes {
        if(releaseEnabled) {
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
        isWarningsAsErrors = true
        isAbortOnError = true
    }

    buildFeatures {
        compose = true
    }

    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = listOf("-Xopt-in=kotlin.RequiresOptIn")
    }

    packagingOptions {
        exclude("META-INF/*.kotlin_module")
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }
}

dependencies {
    implementation(project(":shared"))

    implementation(libs.bundles.androidx.compose)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.koin.core)
    implementation(libs.koin.android)
    implementation(libs.kotlinx.datetime)
    implementation(libs.accompanist.coil)
    implementation(libs.accompanist.insets)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)

    coreLibraryDesugaring(libs.android.desugar)
}
