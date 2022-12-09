import com.android.build.gradle.internal.tasks.ProcessJavaResTask
import de.undercouch.gradle.tasks.download.Download

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("com.android.library")
    id("com.squareup.sqldelight")
    id("com.github.gmazzo.buildconfig")
    id("de.undercouch.download")
}

val conferenceTimeZone = project.properties["co.touchlab.droidcon.constants.conferenceTimeZone"]
val conferenceTimeZoneHash = project.properties["co.touchlab.droidcon.constants.conferenceTimeZoneHash"]
val projectId = project.properties["co.touchlab.droidcon.constants.firestore.projectId"]
val databaseName = project.properties["co.touchlab.droidcon.constants.firestore.databaseName"]
val collectionName = project.properties["co.touchlab.droidcon.constants.firestore.collectionName"]
val apiKey = project.properties["co.touchlab.droidcon.constants.firestore.apiKey"]
val firestoreUrl = project.properties["co.touchlab.droidcon.constants.firestore.baseUrl"]
val scheduleId = project.properties["co.touchlab.droidcon.constants.sessionize.scheduleId"]
val sponsorsId = project.properties["co.touchlab.droidcon.constants.sessionize.sponsorsId"]
val sessionizeUrl = project.properties["co.touchlab.droidcon.constants.sessionize.baseUrl"]

buildConfig {
    packageName("co.touchlab.droidcon")
    className("Constants")
    buildConfigField("kotlinx.datetime.TimeZone", "conferenceTimeZone", "TimeZone.of(\"$conferenceTimeZone\")")
    buildConfigField("String", "conferenceTimeZoneHash", "\"$conferenceTimeZoneHash\"")
    buildConfigField("String", "firestoreProjectId", "\"$projectId\"")
    buildConfigField("String", "firestoreDatabaseName", "\"$databaseName\"")
    buildConfigField("String", "firestoreCollectionName", "\"$collectionName\"")
    buildConfigField("String", "firestoreApiKey", "\"$apiKey\"")
    buildConfigField("String", "firestoreUrl", "\"$firestoreUrl\"")
    buildConfigField("String", "sessionizeScheduleId", "\"$scheduleId\"")
    buildConfigField("String", "sessionizeSponsorsId", "\"$sponsorsId\"")
    buildConfigField("String", "sessionizeUrl", "\"$sessionizeUrl\"")
}

val downloadedResourcesDir = layout.buildDirectory.dir("droidcon/downloadedResources")

// version = "1.0"

android {
    val androidMinSdk: String by project
    val androidCompileSdk: String by project
    val androidTargetSdk: String by project

    compileSdk = androidCompileSdk.toInt()
    defaultConfig {
        minSdk = androidMinSdk.toInt()
        targetSdk = androidTargetSdk.toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    lint {
        warningsAsErrors = true
        abortOnError = true
    }

    sourceSets {
        val main by getting
        main.java.setSrcDirs(listOf("src/androidMain/kotlin"))
        main.res.setSrcDirs(listOf("src/androidMain/res"))
        main.resources.setSrcDirs(
            listOf(
                "src/androidMain/resources",
                "src/commonMain/resources",
                downloadedResourcesDir
            )
        )
        main.manifest.srcFile("src/androidMain/AndroidManifest.xml")
    }
}

version = "1.0"
android {
    configurations {
        create("androidTestApi")
        create("androidTestDebugApi")
        create("androidTestReleaseApi")
        create("testApi")
        create("testDebugApi")
        create("testReleaseApi")
    }
}

kotlin {
    android()
    ios()
    iosSimulatorArm64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(libs.kermit)
                api(libs.kermit.crashlytics)
                api(libs.kotlinx.coroutines.core)
                api(libs.kotlinx.datetime)
                api(libs.multiplatformSettings.core)
                api(libs.uuid)

                implementation(libs.bundles.ktor.common)
                implementation(libs.bundles.sqldelight.common)

                implementation(libs.stately.common)
                implementation(libs.koin.core)
                implementation(libs.korio)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.multiplatformSettings.test)
                implementation(libs.kotlin.test.common)
                implementation(libs.koin.test)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(libs.sqldelight.driver.android)
                implementation(libs.kotlinx.coroutines.android)
                implementation(libs.ktor.client.okhttp)
                implementation(libs.androidx.core)
            }
        }
        val androidTest by getting {
            dependencies {
                implementation(libs.test.junit)
                implementation(libs.test.junitKtx)
                implementation(libs.test.coroutines)
            }
        }
        val iosMain by getting {
            dependencies {
                implementation(libs.sqldelight.driver.ios)
                implementation(libs.ktor.client.ios)
            }
        }
        val iosTest by getting {}

        sourceSets["iosSimulatorArm64Main"].dependsOn(iosMain)
        sourceSets["iosSimulatorArm64Test"].dependsOn(iosTest)
    }

    sourceSets.all {
        languageSettings.apply {
            optIn("kotlin.RequiresOptIn")
            optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
        }
    }

    sourceSets.matching { it.name.endsWith("Test") }.configureEach {
        languageSettings.optIn("kotlin.time.ExperimentalTime")
    }

    // Enable concurrent sweep phase in new native memory manager. (This will be enabled by default in 1.7.0)
    // https://kotlinlang.org/docs/whatsnew1620.html#concurrent-implementation-for-the-sweep-phase-in-new-memory-manager
    // (This might not be necessary here since the other module creates the framework, but it's here as well just in case it matters for
    //  test binaries)
    targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget> {
        binaries.all {
            freeCompilerArgs += "-Xgc=cms"
        }
    }
}

sqldelight {
    database("DroidconDatabase") {
        packageName = "co.touchlab.droidcon.db"
    }
}

val downloadResources by tasks.registering(Download::class) {
    val urlsWithFilenames = mapOf(
        "$sessionizeUrl/api/v2/$sponsorsId/view/speakers" to "speakers.json",
        "$sessionizeUrl/api/v2/$scheduleId/view/gridtable" to "schedule.json",
        "$sessionizeUrl/api/v2/$sponsorsId/view/sessions" to "sponsor_sessions.json",
        "$firestoreUrl/v1/projects/$projectId/databases/$databaseName/documents/$collectionName?key=$apiKey" to "sponsors.json"
    )

    src(urlsWithFilenames.keys)

    dest(downloadedResourcesDir)
    kotlin.sourceSets.commonMain.configure {
        resources.srcDir(downloadedResourcesDir)
    }

    onlyIfModified(true)
    eachFile {
        name = urlsWithFilenames[sourceURL.toString()]
    }
}

// for iOS
tasks.withType<ProcessResources> {
    dependsOn(downloadResources)
}

// for Android
tasks.withType<ProcessJavaResTask> {
    dependsOn(downloadResources)
}
