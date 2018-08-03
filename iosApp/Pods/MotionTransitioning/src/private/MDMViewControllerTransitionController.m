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

#import "MDMViewControllerTransitionController.h"

#import "MDMTransition.h"
#import "MDMViewControllerTransitionCoordinator.h"

@interface MDMViewControllerTransitionController () <UIViewControllerTransitioningDelegate, MDMViewControllerTransitionCoordinatorDelegate>
@end

@implementation MDMViewControllerTransitionController {
  // We expect the view controller to hold a strong reference to its transition controller, so keep
  // a weak reference to the view controller here.
  __weak UIViewController *_associatedViewController;

  __weak UIPresentationController *_presentationController;

  MDMViewControllerTransitionCoordinator *_coordinator;
  __weak UIViewController *_source;
}

@synthesize transition = _transition;

- (nonnull instancetype)initWithViewController:(nonnull UIViewController *)viewController {
  self = [super init];
  if (self) {
    _associatedViewController = viewController;
  }
  return self;
}

#pragma mark - Public

- (void)setTransition:(id<MDMTransition>)transition {
  _transition = transition;

  // Set the default modal presentation style.
  id<MDMTransitionWithPresentation> withPresentation = [self presentationTransition];
  if (withPresentation != nil) {
    UIModalPresentationStyle style = [withPresentation defaultModalPresentationStyle];
    _associatedViewController.modalPresentationStyle = style;
  }
}

- (id<MDMTransition>)activeTransition {
  return [self.activeTransitions firstObject];
}

- (NSArray<id<MDMTransition>> *)activeTransitions {
  return [_coordinator activeTransitions];
}

- (id<MDMTransitionWithPresentation>)presentationTransition {
  if ([self.transition respondsToSelector:@selector(defaultModalPresentationStyle)]) {
    return (id<MDMTransitionWithPresentation>)self.transition;
  }
  return nil;
}

#pragma mark - UIViewControllerTransitioningDelegate

// Animated transitions

- (id<UIViewControllerAnimatedTransitioning>)animationControllerForPresentedController:(UIViewController *)presented
                                                                  presentingController:(UIViewController *)presenting
                                                                      sourceController:(UIViewController *)source {
  _source = source;

  [self prepareForTransitionWithSourceViewController:source
                                  backViewController:presenting
                                  foreViewController:presented
                                           direction:MDMTransitionDirectionForward];
  return _coordinator;
}

- (id<UIViewControllerAnimatedTransitioning>)animationControllerForDismissedController:(UIViewController *)dismissed {
  [self prepareForTransitionWithSourceViewController:_source
                                  backViewController:dismissed.presentingViewController
                                  foreViewController:dismissed
                                           direction:MDMTransitionDirectionBackward];
  return _coordinator;
}

// Presentation

- (UIPresentationController *)presentationControllerForPresentedViewController:(UIViewController *)presented
                                                      presentingViewController:(UIViewController *)presenting
                                                          sourceViewController:(UIViewController *)source {
  id<MDMTransitionWithPresentation> withPresentation = [self presentationTransition];
  if (withPresentation == nil) {
    return nil;
  }
  UIPresentationController *presentationController =
      [withPresentation presentationControllerForPresentedViewController:presented
                                                presentingViewController:presenting
                                                    sourceViewController:source];
  // _presentationController is weakly-held, so we have to do this local var dance to keep it
  // from being nil'd on assignment.
  _presentationController = presentationController;
  return presentationController;
}

#pragma mark - MDMViewControllerTransitionCoordinatorDelegate

- (void)transitionDidCompleteWithCoordinator:(MDMViewControllerTransitionCoordinator *)coordinator {
  if (_coordinator == coordinator) {
    _coordinator = nil;
  }
}

#pragma mark - Private

- (void)prepareForTransitionWithSourceViewController:(nullable UIViewController *)source
                                  backViewController:(nonnull UIViewController *)back
                                  foreViewController:(nonnull UIViewController *)fore
                                           direction:(MDMTransitionDirection)direction {
  if (direction == MDMTransitionDirectionBackward) {
    _coordinator = nil;
  }
  NSAssert(!_coordinator, @"A transition is already active.");

  _coordinator = [[MDMViewControllerTransitionCoordinator alloc] initWithTransition:self.transition
                                                                                direction:direction
                                                                     sourceViewController:source
                                                                       backViewController:back
                                                                       foreViewController:fore
                                                                   presentationController:_presentationController];
  _coordinator.delegate = self;
}

@end
