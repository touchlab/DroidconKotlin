plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    val androidMinSdk: String by project
    val androidCompileSdk: String by project
    val androidTargetSdk: String by project

    compileSdk = androidCompileSdk.toInt()
    defaultConfig {
        applicationId = "co.touchlab.droidcon"
        minSdk = androidMinSdk.toInt()
        targetSdk = androidTargetSdk.toInt()
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    packagingOptions {
        resources.excludes.add("META-INF/*.kotlin_module")
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
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

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.get()
    }
}

dependencies {
    implementation(project(":shared"))

    implementation(libs.bundles.androidx.compose)
    implementation(libs.koin.core)
    implementation(libs.koin.android)
//    implementation(Deps.material)
//    coreLibraryDesugaring(Deps.desugarJdkLibs)
//    implementation(Deps.AndroidX.appcompat)
//    implementation(Deps.AndroidX.core_ktx)
//    implementation(Deps.Ktor.androidCore)
//    implementation(Deps.SqlDelight.runtimeJdk)
//    implementation(Deps.SqlDelight.driverAndroid)
//    implementation(Deps.Coroutines.common)
//    implementation(Deps.Coroutines.android)
//    implementation(Deps.multiplatformSettings)
//    implementation(Deps.koinCore)
//    implementation(Deps.koinAndroid)
//    implementation(Deps.AndroidX.lifecycle_runtime)
//    implementation(Deps.AndroidX.lifecycle_viewmodel)
//    implementation(Deps.AndroidX.lifecycle_viewmodel_extensions)
//
//    implementation(Deps.Compose.activityCompose)
//    implementation(Deps.Compose.ui)
//    // Tooling support (Previews, etc.)
//    implementation(Deps.Compose.uiTooling)
//    // Foundation (Border, Background, Box, Image, Scroll, shapes, animations, etc.)
//    implementation(Deps.Compose.foundation)
//    // Material Design
//    implementation(Deps.Compose.material)
//    implementation(Deps.Compose.Accompanist.swipeRefresh)
//
//    testImplementation(Deps.junit)
}
