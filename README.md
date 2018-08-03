# Sessionize/Droidcon Mobile Clients

This project has a pair of native mobile applications backed by the Sessionize data api for use in 
events hosted by the Sessionize web application. These are specifically for Droidcon events, but can 
be forked and customized for anything run on Sessionize.

## Libraries

Kotlin multiplatform libraries used:

* [KNarch.db](https://github.com/touchlab/knarch.db) - SQLite interaction library. Modeled on AOSP SQLite stack. From 
[https://touchlab.co/](https://touchlab.co/) and [yours truly](https://github.com/kpgalligan). Click stars.
Show some love.

* [SQLDelight](https://github.com/square/sqldelight) - Amazing SQL model generator from Square and 
[AlecStrong](https://github.com/AlecStrong).

* [multiplatform-settings](https://github.com/russhwolf/multiplatform-settings) - Shared settings for Android and iOS from
[russhwolf](https://github.com/russhwolf).

* [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization/) - JSON parsing provided by kotlinx.serialization.
(Source copied directly due to packaging issues)

* [knarch.threads](https://github.com/touchlab/knarch.threads/) - Threading support library. Temporary until something 
more mature appears. Implements a simple LiveData analog and ThreadLocal helpers. 

## Building

Clone, and at the base, run:

```
./gradlew build
```

## Intellij

To see the project, use the latest Intellij EAP, and make sure Android and anything Kotlin related is installed and updated.
There are modules for Android (app), iOS (ios), and the common code (src).

## Xcode

The ios project is in the iosApp folder. CD there, then run cocoapods

```
cd iosApp
pod install
```

After building the kotlin code, open and run the Xcode workspace.

```
open iosApp.xcworkspace
```

## Customizing

If you're using Sessionize for your event, you can use the app pretty easily. Customizing config touches a number of places
which will be refactored in the coming weeks. Primarily you'll need to point to your data urls, change the data seed files, and
change the color settings.

The UI is in the process of being fully ported to Google's Material Components, with a centralized color and font theme. That's 
a work in progress, but eventually you should be able to modify central files for Android and iOS to provide for a specific look.

It would be super great if you could keep us in the about section of your app, though. We're a consulting company that turns 
project revenue into open source stuff, so we need eyeballs. Thanks XOXO. Speaking of...

[![Touchlab Logo](iosApp/iosApp/Assets.xcassets/about_touchlab.imageset/TL_Gradient.png "Touchlab Logo")](https://touchlab.co)