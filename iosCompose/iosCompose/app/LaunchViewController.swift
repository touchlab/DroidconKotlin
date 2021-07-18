//
//  LaunchViewController.swift
//  iosApp
//
//  Created by Kevin Galligan on 7/13/18.
//  Copyright © 2018 Kevin Galligan. All rights reserved.
//

import UIKit

class LaunchViewController: UIViewController{
    override func viewDidAppear(_ animated: Bool)
    {
//        let viewModel = DVMConferenceDataViewModel.forIosWithBoolean(true)
//        let appScreen = viewModel.goToScreen()
        let segueName = "Schedule"
        performSegue(withIdentifier: segueName, sender: self)
    }
    
    override var preferredStatusBarStyle: UIStatusBarStyle {
        return .lightContent
    }
}
