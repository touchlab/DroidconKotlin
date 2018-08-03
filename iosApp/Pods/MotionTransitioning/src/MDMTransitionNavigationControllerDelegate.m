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

#import "MDMTransitionNavigationControllerDelegate.h"

#import "MDMTransitionContext.h"
#import "private/MDMViewControllerTransitionController.h"

@interface MDMTransitionNavigationControllerDelegate () <UINavigationControllerDelegate>
@end

@implementation MDMTransitionNavigationControllerDelegate

- (instancetype)init {
  [self doesNotRecognizeSelector:_cmd];
  return nil;
}

- (instancetype)initInternally {
  return [super init];
}

+ (instancetype)sharedInstance {
  static id sharedInstance = nil;
  static dispatch_once_t onceToken;
  dispatch_once(&onceToken, ^{
    sharedInstance = [[self alloc] initInternally];
  });
  return sharedInstance;
}

+ (id<UINavigationControllerDelegate>)sharedDelegate {
  return [self sharedInstance];
}

#pragma mark - UINavigationControllerDelegate

- (id<UIViewControllerAnimatedTransitioning>)navigationController:(UINavigationController *)navigationController
                                  animationControllerForOperation:(UINavigationControllerOperation)operation
                                               fromViewController:(UIViewController *)fromVC
                                                 toViewController:(UIViewController *)toVC {
  id<UIViewControllerAnimatedTransitioning> animator = nil;

  if (operation == UINavigationControllerOperationPush) {
    animator = [toVC.transitioningDelegate animationControllerForPresentedController:toVC
                                                                presentingController:fromVC
                                                                    sourceController:navigationController];
  } else {
    animator = [fromVC.transitioningDelegate animationControllerForDismissedController:fromVC];
  }

  if (!animator) {
    // For some reason UIKit decides to stop responding to edge swipe dismiss gestures when we
    // customize the navigation controller delegate's animation methods. Clearing the delegate for
    // the interactive pop gesture recognizer re-enables this edge-swiping behavior.
    navigationController.interactivePopGestureRecognizer.delegate = nil;
  }

  return animator;
}

- (id<UIViewControllerInteractiveTransitioning>)navigationController:(UINavigationController *)navigationController
                         interactionControllerForAnimationController:(id<UIViewControllerAnimatedTransitioning>)animationController {
  if ([animationController conformsToProtocol:@protocol(UIViewControllerInteractiveTransitioning)]) {
    return (id<UIViewControllerInteractiveTransitioning>)animationController;
  }
  return nil;
}

@end
