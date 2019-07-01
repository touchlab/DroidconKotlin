//
//  FeedbackManager.swift
//  iosApp
//
//  Created by Kevin Schildhorn on 4/30/19.
//  Copyright © 2019 Kevin Galligan. All rights reserved.
//

import UIKit
import lib

class FeedbackManager: NSObject,FeedbackApi {
    
    static let FeedbackDisabledNotificationName = "FeedbackDisabled"
    
    var viewController:UIViewController?
    private var feedbackModel:FeedbackModel = FeedbackModel()
    
    func setViewController(_ vc: UIViewController){
        viewController = vc
    }
    
    func showFeedbackForPastSessions(){
        if(NotificationsModel().feedbackEnabled()) {
            feedbackModel.showFeedbackForPastSessions(listener: self)
        }
    }
    
    func close(){
        viewController?.dismiss(animated: true, completion: nil)
        viewController = nil
    }
    
    func disableFeedback(){
        NotificationsModel().setFeedbackEnabled(enabled: false)
        NotificationCenter.default.post(name: Notification.Name(FeedbackDisabledNotificationName), object: nil)
    }
    
    func generateFeedbackDialog(session: MyPastSession) {
        let test = viewController?.storyboard?.instantiateViewController(withIdentifier: "Feedback")
        let feedbackView = test as! FeedbackViewController
        feedbackView.providesPresentationContextTransitionStyle = true
        feedbackView.definesPresentationContext = true
        feedbackView.modalPresentationStyle = UIModalPresentationStyle.overCurrentContext
        feedbackView.modalTransitionStyle = UIModalTransitionStyle.crossDissolve
        viewController?.present(feedbackView, animated: true, completion: nil)
        feedbackView.setFeedbackManager(fbManager: self)
        feedbackView.setSessionInfo(sessionId: session.id, sessionTitle: session.title)
    }
    
    func finishedFeedback(sessionId:String, rating:Int, comment: String) {
        feedbackModel.finishedFeedback(sessionId: sessionId,rating: Int32(rating),comment: comment)
    }
    
    func onError(error: FeedbackApiFeedBackError) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

