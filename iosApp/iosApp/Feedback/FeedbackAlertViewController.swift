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
            
            let width = UIScreen.main.bounds.width - 32
            let height = UIScreen.main.bounds.height / 2
            // Constraints
            feedbackView.translatesAutoresizingMaskIntoConstraints = false
            feedbackView.topAnchor.constraint(equalTo: self.view.topAnchor, constant: 0).isActive = true
            feedbackView.rightAnchor.constraint(equalTo: self.view.rightAnchor, constant: 0).isActive = true
            feedbackView.leftAnchor.constraint(equalTo: self.view.leftAnchor, constant: 0).isActive = true
            feedbackView.heightAnchor.constraint(equalToConstant: height).isActive = true
            feedbackView.widthAnchor.constraint(equalToConstant: width).isActive = true
            
            self.view.frame = CGRect(x: 0,y: 0,width: width,height: height)
            
            self.view.translatesAutoresizingMaskIntoConstraints = false
            self.view.bottomAnchor.constraint(equalTo: feedbackView.bottomAnchor, constant: 20).isActive = true
        }
        
        
        self.title = ""

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
