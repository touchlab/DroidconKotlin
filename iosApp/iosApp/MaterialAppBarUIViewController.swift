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
        
//        appBar.navigationBar.observe(self.navigationItem)
        
    }
    
    func showArrowBack(){
        let backButtonImage = MDCIcons.imageFor_ic_arrow_back()
        
        let barItem = UIBarButtonItem(image: backButtonImage, style: .done, target: self,
                                      action: #selector(dismissMe))
        
        appBar.navigationBar.backItem = barItem
    }
    
    @objc func dismissMe(){
        navigationController?.popViewController(animated: true)
    }

    // Optional step: If you allow the header view to hide the status bar you must implement this
    //                method and return the headerViewController.
    override var childViewControllerForStatusBarHidden: UIViewController? {
        return appBar.headerViewController
    }
    
    // Optional step: The Header View Controller does basic inspection of the header view's background
    //                color to identify whether the status bar should be light or dark-themed.
    override var childViewControllerForStatusBarStyle: UIViewController? {
        return appBar.headerViewController
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        self.navigationController?.setNavigationBarHidden(true, animated: animated)
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
