//
//  SponsorViewController.swift
//  iosApp
//
//  Created by Kevin Galligan on 8/19/18.
//  Copyright © 2018 Kevin Galligan. All rights reserved.
//

import UIKit
import lib

class SponsorViewController: MaterialAppBarUIViewController, UICollectionViewDataSource, UICollectionViewDelegate {
    
    var viewModel:SponsorViewModel!
    var sponsorGroups: [SponsorGroup]?
    @IBOutlet weak var sponsorsCollectionView: UICollectionView!
    
    override func viewDidLoad() {
        super.viewDidLoad()

        sponsorsCollectionView.delegate = self
        sponsorsCollectionView.dataSource = self
        
        viewModel = SponsorViewModel()
    }
    
    override func viewDidAppear(_ animated: Bool) {
        viewModel.load(
            proc: sponsorResult,
            error: errorCallback
        )
    }
    
    private func errorCallback(ex: KotlinThrowable){
        showNetworkError(message: "Network error. Try again later.")
    }
    
    private func showNetworkError(message: String){
        let alertController = UIAlertController(
            title: "Error",
            message: message,
            preferredStyle: .alert
        )
        alertController.addAction(UIAlertAction(title: "OK", style: .default))
        
        self.present(alertController, animated: true, completion: nil)
    }
    
    func sponsorResult(sponsorGroups:[SponsorGroup]) {
        self.sponsorGroups = sponsorGroups
        sponsorsCollectionView.reloadData()
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        
        let sponsorView = collectionView.dequeueReusableCell(withReuseIdentifier: "sponsorIconView", for: indexPath) as! SponsorCollectionViewCell
        
        if(sponsorGroups != nil)
        {
            let sponsorInfo = sponsorGroups![(indexPath as NSIndexPath).section].sponsors[indexPath.item]
            sponsorView.sponsorImageView.kf.setImage(with: URL(string: sponsorInfo.icon)!)
            sponsorView.sponsorImageView.backgroundColor = UIColor.white
            
        }
        
        return sponsorView
    }
    
    func collectionView(_ collectionView: UICollectionView, viewForSupplementaryElementOfKind kind: String, at indexPath: IndexPath) -> UICollectionReusableView {
        let headerView = collectionView.dequeueReusableSupplementaryView(ofKind: kind,
                                                                         withReuseIdentifier: "sponsorGroupHeader",
                                                                         for: indexPath) as! SponsorGroupCollectionReusableView
        
        headerView.sponsorGroupLabel.text = sponsorGroups![(indexPath as NSIndexPath).section].groupName
        return headerView
    }
    
    func numberOfSections(in collectionView: UICollectionView) -> Int {
        if(sponsorGroups == nil)
        {
            return 0
        }else{
            return sponsorGroups!.count
        }
    }
    
    func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
        let sponsorInfo = sponsorGroups![(indexPath as NSIndexPath).section].sponsors[indexPath.item]
        SponsorModelKt.sponsorClicked(sponsor: sponsorInfo)
        if sponsorInfo.sponsorId != nil {
            performSegue(withIdentifier: "ShowSponsorDetail", sender: sponsorInfo)
        } else {
            guard let url = URL(string: sponsorInfo.url) else {
                return //be safe
            }
            UIApplication.shared.open(url, options: [:], completionHandler: nil)
        }
    }
    
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        if(sponsorGroups == nil)
        {
            return 0
        }else{
            return sponsorGroups![section].sponsors.count
        }
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if(segue.identifier == "ShowSponsorDetail"){
            let sponsor = sender as? Sponsor
            if(sponsor != nil){
                SponsorSessionModel().sponsor = sponsor
            }
        }
    }

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}
