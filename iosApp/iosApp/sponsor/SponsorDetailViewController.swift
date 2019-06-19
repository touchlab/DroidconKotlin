//
//  SponsorDetailViewController.swift
//  iosApp
//
//  Created by Ben Whitley on 6/3/19.
//  Copyright © 2019 Kevin Galligan. All rights reserved.
//

import UIKit
import lib
import MaterialComponents

@objc class SponsorDetailViewController: MaterialAppBarUIViewController, UITableViewDelegate, UITableViewDataSource {
    
    @IBOutlet weak var tableView : UITableView!
    @IBOutlet weak var headerImage: UIImageView!
    @IBOutlet weak var sponsorName: UILabel!
    @IBOutlet weak var groupNameLabel: UILabel!
    @IBOutlet weak var sponsorImage: UIImageView!
    
    // MARK: Properties
    var sponsorId: String!
    var groupName: String!
    
    var viewModel: SponsorSessionViewModel!
    var sponsorSessionInfo: SponsorSessionInfo!
    
    var sponsor: Sponsor {
        return sponsorSessionInfo.sponsor
    }
    
    var speakers: [UserAccount] {
        return sponsorSessionInfo.speakers
    }
    
    // MARK: Lifecycle events
    override func viewDidLoad() {
        super.viewDidLoad()
        
        viewModel = SponsorSessionViewModel(sponsorId: sponsorId, groupName: groupName)
        viewModel.registerForChanges(proc: updateUi)
        
        tableView.estimatedRowHeight = tableView.rowHeight
        tableView.rowHeight = UITableViewAutomaticDimension
        
        let nib = UINib(nibName: "SponsorTableViewCell", bundle: nil)
        tableView.register(nib, forCellReuseIdentifier: "sponsorCell")
        
        let nib2 = UINib(nibName: "SpeakerTableViewCell", bundle: nil)
        tableView.register(nib2, forCellReuseIdentifier: "speakerCell")
        
        self.tableView.contentInset = UIEdgeInsets.zero
        self.tableView.separatorStyle = .none
        self.tableView.reloadData()
        
        headerImage.backgroundColor = ApplicationScheme.shared.colorScheme.secondaryColor
    }
    
    func updateUi(sponsor: SponsorSessionInfo) -> KotlinUnit {
        
        self.sponsorSessionInfo = sponsor
        updateAllUi()
        return KotlinUnit()
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "ShowSpeakerDetailFromSponsor" {
            let detailViewController = segue.destination as! SpeakerViewController
            let speaker = sender as! UserAccount
            detailViewController.speakerId = speaker.id
        }
    }
    
    deinit {
        viewModel.unregister()
    }
    
    func reportError(with error: String){
        let alert = UIAlertController(title: "Error", message: error as String, preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: "Ok", style: .default) { _ in })
        self.present(alert, animated: true) {}
    }
    
    func updateAllUi() {
        if let icon = sponsor.icon {
            sponsorImage.kf.setImage(with: URL(string: icon))
        }

        sponsorName.text = sponsor.name
        groupNameLabel.text = groupName
        tableView.reloadData()
    }
    
    // MARK: TableView
    func numberOfSections(in tableView: UITableView) -> Int {
        return 2
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if section == 0 {
            return 1
        } else if sponsorSessionInfo == nil {
            return 0
        }
        
        return speakers.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if indexPath.section == 0 {
            if let cell = tableView.dequeueReusableCell(withIdentifier: "sponsorCell") as? SponsorTableViewCell {
                if sponsorSessionInfo != nil {
                    cell.loadInfo(sponsorSessionInfo, viewModel: viewModel)
                }
                cell.selectionStyle = .none
                return cell
            }
            
        } else {
            if let cell: SpeakerTableViewCell = tableView.dequeueReusableCell(withIdentifier: "speakerCell") as? SpeakerTableViewCell {
                cell.selectionStyle = .none
                let speaker = speakers[indexPath.row]
                
                if let description = speaker.bio {
                    let imageUrl = speaker.profilePicture ?? ""
                    cell.loadInfo(speaker.fullName, info: description, imgUrl: imageUrl)
                    return cell
                }

            }
        }
 
        return UITableViewCell()
    }
    
    func tableView(_ tableView: UITableView, estimatedHeightForRowAt indexPath: IndexPath) -> CGFloat {
        return UITableViewAutomaticDimension
    }
    
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return UITableViewAutomaticDimension
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: false)
        
        if (indexPath as NSIndexPath).section == 1 {
            let speaker = speakers[indexPath.row]
            showSpeakerDetailView(speaker: speaker)
        }
    }
    
    func showSpeakerDetailView(speaker: UserAccount) {
        performSegue(withIdentifier: "ShowSpeakerDetailFromSponsor", sender: speaker)
    }
}
