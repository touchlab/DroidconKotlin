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

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

@protocol MDMTransitionContext;
@protocol MDMTransitionPresentationAnimationControlling;

NS_SWIFT_NAME(TransitionFrameCalculation)
typedef CGRect (^MDMTransitionFrameCalculation)(UIPresentationController * _Nonnull);

/**
 A transition presentation controller implementation that supports animation delegation, a darkened
 overlay view, and custom presentation frames.
 
 The presentation controller will create and manage the lifecycle of the scrim view, ensuring that
 it is removed upon a completed dismissal of the presented view controller.
 */
NS_SWIFT_NAME(TransitionPresentationController)
@interface MDMTransitionPresentationController : UIPresentationController

/**
 Initializes a presentation controller with the standard values and a frame calculation block.
 
 The frame calculation block is expected to return the desired frame of the presented view
 controller.
 */
- (nonnull instancetype)initWithPresentedViewController:(nonnull UIViewController *)presentedViewController
                               presentingViewController:(nonnull UIViewController *)presentingViewController
                          calculateFrameOfPresentedView:(nullable MDMTransitionFrameCalculation)calculateFrameOfPresentedView
NS_DESIGNATED_INITIALIZER;

/**
 The presentation controller's scrim view.
 */
@property(nonatomic, strong, nullable, readonly) UIView * scrimView;

/**
 The animation controller is able to customize animations in reaction to view controller
 presentation and dismissal events.

 The animation controller is explicitly nil'd upon completion of the dismissal transition.
 */
@property(nonatomic, strong, nullable) id <MDMTransitionPresentationAnimationControlling> animationController;

@end

/**
 An animation controller receives additional presentation- and dismissal-related events during a
 view controller transition.
 */
NS_SWIFT_NAME(TransitionPresentationAnimationControlling)
@protocol MDMTransitionPresentationAnimationControlling <NSObject>
@optional

/**
 Allows the receiver to register animations for the given transition context.

 Invoked prior to the Transition instance's startWithContext.
 
 If not implemented, the scrim view will be faded in during presentation and out during dismissal.
 */
- (void)presentationController:(nonnull MDMTransitionPresentationController *)presentationController
              startWithContext:(nonnull NSObject<MDMTransitionContext> *)context;

/**
 Informs the receiver that the dismissal transition is about to begin.
 */
- (void)dismissalTransitionWillBeginWithPresentationController:(nonnull MDMTransitionPresentationController *)presentationController;

/**
 Informs the receiver that the dismissal transition has completed.
 */
- (void)presentationController:(nonnull MDMTransitionPresentationController *)presentationController
     dismissalTransitionDidEnd:(BOOL)completed;

@end
