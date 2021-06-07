import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.android.application")
    kotlin("multiplatform")
    kotlin("android.extensions")
    id("androidx.navigation.safeargs")
}

val firebaseEnabled = project.isFirebaseEnabled()

android {
    compileSdk = BuildConfig.compileSdk

    defaultConfig {
        applicationId = "co.touchlab.droidconsf2018"
        buildConfigField("String", "TIME_ZONE", "\"America/Los_Angeles\"")
        buildConfigField("boolean", "FIREBASE_ENABLED", "$firebaseEnabled")

        minSdk = BuildConfig.minSdk
        targetSdk = BuildConfig.targetSdk
        versionCode = 20000
        versionName = "2.0.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    val releaseEnabled = project.isReleaseEnabled()
    if (releaseEnabled) {
        val releasePassword = project.signingKeyPassword()
        signingConfigs {
            getByName("release") {
                storeFile = file("release.jks")
                keyAlias = "key0"
                storePassword = releasePassword
                keyPassword = releasePassword
            }
        }
    }

    buildTypes {
        if (releaseEnabled) {
            release {
                isMinifyEnabled = false
                proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
                signingConfig = signingConfigs.getByName("release")
            }
        }

        //This is for MultiplatformSettings
        debug {
            // MPP libraries don't currently get this resolution automatically
            matchingFallbacks.addAll(listOf("release"))
        }
    }

    lint {
        isAbortOnError = false
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    tasks.withType(KotlinCompile::class.java).all {
        kotlinOptions {
            jvmTarget = "11"
            useIR = true
        }
    }
}

kotlin {
    android()

    sourceSets {
        commonMain {
            dependencies {}
        }
    }
}

dependencies {
    coreLibraryDesugaring(Deps.Desugar.desugar_libs)

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    implementation(project(":sessionize:lib"))

    implementation(Deps.Android.flow_layout)
    implementation(Deps.Android.Lifecycle.extensions)
    implementation(Deps.Android.Lifecycle.viewmodel)
    implementation(Deps.Android.Lifecycle.common)

    implementation(Deps.Android.appcompat)
    implementation(Deps.Android.material)
    implementation(Deps.Android.constraintlayout)
    implementation(Deps.Android.vector_drawable)

    implementation(Deps.Android.circle_imageview)
    implementation(Deps.Android.picasso)

    implementation(Deps.SqlDelight.runtimeJdk)
    implementation(Deps.SqlDelight.driverAndroid)
    implementation(Deps.Kotlin.Coroutines.android)
    implementation(Deps.Firebase.androidCore)
    implementation(Deps.Firebase.androidMessaging)
    implementation(Deps.crashlytics)

    testImplementation(Deps.Android.Test.junit)
    androidTestImplementation(Deps.Android.Test.runner)
    androidTestImplementation(Deps.Android.Test.Espresso.core)
    implementation(Deps.multiplatformSettings)

    implementation(Deps.Android.Navigation.fragment)
    implementation(Deps.Android.Navigation.navigation_ui)
    implementation(Deps.Firebase.firestoreAndroid)
}

if (firebaseEnabled) {
    apply(mapOf(
            "plugin" to "com.google.gms.google-services",
            "plugin" to "io.fabric"
    ))
}