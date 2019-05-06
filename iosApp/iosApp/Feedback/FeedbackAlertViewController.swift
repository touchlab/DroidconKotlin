//
//  FeedbackAlertViewController.swift
//  iosApp
//
//  Created by Kevin Schildhorn on 4/5/19.
//  Copyright © 2019 Kevin Galligan. All rights reserved.
//

import UIKit
import lib


class FeedbackAlertViewController: UIAlertController {

    private var feedbackDialogDelegate:FeedbackDialogDelegate?

    
    convenience init(preferredStyle: UIAlertControllerStyle,sessionid:String?,sessionTitle:String, feedbackManager: FeedbackManager){
        self.init(title: nil, message: nil, preferredStyle: preferredStyle)
        
        let customView = FeedbackView.createFeedbackView()
        if let feedbackView = customView {
            feedbackView.setAlertView(alertView: self)
            feedbackView.setFeedbackManager(fbManager: feedbackManager)
            feedbackView.setSessionInfo(sessionId: sessionid, sessionTitle: sessionTitle)
            self.view.addSubview(feedbackView)
            
            // Constraints
            feedbackView.translatesAutoresizingMaskIntoConstraints = false
            feedbackView.topAnchor.constraint(equalTo: self.view.topAnchor, constant: 0).isActive = true
            feedbackView.rightAnchor.constraint(equalTo: self.view.rightAnchor, constant: 0).isActive = true
            feedbackView.leftAnchor.constraint(equalTo: self.view.leftAnchor, constant: 0).isActive = true
            feedbackView.heightAnchor.constraint(equalToConstant: 350).isActive = true
            self.view.translatesAutoresizingMaskIntoConstraints = false
            self.view.bottomAnchor.constraint(equalTo: feedbackView.bottomAnchor, constant: 20).isActive = true
        }
        
        // This is being hidden by a custom close button
        self.addAction(UIAlertAction(title: NSLocalizedString("Cancel", comment: "Default action"), style: .default, handler: { _ in
        }))
    }
    
    func closeWithFeedback(sessionId:String,rating:FeedbackRating, comments: String){
        feedbackDialogDelegate?.finishedFeedback(sessionId: String(sessionId),rating: rating.rawValue,comment: comments)
        close()
    }
    
    func close(){
        dismiss(animated: true, completion: nil)
    }
    
    func setFeedbackDialogDelegate(feedbackDialogDelegate: FeedbackDialogDelegate){
        self.feedbackDialogDelegate = feedbackDialogDelegate
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
    }
}
