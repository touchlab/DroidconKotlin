//
//  MaterialTabNavController.swift
//  iosApp
//
//  Created by Kevin Galligan on 7/29/18.
//  Copyright © 2018 Kevin Galligan. All rights reserved.
//

import UIKit
import lib
import MaterialComponents

class MaterialTabNavController: UITabBarController, MDCBottomNavigationBarDelegate {

    private var feedbackManager = FeedbackManager()

    let bottomNavBar = MDCBottomNavigationBar()
    let firebaseMessageHandler = FirebaseMessageHandler()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        // Disable inclusion of safe area in size calculations.
        bottomNavBar.sizeThatFitsIncludesSafeArea = false
        
        bottomNavBar.items = tabBar.items!
        if(tabBar.selectedItem == nil){
            bottomNavBar.selectedItem = bottomNavBar.items[0]
        }else{
            bottomNavBar.selectedItem = tabBar.selectedItem
        }

        view.addSubview(bottomNavBar)

        bottomNavBar.delegate = self

        MDCBottomNavigationBarColorThemer.applySemanticColorScheme(ApplicationScheme.shared.menuColorScheme, toBottomNavigation: bottomNavBar)
        bottomNavBar.unselectedItemTintColor = bottomNavBar.selectedItemTintColor
//        MDCAppBarColorThemer.applySemanticColorScheme(ApplicationScheme.shared.colorScheme, to:bottomNavBar)
       
        NotificationCenter.default.addObserver(self, selector:#selector(showFeedback), name: NSNotification.Name.UIApplicationWillEnterForeground, object: nil)
        showFeedback()
    }

    @objc func showFeedback(){
        feedbackManager.setViewController(self)
        feedbackManager.showFeedbackForPastSessions()
    }

    override func viewDidDisappear(_ animated: Bool) {
        feedbackManager.close()
    }

    func bottomNavigationBar(_ bottomNavigationBar: MDCBottomNavigationBar, didSelect item: UITabBarItem) {
        var count = 0
        for barItem in bottomNavBar.items {
            if(barItem === item){
                selectedIndex = count
                break
            }
            count += 1
        }
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

    override func viewWillLayoutSubviews() {
        super.viewWillLayoutSubviews()
        layoutBottomNavBar()
    }

    #if swift(>=3.2)
    @available(iOS 11, *)
    override func viewSafeAreaInsetsDidChange() {
        super.viewSafeAreaInsetsDidChange()
        layoutBottomNavBar()
    }
    #endif

    func layoutBottomNavBar() {
        let size = bottomNavBar.sizeThatFits(view.bounds.size)
        var bottomNavBarFrame = CGRect(x: 0,
                                       y: view.bounds.height - size.height,
                                       width: size.width,
                                       height: size.height)
        // Extend the Bottom Navigation to the bottom of the screen.
        if #available(iOS 11.0, *) {
            bottomNavBarFrame.size.height += view.safeAreaInsets.bottom
            bottomNavBarFrame.origin.y -= view.safeAreaInsets.bottom
        }
        bottomNavBar.frame = bottomNavBarFrame
    }

    override var preferredStatusBarStyle: UIStatusBarStyle {
        return .lightContent
    }

    deinit {
        NotificationCenter.default.removeObserver(self)
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
