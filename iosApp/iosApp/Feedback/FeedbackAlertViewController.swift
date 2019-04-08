//
//  FeedbackAlertViewController.swift
//  iosApp
//
//  Created by Kevin Schildhorn on 4/5/19.
//  Copyright © 2019 Kevin Galligan. All rights reserved.
//

import UIKit
import main


class FeedbackAlertViewController: UIAlertController {

    convenience init(preferredStyle: UIAlertControllerStyle,session:SessionWithRoom){
        self.init(title: nil, message: nil, preferredStyle: preferredStyle)
        
        let customView = FeedbackView.createFromNib()
        if let feedbackView = customView {
            feedbackView.setAlertView(alertView: self)
            feedbackView.setSession(session: session)
            self.view.addSubview(feedbackView)
            
            // Constraints
            feedbackView.translatesAutoresizingMaskIntoConstraints = false
            feedbackView.topAnchor.constraint(equalTo: self.view.topAnchor, constant: 0).isActive = true
            feedbackView.rightAnchor.constraint(equalTo: self.view.rightAnchor, constant: 0).isActive = true
            feedbackView.leftAnchor.constraint(equalTo: self.view.leftAnchor, constant: 0).isActive = true
            feedbackView.heightAnchor.constraint(equalToConstant: 250).isActive = true
            self.view.translatesAutoresizingMaskIntoConstraints = false
            self.view.bottomAnchor.constraint(equalTo: feedbackView.bottomAnchor, constant: 20).isActive = true
        }
        
        self.addAction(UIAlertAction(title: NSLocalizedString("Cancel", comment: "Default action"), style: .default, handler: { _ in
            NSLog("The \"OK\" alert occured.")
        }))
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
    }
}
