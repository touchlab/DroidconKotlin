## Customizing The App

Is there another Droidcon conference coming up and you need an app? Search no more, we have your back! With these steps you will have a new
app ready in the blink of an eye.

If you're using Sessionize for your event, you can use the app pretty easily. Customized config is kind of spread throughout the app.
Primarily you'll need to point to your data urls, change the data seed files for speakers/sessions/sponsors, and change the color settings.
Just follow these steps:

### Colors
Android
- Change **colors** in `Colors.kt` (`shared-ui` module) and check `Theme.kt`

iOS
- Change **colors** in `Assets.xcassets` through Xcode: 
  - `NavBar_Background.colorset` 
  - `Accent.colorset`
  - `AttendButton.colorset`
  - `TabBar_Background.colorset`

### Name and ID
- Change app **name** `droidcon_title` in `strings.xml` and title text in `BottomNavigationView.kt`
- Change **Bundle Name** in `Info.plist` and **Bundle Identifier** in `project.pbxproj`
    - To avoid having to wait for the full verification process from apple use the already existing bundle id for the previous conference in  the city
- Change **applicationId** in `build.gradle.kts` (android module)
    - To avoid having to wait for the full verification process from google use the already existing app id for the previous conference in the city

### Constants
In `Constants.kt` update:
- **Conference Time Zone**
- **Conference Time Zone Hash**
- **Firestore Collection Name**
- **Sessionize IDs**

### Images

Android
- `ic_launcher_foreground.xml`
- `ic_launcher_background.xml` 
- `ic_launcher-playstore.png`
- `ic_splash_screen.xml`

iOS
- `AppIcon.appiconset`
- `LaunchScreen_Icon.imageset`
- `LaunchScreen_Background.colorset`

### Sessionize
Replace the Sessionize JSON files by replacing them with new versions from Sessionize and Firebase. Update:
- `schedule.json`
- `speakers.json`
- `sponsor_sessions.json`
- `sponsors.json` 

It would be super great if you could keep us in the about section of your app, though. We're a consulting company that turns
project revenue into open source stuff, so we need eyeballs. Thanks XOXO. Speaking of...

[![Touchlab Logo](tlsmall.png "Touchlab Logo")](https://touchlab.co)
