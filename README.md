# SQLite & SQLDelight Sample

This example shows how to use SQLDelight with Kotlin Multiplatform on iOS.

## The App

It's a simple "notepad" app. Obviously not super useful on its own. You type in a title and note, and click the button.
This will add the note to the local database and update the table view. If you click on a note title in the table, it'll 
write the note detail to the log.

### NO ANDROID!!!

We haven't completed the android side yet, but that's not really the interesting part of the sample. Soon...

## Building

Run 

```
./gradlew build
```

Assuming that builds, open the iosApp/iosApp.xcodeproj project in Xcode and run the sample.

## Status

This release of db support and SQLDelight libraries is *very* early and will be going under 
significant refactoring in the near future.

[SQLDelight branch with multiplatform iOS support](https://github.com/touchlab/sqldelight/tree/iossupport)


