//
//  SettingsTableViewCell.swift
//  iosApp
//
//  Created by Kevin Schildhorn on 4/19/19.
//  Copyright © 2019 Kevin Galligan. All rights reserved.
//

import UIKit
import shared

class SettingsTableViewCell: UITableViewCell {

    @IBOutlet weak var settingIcon : UIImageView!
    @IBOutlet weak var titleLabel : UILabel!
    @IBOutlet weak var settingSwitch : UISwitch!
    
    var viewModel:SettingsViewModel!
    
    func loadInfo(_ settingDetail:SettingsViewController.Detail) {
        titleLabel.text = settingDetail.title
        settingIcon.image = settingDetail.image
        
        if let detail = settingDetail as? SettingsViewController.SwitchDetail {
            settingSwitch.isOn = detail.enabled
        }
    }
}
