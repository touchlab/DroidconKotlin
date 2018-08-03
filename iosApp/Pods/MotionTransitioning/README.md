# Motion Transitioning

> Light-weight API for building UIViewController transitions.

[![Build Status](https://travis-ci.org/material-motion/transitioning-objc.svg?branch=develop)](https://travis-ci.org/material-motion/transitioning-objc)
[![codecov](https://codecov.io/gh/material-motion/transitioning-objc/branch/develop/graph/badge.svg)](https://codecov.io/gh/material-motion/transitioning-objc)
[![CocoaPods Compatible](https://img.shields.io/cocoapods/v/MotionTransitioning.svg)](https://cocoapods.org/pods/MotionTransitioning)
[![Platform](https://img.shields.io/cocoapods/p/MotionTransitioning.svg)](http://cocoadocs.org/docsets/MotionTransitioning)
[![Docs](https://img.shields.io/cocoapods/metrics/doc-percent/MotionTransitioning.svg)](http://cocoadocs.org/docsets/MotionTransitioning)

This library standardizes the way transitions are built on iOS so that with a single line of code
you can pick the custom transition you want to use:

```swift
let viewController = MyViewController()
viewController.mdm_transitionController.transition = CustomTransition()
present(viewController, animated: true)
```

```objc
MyViewController *viewController = [[MyViewController alloc] init];
viewController.mdm_transitionController.transition = [[CustomTransition alloc] init];
[self presentViewController:viewController animated:true completion:nil];
```

The easiest way to make a transition with this library is to create a class that conforms to the
`Transition` protocol:

```swift
final class CustomTransition: NSObject, Transition {
  func start(with context: TransitionContext) {
    CATransaction.begin()

    CATransaction.setCompletionBlock {
      context.transitionDidEnd()
    }

    // Add animations...

    CATransaction.commit()
  }
}
```

```objc
@interface CustomTransition: NSObject <MDMTransition>
@end

@implementation CustomTransition

- (void)startWithContext:(id<MDMTransitionContext>)context {
  [CATransaction begin];
  [CATransaction setCompletionBlock:^{
    [context transitionDidEnd];
  }];

  // Add animations...

  [CATransaction commit];
}

@end
```

## Installation

### Installation with CocoaPods

> CocoaPods is a dependency manager for Objective-C and Swift libraries. CocoaPods automates the
> process of using third-party libraries in your projects. See
> [the Getting Started guide](https://guides.cocoapods.org/using/getting-started.html) for more
> information. You can install it with the following command:
>
>     gem install cocoapods

Add `MotionTransitioning` to your `Podfile`:

    pod 'MotionTransitioning'

Then run the following command:

    pod install

### Usage

Import the framework:

    @import MotionTransitioning;

You will now have access to all of the APIs.

## Example apps/unit tests

Check out a local copy of the repo to access the Catalog application by running the following
commands:

    git clone https://github.com/material-motion/transitioning-objc.git
    cd transitioning-objc
    pod install
    open MotionTransitioning.xcworkspace

## Guides

1. [Architecture](#architecture)
2. [How to create a fade transition](#how-to-create-a-fade-transition)
3. [How to customize presentation](#how-to-customize-presentation)
4. [How to customize navigation controller transitions](#how-to-customize-navigation-controller-transitions)

### Architecture

> Background: Transitions in iOS are customized by setting a `transitioningDelegate` on a view
> controller. When a view controller is presented, UIKit will ask the transitioning delegate for an
> animation, interaction, and presentation controller. These controllers are then expected to
> implement the transition's motion.

MotionTransitioning provides a thin layer atop these protocols with the following advantages:

- Every view controller has its own **transition controller**. This encourages choosing the
  transition based on the context.
- Transitions are represented in terms of **backward/forward** rather than from/to. When presenting,
  we're moving forward. When dismissing, we're moving backward. This allows transition code to be
  written with fewer conditional branches of logic.
- Transition objects can customize their behavior by conforming to the family of `TransitionWith*` protocols.

### How to create a fade transition

We'll create a new fade transition so that the following lines of code customizes the presentation
and dismissal of our view controller:

```swift
let viewController = MyViewController()
viewController.mdm_transitionController.transition = FadeTransition()
present(viewController, animated: true)
```

#### Step 1: Define a new Transition type

A transition is an `NSObject` subclass that conforms to the `Transition` protocol.

The only method you have to implement is `start(with context:)`. This method is invoked each time
the associated view controller is presented or dismissed.

```swift
final class FadeTransition: NSObject, Transition {
  func start(with context: TransitionContext) {

  }
}
```

#### Step 2: Invoke the completion handler once all animations are complete

Every transition is provided with a transition context. The transition context must be told when the
transition's motion has completed so that the context can then inform UIKit of the view controller
transition's completion.

If using explicit Core Animation animations:

```swift
final class FadeTransition: NSObject, Transition {
  func start(with context: TransitionContext) {
    CATransaction.begin()

    CATransaction.setCompletionBlock {
      context.transitionDidEnd()
    }

    // Your motion...

    CATransaction.commit()
  }
}
```

If using implicit UIView animations:

```swift
final class FadeTransition: NSObject, Transition {
  func start(with context: TransitionContext) {
    UIView.animate(withDuration: context.duration, animations: {
      // Your motion...

    }, completion: { didComplete in
      context.transitionDidEnd()
    })
  }
}
```

#### Step 3: Implement the motion

With the basic scaffolding in place, you can now implement your motion. For simplicity's sake we'll
use implicit UIView animations in this example to build our motion, but you're free to use any
animation system you prefer.

```swift
final class FadeTransition: NSObject, Transition {
  func start(with context: TransitionContext) {
    // This is a fairly rudimentary way to calculate the values on either side of the transition.
    // You may want to try different patterns until you find one that you prefer.
    // Also consider trying the MotionAnimator library provided by the Material Motion team:
    // https://github.com/material-motion/motion-animator-objc
    let backOpacity = 0
    let foreOpacity = 1
    let initialOpacity = context.direction == .forward ? backOpacity : foreOpacity
    let finalOpacity = context.direction == .forward ? foreOpacity : backOpacity
    context.foreViewController.view.alpha = initialOpacity
    UIView.animate(withDuration: context.duration, animations: {
      context.foreViewController.view.alpha = finalOpacity

    }, completion: { didComplete in
      context.transitionDidEnd()
    })
  }
}
```

### How to customize presentation

Customize the presentation of a transition when you need to do any of the following:

- Add views, such as dimming views, that live beyond the lifetime of the transition.
- Change the destination frame of the presented view controller.

You have two options for customizing presentation:

1. Use the provided `TransitionPresentationController` API.
2. Build your own UIPresentationController subclass.

#### Option 2: Subclass UIPresentationController

Start by defining a new presentation controller type:

```swift
final class MyPresentationController: UIPresentationController {
}
```

Your Transition type must conform to `TransitionWithPresentation` in order to customize
presentation. Return your custom presentation controller class from the required methods and be sure
to return the `.custom` presentation style, otherwise UIKit will not use your presentation
controller.

```swift
extension VerticalSheetTransition: TransitionWithPresentation {
  func defaultModalPresentationStyle() -> UIModalPresentationStyle {
    return .custom
  }

  func presentationController(forPresented presented: UIViewController,
                              presenting: UIViewController,
                              source: UIViewController?) -> UIPresentationController? {
    return MyPresentationController(presentedViewController: presented, presenting: presenting)
  }
}
```

If your presentation controller needs to animate anything, you can conform to the `Transition`
protocol in order to receive a `start` invocation each time a transition begins. The presentation
controller's `start` will be invoked before the transition's `start`.

> Note: Just like your transition, your presentation controller must eventually call
> `transitionDidEnd` on its context, otherwise your transition will not complete. This is because
> the transitioning controller waits until all associated transitions have completed before
> informing UIKit of the view controller transition's completion.

```swift
extension MyPresentationController: Transition {
  func start(with context: TransitionContext) {
    // Your motion...
  }
}
```

### How to customize navigation controller transitions

`UINavigationController` ignores the `transitioningDelegate` property on any view
controller pushed onto or popped off of the stack, instead relying on its delegate instance to
customize any transitions. This means that our `transitionController`  will be
ignored by a navigation controller.

In order to customize individual push/pop transitions with the `transitionController`, you
can make use of the `TransitionNavigationControllerDelegate` singleton class. If you
assign a shared delegate to your navigation controller's delegate, your navigation controller
will honor the animation and interaction settings defined by your individual view controller's
`transitionController`.

```swift
navigationController.delegate = TransitionNavigationControllerDelegate.sharedDelegate()

// Subsequent pushes and pops will honor the pushed/popped view controller's
// transitionController settings as though the view controllers were being
// presented/dismissed.
```

## Contributing

We welcome contributions!

Check out our [upcoming milestones](https://github.com/material-motion/transitioning-objc/milestones).

Learn more about [our team](https://material-motion.github.io/material-motion/team/),
[our community](https://material-motion.github.io/material-motion/team/community/), and
our [contributor essentials](https://material-motion.github.io/material-motion/team/essentials/).

## License

Licensed under the Apache 2.0 license. See LICENSE for details.
