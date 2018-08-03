![Answers Header](https://docs.fabric.io/ios/cocoapod-readmes/cocoapods-answers-header.png)

Part of [Google Fabric](https://get.fabric.io), [Answers](https://answers.io/) is real-time mobile analytics that you don't need to analyze. It was created to make understanding your user base incredibly simple -- so you can spend your time building amazing experiences in your product, not digging through data.

## Setup

1. Visit https://fabric.io/sign_up to create your Fabric account and to download Fabric.app

1. Open Fabric.app, login and select the Answers SDK.

    ![Fabric Plugin](https://docs.fabric.io/ios/cocoapod-readmes/cocoapods-fabric-plugin.png)

1. The Fabric app automatically detects when a project uses CocoaPods and gives you the option to install via the Podfile or Xcode.

	![Fabric Installation Options](https://docs.fabric.io/ios/cocoapod-readmes/cocoapods-pod-installation-option.png)

1. Select the Podfile option and follow the installation instructions to update your Podfile. **Note:** Answers is included in the Crashlytics Pod. If you only want to use Answers without Crashlytics, the Crashlytics Pod should be removed from your Podfile to avoid duplicate symbol errors.

	`pod 'Fabric'`
	`pod 'Answers'`

1. Run `pod install`

1. Add a Run Script Build Phase and build your app.

	![Fabric Run Script Build Phase](https://docs.fabric.io/ios/cocoapod-readmes/cocoapods-rsbp.png)

1. Initialize the SDK by inserting code outlined in the Fabric.app.

1. Run your app to finish the installation.

## Resources

* [Documentation](https://docs.fabric.io/apple/answers/overview.html)
* [Forums](https://stackoverflow.com/questions/tagged/google-fabric)
* [Website](http://www.answers.io/)
* Follow us on Twitter: [@fabric](https://twitter.com/fabric)
