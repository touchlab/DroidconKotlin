# Sessionize/Droidcon Mobile Clients

This project has a pair of native mobile applications backed by the Sessionize data api for use in 
events hosted by the Sessionize web application. These are specifically for Droidcon events, but can 
be forked and customized for anything run on Sessionize.

## Libraries

Kotlin multiplatform libraries used:

* SQLiter - New SQLite driver library. Not quite open source yet ...

* [SQLDelight](https://github.com/square/sqldelight) - Amazing SQL model generator from Square and 
[AlecStrong](https://github.com/AlecStrong).

* [multiplatform-settings](https://github.com/russhwolf/multiplatform-settings) - Shared settings for Android and iOS from
[russhwolf](https://github.com/russhwolf).

* [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization/) - JSON parsing provided by kotlinx.serialization.
(Source copied directly due to packaging issues)

* [Stately](https://github.com/touchlab/Stately/) - Multiplatform threading state library. 

* [Timber](https://github.com/touchlab/timber/tree/native) - Popular logging library on Android/Java, now with more platforms!

As an aside, I added Timber, but I didn't exactly add much "logging". On the todo list.

## Media

[Medium - Droidcon NYC App!](https://medium.com/@kpgalligan/droidcon-nyc-app-da868bdef387)

[Medium - Kotlin Multiplatform in the App Store!](https://medium.com/@kpgalligan/kotlin-multiplatform-in-the-app-store-c3a50c24f93b)

[Youtube - Kotlin Multiplatform @ Android Summit](https://www.youtube.com/watch?v=oeREzhXx7uw)

[Youtube - Droidcon App Kotlin Multiplatform](https://www.youtube.com/watch?v=YAeDK3Ei0Lk&feature=youtu.be)

## Building

Clone, and at the base, run:

```
./gradlew generateSqlDelightInterface
```

then ...

```
./gradlew build
```

## Intellij

To see the project, use the latest Intellij EAP, and make sure Android and anything Kotlin related is installed and updated.
There are modules for Android (app), iOS (ios), and the common code (src).

## NOTE ON GRADLE!!!

Until the new Multiplatform gradle plugins and dependencies are stable, you need to stick to Gradle 4.7.

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
