//
//  ScheduleListCell.swift
//  ios
//
//  Created by Kevin Galligan on 4/26/16.
//  Copyright © 2016 Kevin Galligan. All rights reserved.
//

import UIKit

@objc class ScheduleListCell: UITableViewCell {
    
    @IBOutlet var titleLabel: UILabel!
    @IBOutlet var speakerNamesLabel: UILabel!
    @IBOutlet var timeLabel: UILabel!
    
    @IBOutlet var cardBackgroundToTopConstraint: NSLayoutConstraint!
    
    var startOfBlock: Bool = false {
        didSet {
            cardBackgroundToTopConstraint.constant = startOfBlock ? 10 : 0
        }
    }
    override var isHighlighted: Bool {
        didSet {
            updateBackgroundColor()
        }
    }
    @IBInspectable var highlightedColor : UIColor?
    
    
    func updateBackgroundColor() {
        titleLabel.superview!.backgroundColor = isHighlighted ? highlightedColor : UIColor.white
    }
}
