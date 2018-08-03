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

@protocol MDMTransitionController;

@interface UIViewController (MDMTransitionController)

/**
 A transition controller may be used to implement custom transitions.

 The transition controller is lazily created upon access.

 Side effects: If the view controller's transitioningDelegate is nil when the controller is created,
 then the controller will also be set to the transitioningDelegate property.
 */
@property(nonatomic, strong, readonly, nonnull) id<MDMTransitionController> mdm_transitionController;

@end
