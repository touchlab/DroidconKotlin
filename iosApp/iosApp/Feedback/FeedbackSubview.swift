//
//  FeedbackSubview.swift
//  iosApp
//
//  Created by Kevin Schildhorn on 4/5/19.
//  Copyright © 2019 Kevin Galligan. All rights reserved.
//

import UIKit

class FeedbackSubview: UIView {
    var feedbackHandler: FeedbackInteractionDelegate?

    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }
    
    func setFeedbackHandler(handler:FeedbackInteractionDelegate) {
        feedbackHandler = handler
    }
}
