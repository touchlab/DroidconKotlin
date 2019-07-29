//
//  FeedbackViewController.swift
//  iosApp
//
//  Created by Kevin Schildhorn on 6/3/19.
//  Copyright © 2019 Kevin Galligan. All rights reserved.
//

import UIKit

public enum FeedbackRating: Int {
    case none = 0
    case good = 1
    case ok = 2
    case bad = 3
}

class FeedbackViewController: UIViewController,UITextViewDelegate {
    
    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var ratingGoodButton: UIButton!
    @IBOutlet weak var ratingOkButton: UIButton!
    @IBOutlet weak var ratingBadButton: UIButton!
    @IBOutlet weak var commentTextView: UITextView!
    @IBOutlet weak var doneButton: UIButton!
    @IBOutlet weak var cancelButton: UIButton!
    @IBOutlet weak var skipButton: UIButton!
    
    private var sessionId:String?
    private var sessionTitle:String?
    private var feedbackManager:FeedbackManager?
    private var rating:FeedbackRating = FeedbackRating.none
    
    private let optionalText = "(Optional) suggest improvements"
    // MARK: - Initialization
    
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }
    
override func viewDidLoad() {
    super.viewDidLoad()
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
    
        titleLabel.text = sessionTitle
    
        commentTextView.layer.borderColor = UIColor(white: 0.75, alpha: 1.0).cgColor
        commentTextView.layer.borderWidth = 1.0
        
        commentTextView.layer.cornerRadius = 4
        commentTextView.clipsToBounds = true
        
        commentTextView.text = optionalText
        commentTextView.textColor = UIColor.lightGray
        commentTextView.delegate = self
        commentTextView.textContainerInset = UIEdgeInsetsMake(16, 8, 0, 8)
    
        let tapGesture = UITapGestureRecognizer(target: self, action: #selector(dismissKeyboard))
        self.view.addGestureRecognizer(tapGesture)
        setDoneOnKeyboard()
    }
    
    func setDoneOnKeyboard() {
        let keyboardToolbar = UIToolbar()
        keyboardToolbar.sizeToFit()
        let flexBarButton = UIBarButtonItem(barButtonSystemItem: .flexibleSpace, target: nil, action: nil)
        let doneBarButton = UIBarButtonItem(barButtonSystemItem: .done, target: self, action: #selector(dismissKeyboard))
        keyboardToolbar.items = [flexBarButton, doneBarButton]
        self.commentTextView.inputAccessoryView = keyboardToolbar
    }

    
    @objc func dismissKeyboard () {
        commentTextView.resignFirstResponder()
    }
    
    public func setSessionInfo(sessionId: String?,sessionTitle:String){
        self.sessionId = sessionId
        self.sessionTitle = "What did you think of \(sessionTitle)?"
        if(titleLabel != nil){
            titleLabel.text = self.sessionTitle
        }

    }
    
    
    // MARK: - Interaction
    
    private func finishAndClose(fromSkip: Bool = false) {
        var comment = ""
        if commentTextView.text != optionalText && !fromSkip {
            comment = commentTextView.text
        }
        
        feedbackManager?.finishedFeedback(sessionId: String(sessionId!),rating: rating.rawValue,comment: comment)
        dismiss(animated: true, completion: nil)
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
        feedbackSelected(rating: rating)
    }
    
    @IBAction func BackButtonPressed(_ sender: Any) {
        feedbackManager?.disableFeedback()
        dismiss(animated: true, completion: nil)
    }
    
    @IBAction func doneButtonPressed(_ sender: Any) {
        finishAndClose()
    }
    
    @IBAction func skipFeedbackButtonPressed(_ sender: Any) {
        buttonPressed(rating: .none)
        finishAndClose(fromSkip: true)
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
        var newHeight = newSize.height + margin
        
        if(newHeight > 82){
            newHeight = 82
            commentTextView.isScrollEnabled = true
        }else{
            commentTextView.isScrollEnabled = false
        }
        commentTextView.frame.size = CGSize(width: max(newSize.width, fixedWidth), height: newHeight)
        
    }
    
    func textViewDidBeginEditing(_ textView: UITextView) {
        if commentTextView.textColor == UIColor.lightGray {
            commentTextView.text = ""
            commentTextView.textColor = UIColor.black
        }
    }
    
    func textViewDidEndEditing(_ textView: UITextView) {
        if commentTextView.text.isEmpty {
            commentTextView.text = optionalText
            commentTextView.textColor = UIColor.lightGray
        }
    }
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
