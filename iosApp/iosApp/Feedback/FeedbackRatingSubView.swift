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
    
    @IBOutlet weak var goodButton: UIButton!
    @IBOutlet weak var okButton: UIButton!
    @IBOutlet weak var badButton: UIButton!
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }
    
    override func didMoveToSuperview() {
        super.didMoveToSuperview()
        
        let tintableImageGood = goodButton.currentBackgroundImage?.withRenderingMode(.alwaysTemplate)
        goodButton.setBackgroundImage(tintableImageGood,for: UIControlState.normal)
        
        let tintableImageOk = okButton.currentBackgroundImage?.withRenderingMode(.alwaysTemplate)
        okButton.setBackgroundImage(tintableImageOk,for: UIControlState.normal)
        
        let tintableImageBad = badButton.currentBackgroundImage?.withRenderingMode(.alwaysTemplate)
        badButton.setBackgroundImage(tintableImageBad,for: UIControlState.normal)
    }
    
    private var feedbackHandler: FeedbackInteractionDelegate?
    
    @IBAction func goodButtonPressed(_ sender: Any) {
        unhighlightButtons()
        goodButton.tintColor = UIColor.blue
        buttonPressed(rating: FeedbackRating.good)
    }
    
    @IBAction func okButtonPressed(_ sender: Any) {
        unhighlightButtons()
        okButton.tintColor = UIColor.blue
        buttonPressed(rating: FeedbackRating.ok)
    }
    
    @IBAction func badButtonPressed(_ sender: Any) {
        unhighlightButtons()
        badButton.tintColor = UIColor.blue
        buttonPressed(rating: FeedbackRating.bad)
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
        addCommentButton.setTitleColor(UIColor.darkGray, for: .normal)
        addCommentButton.isEnabled = true
    }
    
    private func unhighlightButtons(){
        goodButton.tintColor = UIColor.black
        okButton.tintColor = UIColor.black
        badButton.tintColor = UIColor.black
    }
}
