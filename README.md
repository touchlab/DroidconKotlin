# Sessionize/Droidcon Mobile Clients

## General Info

This project has a pair of native mobile applications backed by the Sessionize data api for use in events hosted by the Sessionize web application. These are specifically for Droidcon events, but can be forked and customized for anything run on Sessionize.

> ## Subscribe!
>
> We build solutions that get teams started smoothly with Kotlin Multiplatform and ensure their success in production. Join our community to learn how your peers are adopting KMM.
[Sign up here](https://form.typeform.com/to/MJTpmm?typeform-source=touchlab.co)!

## Building

#### Firebase

The apps need a Firebase account set up to run. You'll need to get the `google-services.json` and put it in `android/google-services.json` for Android, and
the `GoogleService-Info.plist` and put that in `ios/Droidcon/Droidcon/GoogleService-Info.plist` for iOS.

##### Authentication

Additionally for Firebase Authentication you'll need to pass in your client ID into the project.

For Android you'll need to add a `clientId` property to your `local.properties`.
For iOS you'll need to pass the clientId into your [URL Types](https://firebase.google.com/docs/auth/ios/google-signin#implement_google_sign-in).

#### Stream

In order to support Stream Chat you will need to register for [Stream Chat](https://getstream.io/chat/docs/), and pass your api key into the the codebase.

For Android you should add the `streamApiKey` property to your `local.properties`.
For iOS you should add the `streamApiKey` property to your `info.plist`.

## Customization

To customize the app, view the [Customizing Guide](CUSTOMIZING.md) for more details.

## Compose UI for both!

We're running a very early version of Compose UI for iOS as the iOS interface. It mostly shares the screen code with the Android app. While Native Compose UI is obviously experimental, it works surprisingly well.

[Check out the blog post](https://touchlab.co/droidcon-nyc-ios-app-with-compose/)

## Media

[Blog posts and videos ->](MEDIA.md)

## About

Sessionize/Droidcon brought to you by...

[![Touchlab Logo](tlsmall.png "Touchlab Logo")](https://touchlab.co)
