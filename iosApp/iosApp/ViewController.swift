//
//  ViewController.swift
//  calculator
//
//  Created by jetbrains on 01/12/2017.
//  Copyright © 2017 JetBrains. All rights reserved.
//

import UIKit
import SessionizeArch

class ViewController: UIViewController, UITableViewDelegate, UITableViewDataSource {
    let viewModel =  SessionizeArchScheduleViewModel()
    
    @IBOutlet weak var inputText: UITextField!
    @IBOutlet weak var inputDescription: UITextField!
    @IBOutlet weak var inputButton: UIButton!
    @IBOutlet weak var tableView: UITableView!
    
    var users:[SessionizeArchDaySchedule]? = nil
    var hourBlocks:[SessionizeArchHourBlock]? = nil
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        viewModel.registerForChanges(proc: updateUi)
        
        let speakersFile = Bundle.main.path(forResource: "speakers", ofType: "json")
        let scheduleFile = Bundle.main.path(forResource: "schedule", ofType: "json")

        do{
            try viewModel.primeData(speakerJson: String(contentsOfFile: speakersFile!), scheduleJson: String(contentsOfFile: scheduleFile!))
        } catch {
            print(error)
        }
        
        tableView.delegate = self
        tableView.dataSource = self
        tableView.register(UITableViewCell.self, forCellReuseIdentifier: "cell")
    }
    
    deinit {
        viewModel.unregister()
    }
    
    func updateUi(users:[SessionizeArchDaySchedule]) -> SessionizeArchStdlibUnit{
        self.users = users
        self.hourBlocks = users[1].hourBlock
        tableView.reloadData()
        inputButton.isEnabled = true
        print("array size \(users.count)")
        return SessionizeArchStdlibUnit()
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @IBAction func insertStuffAction(_ sender: Any) {
        inputButton.isEnabled = false
        
        viewModel.insertNote(title: inputText.text!, description: inputDescription.text!)
        
        inputText.text = ""
        inputDescription.text = ""
    }
    
    
    // number of rows in table view
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if(self.hourBlocks == nil)
        {return 0}
        else
        {return self.hourBlocks!.count}
    }
    
    // create a cell for each table view row
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        
        // create a new cell if needed or reuse an old one
        let cell:UITableViewCell = self.tableView.dequeueReusableCell(withIdentifier: "cell") as UITableViewCell!
        
        let user = self.hourBlocks![indexPath.row]
        // set the text from the data model
        
        print("Values: \(user.timeBlock.title)")
        
        cell.textLabel?.text = "\(user.hourStringDisplay) : \(user.timeBlock.title)"
        
        return cell
    }
    
    // method to run when table view cell is tapped
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let user = self.hourBlocks![indexPath.row]
        
        print("dayString: \(user.timeBlock.allNames)")
    }
}


