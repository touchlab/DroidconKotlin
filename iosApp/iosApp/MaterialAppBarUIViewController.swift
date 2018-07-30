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
        //        self.collectionView?.backgroundColor = ApplicationScheme.shared.colorScheme
        //            .surfaceColor
        
        // Allows us to avoid forwarding events, but means we can't enable shift behaviors.
        //        appBar.headerViewController.headerView.observesTrackingScrollViewScrollEvents = true
        
        // Recommended step: Set the tracking scroll view.
        //        appBar.headerViewController.headerView.trackingScrollView = self.tableView
        
        // Step 2: Register the App Bar views.
        appBar.addSubviewsToParent()
        
        appBar.navigationBar.observe(self.navigationItem)
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}
