//
//  UITextViewFixed.swift
//  iosApp
//
//  Created by Kevin Galligan on 8/1/18.
//  Copyright © 2018 Kevin Galligan. All rights reserved.
//

import UIKit

class UITextViewFixed: UITextView {

    override func layoutSubviews() {
        super.layoutSubviews()
        setup()
    }
    func setup() {
        textContainerInset = UIEdgeInsets.zero
        textContainer.lineFragmentPadding = 0
    }

}
