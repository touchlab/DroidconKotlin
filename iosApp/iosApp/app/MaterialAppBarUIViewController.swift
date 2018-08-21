//
//  MaterialAppBarUIViewController.swift
//  iosApp
//
//  Created by Kevin Galligan on 7/30/18.
//  Copyright © 2018 Kevin Galligan. All rights reserved.
//

import UIKit

import MaterialComponents.MaterialAppBar
import MaterialComponents.MaterialAppBar_ColorThemer
import MaterialComponents.MaterialAppBar_TypographyThemer

class MaterialAppBarUIViewController: UIViewController {

    let appBar = MDCAppBar()
    
    deinit {
        // Required for pre-iOS 11 devices because we've enabled observesTrackingScrollViewScrollEvents.
        appBar.headerViewController.headerView.trackingScrollView = nil
    }
    
    init(){
        super.init(nibName: nil, bundle: nil)
        self.addChildViewController(appBar.headerViewController)
    }
    
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        self.addChildViewController(appBar.headerViewController)
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()

        MDCAppBarColorThemer.applySemanticColorScheme(ApplicationScheme.shared.colorScheme
            , to:self.appBar)

        MDCAppBarTypographyThemer.applyTypographyScheme(ApplicationScheme.shared.typographyScheme, to: appBar)
        
        self.view.backgroundColor = ApplicationScheme.shared.colorScheme.surfaceColor
        appBar.addSubviewsToParent()
        appBar.navigationBar.observe(self.navigationItem)
        
    }
    
    // Optional step: The Header View Controller does basic inspection of the header view's background
    //                color to identify whether the status bar should be light or dark-themed.
    override var childViewControllerForStatusBarStyle: UIViewController? {
        return appBar.headerViewController
    }
    
    override var preferredStatusBarStyle: UIStatusBarStyle {
        return .lightContent
    }
}
