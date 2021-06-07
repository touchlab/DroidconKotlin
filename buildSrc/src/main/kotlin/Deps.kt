object Deps {

    object Android {
        const val plugin = "com.android.tools.build:gradle:7.1.0-alpha01"
        const val google_services = "com.google.gms:google-services:4.3.8"

        const val flow_layout = "com.nex3z:flow-layout:1.2.2"

        const val core = "androidx.core:core:1.5.0"
        const val appcompat = "androidx.appcompat:appcompat:1.3.0"
        const val material = "com.google.android.material:material:1.3.0"
        const val vector_drawable = "androidx.vectordrawable:vectordrawable:1.1.0"
        const val constraintlayout = "androidx.constraintlayout:constraintlayout:2.0.4"
        const val circle_imageview = "de.hdodenhof:circleimageview:2.1.0"
        const val picasso = "com.squareup.picasso:picasso:2.71828"

        object Lifecycle {
            const val extensions = "androidx.lifecycle:lifecycle-extensions:2.2.0"
            const val viewmodel = "androidx.lifecycle:lifecycle-viewmodel-ktx:2.3.1"
            const val common = "androidx.lifecycle:lifecycle-common-java8:2.3.1"
        }

        object Navigation {
            const val version = "2.4.0-alpha01"

            const val plugin = "androidx.navigation:navigation-safe-args-gradle-plugin:$version"

            const val fragment = "androidx.navigation:navigation-fragment-ktx:$version"
            const val navigation_ui = "androidx.navigation:navigation-ui-ktx:$version"
        }

        object Test {
            const val junit = "junit:junit:4.13.2"
            const val core = "androidx.test:core:1.3.0"
            const val junit_ext = "androidx.test.ext:junit:1.1.2"

            const val runner = "androidx.test:runner:1.3.0"

            object Espresso {
                const val core = "androidx.test.espresso:espresso-core:3.3.0"
            }

            object Robolectric {
                const val core = "org.robolectric:robolectric:4.0"
            }
        }
    }

    object Desugar {
        private const val version = "1.1.5"
        const val desugar_libs = "com.android.tools:desugar_jdk_libs:$version"
    }

    const val fabric_plugin = "io.fabric.tools:gradle:1.31.2"

    object Kotlin {
        const val version = "1.5.0"
        const val plugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$version"

        object Test {
            const val common = "org.jetbrains.kotlin:kotlin-test-common:${Kotlin.version}"
            const val annotations = "org.jetbrains.kotlin:kotlin-test-annotations-common:${Kotlin.version}"
            const val jvm = "org.jetbrains.kotlin:kotlin-test:${Kotlin.version}"
            const val junit = "org.jetbrains.kotlin:kotlin-test-junit:${Kotlin.version}"
            const val reflect = "org.jetbrains.kotlin:kotlin-reflect:${Kotlin.version}"
        }

        object Coroutines {
            const val version = "1.4.3-native-mt"
            const val common = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$version"
            const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$version"
        }
    }

    object Stately {
        const val version = "1.0.2"
        const val common = "co.touchlab:stately-common:$version"
        const val concurrency = "co.touchlab:stately-concurrency:$version"
        const val collections = "co.touchlab:stately-collections:$version"
    }

//    ext.versions = [
//    'supportLibrary'       : '29.0.0'
//    ]

    const val multiplatformSettings = "com.russhwolf:multiplatform-settings:0.7.5"

    object SqlDelight {
        const val version = "1.4.4"
        const val runtime       = "com.squareup.sqldelight:runtime:$version"
        const val runtimeJdk    = "com.squareup.sqldelight:runtime-jvm:$version"
        const val driverIos     = "com.squareup.sqldelight:native-driver:$version"
        const val driverAndroid = "com.squareup.sqldelight:android-driver:$version"
        const val driverSqlite  = "com.squareup.sqldelight:sqlite-driver:$version"

        const val plugin = "com.squareup.sqldelight:gradle-plugin:$version"
    }

    object SqLiter {
        const val version = "0.6.8"
        const val ios = "co.touchlab:sqliter:$version"
    }

    object Ktor {
        const val version = "1.3.2"
        const val commonCore =  "io.ktor:ktor-client-core:$version"
        const val commonJson =  "io.ktor:ktor-client-json:$version"
        const val jvmCore =     "io.ktor:ktor-client-core-jvm:$version"
        const val androidCore = "io.ktor:ktor-client-okhttp:$version"
        const val jvmJson =     "io.ktor:ktor-client-json-jvm:$version"
        const val ios =         "io.ktor:ktor-client-ios:$version"
        const val iosCore =     "io.ktor:ktor-client-core-native:$version"
        const val iosJson =     "io.ktor:ktor-client-json-native:$version"
    }

    object Serialization {
        const val version = "1.2.1"
        const val commonRuntime = "org.jetbrains.kotlinx:kotlinx-serialization-json:$version"

        const val plugin = "org.jetbrains.kotlin:kotlin-serialization:${Kotlin.version}"
    }

    object Firebase {
        const val version = "17.4.0"
        const val androidCore =       "com.google.firebase:firebase-core:$version"
        const val androidMessaging =  "com.google.firebase:firebase-messaging:20.1.+"
        const val firestoreAndroid =  "com.google.firebase:firebase-firestore:21.4.3"

        object GitLive {
            const val version = "1.3.1"
            const val firestore = "dev.gitlive:firebase-firestore:$version"
        }
    }

    const val xcodesync = "co.touchlab:kotlinxcodesync:0.1.5"

    const val testhelp = "co.touchlab:testhelp:0.2.8"

    const val crashlytics = "com.crashlytics.sdk.android:crashlytics:2.10.1"
}