# Sessionize/Droidcon Mobile Clients


[![Build Status](https://dev.azure.com/touchlabApps/DroidconApp/_apis/build/status/touchlab.DroidconKotlin?branchName=master)](https://dev.azure.com/touchlabApps/DroidconApp/_build/latest?definitionId=1&branchName=master)

## General Info

This project has a pair of native mobile applications backed by the Sessionize data api for use in 
events hosted by the Sessionize web application. These are specifically for Droidcon events, but can 
be forked and customized for anything run on Sessionize.


> ## Touchlab's Hiring!
>
> We're looking for a Mobile Developer, with Android/Kotlin experience, who is eager to dive into Kotlin Multiplatform Mobile (KMM) development. Come join the remote-first team putting KMM in production. [More info here](https://go.touchlab.co/careers-gh).


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

## Time Zone

The time zone of the conference can be changed for both the Android and iOS applications. The time zone is formatted by the country / city, for example for New York  you would set the variable as "America/New_York".

In Android, you can find the variable in the apps build.gradle. 

```
buildConfigField "String", "TIME_ZONE", "\"America/Los_Angeles\""
```

In iOS you can find the variable in the projects info.plist, under Time Zone.

If you want a full list of available Time Zone options, take a look at the TimeZoneAndroid and TimeZoneiOS files in the root.


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
