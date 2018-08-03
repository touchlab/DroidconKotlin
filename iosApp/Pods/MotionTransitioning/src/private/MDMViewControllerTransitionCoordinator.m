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

#import "MDMViewControllerTransitionCoordinator.h"

#import "MDMTransition.h"

@class MDMViewControllerTransitionContextNode;

@protocol MDMViewControllerTransitionContextNodeParent <NSObject>
- (void)childNodeTransitionDidEnd:(MDMViewControllerTransitionContextNode *)childNode;
@end

@interface MDMViewControllerTransitionContextNode : NSObject <MDMTransitionContext, MDMViewControllerTransitionContextNodeParent>
@property(nonatomic, strong) id<UIViewControllerContextTransitioning> transitionContext;
@property(nonatomic, strong, readonly) id<MDMTransition> transition;
@property(nonatomic, copy, readonly) NSMutableArray<MDMViewControllerTransitionContextNode *> *children;
@end

@implementation MDMViewControllerTransitionContextNode {
  // Every node points to the same array in memory.
  NSMutableArray *_sharedCompletionBlocks;

  BOOL _hasStarted;
  BOOL _didEnd;
  __weak id<MDMViewControllerTransitionContextNodeParent> _parent;
}

@synthesize duration = _duration;
@synthesize direction = _direction;
@synthesize sourceViewController = _sourceViewController;
@synthesize backViewController = _backViewController;
@synthesize foreViewController = _foreViewController;
@synthesize presentationController = _presentationController;

- (instancetype)initWithTransition:(id<MDMTransition>)transition
                         direction:(MDMTransitionDirection)direction
              sourceViewController:(UIViewController *)sourceViewController
                backViewController:(UIViewController *)backViewController
                foreViewController:(UIViewController *)foreViewController
            presentationController:(UIPresentationController *)presentationController
            sharedCompletionBlocks:(NSMutableArray *)sharedCompletionBlocks
                            parent:(id<MDMViewControllerTransitionContextNodeParent>)parent {
  self = [super init];
  if (self) {
    _children = [NSMutableArray array];
    _transition = transition;
    _direction = direction;
    _sourceViewController = sourceViewController;
    _backViewController = backViewController;
    _foreViewController = foreViewController;
    _presentationController = presentationController;
    _sharedCompletionBlocks = sharedCompletionBlocks;
    _parent = parent;
  }
  return self;
}

#pragma mark - Private

- (MDMViewControllerTransitionContextNode *)spawnChildWithTransition:(id<MDMTransition>)transition {
  MDMViewControllerTransitionContextNode *node =
    [[MDMViewControllerTransitionContextNode alloc] initWithTransition:transition
                                                             direction:_direction
                                                  sourceViewController:_sourceViewController
                                                    backViewController:_backViewController
                                                    foreViewController:_foreViewController
                                                presentationController:_presentationController
                                                sharedCompletionBlocks:_sharedCompletionBlocks
                                                                parent:self];
  node.transitionContext = _transitionContext;
  return node;
}

- (void)checkAndNotifyOfCompletion {
  BOOL anyChildActive = NO;
  for (MDMViewControllerTransitionContextNode *child in _children) {
    if (!child->_didEnd) {
      anyChildActive = YES;
      break;
    }
  }

  if (!anyChildActive && _didEnd) { // Inform our parent of completion.
    [_parent childNodeTransitionDidEnd:self];
  }
}

#pragma mark - Public

- (void)start {
  if (_hasStarted) {
    return;
  }

  _hasStarted = YES;

  for (MDMViewControllerTransitionContextNode *child in _children) {
    [child attemptFallback];

    [child start];
  }

  if ([_transition respondsToSelector:@selector(startWithContext:)]) {
    [_transition startWithContext:self];
  } else {
    _didEnd = YES;

    [self checkAndNotifyOfCompletion];
  }
}

- (NSArray *)activeTransitions {
  NSMutableArray *activeTransitions = [NSMutableArray array];
  if (!_didEnd) {
    [activeTransitions addObject:self];
  }
  for (MDMViewControllerTransitionContextNode *child in _children) {
    [activeTransitions addObjectsFromArray:[child activeTransitions]];
  }
  return activeTransitions;
}

- (void)setTransitionContext:(id<UIViewControllerContextTransitioning>)transitionContext {
  _transitionContext = transitionContext;

  for (MDMViewControllerTransitionContextNode *child in _children) {
    child.transitionContext = transitionContext;
  }
}

- (void)setDuration:(NSTimeInterval)duration {
  _duration = duration;

  for (MDMViewControllerTransitionContextNode *child in _children) {
    child.duration = duration;
  }
}

- (void)attemptFallback {
  id<MDMTransition> transition = _transition;
  while ([transition respondsToSelector:@selector(fallbackTransitionWithContext:)]) {
    id<MDMTransitionWithFallback> withFallback = (id<MDMTransitionWithFallback>)transition;

    id<MDMTransition> fallback = [withFallback fallbackTransitionWithContext:self];
    if (fallback == transition) {
      break;
    }
    transition = fallback;
  }
  _transition = transition;
}

#pragma mark - MDMViewControllerTransitionContextNodeDelegate

- (void)childNodeTransitionDidEnd:(MDMViewControllerTransitionContextNode *)contextNode {
  [self checkAndNotifyOfCompletion];
}

#pragma mark - MDMTransitionContext

- (void)composeWithTransition:(id<MDMTransition>)transition {
  MDMViewControllerTransitionContextNode *child = [self spawnChildWithTransition:transition];

  [_children addObject:child];

  if (_hasStarted) {
    [child start];
  }
}

- (UIView *)containerView {
  return _transitionContext.containerView;
}

- (void)deferToCompletion:(void (^)(void))work {
  [_sharedCompletionBlocks addObject:[work copy]];
}

- (void)transitionDidEnd {
  if (_didEnd) {
    return; // No use in re-notifying.
  }
  _didEnd = YES;

  [self checkAndNotifyOfCompletion];
}

@end

@interface MDMViewControllerTransitionCoordinator() <MDMViewControllerTransitionContextNodeParent>
@end

@implementation MDMViewControllerTransitionCoordinator {
  MDMTransitionDirection _direction;
  UIPresentationController *_presentationController;

  MDMViewControllerTransitionContextNode *_root;
  NSMutableArray *_completionBlocks;

  id<UIViewControllerContextTransitioning> _transitionContext;
}

- (instancetype)initWithTransition:(NSObject<MDMTransition> *)transition
                         direction:(MDMTransitionDirection)direction
              sourceViewController:(UIViewController *)sourceViewController
                backViewController:(UIViewController *)backViewController
                foreViewController:(UIViewController *)foreViewController
            presentationController:(UIPresentationController *)presentationController {
  self = [super init];
  if (self) {
    _direction = direction;
    _presentationController = presentationController;

    _completionBlocks = [NSMutableArray array];

    // Build our contexts:

    _root = [[MDMViewControllerTransitionContextNode alloc] initWithTransition:transition
                                                                     direction:direction
                                                          sourceViewController:sourceViewController
                                                            backViewController:backViewController
                                                            foreViewController:foreViewController
                                                        presentationController:presentationController
                                                        sharedCompletionBlocks:_completionBlocks
                                                                        parent:self];

    if (_presentationController
        && [_presentationController respondsToSelector:@selector(startWithContext:)]) {
      MDMViewControllerTransitionContextNode *presentationNode =
        [[MDMViewControllerTransitionContextNode alloc] initWithTransition:(id<MDMTransition>)_presentationController
                                                                 direction:direction
                                                      sourceViewController:sourceViewController
                                                        backViewController:backViewController
                                                        foreViewController:foreViewController
                                                    presentationController:presentationController
                                                    sharedCompletionBlocks:_completionBlocks
                                                                    parent:_root];
      [_root.children addObject:presentationNode];
    }

    if ([transition respondsToSelector:@selector(canPerformTransitionWithContext:)]) {
      id<MDMTransitionWithFeasibility> withFeasibility = (id<MDMTransitionWithFeasibility>)transition;
      if (![withFeasibility canPerformTransitionWithContext:_root]) {
        self = nil;
        return nil; // No active transitions means no need for a coordinator.
      }
    }
  }
  return self;
}

#pragma mark - MDMViewControllerTransitionContextNodeDelegate

- (void)childNodeTransitionDidEnd:(MDMViewControllerTransitionContextNode *)node {
  if (_root != nil && _root == node) {
    _root = nil;

    for (void (^work)(void) in _completionBlocks) {
      work();
    }
    [_completionBlocks removeAllObjects];

    [_transitionContext completeTransition:true];
    _transitionContext = nil;

    [_delegate transitionDidCompleteWithCoordinator:self];
  }
}

#pragma mark - UIViewControllerAnimatedTransitioning

- (NSTimeInterval)transitionDuration:(id<UIViewControllerContextTransitioning>)transitionContext {
  NSTimeInterval duration = 0.35;
  if ([_root.transition respondsToSelector:@selector(transitionDurationWithContext:)]) {
    id<MDMTransitionWithCustomDuration> withCustomDuration = (id<MDMTransitionWithCustomDuration>)_root.transition;
    duration = [withCustomDuration transitionDurationWithContext:_root];
  }
  _root.duration = duration;
  return duration;
}

- (void)animateTransition:(id<UIViewControllerContextTransitioning>)transitionContext {
  _transitionContext = transitionContext;

  [self initiateTransition];
}

// TODO(featherless): Implement interactive transitioning. Need to implement
// UIViewControllerInteractiveTransitioning here and isInteractive and interactionController* in
// MDMViewControllerTransitionController.

- (NSArray<NSObject<MDMTransition> *> *)activeTransitions {
  return [_root activeTransitions];
}

#pragma mark - Private

- (void)initiateTransition {
  _root.transitionContext = _transitionContext;

  UIViewController *from = [_transitionContext viewControllerForKey:UITransitionContextFromViewControllerKey];
  UIView *fromView = [_transitionContext viewForKey:UITransitionContextFromViewKey];
  if (fromView == nil) {
    fromView = from.view;
  }
  if (fromView != nil && fromView == from.view) {
    CGRect finalFrame = [_transitionContext finalFrameForViewController:from];
    if (!CGRectIsEmpty(finalFrame)) {
      fromView.frame = finalFrame;
    }
  }

  UIViewController *to = [_transitionContext viewControllerForKey:UITransitionContextToViewControllerKey];
  UIView *toView = [_transitionContext viewForKey:UITransitionContextToViewKey];
  if (toView == nil) {
    toView = to.view;
  }
  if (toView != nil && toView == to.view) {
    CGRect finalFrame = [_transitionContext finalFrameForViewController:to];
    if (!CGRectIsEmpty(finalFrame)) {
      toView.frame = finalFrame;
    }

    if (toView.superview == nil) {
      switch (_direction) {
        case MDMTransitionDirectionForward:
          [_transitionContext.containerView addSubview:toView];
          break;

        case MDMTransitionDirectionBackward:
          [_transitionContext.containerView insertSubview:toView atIndex:0];
          break;
      }
    }
  }

  [toView layoutIfNeeded];

  [_root attemptFallback];
  [self anticipateOnlyExplicitAnimations];

  [CATransaction begin];
  [CATransaction setAnimationDuration:[self transitionDuration:_transitionContext]];

  [_root start];

  [CATransaction commit];
}

// UIKit transitions will not animate any of the system animations (status bar changes, notably)
// unless we have at least one implicit UIView animation. Material Motion doesn't use implicit
// animations out of the box, so to ensure that system animations still occur we create an
// invisible throwaway view and apply an animation to it.
- (void)anticipateOnlyExplicitAnimations {
  UIView *throwawayView = [[UIView alloc] init];
  [_transitionContext.containerView addSubview:throwawayView];

  [UIView animateWithDuration:[self transitionDuration:_transitionContext]
                   animations:^{
                     throwawayView.frame = CGRectOffset(throwawayView.frame, 1, 0);

                   }
                   completion:^(BOOL finished) {
                     [throwawayView removeFromSuperview];
                   }];
}

@end
