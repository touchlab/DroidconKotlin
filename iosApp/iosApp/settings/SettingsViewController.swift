//
//  SettingsViewController.swift
//  iosApp
//
//  Created by Kevin Schildhorn on 4/19/19.
//  Copyright © 2019 Kevin Galligan. All rights reserved.
//

import UIKit
import lib

class SettingsViewController: MaterialAppBarUIViewController, UITableViewDelegate, UITableViewDataSource {

    private var data:Array<Detail>?
    
    @IBOutlet weak var tableView : UITableView!
    
    // MARK: Properties
    var viewModel:SettingsViewModel!
    
    // MARK: Lifecycle events
    override func viewDidLoad() {
        super.viewDidLoad()
        
        viewModel = SettingsViewModel()
        
        tableView.estimatedRowHeight = tableView.rowHeight
        tableView.rowHeight = UITableViewAutomaticDimension
        
        let nib = UINib(nibName: "EventTableViewCell", bundle: nil)
        tableView.register(nib, forCellReuseIdentifier: "eventCell")
        
        self.tableView.contentInset = UIEdgeInsets.zero
        self.tableView.separatorStyle = .none
        updateContent()
        
        NotificationCenter.default.addObserver(self, selector:#selector(updateContent), name: Notification.Name(FeedbackManager.FeedbackDisabledNotificationName), object: nil)

    }
    
    @objc func updateContent(){
        data = [
            SwitchDetail(title: "Enable Feedback",
                         image: UIImage.init(named: "icon_feedback")!,
                         enabled:  ServiceRegistry().appSettings.getBoolean(key: SettingsKeys().FEEDBACK_ENABLED, defaultValue: true),
                         listener: { isOn in
                            self.viewModel.settingsModel.setFeedbackSettingEnabled(enabled: isOn)
            }),
            SwitchDetail(title: "Enable Reminders",
                         image: UIImage.init(named: "ic_event")!,
                         enabled: ServiceRegistry().appSettings.getBoolean(key: SettingsKeys().REMINDERS_ENABLED, defaultValue: true),
                         listener: { isOn in
                            self.viewModel.settingsModel.setRemindersSettingEnabled(enabled: isOn)
            }),
            ButtonDetail(title: "About",
                         image: UIImage.init(named: "ic_info_outline_white")!,
                         listener: {
                            self.performSegue(withIdentifier: "AboutSegue", sender: nil)
            })
        ]
        self.tableView.reloadData()
    }

    
    // MARK: TableView
    func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return data?.count ?? 0
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell:SettingsTableViewCell = tableView.dequeueReusableCell(withIdentifier: "settingCell") as! SettingsTableViewCell
        let row = (indexPath as NSIndexPath).row
        cell.loadInfo(data![row])
        cell.selectionStyle = UITableViewCellSelectionStyle.none
        
        if let _ = data![row] as? ButtonDetail {
            cell.settingSwitch.isHidden = true
        }else{
            cell.settingSwitch.addTarget(data![row], action: #selector(SwitchDetail.onSwitchChanged(sender:)), for:UIControlEvents.valueChanged)
        }
        
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
        
        NotificationsModel().setNotificationsEnabled(enabled: true)
        
        let row = (indexPath as NSIndexPath).row

        if let buttonRow = data![row] as? ButtonDetail {
            buttonRow.settingListener()
        }
    }
    
    
    
    enum EntryType{
        case TYPE_BODY
        case TYPE_SWITCH
        case TYPE_BUTTON
    }
    
    public class Detail: NSObject {
        var type:EntryType?
        var title:String?
        var image:UIImage?
        
        init(type:EntryType, title:String, image:UIImage){
            self.type = type
            self.title = title
            self.image = image
        }
    }
    
    private class ButtonDetail: Detail {
        var settingListener: () -> Void
        
        init(title: String, image: UIImage, listener:@escaping () -> Void) {
            self.settingListener = listener
            super.init(type: .TYPE_BUTTON, title: title, image: image)
        }
    }
    
    public class SwitchDetail: Detail {
        var enabled:Bool
        var settingListener: (Bool) -> Void
        
        init(title: String, image: UIImage, enabled: Bool, listener:@escaping (Bool) -> Void) {
            self.settingListener = listener
            self.enabled = enabled
            super.init(type: .TYPE_SWITCH, title: title, image: image)
        }
        
        @objc func onSwitchChanged(sender: UISwitch) {
            enabled = sender.isOn
            settingListener(enabled)
        }
        
    }
    
    deinit {
        NotificationCenter.default.removeObserver(self)
    }
    
}
