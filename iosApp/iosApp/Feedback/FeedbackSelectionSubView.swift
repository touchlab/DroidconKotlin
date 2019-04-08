//
//  FeedbackSelectionSubView.swift
//  iosApp
//
//  Created by Kevin Schildhorn on 4/5/19.
//  Copyright © 2019 Kevin Galligan. All rights reserved.
//

import UIKit

class FeedbackSelectionSubView: FeedbackSubview {
    @IBOutlet weak var selectionTitle: UILabel!
    
    func setSessionTitle(title:String){
        selectionTitle.text = "What did you think of \(title)?"
    }
    
    @IBAction func goodButtonPressed(_ sender: Any) {
        buttonPressed(rating: FeedbackRating.good)
    }
    
    @IBAction func okButtonPressed(_ sender: Any) {
        buttonPressed(rating: FeedbackRating.ok)
    }
    
    @IBAction func badButtonPressed(_ sender: Any) {
        buttonPressed(rating: FeedbackRating.bad)
    }
    
    private func buttonPressed(rating:FeedbackRating){
        feedbackHandler?.feedbackSelected(rating: rating)
        feedbackHandler?.finishedViewsFeedback()
    }
}
