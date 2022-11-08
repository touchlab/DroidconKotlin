## Customizing The App

Is there another Droidcon conference coming up and you need an app? Search no more, we have your back! With these steps you will have a new
app ready in the blink of an eye.

If you're using Sessionize for your event, you can use the app pretty easily. Customized config is kind of spread throughout the app.
Primarily you'll need to point to your data urls, change the data seed files for speakers/sessions/sponsors, and change the color settings.
Just follow these steps:

- Change **colors** in `Colors.kt` (`shared-ui` module) and check `Theme.kt` if everything looks okay for Android
- Change **colors** for iOS in `Assets.xcassets` through Xcode: `NavBar_Background.colorset`, `Accent.colorset`, `AttendButton.colorset`
  and `TabBar_Background.colorset`
- Change app **name** `droidcon_title` in `strings.xml` and title text in `SessionListView.kt`
- Change **Bundle Name** in `Info.plist` and **Bundle Identifier** in `project.pbxproj`
    - To avoid having to wait for the full verification process from apple use the already existing bundle id for the previous conference in
      the city
- Change **applicationId** in `build.gradle.kts` (android module)
    - To avoid having to wait for the full verification process from google use the already existing app id for the previous conference in
      the city
- Change conference **time zone** and **time zone hash** in `Constants.kt`
- Change **sponsors collection name** and **Sessionize ids** in `Constants.kt`
- Change **icon** by changing `ic_launcher_foreground.xml`, `ic_launcher_background.xml` and `ic_launcher-playstore.png` for Android and
  `AppIcon.appiconset` for iOS
- Change **launch screen image** by changing `ic_splash_screen.xml` for Android and `LaunchScreen_Icon.imageset` and
  `LaunchScreen_Background.colorset` for iOS
- Change `schedule.json`, `speakers.json`, `sponsor_sessions.json` and `sponsors.json` by replacing them with new versions from Sessionize
  and Firebase

It would be super great if you could keep us in the about section of your app, though. We're a consulting company that turns
project revenue into open source stuff, so we need eyeballs. Thanks XOXO. Speaking of...

[![Touchlab Logo](tlsmall.png "Touchlab Logo")](https://touchlab.co)
