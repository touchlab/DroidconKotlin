#ifdef __OBJC__
#import <UIKit/UIKit.h>
#else
#ifndef FOUNDATION_EXPORT
#if defined(__cplusplus)
#define FOUNDATION_EXPORT extern "C"
#else
#define FOUNDATION_EXPORT extern
#endif
#endif
#endif

#import "MDMTransition.h"
#import "MDMTransitionContext.h"
#import "MDMTransitionController.h"
#import "MDMTransitionNavigationControllerDelegate.h"
#import "MDMTransitionPresentationController.h"
#import "MDMTransitionViewSnapshotter.h"
#import "MotionTransitioning.h"
#import "UIViewController+TransitionController.h"

FOUNDATION_EXPORT double MotionTransitioningVersionNumber;
FOUNDATION_EXPORT const unsigned char MotionTransitioningVersionString[];

