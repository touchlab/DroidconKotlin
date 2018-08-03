/*
 Copyright 2017-present The Material Motion Authors. All Rights Reserved.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

#import <UIKit/UIKit.h>

/**
 This class provides a singleton implementation of UINavigationControllerDelegate that makes it
 possible to configure view controller transitions using each view controller's transition
 controller.

 This class is not meant to be instantiated directly.

 The +delegate should be assigned as the delegate for any UINavigationController instance that
 wishes to configure transitions using the mdm_transitionController (transitionController in Swift)
 property on a view controller.

 If a navigation controller already has its own delegate, then that delegate can simply forward
 the two necessary methods to the +sharedInstance of this class.
 */
NS_SWIFT_NAME(TransitionNavigationControllerDelegate)
@interface MDMTransitionNavigationControllerDelegate : NSObject

/**
 Use when directly invoking methods.

 Only supported methods are exposed.
 */
+ (nonnull instancetype)sharedInstance;

/**
 Can be set as a navigation controller's delegate.
 */
+ (nonnull id<UINavigationControllerDelegate>)sharedDelegate;

#pragma mark <UINavigationControllerDelegate> Support

- (nullable id<UIViewControllerAnimatedTransitioning>)navigationController:(nonnull UINavigationController *)navigationController
                                           animationControllerForOperation:(UINavigationControllerOperation)operation
                                                        fromViewController:(nonnull UIViewController *)fromVC
                                                          toViewController:(nonnull UIViewController *)toVC;
- (nullable id<UIViewControllerInteractiveTransitioning>)navigationController:(nonnull UINavigationController *)navigationController
                                  interactionControllerForAnimationController:(nonnull id<UIViewControllerAnimatedTransitioning>)animationController;

@end
