//
//  DotView.swift
//  iosApp
//
//  Created by Kevin Galligan on 7/30/18.
//  Copyright © 2018 Kevin Galligan. All rights reserved.
//

import UIKit

class DotView: UIView {

    override func layoutSubviews() {
        layer.cornerRadius = bounds.size.width/2;
    }

}
