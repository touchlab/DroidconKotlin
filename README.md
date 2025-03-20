# Droidcon Mobile Clients

## General Info

This project has a pair of native mobile applications for Global Droidcon events. It is built with Compose Multiplatform to run on both Android and iOS.

> ## Subscribe!
>
> We build solutions that get teams started smoothly with Kotlin Multiplatform and ensure their success in production. Join our community to learn how your peers are adopting KMP.
[Sign up here](https://touchlab.co/?s=shownewsletter)!

## Building

The apps need a Firebase account set up to run. You'll need to get the `google-services.json` and put it in `android/google-services.json` for Android, and
the `GoogleService-Info.plist` and put that in `ios/Droidcon/Droidcon/GoogleService-Info.plist` for iOS.

## Compose UI for both!

This app has come a long way! It was one of the earliest KMP apps in the iOS app store, and was certainly the first Compose Multiplatform app in the app store, as it was built and released before there was technically a tech preview.

[Check out the blog post](https://touchlab.co/droidcon-nyc-ios-app-with-compose/)

CMP has come a long way. Back in 2022, the Compose experience on iOS wasn't great. We kept a SwiftUI version of the UI as the main UI for the iOS app, and let you turn on CMP as an early preview. As of March 2025, the SwiftUI version has been removed entirely. Why do the extra work? But, if you want to do a side-by-side, grab a version from main through Feb 2025 and check it out. Prior versions were built and released for each conference, but this updated version is designed to be used for all Droidcon events. As that was a fairly major update, we decided to drop the parallel SwiftUI.

## Media

[Blog posts and videos ->](MEDIA.md)

## About

Droidcon Mobile App brought to you by...

[![Touchlab Logo](tlsmall.png "Touchlab Logo")](https://touchlab.co)
