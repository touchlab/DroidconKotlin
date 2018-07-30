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

class ScheduleViewController : MaterialAppBarUIViewController, UITableViewDelegate, UITableViewDataSource {
    
    var viewModel:NotepadArchitectureScheduleViewModel!
    
    @IBOutlet weak var dayChooser: UISegmentedControl!
    @IBOutlet weak var eventList: UITableView!
    
    // MARK: Properties
    var conferenceDays: [NotepadArchitectureDaySchedule]?

    var allEvents = true
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        allEvents = self.tabBarController?.selectedIndex == 0
        
        viewModel =  NotepadArchitectureScheduleViewModel()
        viewModel.registerForChanges(proc: updateUi, allEvents: allEvents)
        
        eventList.delegate = self
        eventList.dataSource = self
        
        navigationItem.title = allEvents ? "Droidcon NYC" : "My Agenda"
        
        // Hide the nav bar shadow
        navigationController?.navigationBar.shadowImage = UIImage()
        navigationController?.navigationBar.setBackgroundImage(UIImage(), for: UIBarMetrics.default)
        navigationController?.navigationBar.isTranslucent = false
        
        eventList.estimatedRowHeight = 44
        eventList.rowHeight = UITableViewAutomaticDimension
    }
    
    func updateUi(conferenceDays:[NotepadArchitectureDaySchedule]) -> NotepadArchitectureStdlibUnit{
        self.conferenceDays = conferenceDays
        updateTabs(conferenceDays: conferenceDays)
        eventList.reloadData()
        return NotepadArchitectureStdlibUnit()
    }
    
    func updateTabs(conferenceDays:[NotepadArchitectureDaySchedule]) {
        let lastSelected = dayChooser.selectedSegmentIndex
        
        dayChooser.removeAllSegments()
        for day in conferenceDays {
            dayChooser.insertSegment(withTitle: day.dayString, at: dayChooser.numberOfSegments, animated: false)
        }
        
        if(dayChooser.numberOfSegments > 0){
            dayChooser.selectedSegmentIndex = lastSelected == -1 ? 0 : min(lastSelected, dayChooser.numberOfSegments-1)
        }
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "ShowEventDetail" {
            let detailViewController = segue.destination as! EventDetailViewController
            let networkEvent = sender as! NotepadArchitectureHourBlock
            detailViewController.sessionId = networkEvent.timeBlock.id
        }
    }
    
    deinit {
        viewModel.unregister()
    }
    
    // MARK: Data refresh
    @IBAction func updateTable(_ sender: AnyObject) {
        eventList.reloadData()
    }
    
    func showEventDetailView(with hourBlock: NotepadArchitectureHourBlock, andIndex index: Int) {
        performSegue(withIdentifier: "ShowEventDetail", sender: hourBlock)
    }
    
    //MARK: TableView
    func tableView(_ tableView: UITableView, didHighlightRowAt indexPath: IndexPath) {
        tableView.cellForRow(at: indexPath)?.isHighlighted = true
    }
    
    func tableView(_ tableView: UITableView, didUnhighlightRowAt indexPath: IndexPath) {
        tableView.cellForRow(at: indexPath)?.isHighlighted = false
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: false)
        
        let cday = conferenceDays![dayChooser.selectedSegmentIndex]
        let hourHolder = cday.hourBlock[indexPath.row]
        
        showEventDetailView(with: hourHolder, andIndex: indexPath.row)
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
        
        let rsvpConflict = hourHolder.rsvpConflictString(others: cday.hourBlock)
        cell.titleLabel.text = "\(eventObj.title)\(rsvpConflict)"//.replacingOccurrences(of: "Android", with: "Lulu")
        cell.speakerNamesLabel.text = eventObj.allNames
        cell.timeLabel.text = hourHolder.hourStringDisplay.lowercased()
        cell.startOfBlock = hourHolder.hourStringDisplay.count > 0
        cell.layer.isOpaque = true
        
        return cell
    }
}

