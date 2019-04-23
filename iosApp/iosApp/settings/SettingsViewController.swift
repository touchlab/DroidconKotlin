//
//  SettingsViewController.swift
//  iosApp
//
//  Created by Kevin Schildhorn on 4/19/19.
//  Copyright © 2019 Kevin Galligan. All rights reserved.
//

import UIKit
import main

class SettingsViewController: MaterialAppBarUIViewController, UITableViewDelegate, UITableViewDataSource {

    public class SettingDetail: NSObject {
        var title:String?
        var image:UIImage?
        var enabled:Bool?
    
        init(title:String, image:UIImage, enabled: Bool){
            self.title = title
            self.image = image
            self.enabled = enabled
        }
    }
    
    var settingRows:Array<SettingDetail>?
    
    @IBOutlet weak var tableView : UITableView!
    @IBOutlet weak var headerImage: UIImageView!
    
    // MARK: Properties
    var viewModel:SettingsViewModel!
    
    // MARK: Lifecycle events
    override func viewDidLoad() {
        super.viewDidLoad()
        
        viewModel = SettingsViewModel()
        
        settingRows = [
            SettingDetail(title: "Enable Feedback",image: UIImage.init(named: "")!,enabled: false),
            SettingDetail(title: "Enable Reminders",image: UIImage.init(named: "")!,enabled: false),
            SettingDetail(title: "Enable Notifications",image: UIImage.init(named: "")!,enabled: false)
        ]
        
        tableView.estimatedRowHeight = tableView.rowHeight
        tableView.rowHeight = UITableViewAutomaticDimension
        
        let nib = UINib(nibName: "EventTableViewCell", bundle: nil)
        tableView.register(nib, forCellReuseIdentifier: "eventCell")
        
        self.tableView.contentInset = UIEdgeInsets.zero
        self.tableView.separatorStyle = .none
        self.tableView.reloadData()
        
        headerImage.backgroundColor = ApplicationScheme.shared.colorScheme.secondaryColor
    }

    
    // MARK: TableView
    func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return 3
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell:SettingsTableViewCell = tableView.dequeueReusableCell(withIdentifier: "settingCell") as! SettingsTableViewCell
        let row = (indexPath as NSIndexPath).row
        cell.loadInfo(settingRows![row])
        cell.selectionStyle = UITableViewCellSelectionStyle.none
        return cell
    }
    
    func tableView(_ tableView: UITableView, estimatedHeightForRowAt indexPath: IndexPath) -> CGFloat {
        return UITableViewAutomaticDimension
    }
    
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return UITableViewAutomaticDimension
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: false)
        
    }
}
