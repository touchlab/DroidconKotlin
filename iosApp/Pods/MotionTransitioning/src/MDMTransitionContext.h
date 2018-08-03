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

@protocol MDMTransition;

/**
 The possible directions of a transition.
 */
typedef NS_ENUM(NSUInteger, MDMTransitionDirection) {
  /**
   The fore view controller is being presented.
   */
  MDMTransitionDirectionForward,

  /**
   The fore view controller is being dismissed.
   */
  MDMTransitionDirectionBackward,
} NS_SWIFT_NAME(TransitionDirection);

/**
 A presentation info instance contains objects related to a transition.
 */
NS_SWIFT_NAME(TransitionContext)
@protocol MDMTransitionContext

/**
 Informs the context that the transition has ended.
 */
- (void)transitionDidEnd;

/**
 The direction this transition is moving in.
 */
@property(nonatomic, readonly) MDMTransitionDirection direction;

/**
 The duration of this transition.
 */
@property(nonatomic, readonly) NSTimeInterval duration;

/**
 The source view controller for this transition.

 This is the view controller that initiated the transition.
 */
@property(nonatomic, strong, readonly, nullable) UIViewController *sourceViewController;

/**
 The back view controller for this transition.

 This is the destination when the transition's direction is backward.
 */
@property(nonatomic, strong, readonly, nonnull) UIViewController *backViewController;

/**
 The fore view controller for this transition.

 This is the destination when the transition's direction is forward.
 */
@property(nonatomic, strong, readonly, nonnull) UIViewController *foreViewController;

/**
 The container view for the transition as reported by UIKit's transition context.
 */
@property(nonatomic, strong, readonly, nonnull) UIView *containerView;

/**
 The presentation view controller for this transition.
 */
@property(nonatomic, strong, readonly, nullable) UIPresentationController *presentationController;

/**
 Adds the provided transition as a child of the current transition and invokes its start method.

 Each child transition will receive its own transition context instance to which the transition must
 eventually invoke transitionDidEnd. Only once both the parent transition and all of its children
 (and their children) have completed will the overall view controller transition be completed.
 */
- (void)composeWithTransition:(nonnull id<MDMTransition>)transition;

/**
 Defers execution of the provided work until the completion of the transition.

 Upon completion, each block of work will be executed in the order it was provided to the context.
 */
- (void)deferToCompletion:(void (^ _Nonnull)(void))work;

@end
