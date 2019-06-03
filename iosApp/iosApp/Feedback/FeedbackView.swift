//
//  FeedbackView.swift
//  iosApp
//
//  Created by Kevin Schildhorn on 4/4/19.
//  Copyright © 2019 Kevin Galligan. All rights reserved.
//

import UIKit
import lib

protocol FeedbackDialogDelegate{
    func finishedFeedback(sessionId:String, rating:Int, comment: String)
}

public enum FeedbackRating: Int {
    case none = 0
    case good = 1
    case ok = 2
    case bad = 3
}

class FeedbackView: UIView, FeedbackInteractionDelegate, UITextViewDelegate {
    
    
    private var alertViewController: FeedbackAlertViewController?
    private var ratingView: FeedbackRatingSubView?
    
    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var ratingGoodButton: UIButton!
    @IBOutlet weak var ratingOkButton: UIButton!
    @IBOutlet weak var ratingBadButton: UIButton!
    @IBOutlet weak var commentTextView: UITextView!
    @IBOutlet weak var doneButton: UIButton!
    @IBOutlet weak var cancelButton: UIButton!
    @IBOutlet weak var baseView: UIView!
    
    private let animationTime = 0.4
    
    private var sessionId:String?
    private var feedbackManager:FeedbackManager?
    private var rating:FeedbackRating = FeedbackRating.none
    private var comments:String?
    
    // MARK: - Initialization
    
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }
    
    override func layoutSubviews() {
        super.layoutSubviews()
        initRatingView()
        setDoneButtonEnabled(enabled: false)
        
        doneButton.layer.cornerRadius = 24
        doneButton.clipsToBounds = true
        
        let tintableImageGood = ratingGoodButton.currentBackgroundImage?.withRenderingMode(.alwaysTemplate)
        ratingGoodButton.setBackgroundImage(tintableImageGood,for: UIControlState.normal)
        
        let tintableImageOk = ratingOkButton.currentBackgroundImage?.withRenderingMode(.alwaysTemplate)
        ratingOkButton.setBackgroundImage(tintableImageOk,for: UIControlState.normal)
        
        let tintableImageBad = ratingBadButton.currentBackgroundImage?.withRenderingMode(.alwaysTemplate)
        ratingBadButton.setBackgroundImage(tintableImageBad,for: UIControlState.normal)
        
        unhighlightButtons()
        
        commentTextView.layer.borderColor = UIColor(white: 0.75, alpha: 1.0).cgColor
        commentTextView.layer.borderWidth = 1.0
        
        commentTextView.layer.cornerRadius = 4
        commentTextView.clipsToBounds = true
        
        commentTextView.text = "(Optional) suggest improvements"
        commentTextView.textColor = UIColor.lightGray
        commentTextView.delegate = self
        commentTextView.textContainerInset = UIEdgeInsetsMake(16, 16, 0, 16)
    }
    
    public static func createFeedbackView() -> FeedbackView? {
        var feedbackView: FeedbackView?
        var ratingView: FeedbackRatingSubView?
        
        let nibName = "FeedbackView"
        let views = Bundle.main.loadNibNamed(nibName, owner: nil, options: nil)!
        for v in views{
            if let fView = v as? FeedbackView {
                feedbackView = fView
            }else if let sView = v as? FeedbackRatingSubView {
                ratingView = sView
            }
        }
        feedbackView?.ratingView = ratingView
        return feedbackView
    }

    public func setAlertView(alertView: FeedbackAlertViewController) {
        alertViewController = alertView
    }
    
    private func initRatingView() {
        ratingView?.setFeedbackHandler(handler: self)
        addFeedbackSubview(ratingView,hidden: false)
    }
    
    private func addFeedbackSubview(_ v:UIView?,hidden:Bool){
        /*
        if let realSelectionView = v {
            if !realSelectionView.isDescendant(of: baseView) {
                baseView.addSubview(realSelectionView)
                
                let x = hidden ? baseView.frame.width : 0
                realSelectionView.frame = CGRect(x: x,y: 0,width: baseView.frame.width,height: baseView.frame.height)
            }
        }*/
    }
    
    
    
    public func setSessionInfo(sessionId: String?,sessionTitle:String){
        self.sessionId = sessionId
        titleLabel.text = "What did you think of \(sessionTitle)?"
    }

    
    // MARK: - Interaction

    private func finishAndClose(){
        alertViewController?.closeWithFeedback(sessionId: sessionId!,rating: rating,comments: comments!)
    }
    
    @IBAction func goodButtonPressed(_ sender: Any) {
        unhighlightButtons()
        ratingGoodButton.tintColor = UIColor.blue
        buttonPressed(rating: FeedbackRating.good)
    }
    
    @IBAction func okButtonPressed(_ sender: Any) {
        unhighlightButtons()
        ratingOkButton.tintColor = UIColor.blue
        buttonPressed(rating: FeedbackRating.ok)
    }
    
    @IBAction func badButtonPressed(_ sender: Any) {
        unhighlightButtons()
        ratingBadButton.tintColor = UIColor.blue
        buttonPressed(rating: FeedbackRating.bad)
    }
    
    private func unhighlightButtons(){
        ratingGoodButton.tintColor = UIColor.black
        ratingOkButton.tintColor = UIColor.black
        ratingBadButton.tintColor = UIColor.black
    }
    
    private func buttonPressed(rating:FeedbackRating){
        //feedbackHandler?.feedbackSelected(rating: rating)
    }
    
    @IBAction func BackButtonPressed(_ sender: Any) {
        feedbackManager?.disableFeedback()
        alertViewController?.close()
    }
    
    @IBAction func doneButtonPressed(_ sender: Any) {
        finishAndClose()
    }
    
    internal func feedbackSelected(rating:FeedbackRating){
        self.rating = rating
        setDoneButtonEnabled(enabled: true)

    }
    
    func setFeedbackManager(fbManager: FeedbackManager){
        self.feedbackManager = fbManager
    }
    
    private func setDoneButtonEnabled(enabled:Bool){
        doneButton.isEnabled = enabled
        if(enabled){
            doneButton.backgroundColor = UIColor.init(hex: "0086ECFF")
        }else{
            doneButton.backgroundColor = UIColor.lightGray
        }
        
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

protocol FeedbackInteractionDelegate {
    func feedbackSelected(rating:FeedbackRating)
}


extension UIColor {
    public convenience init?(hex: String) {
        let r, g, b, a: CGFloat
        
        let scanner = Scanner(string: hex)
        var hexNumber: UInt64 = 0
    
        if scanner.scanHexInt64(&hexNumber) {
            r = CGFloat((hexNumber & 0xff000000) >> 24) / 255
            g = CGFloat((hexNumber & 0x00ff0000) >> 16) / 255
            b = CGFloat((hexNumber & 0x0000ff00) >> 8) / 255
            a = CGFloat(hexNumber & 0x000000ff) / 255
            
            self.init(red: r, green: g, blue: b, alpha: a)
            return
        }
        
        return nil
    }
}
