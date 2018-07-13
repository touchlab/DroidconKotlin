//
//  ViewController.swift
//  calculator
//
//  Created by jetbrains on 01/12/2017.
//  Copyright © 2017 JetBrains. All rights reserved.
//

import UIKit
import NotepadArchitecture

class ViewController: UIViewController, UITableViewDelegate, UITableViewDataSource {
    let viewModel =  NotepadArchitectureNotesViewModel()
    
    @IBOutlet weak var inputText: UITextField!
    @IBOutlet weak var inputDescription: UITextField!
    @IBOutlet weak var inputButton: UIButton!
    @IBOutlet weak var tableView: UITableView!
    
    var users:[NotepadArchitectureSessionWithRoom]? = nil
    
    override func viewDidLoad() {
        super.viewDidLoad()
        print("swift A")
        
        viewModel.registerForChanges(proc: updateUi)
        
        print("swift B")
        tableView.delegate = self
        tableView.dataSource = self
        tableView.register(UITableViewCell.self, forCellReuseIdentifier: "cell")
    }
    
    deinit {
        viewModel.unregister()
    }
    
    func updateUi(users:[NotepadArchitectureSessionWithRoom]) -> NotepadArchitectureStdlibUnit{
        self.users = users
        tableView.reloadData()
        inputButton.isEnabled = true
        print("array size \(users.count)")
        return NotepadArchitectureStdlibUnit()
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
        if(self.users == nil)
        {return 0}
        else
        {return self.users!.count}
    }
    
    // create a cell for each table view row
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        
        // create a new cell if needed or reuse an old one
        let cell:UITableViewCell = self.tableView.dequeueReusableCell(withIdentifier: "cell") as UITableViewCell!
        
        let user = self.users![indexPath.row]
        // set the text from the data model
        
        print(user.title)
        
        print("Values: \(user.title) \(user.allNames)")
        
        cell.textLabel?.text = user.allNames
        
        return cell
    }
    
    // method to run when table view cell is tapped
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let user = self.users![indexPath.row]
        
        print("Description: \(user.description)")
    }
}


