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

#import "MDMTransitionViewSnapshotter.h"

static UIView *FastSnapshotOfView(UIView *view) {
  return [view snapshotViewAfterScreenUpdates:NO];
}

static UIView *SlowSnapshotOfView(UIView *view) {
  UIGraphicsBeginImageContextWithOptions(view.bounds.size, NO, 0);
  [view.layer renderInContext:UIGraphicsGetCurrentContext()];
  UIImage *copied = UIGraphicsGetImageFromCurrentImageContext();
  UIView *copiedView = [[UIImageView alloc] initWithImage:copied];
  UIGraphicsEndImageContext();
  return copiedView;
}

@implementation MDMTransitionViewSnapshotter {
  UIView *_containerView;
  NSMutableArray *_snapshotViews;
  NSMutableArray *_hiddenViews;
}

- (void)dealloc {
  for (UIView *view in _snapshotViews) {
    [view removeFromSuperview];
  }
  for (UIView *view in _hiddenViews) {
    view.hidden = NO;
  }
}

- (instancetype)initWithContainerView:(UIView *)containerView {
  self = [super init];
  if (self) {
    _containerView = containerView;

    _snapshotViews = [NSMutableArray array];
    _hiddenViews = [NSMutableArray array];
  }
  return self;
}

- (UIView *)snapshotOfView:(UIView *)view isAppearing:(BOOL)isAppearing {
  UIView *snapshotView;
  if ([view isKindOfClass:[UIImageView class]]) {
    snapshotView = [self richReplicaOfImageView:(UIImageView *)view];

  } else {
    snapshotView = isAppearing ? SlowSnapshotOfView(view) : FastSnapshotOfView(view);
  }

  snapshotView.layer.borderColor = view.layer.borderColor;
  snapshotView.layer.borderWidth = view.layer.borderWidth;
  snapshotView.layer.cornerRadius = view.layer.cornerRadius;
  snapshotView.layer.shadowColor = view.layer.shadowColor;
  snapshotView.layer.shadowOffset = view.layer.shadowOffset;
  snapshotView.layer.shadowOpacity = view.layer.shadowOpacity;
  snapshotView.layer.shadowPath = view.layer.shadowPath;
  snapshotView.layer.shadowRadius = view.layer.shadowRadius;

  snapshotView.layer.position = [_containerView convertPoint:view.layer.position fromView:view.superview];
  snapshotView.layer.bounds = view.layer.bounds;
  snapshotView.layer.transform = view.layer.transform;

  [_containerView addSubview:snapshotView];
  [_snapshotViews addObject:snapshotView];

  [_hiddenViews addObject:view];
  view.hidden = YES;

  return snapshotView;
}

- (void)removeAllSnapshots {
  for (UIView *view in _snapshotViews) {
    [view removeFromSuperview];
  }
  for (UIView *view in _hiddenViews) {
    view.hidden = NO;
  }

  [_snapshotViews removeAllObjects];
  [_hiddenViews removeAllObjects];
}

#pragma mark - Private

- (UIView *)richReplicaOfImageView:(UIImageView *)imageView {
  UIImageView *copiedImageView = [[UIImageView alloc] init];

  copiedImageView.image = imageView.image;
  copiedImageView.highlightedImage = imageView.highlightedImage;

  copiedImageView.animationImages = imageView.animationImages;
  copiedImageView.highlightedAnimationImages = imageView.highlightedAnimationImages;
  copiedImageView.animationDuration = imageView.animationDuration;
  copiedImageView.animationRepeatCount = imageView.animationRepeatCount;

  [self copyPropertiesFrom:imageView toView:copiedImageView];

  return copiedImageView;
}

- (void)copyPropertiesFrom:(UIView *)view toView:(UIView *)copiedView {
  copiedView.clipsToBounds = view.clipsToBounds;
  copiedView.backgroundColor = view.backgroundColor;
  copiedView.alpha = view.alpha;
  copiedView.opaque = view.isOpaque;
  copiedView.clearsContextBeforeDrawing = view.clearsContextBeforeDrawing;
  copiedView.hidden = view.isHidden;
  copiedView.contentMode = view.contentMode;
  copiedView.maskView = view.maskView;
  copiedView.tintColor = view.tintColor;
  copiedView.userInteractionEnabled = view.isUserInteractionEnabled;
}

@end
