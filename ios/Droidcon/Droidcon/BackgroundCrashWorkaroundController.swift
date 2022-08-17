import SwiftUI
import DroidconKit

class BackgroundCrashWorkaroundController: UIViewController {
    
    let viewModel: ApplicationViewModel
    let composeController: UIViewController
    
    init(viewModel: ApplicationViewModel) {
        self.viewModel = viewModel
        
        composeController = MainComposeViewKt.getRootController(viewModel: viewModel)
        
        super.init(nibName: nil, bundle: nil)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        
        if composeController.parent == nil {
            addChild(composeController)
            composeController.view.frame = view.bounds
            view.addSubview(composeController.view)
            composeController.didMove(toParent: self)
        }
    }
}
