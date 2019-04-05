//
//  FeedbackView.swift
//  iosApp
//
//  Created by Kevin Schildhorn on 4/4/19.
//  Copyright © 2019 Kevin Galligan. All rights reserved.
//

import UIKit

public enum FeedbackRating {
    case good
    case ok
    case bad
}

class FeedbackView: UIView, FeedbackInteractionDelegate {
    
    private var alertViewController: UIAlertController?
    var commentView: FeedbackCommentSubView?
    var selectionView: FeedbackSelectionSubView?
    
    enum SubviewType : Int {
        case rating = 0
        case comment = 1
        
    }
    var subviewIdx:SubviewType = .rating
    
    @IBOutlet weak var baseView: UIView!
    @IBOutlet weak var BackButton: UIButton!
    
    let animationTime = 0.4
    
    var rating:FeedbackRating?
    var comments:String?
    
    // MARK: - Initialization
    
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }
    
    override func layoutSubviews() {
        super.layoutSubviews()
        print(baseView.frame.width)
        addFeedbackSubviews()
    }

    func AddFeedbackSubview(_ v:UIView?,hidden:Bool){
        if let realSelectionView = v {
            if !realSelectionView.isDescendant(of: baseView) {
                baseView.addSubview(realSelectionView)
                
                let x = hidden ? baseView.frame.width : 0
                realSelectionView.frame = CGRect(x: x,y: 0,width: baseView.frame.width,height: baseView.frame.height)
            }
        }
    }
    
    func setAlertView(alertView: UIAlertController) {
        alertViewController = alertView
    }
    
    func addFeedbackSubviews() {
        selectionView?.setFeedbackHandler(handler: self)
        AddFeedbackSubview(selectionView,hidden: false)
        
        commentView?.setFeedbackHandler(handler: self)
        AddFeedbackSubview(commentView,hidden: true)
    }
    
    
    // MARK: - Interaction

    @IBAction func BackButtonPressed(_ sender: Any) {
        closeParentAlertViewController()
    }
    
    func feedbackSelected(rating:FeedbackRating){
        self.rating = rating
    }
    
    func commentEntered(comment: String) {
        self.comments = comment
    }
    
    func finishedViewsFeedback() {
        
        switch subviewIdx {
        case .rating:
            subviewIdx = SubviewType.comment
            showCommentView()
            break
        case .comment:
            closeParentAlertViewController()
            break
        default:
            break
        }
    }

    func closeParentAlertViewController(){
        alertViewController?.dismiss(animated: true, completion: nil)

    }
    
    // MARK: - Showing SubViews
    
    func showCommentView(){
        animateOut(selectionView!)
        animateIn(commentView!)
    }
    
    func animateIn(_ v:UIView) {
        v.frame.origin.x = v.frame.width
        
        UIView.animate(withDuration: animationTime, delay: 0, options: .curveEaseInOut, animations: {
            v.frame.origin.x = 0
        })
    }
    
    func animateOut(_ v:UIView) {
        
        UIView.animate(withDuration: animationTime, delay: 0, options: .curveEaseInOut, animations: {
            v.frame.origin.x -= v.frame.width
        })
    }
}

extension FeedbackView {
    public static func createFromNib() -> FeedbackView? {
        var feedbackView: FeedbackView?
        var commentView: FeedbackCommentSubView?
        var selectionView: FeedbackSelectionSubView?
        
        let nibName = "FeedbackView"
        let views = Bundle.main.loadNibNamed(nibName, owner: nil, options: nil)!
        for v in views{
            if let fView = v as? FeedbackView {
                feedbackView = fView
            }else if let cView = v as? FeedbackCommentSubView {
                commentView = cView
            }else if let sView = v as? FeedbackSelectionSubView {
                selectionView = sView
            }
        }
        feedbackView?.commentView = commentView
        feedbackView?.selectionView = selectionView
        return feedbackView
    }
}

protocol FeedbackInteractionDelegate {
    func feedbackSelected(rating:FeedbackRating)
    func commentEntered(comment:String)
    func finishedViewsFeedback()

}
