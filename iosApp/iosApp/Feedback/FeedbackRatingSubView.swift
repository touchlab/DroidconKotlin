//
//  FeedbackSelectionSubView.swift
//  iosApp
//
//  Created by Kevin Schildhorn on 4/5/19.
//  Copyright © 2019 Kevin Galligan. All rights reserved.
//

import UIKit

class FeedbackRatingSubView: UIView, UITextViewDelegate {
    @IBOutlet weak var selectionTitle: UILabel!
    
    @IBOutlet weak var commentTextView: UITextView!
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
        
        commentTextView.layer.borderColor = UIColor(white: 0.75, alpha: 1.0).cgColor
        commentTextView.layer.borderWidth = 1.0
        
        commentTextView.layer.cornerRadius = 4
        commentTextView.clipsToBounds = true
        
        commentTextView.text = "(Optional) suggest improvements"
        commentTextView.textColor = UIColor.lightGray
        commentTextView.delegate = self
        commentTextView.textContainerInset = UIEdgeInsetsMake(16, 16, 0, 16)

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
        feedbackHandler?.feedbackSelected(rating: rating)
    }
    
    private func unhighlightButtons(){
        goodButton.tintColor = UIColor.black
        okButton.tintColor = UIColor.black
        badButton.tintColor = UIColor.black
    }
    
    
    func textViewDidChange(_ textView: UITextView) {
        let margin:CGFloat = 16
        let fixedWidth = textView.frame.size.width
        let newSize = textView.sizeThatFits(CGSize(width: fixedWidth, height: CGFloat.greatestFiniteMagnitude))
        commentTextView.frame.size = CGSize(width: max(newSize.width, fixedWidth), height: newSize.height + margin)
        
    }
    
    func textViewDidBeginEditing(_ textView: UITextView) {
        if commentTextView.textColor == UIColor.lightGray {
            commentTextView.text = ""
            commentTextView.textColor = UIColor.black
        }
    }
    
    func textViewDidEndEditing(_ textView: UITextView) {
        if commentTextView.text.isEmpty {
            commentTextView.text = "Placeholder"
            commentTextView.textColor = UIColor.lightGray
        }
    }
}
