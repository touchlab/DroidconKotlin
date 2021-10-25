# Sessionize/Droidcon Mobile Clients


[![Build Status](https://dev.azure.com/touchlabApps/DroidconApp/_apis/build/status/touchlab.DroidconKotlin?branchName=master)](https://dev.azure.com/touchlabApps/DroidconApp/_build/latest?definitionId=1&branchName=master)

## General Info

This project has a pair of native mobile applications backed by the Sessionize data api for use in 
events hosted by the Sessionize web application. These are specifically for Droidcon events, but can 
be forked and customized for anything run on Sessionize.


> ## Touchlab's Hiring!
>
> We're looking for a Mobile Developer, with Android/Kotlin experience, who is eager to dive into Kotlin Multiplatform Mobile (KMM) development. Come join the remote-first team putting KMM in production. [More info here](https://go.touchlab.co/careers-gh).

## Building

The apps need a Firebase account set up to run. You'll need to get the `google-services.json` and put it in `android/google-services.json` for Android, and
the `GoogleService-Info.plist` and put that in `ios/Droidcon/Droidcon/GoogleService-Info.plist` for iOS.

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

## About

Sessionize/Droidcon brought to you by...

[![Touchlab Logo](tlsmall.png "Touchlab Logo")](https://touchlab.co)
