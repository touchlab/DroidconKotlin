//
//  FeedbackSelectionSubView.swift
//  iosApp
//
//  Created by Kevin Schildhorn on 4/5/19.
//  Copyright © 2019 Kevin Galligan. All rights reserved.
//

import UIKit

class FeedbackRatingSubView: UIView {
    @IBOutlet weak var selectionTitle: UILabel!
    @IBOutlet weak var addCommentButton: UIButton!
    
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }
    
    private var feedbackHandler: FeedbackInteractionDelegate?
    
    @IBAction func goodButtonPressed(_ sender: Any) {
        buttonPressed(rating: FeedbackRating.good)
    }
    
    @IBAction func okButtonPressed(_ sender: Any) {
        buttonPressed(rating: FeedbackRating.ok)
    }
    
    @IBAction func badButtonPressed(_ sender: Any) {
        buttonPressed(rating: FeedbackRating.bad)
    }
    
    @IBAction func addCommentButtonPressed(_ sender: Any) {
        feedbackHandler?.requestCommentView()
    }
    
    func setFeedbackHandler(handler:FeedbackInteractionDelegate) {
        feedbackHandler = handler
    }
    
    func setSessionTitle(title:String){
        selectionTitle.text = "What did you think of \(title)?"
    }
    
    private func buttonPressed(rating:FeedbackRating){
        activateCommentButton()
        feedbackHandler?.feedbackSelected(rating: rating)
    }
    
    private func activateCommentButton(){
        addCommentButton.titleLabel?.textColor = UIColor.darkGray
        addCommentButton.isEnabled = true
    }
}
