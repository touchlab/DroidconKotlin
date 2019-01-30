# Sessionize/Droidcon Mobile Clients

This project has a pair of native mobile applications backed by the Sessionize data api for use in 
events hosted by the Sessionize web application. These are specifically for Droidcon events, but can 
be forked and customized for anything run on Sessionize.

## Libraries

Kotlin multiplatform libraries used:

* [SQLDelight](https://github.com/square/sqldelight) - SQL model generator from Square and 
[AlecStrong](https://github.com/AlecStrong).

* [SQLiter](https://github.com/touchlab/SQLiter) - Lightly opinionated sqlite access driver

* [multiplatform-settings](https://github.com/russhwolf/multiplatform-settings) - Shared settings for Android and iOS from
[russhwolf](https://github.com/russhwolf).

* [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization/)

* [kotlinx.coroutines](https://github.com/Kotlin/kotlinx.coroutines)

* [Stately](https://github.com/touchlab/Stately/) - Multiplatform threading state library. 

## Media

[Medium - Droidcon NYC App!](https://medium.com/@kpgalligan/droidcon-nyc-app-da868bdef387)

[Medium - Kotlin Multiplatform in the App Store!](https://medium.com/@kpgalligan/kotlin-multiplatform-in-the-app-store-c3a50c24f93b)

[Youtube - Droidcon SF](https://www.youtube.com/watch?v=c8IkWGmlcNE)

[Youtube - Kotlinconf](https://www.youtube.com/watch?v=Dul17VSiejo)

[Youtube - Kotlin Multiplatform @ Android Summit](https://www.youtube.com/watch?v=oeREzhXx7uw)

[Youtube - Droidcon App Kotlin Multiplatform](https://www.youtube.com/watch?v=YAeDK3Ei0Lk&feature=youtu.be)

## Building

Clone, and at the base, run:

```
./gradlew build
```

## NOTE ON GRADLE!!!

Most Kotlin Multiplatform libraries are published on Gradle 4.7, but new Android Studio 3.3+ versions require Gradle 4.10. This application currently uses 4.7.

## Intellij

While the gradle issues are being sorted out, *you can't run this with Android Studio*. [Install Intellij 2018.3+](https://www.jetbrains.com/idea/download/)

## Xcode

The ios project is in the iosApp folder. CD into it and open the xcworkspace file with Xcode.

```
cd iosApp
open iosApp.xcworkspace
```

**NOTE** Originally you would've needed to run 'pod install'. We've included the full Pods folder in the repo because of some type of cocoapods issue. You shouldn't actually need to run 'pod install', but optionally you can to refresh.

## Customizing

If you're using Sessionize for your event, you can use the app pretty easily. Customized config is kind of spread throughout the app. 
Primarily you'll need to point to your data urls, change the data seed files for speakers/sessions/sponsors, and
change the color settings.

The UI is in the process of being fully ported to Google's Material Components, with a centralized color and font theme. That's 
a work in progress, but eventually you should be able to modify central files for Android and iOS to provide for a specific look.

It would be super great if you could keep us in the about section of your app, though. We're a consulting company that turns 
project revenue into open source stuff, so we need eyeballs. Thanks XOXO. Speaking of...

[![Touchlab Logo](tlsmall.png "Touchlab Logo")](https://touchlab.co)
