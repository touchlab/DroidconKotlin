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

class MaterialTabNavController: UITabBarController, MDCBottomNavigationBarDelegate, FeedbackDialogDelegate {
    
    let bottomNavBar = MDCBottomNavigationBar()
    
    override func viewDidLoad() {
        super.viewDidLoad()
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
        let bottomNavBarFrame = CGRect(x: 0,
                                       y: view.bounds.height - size.height,
                                       width: size.width,
                                       height: size.height)
        bottomNavBar.frame = bottomNavBarFrame
    }
    
    override var preferredStatusBarStyle: UIStatusBarStyle {
        return .lightContent
    }
    
    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

    private func showFeedbackAlert(session:MySessions){
        let alert = FeedbackAlertViewController(preferredStyle: .alert,sessionid: session.id,sessionTitle: session.title)
        alert.setFeedbackDialogDelegate(feedbackDialogDelegate: self)
        self.present(alert, animated: true, completion: nil)
    }
    
    func finishedFeedback(sessionId: String, rating: Int, comment: String) {
        AppContext().dbHelper.updateFeedback(feedbackRating: NSNumber(value: rating) as! KotlinLong, feedbackComment: comment, id: sessionId)

    }
}
