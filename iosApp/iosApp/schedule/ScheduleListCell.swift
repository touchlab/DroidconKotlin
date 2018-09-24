//
//  ScheduleListCell.swift
//  ios
//
//  Created by Kevin Galligan on 4/26/16.
//  Copyright © 2016 Kevin Galligan. All rights reserved.
//

import UIKit
import SessionizeArch

@objc class ScheduleListCell: UITableViewCell, EventRow {
    
    @IBOutlet var titleLabel: UILabel!
    @IBOutlet var speakerNamesLabel: UILabel!
    @IBOutlet var timeLabel: UILabel!
    @IBOutlet weak var rsvpDot: DotView!
    @IBOutlet var cardBackgroundToTopConstraint: NSLayoutConstraint!
    @IBInspectable var highlightedColor : UIColor?
    
    func setTimeGap(b: Bool) {
        cardBackgroundToTopConstraint.constant = b ? 4 : 0
    }
    
    func setTitleText(s: String) {
        titleLabel.text = s
    }
    
    func setTimeText(s: String) {
        timeLabel.text = s
    }
    
    func setSpeakerText(s: String) {
        speakerNamesLabel.text = s
    }
    
    func setDescription(s: String) {
        
    }
    
    func setLiveNowVisible(b: Bool) {
        
    }
    
    func setRsvpState(state: RsvpState) {
        if(state == RsvpState.none){
            rsvpDot.isHidden = true
        }
        else {
            rsvpDot.isHidden = false
            if(state == RsvpState.conflict){
                rsvpDot.backgroundColor = ApplicationScheme.shared.rsvpColorConflict
            }else if(state == RsvpState.rsvp){
                rsvpDot.backgroundColor = ApplicationScheme.shared.rsvpColor
            }else if(state == RsvpState.rsvppast){
                rsvpDot.backgroundColor = ApplicationScheme.shared.rsvpColorPast
            }
        }
    }
    
    override var isHighlighted: Bool {
        didSet {
            updateBackgroundColor()
        }
    }
    
    func updateBackgroundColor() {
        titleLabel.superview!.backgroundColor = isHighlighted ? highlightedColor : UIColor.white
    }
}
