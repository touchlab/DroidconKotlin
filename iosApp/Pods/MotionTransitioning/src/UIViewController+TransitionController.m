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

#import "UIViewController+TransitionController.h"

#import "private/MDMViewControllerTransitionController.h"

#import <objc/runtime.h>

@implementation UIViewController (MDMTransitionController)

#pragma mark - Public

- (id<MDMTransitionController>)mdm_transitionController {
  const void *key = [self mdm_transitionControllerKey];

  MDMViewControllerTransitionController *controller = objc_getAssociatedObject(self, key);
  if (!controller) {
    controller = [[MDMViewControllerTransitionController alloc] initWithViewController:self];
    [self mdm_setTransitionController:controller];
  }
  return controller;
}

#pragma mark - Private

- (void)mdm_setTransitionController:(MDMViewControllerTransitionController *)controller {
  const void *key = [self mdm_transitionControllerKey];

  // Clear the previous delegate if we'd previously set one.
  MDMViewControllerTransitionController *existingController = objc_getAssociatedObject(self, key);
  id<UIViewControllerTransitioningDelegate> delegate = self.transitioningDelegate;
  if (existingController == delegate) {
    self.transitioningDelegate = nil;
  }

  objc_setAssociatedObject(self, key, controller, OBJC_ASSOCIATION_RETAIN_NONATOMIC);

  if (!delegate) {
    self.transitioningDelegate = controller;
  }
}

- (const void *)mdm_transitionControllerKey {
  return @selector(mdm_transitionController);
}

@end
