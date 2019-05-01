//
//  ScheduleListCell.swift
//  ios
//
//  Created by Kevin Galligan on 4/26/16.
//  Copyright © 2016 Kevin Galligan. All rights reserved.
//

import UIKit
import lib

@objc class ScheduleListCell: UITableViewCell {
    
    @IBOutlet var titleLabel: UILabel!
    @IBOutlet var speakerNamesLabel: UILabel!
    @IBOutlet var timeLabel: UILabel!
    @IBOutlet weak var rsvpDot: DotView!
    @IBOutlet var cardBackgroundToTopConstraint: NSLayoutConstraint!
    @IBOutlet weak var eventView: UIView!
    @IBInspectable var highlightedColor : UIColor?
    private var isPast: Bool = false
    private var notHighlightedColor: UIColor {
        return isPast ? colorWithHexString(hexString: "DEDEDE") : .white
    }
    
    
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
    
    func setPast(b: Bool) {
        isPast = b
        updateBackgroundColor()
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
    
    func bind(hourBlock:HourBlock, allEvents:Bool, allBlocks:[HourBlock]){
        setTimeText(s: hourBlock.hourStringDisplay)
        setTitleText(s: hourBlock.timeBlock.title)
        setSpeakerText(s: hourBlock.speakerText)
        setDescription(s: hourBlock.timeBlock.description)
        setRsvpState(state: hourBlock.getRsvpState(allEvents: allEvents, allBlocks: allBlocks))
        setTimeGap(b: hourBlock.timeGap)
    }
    
    override var isHighlighted: Bool {
        didSet {
            updateBackgroundColor()
        }
    }
    
    func updateBackgroundColor() {
        eventView.backgroundColor = isHighlighted ? highlightedColor : notHighlightedColor
    }
}
