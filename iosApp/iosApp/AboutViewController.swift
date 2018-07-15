//
//  AboutViewController.swift
//  ios
//
//  Created by Ramona Harrison on 7/26/16.
//  Copyright © 2016 Kevin Galligan. All rights reserved.
//

import UIKit

class AboutViewController: UIViewController {
    
    @IBOutlet weak var navBar: UINavigationBar!
    @IBOutlet weak var scrollView: UIScrollView!
    @IBOutlet weak var contentView: UIView!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        navBar.isTranslucent = false
    }
    
    override func viewDidLayoutSubviews() {
        scrollView.addSubview(contentView)
        scrollView.contentSize = contentView.frame.size
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
}
