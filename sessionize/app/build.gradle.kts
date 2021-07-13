plugins {
    id("com.android.application")
    kotlin("android")
    id("kotlin-android-extensions")
}

android {
    compileSdkVersion(Versions.compile_sdk)
    buildToolsVersion = Versions.buildToolsVersion
    defaultConfig {
        applicationId = "co.touchlab.droidconny2021"
        minSdkVersion(Versions.min_sdk)
        targetSdkVersion(Versions.target_sdk)
        versionCode = 30000
        versionName = "3.0.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "TIME_ZONE", "\"America/Los_Angeles\"")
        buildConfigField("boolean", "FIREBASE_ENABLED", "${false}")
    }
    packagingOptions {
        exclude("META-INF/*.kotlin_module")
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    lintOptions {
        isWarningsAsErrors = true
        isAbortOnError = true
    }

    buildFeatures {
        viewBinding = true
    }

    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
    }
}

dependencies {
    implementation(project(":sessionize:lib"))

    implementation("com.nex3z:flow-layout:1.2.2")
    implementation("androidx.lifecycle:lifecycle-extensions:2.0.0")
    annotationProcessor("androidx.lifecycle:lifecycle-compiler:2.0.0")

    implementation("androidx.appcompat:appcompat:1.0.2")
    implementation("com.google.android.material:material:1.0.0")
    implementation("androidx.constraintlayout:constraintlayout:1.1.3")
    implementation("androidx.vectordrawable:vectordrawable:1.0.1")

    implementation("de.hdodenhof:circleimageview:2.1.0")
    implementation("com.squareup.picasso:picasso:2.71828")

    implementation(Deps.SqlDelight.runtimeJdk)
            implementation(Deps.SqlDelight.driverAndroid)
            implementation(Deps.Coroutines.common)
            implementation(Deps.Coroutines.android)
//    implementation deps.kotlin.coroutines.android

//            implementation(deps.firebase.androidCore)
    implementation(platform("com.google.firebase:firebase-bom:28.2.1"))
            implementation("com.google.firebase:firebase-analytics-ktx")
            implementation(Deps.Firebase.androidMessaging)
            implementation(Deps.crashlytics)

            testImplementation("junit:junit:4.12")
            androidTestImplementation("androidx.test:runner:1.1.1")
            androidTestImplementation("androidx.test.espresso:espresso-core:3.1.1")
            implementation(Deps.multiplatformSettings)

            implementation(Deps.Navigation.fragment_ktx) //"androidx.navigation:navigation-fragment-ktx:2.1.0-alpha06"
            implementation(Deps.Navigation.ui_ktx) //"androidx.navigation:navigation-ui-ktx:2.1.0-alpha06"

    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.1.5")
//            implementation(deps.firebase.firestoreAndroid)




//    implementation(Deps.AndroidX.recyclerView)
//    implementation(Deps.AndroidX.swipeRefresh)
//    implementation(Deps.material)
//    coreLibraryDesugaring(Deps.desugarJdkLibs)
//    implementation(Deps.AndroidX.appcompat)
//    implementation(Deps.AndroidX.core_ktx)
//    implementation(Deps.Ktor.androidCore)
////    implementation(Deps.AndroidX.constraintlayout)
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
//    implementation(Deps.AndroidX.lifecycle_livedata)
//    testImplementation(Deps.junit)
}