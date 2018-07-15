//
//  ViewController.swift
//  ios
//
//  Created by Kachi Nwaobasi on 7/11/17.
//  Copyright © 2017 Kevin Galligan. All rights reserved.
//

import Foundation
import UIKit
import NotepadArchitecture

class ScheduleViewController : UIViewController, UITableViewDelegate, UITableViewDataSource {
    
    let viewModel =  NotepadArchitectureScheduleViewModel()
    
    @IBOutlet weak var dayChooser: UISegmentedControl!
    @IBOutlet weak var tableView: UITableView!
    
    // MARK: Properties
    var conferenceDays: [NotepadArchitectureDaySchedule]?

    var allEvents = true
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        viewModel.registerForChanges(proc: updateUi)
        
        tableView.delegate = self
        tableView.dataSource = self
        
        allEvents = self.tabBarController?.selectedIndex == 0
        navigationItem.title = allEvents ? "Droidcon NYC" : "My Agenda"
        
        // Hide the nav bar shadow
        navigationController?.navigationBar.shadowImage = UIImage()
        navigationController?.navigationBar.setBackgroundImage(UIImage(), for: UIBarMetrics.default)
        navigationController?.navigationBar.isTranslucent = false
        
        tableView.estimatedRowHeight = 44
        tableView.rowHeight = UITableViewAutomaticDimension
    }
    
    func updateUi(conferenceDays:[NotepadArchitectureDaySchedule]) -> NotepadArchitectureStdlibUnit{
        self.conferenceDays = conferenceDays
        replaceDayTabs(conferenceDays: conferenceDays)
        tableView.reloadData()
        return NotepadArchitectureStdlibUnit()
    }
    
    func replaceDayTabs(conferenceDays:[NotepadArchitectureDaySchedule]) {
        dayChooser.removeAllSegments()
        for day in conferenceDays {
            dayChooser.insertSegment(withTitle: day.dayString, at: dayChooser.numberOfSegments, animated: false)
        }
        dayChooser.selectedSegmentIndex = 0
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
//        if segue.identifier == "ShowEventDetail" {
//            let detailViewController = segue.destination as! EventDetailViewController
//            let networkEvent = sender as! DDATEvent
//            detailViewController.eventId = networkEvent.getId()
//        }
    }
    
    deinit {
        viewModel.unregister()
    }
    
    // MARK: Data refresh
    @IBAction func updateTable(_ sender: AnyObject) {
        tableView.reloadData()
    }
    
//    func showEventDetailView(with networkEvent: DDATTimeBlock, andIndex index: Int) {
//        performSegue(withIdentifier: "ShowEventDetail", sender: networkEvent)
//    }
    
    //MARK: TableView
    func tableView(_ tableView: UITableView, didHighlightRowAt indexPath: IndexPath) {
        tableView.cellForRow(at: indexPath)?.isHighlighted = true
    }
    
    func tableView(_ tableView: UITableView, didUnhighlightRowAt indexPath: IndexPath) {
        tableView.cellForRow(at: indexPath)?.isHighlighted = false
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: false)
        
//        let eventObj = hourBlocks[indexPath.row].getTime()
//        if let networkEvent = eventObj {
//            if networkEvent is DDATEvent {
//                showEventDetailView(with: networkEvent, andIndex: indexPath.row)
//            }
//        }
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if(conferenceDays == nil)
        {
            return 0
        }
        
        let index = dayChooser.selectedSegmentIndex
        guard let days = self.conferenceDays, days.count > index else {
            return 0
        }
        let daySchedule = days[index]
        return Int(daySchedule.hourBlock.count)
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        
        let cell = tableView.dequeueReusableCell(withIdentifier: "scheduleListCellIdentifier", for: indexPath) as! ScheduleListCell
        let cday = conferenceDays![dayChooser.selectedSegmentIndex]
        let hourHolder = cday.hourBlock[indexPath.row]
        
        let eventObj = hourHolder.timeBlock
        
        cell.titleLabel.text = eventObj.title//.replacingOccurrences(of: "Android", with: "Lulu")
        cell.speakerNamesLabel.text = eventObj.allNames
        cell.timeLabel.text = hourHolder.hourStringDisplay.lowercased()
        cell.startOfBlock = hourHolder.hourStringDisplay.count > 0
        cell.layer.isOpaque = true
        
        return cell
    }
}
