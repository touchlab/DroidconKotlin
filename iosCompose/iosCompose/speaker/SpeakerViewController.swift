//
//  SpeakerViewController.swift
//  iosApp
//
//  Created by Kevin Galligan on 7/30/18.
//  Copyright © 2018 Kevin Galligan. All rights reserved.
//

import UIKit
import shared
//import Kingfisher

class SpeakerViewController: MaterialAppBarUIViewController, UITableViewDelegate, UITableViewDataSource {

    @IBOutlet weak var speakerImage: UIImageView!
    @IBOutlet weak var speakerName: UILabel!
    @IBOutlet weak var speakerCompany: UILabel!
    @IBOutlet weak var tableView: UITableView!
    @IBOutlet weak var headerImage: UIImageView!
    
    var speakerId:String!
    var viewModel:SpeakerViewModel!
    var speakerUiData:SpeakerUiData?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        tableView.delegate = self
        tableView.dataSource = self
        tableView.rowHeight = UITableView.automaticDimension
        tableView.estimatedRowHeight = 80
        
        viewModel = SpeakerViewModel(sessionId: speakerId)
        viewModel.registerForChanges { [weak self] speakerUiData in
            guard let self = self else { return }
            self.updateUi(speakerUiData: speakerUiData)
        }
        
        headerImage.backgroundColor = ApplicationScheme.shared.colorScheme.secondaryColor
        // Do any additional setup after loading the view.
    }
    
    deinit {
        viewModel.unregister()
    }

    func updateUi(speakerUiData:SpeakerUiData) {
        self.speakerUiData = speakerUiData
        speakerName.text = speakerUiData.fullName
        speakerCompany.text = speakerUiData.company
        if (speakerUiData.profilePicture != nil) {
            speakerImage.kf.setImage(with: URL(string: speakerUiData.profilePicture!)!)
            speakerImage.layer.cornerRadius = 36
            speakerImage.layer.masksToBounds = true
        }
        
        tableView.reloadData()
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return speakerUiData == nil ? 0 : speakerUiData!.infoRows.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell: SpeakerInfoCellTableViewCell = tableView.dequeueReusableCell(withIdentifier: "speakerInfoCell") as! SpeakerInfoCellTableViewCell

        let info = speakerUiData!.infoRows[indexPath.row]
        cell.info.text = info.info
        
        cell.icon.image = UIImage(named: info.type.icon)?.withRenderingMode(.alwaysOriginal)
        cell.selectionStyle = UITableViewCell.SelectionStyle.none
        
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let info = speakerUiData!.infoRows[indexPath.row]
        if(info.info.starts(with: "http")){
            UIApplication.shared.open(URL(string: info.info)!)
        }
    }

}
