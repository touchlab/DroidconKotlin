//
//  AboutViewController.swift
//  ios
//
//  Created by Ramona Harrison on 7/26/16.
//  Copyright © 2016 Kevin Galligan. All rights reserved.
//

import UIKit
import MaterialComponents.MaterialAppBar
import MaterialComponents.MaterialAppBar_ColorThemer
import MaterialComponents.MaterialAppBar_TypographyThemer
import SessionizeArch

class AboutViewController: MaterialAppBarUIViewController, UITableViewDelegate, UITableViewDataSource {
    
    @IBOutlet weak var scrollView: UIScrollView!
    @IBOutlet weak var contentView: UIView!
    @IBOutlet weak var tableView: UITableView!
    
    var aboutInfoData:[SessionizeArchAboutInfo]?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        tableView.delegate = self
        tableView.dataSource = self
        tableView.rowHeight = UITableViewAutomaticDimension
        tableView.estimatedRowHeight = 80
        SessionizeArchAboutModel().loadAboutInfo(proc: updateUi)
    }
    
    func updateUi(aboutInfoData:[SessionizeArchAboutInfo]) -> SessionizeArchStdlibUnit{
        self.aboutInfoData = aboutInfoData
        tableView.reloadData()
        return SessionizeArchStdlibUnit()
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return aboutInfoData == nil ? 0 : aboutInfoData!.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell: AboutInfoTableViewCell = tableView.dequeueReusableCell(withIdentifier: "aboutInfoCell") as! AboutInfoTableViewCell
        
        let info = aboutInfoData![indexPath.row]
        cell.headerText.text = info.title
        cell.bodyText.text = info.detail
        
        if(info.icon.isEmpty){
            cell.logoImage.isHidden = true
        }else{
            cell.logoImage.isHidden = false
            let aboutIcon = UIImage(named: info.icon)!.withRenderingMode(.alwaysOriginal)
            cell.logoImage.image = aboutIcon
//            let newHeight = (aboutIcon.size.height * 240)/aboutIcon.size.width
//            cell.logoImage.frame = CGRect(x: 0, y: 0, width: 240, height: newHeight)
        }
        
//        cell.info.text = info.info
//
//        cell.icon.image = UIImage(named: info.type.icon)?.withRenderingMode(.alwaysOriginal)
        cell.selectionStyle = UITableViewCellSelectionStyle.none
        
        //        cell.icon.tintColor = nil
        
        return cell
    }
}
