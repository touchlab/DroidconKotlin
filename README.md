# Sessionize/Droidcon Mobile Clients

This project has a pair of native mobile applications backed by the Sessionize data api for use in 
events hosted by the Sessionize web application. These are specifically for Droidcon events, but can 
be forked and customized for anything run on Sessionize.

## Kotlin 1.3.21 Updates!!

With the release of Kotlin 1.3.20, the Jetbrains standard libraries support Gradle 4.10.2+. Now
all libraries used in this app are their standard supported versions, and the app can be developed
with Android Studio as well as Intellij.

## Libraries

Kotlin multiplatform libraries used:

* [SQLDelight](https://github.com/square/sqldelight) - SQL model generator from Square and 
[AlecStrong](https://github.com/AlecStrong).

* [SQLiter](https://github.com/touchlab/SQLiter) - Lightly opinionated sqlite access driver. Powering
the sqldelight native driver.

* [multiplatform-settings](https://github.com/russhwolf/multiplatform-settings) - Shared settings for Android and iOS from
[russhwolf](https://github.com/russhwolf).

* [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization/)

* [kotlinx.coroutines](https://github.com/Kotlin/kotlinx.coroutines)

* [Stately](https://github.com/touchlab/Stately/) - Multiplatform threading state library. 

## Media

[Medium - Droidcon NYC App!](https://medium.com/@kpgalligan/droidcon-nyc-app-da868bdef387)

[More Media ->](MEDIA.md)

## Building

Clone, and at the base, run:

```
./gradlew build
```

## Intellij or Android Studio

You can use any recent version of Intellij (2018.3+) or Android Studio 3.3+. You should be able to open the
project folder directly, or import the project as a gradle project.

## Xcode

The ios project is in the iosApp folder. CD into it and open the xcworkspace file with Xcode.

```
cd iosApp
open iosApp.xcworkspace
```

### Xcode Sync

There is an experimental plugin called Xcode Sync. It imports Kotlin files into the Xcode project.
You can safely ignore that for now, but if you'd like to have new Kotlin files available in Xcode,
run the task added by the plugin.

### CocoaPods

CocoaPods is used by the project to import dependencies. We would normally not include the binaries in
the repo, but a much earlier version of Kotlin native had some odd incompatibility, so we checked them in.
It's also easier to run the project without needing to set up CocoaPods, so we're just leaving them in for
now.

## Customizing

General instructions for [customizing](CUSTOMIZING.md) the app. This app is backed by Sessionize and with some tweaks would
be generally useful for any Sessionize event.

## Contributing

Check out the issues section to see what we're looking for. We will be adding a number of new features for
Droidcon NYC 2019, as well as keeping up with the latest additions to the Kotlin Multiplatform ecosystem.

## About

Sessionize/Droidcon brought to you by...

[![Touchlab Logo](tlsmall.png "Touchlab Logo")](https://touchlab.co)
