# Sessionize/Droidcon Mobile Clients

This project has the mobile clients for Droidcon NYC, but as Droidcon NYC uses Sessionize, this project could serve 
as the base for any event using Sessionize.

The apps are native Android and iOS, using Kotlin Multiplatform for shared logic and architecture.

## Building

Clone, and at the base, run:

```
./gradlew build
```

## Intellij

To see the project, use the latest Intellij EAP, and make sure Android and anything Kotlin related is installed and updated.
There are modules for Android (app), iOS (ios), and the common code (src).

## Xcode

After building the kotlin code, open and run the Xcode project in iosApp. Run Cocoapod install first.


## Architecture

The shared code includes the sqldelight declarations, as well as the LiveData setup. Both the Android and iOS code sets 
wire up the view models and UI's.

 



