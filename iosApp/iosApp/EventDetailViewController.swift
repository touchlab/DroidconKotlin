//
//  ShowEventDetailViewController.swift
//  ios
//
//  Created by Sahil Ishar on 3/14/16.
//  Copyright © 2016 Kevin Galligan. All rights reserved.
//

import UIKit
import NotepadArchitecture

@objc class EventDetailViewController: UIViewController, UITableViewDelegate, UITableViewDataSource {
    
    @IBOutlet weak var tableView : UITableView!
    @IBOutlet weak var rsvpButton: UIButton!
    @IBOutlet weak var headerImage: UIImageView!
    
    // MARK: Properties
    var sessionId: String!
    var viewModel:NotepadArchitectureEventViewModel!
    var sessionInfo: NotepadArchitectureSessionInfo!
    
    // MARK: Lifecycle events
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(true)
        navigationItem.hidesBackButton = false
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        viewModel = NotepadArchitectureEventViewModel(sessionId: sessionId)
        viewModel.registerForChanges(proc: updateUi)
        
        tableView.estimatedRowHeight = tableView.rowHeight
        tableView.rowHeight = UITableViewAutomaticDimension
        
        let nib = UINib(nibName: "EventTableViewCell", bundle: nil)
        tableView.register(nib, forCellReuseIdentifier: "eventCell")
        
        let nib2 = UINib(nibName: "SpeakerTableViewCell", bundle: nil)
        tableView.register(nib2, forCellReuseIdentifier: "speakerCell")
        
        self.tableView.contentInset = UIEdgeInsets.zero
        self.tableView.separatorStyle = .none
        self.tableView.reloadData()
    }
    
    func updateUi(sessionInfo:NotepadArchitectureSessionInfo) -> NotepadArchitectureStdlibUnit{
        self.sessionInfo = sessionInfo
        styleButton()
        updateAllUi()
        return NotepadArchitectureStdlibUnit()
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
//        if segue.identifier == "ShowSpeakerDetail" {
//            let detailViewController = segue.destination as! UserDetailViewController
//            let speaker = sender as! DDATUserAccount
//            detailViewController.userId = speaker.getId().longLongValue()
//        }
    }
    
    deinit {
        viewModel.unregister()
    }
    
    func reportError(with error: String){
        let alert = UIAlertController(title: "Error", message: error as String, preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: "Ok", style: .default) { _ in })
        self.present(alert, animated: true) {}
    }
    
//    func updateRsvp(with event: DDATEvent!) {
//        self.event = event
//        updateAllUi()
//    }
    
    func updateAllUi() {
        updateButton()
        updateHeaderImage()
        tableView.reloadData()
    }
    
    // MARK: TableView
    func numberOfSections(in tableView: UITableView) -> Int {
        return 2
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if section == 0 {
            return 1
        } else if sessionInfo == nil {
            return 0
        }
        
        return sessionInfo!.speakers.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if (indexPath as NSIndexPath).section == 0 {
            let cell:EventTableViewCell = tableView.dequeueReusableCell(withIdentifier: "eventCell") as! EventTableViewCell
            cell.descriptionLabel.numberOfLines = 0
            if (sessionInfo != nil) {
                cell.loadInfo(sessionInfo, viewModel: viewModel)
            }
            cell.selectionStyle = UITableViewCellSelectionStyle.none
            return cell
        } else {
            let cell:SpeakerTableViewCell = tableView.dequeueReusableCell(withIdentifier: "speakerCell") as! SpeakerTableViewCell
            let speaker = sessionInfo.speakers[indexPath.row]
            if let speakerDescription = (speaker.bio) {
                let imageUrl = speaker.profilePicture ?? ""
                cell.loadInfo(speaker.fullName, info: speakerDescription, imgUrl: imageUrl)
            }
            
            cell.selectionStyle = UITableViewCellSelectionStyle.none
            return cell
        }
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
            let speaker = sessionInfo.speakers[indexPath.row] as NotepadArchitectureSpeakerForSession
            showSpeakerDetailView(speaker: speaker)
        }
    }
    
    // MARK: Action
    func styleButton() {
        rsvpButton.layer.cornerRadius = 24
        rsvpButton.layer.masksToBounds = true
        rsvpButton.layer.shadowColor = UIColor.black.cgColor
        rsvpButton.layer.shadowOffset = CGSize(width: 5, height: 5)
        rsvpButton.layer.shadowRadius = 5
        rsvpButton.layer.shadowOpacity = 1.0
        rsvpButton.isHidden = true
        updateButton()
    }
    
    func updateButton() {
        if sessionInfo.isPast() {
            rsvpButton.isHidden = true
        } else {
            rsvpButton.isHidden = false
            if (sessionInfo.isRsvped()) {
                rsvpButton.setImage(UIImage(named: "ic_done"), for: UIControlState())
                rsvpButton.backgroundColor = UIColor.white
            } else {
                rsvpButton.setImage(UIImage(named: "ic_add"), for: UIControlState())
                rsvpButton.backgroundColor = UIColor(red: 93/255.0, green: 253/255.0, blue: 173/255.0, alpha: 1.0)
            }
        }
    }
    
    func updateHeaderImage() {
        let imageName = "talkheader"
        headerImage.image =  UIImage(named:imageName)!
    }
    
    @IBAction func toggleRsvp(_ sender: UIButton) {
        viewModel.toggleRsvp(rsvp: !sessionInfo.isRsvped())
    }
    
    func showSpeakerDetailView(speaker: NotepadArchitectureSpeakerForSession) {
        performSegue(withIdentifier: "ShowSpeakerDetail", sender: speaker)
    }
}
