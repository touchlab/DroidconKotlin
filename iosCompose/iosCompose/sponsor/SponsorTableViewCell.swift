//
//  SponsorTableViewCell.swift
//  iosApp
//
//  Created by Ben Whitley on 6/3/19.
//  Copyright © 2019 Kevin Galligan. All rights reserved.
//

import UIKit
import lib

class SponsorTableViewCell: UITableViewCell {
    
    @IBOutlet weak var descriptionLabel : UITextView!
    
    var viewModel: SponsorSessionViewModel!
    var sponsorSessionInfo: SponsorSessionInfo!
    
    func loadInfo(_ sponsorSessionInfo: SponsorSessionInfo,
                  viewModel: SponsorSessionViewModel) {
        
        self.viewModel = viewModel
        self.sponsorSessionInfo = sponsorSessionInfo
        
        descriptionLabel.text = sponsorSessionInfo.sessionDetail.replacingOccurrences(of: "/n/n", with: "/n")
        
        descriptionLabel.sizeToFit()
    }
    
    func formatHTMLString(_ htmlString: String) -> NSAttributedString {
        let modifiedFont = NSString(format:"<span style=\"font: -apple-system-body; font-size: 12px\">%@</span>", htmlString) as String
        
        let attrStr = NSAttributedString(
            string: modifiedFont
            //            ,
            //            attributes: [NSDocumentTypeDocumentAttribute: NSHTMLTextDocumentType, NSCharacterEncodingDocumentAttribute: String.Encoding.utf8]
            //            attributes: [NSDocumentTypeDocumentOption: NSHTMLTextDocumentType, NSCharacterEncodingDocumentOption: String.Encoding.utf8]
        )
        
        return attrStr
    }
}
