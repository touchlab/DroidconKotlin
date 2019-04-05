//
//  FeedbackCommentView.swift
//  iosApp
//
//  Created by Kevin Schildhorn on 4/5/19.
//  Copyright © 2019 Kevin Galligan. All rights reserved.
//

import UIKit

class FeedbackCommentSubView: FeedbackSubview {

    @IBOutlet weak var commentsTextView: UITextView!
    
    override func layoutSubviews() {
        super.layoutSubviews()
        commentsTextView.layer.borderColor = UIColor(white: 0.75, alpha: 1.0).cgColor
        commentsTextView.layer.borderWidth = 1.0 //make border 1px thick
    }
    
    @IBAction func doneButtonPressed(_ sender: Any) {
        feedbackHandler?.commentEntered(comment: commentsTextView.text)
        feedbackHandler?.finishedViewsFeedback()
    }
    
    
}
