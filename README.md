# Sessionize/Droidcon Mobile Clients

## General Info

This project has a pair of native mobile applications backed by the Sessionize data api for use in events hosted by the Sessionize web application. These are specifically for Droidcon events, but can be forked and customized for anything run on Sessionize.

## Building

The apps need a Firebase account set up to run. You'll need to get the `google-services.json` and put it in `android/google-services.json` for Android, and
the `GoogleService-Info.plist` and put that in `ios/Droidcon/Droidcon/GoogleService-Info.plist` for iOS.

## Compose UI for both!

We're running a very early version of Compose UI for iOS as the iOS interface. It mostly shares the screen code with the Android app. While Native Compose UI is obviously experimental, it works surprisingly well.

[Check out the blog post](https://touchlab.co/droidcon-nyc-ios-app-with-compose/)

## Media

[Blog posts and videos ->](MEDIA.md)

## About

Sessionize/Droidcon brought to you by...

[![Touchlab Logo](tlsmall.png "Touchlab Logo")](https://touchlab.co)
