//
//  FeedbackManager.swift
//  iosApp
//
//  Created by Kevin Schildhorn on 4/30/19.
//  Copyright © 2019 Kevin Galligan. All rights reserved.
//

import UIKit
import lib

class FeedbackManager: NSObject,FeedbackApi, FeedbackDialogDelegate {
    
    var viewController:UIViewController?
    private var feedbackModel:FeedbackModel = FeedbackModel()
    
    func setViewController(_ vc: UIViewController){
        viewController = vc
    }
    
    func showFeedbackForPastSessions(){
        if(NotificationsKt.feedbackEnabled()) {
            feedbackModel.showFeedbackForPastSessions(listener: self)
        }
    }
    
    func close(){
        viewController?.dismiss(animated: true, completion: nil)
        viewController = nil
    }
    
    func disableFeedback(){
        NotificationsKt.setFeedbackEnabled(enabled: false)
    }
    
    func generateFeedbackDialog(session: MyPastSession) {
        let alert = FeedbackAlertViewController(preferredStyle: .alert,sessionid: session.id,sessionTitle: session.title)
        alert.setFeedbackDialogDelegate(feedbackDialogDelegate: self)
        viewController?.present(alert, animated: true, completion: nil)
    }
    
    func finishedFeedback(sessionId:String, rating:Int, comment: String) {
        feedbackModel.finishedFeedback(sessionId: sessionId,rating: Int32(rating),comment: comment)
    }
    
    func onError(error: FeedbackApiFeedBackError) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

