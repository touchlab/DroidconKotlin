//
//  ViewController.swift
//  ios
//
//  Created by Kachi Nwaobasi on 7/11/17.
//  Copyright © 2017 Kevin Galligan. All rights reserved.
//

import Foundation
import UIKit
import SessionizeArch
import MaterialComponents

class ScheduleViewController : MaterialAppBarUIViewController, UITableViewDelegate, UITableViewDataSource, MDCTabBarDelegate {
    
    var viewModel:SessionizeArchScheduleViewModel!
    
    @IBOutlet weak var dayChooserTab: MDCTabBar!
    @IBOutlet weak var eventList: UITableView!
    @IBOutlet weak var noData: UILabel!
    
    // MARK: Properties
    var conferenceDays: [SessionizeArchDaySchedule]?

    var allEvents = true
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        allEvents = self.tabBarController?.selectedIndex == 0
        
        viewModel =  SessionizeArchScheduleViewModel()
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
        
        dayChooserTab.alignment = .justified
        dayChooserTab.itemAppearance = .titles
        
        MDCTabBarColorThemer.applySemanticColorScheme(ApplicationScheme.shared.menuColorScheme, toTabs: dayChooserTab);
        dayChooserTab.inkColor = UIColor.white.withAlphaComponent(0.15)
        dayChooserTab.delegate = self
        
        noData.isHidden = true
        
        dayChooserTab.topAnchor.constraint(equalTo: appBar.navigationBar.bottomAnchor).isActive = true
    }
    
    func updateUi(conferenceDays:[SessionizeArchDaySchedule]) -> SessionizeArchStdlibUnit{
        self.conferenceDays = conferenceDays
        updateTabs(conferenceDays: conferenceDays)
        eventList.reloadData()
        return SessionizeArchStdlibUnit()
    }
    
    func updateTabs(conferenceDays:[SessionizeArchDaySchedule]) {
        let selectedTag = dayChooserTab.selectedItem?.tag

        dayChooserTab.items.removeAll()

        var count = 0
        for day in conferenceDays {
            dayChooserTab.items.append(UITabBarItem(title: day.dayString, image: nil, tag: count))
            count = count + 1
        }

        if(count == 0){
            noData.isHidden = false
            dayChooserTab.isHidden = true
            eventList.isHidden = true
        }else{
            noData.isHidden = true
            dayChooserTab.isHidden = false
            eventList.isHidden = false
            
            if(selectedTag != nil && selectedTag! >= 0){
                dayChooserTab.selectedItem = dayChooserTab.items[selectedTag!]
            }
        }
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "ShowEventDetail" {
            let detailViewController = segue.destination as! EventDetailViewController
            let networkEvent = sender as! SessionizeArchHourBlock
            detailViewController.sessionId = networkEvent.timeBlock.id
        }
    }
    
    deinit {
        viewModel.unregister()
    }
    
    // MARK: Data refresh
    
    func tabBar(_ tabBar: MDCTabBar, didSelect item: UITabBarItem) {
        eventList.reloadData()
    }
    
    func showEventDetailView(with hourBlock: SessionizeArchHourBlock, andIndex index: Int) {
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
        
        let cday = conferenceDays![selectedDayIndex()]
        let hourHolder = cday.hourBlock[indexPath.row]
        
        showEventDetailView(with: hourHolder, andIndex: indexPath.row)
    }
    
    func selectedDayIndex() -> Int {
        if(dayChooserTab.selectedItem == nil){
            return -1
        }else{
            return dayChooserTab.selectedItem!.tag
        }
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if(conferenceDays == nil || conferenceDays?.count == 0)
        {
            return 0
        }
        
        let index = selectedDayIndex()
        guard let days = self.conferenceDays, days.count > index else {
            return 0
        }
        let daySchedule = days[index]
        return Int(daySchedule.hourBlock.count)
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        
        let cell = tableView.dequeueReusableCell(withIdentifier: "scheduleListCellIdentifier", for: indexPath) as! ScheduleListCell
        let cday = conferenceDays![selectedDayIndex()]
        let scheduleBlockHour = cday.hourBlock[indexPath.row]

        viewModel.scheduleModel.weaveSessionDetailsUi(
            hourBlock: scheduleBlockHour,
            allBlocks: cday.hourBlock,
            row: cell,
            allEvents: allEvents)
        
        cell.layer.isOpaque = true
        
        return cell
    }
}

