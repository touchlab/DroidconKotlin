# Minimal iOS Dev Instructions

The absolute bare minimum install instructions, for an iOS dev who maybe hasn't done any Android


1 - Install the Android SDK

2 - Go here and download the .zip file for Mac under “Command Line Tools Only”
On the command line, run the following commands:
```sdkmanager “platform-tools”
sdkmanager “platforms;android-28”
cd ~/Library/Android/sdk
export ANDROID_HOME=~/Library/Android/sdk
export PATH=$PATH:$ANDROID_HOME/tools:$ANDROID_HOME/platorm-tools
source ~/.bash_profile
```
3 - Download IntelliJ Community: https://www.jetbrains.com/idea/download/#section=mac

4 - `git clone https://github.com/touchlab/DroidconKotlin`

5 - Go to the “Terminal” tab on the bottom and run: `./gradlew build` 

6 - Right click on “DroidconKotlin” in the “Project” tab on the left and select “Reveal in Finder”

7 - Go to "/iosApp/” and open “iosApp.xcworkspace”

8 - Run the app!
