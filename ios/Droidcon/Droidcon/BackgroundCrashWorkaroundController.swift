import SwiftUI
import DroidconKit

class BackgroundCrashWorkaroundController: UIViewController {
    
    let component: ApplicationComponent
    let composeController: UIViewController
    
    init(_ component: ApplicationComponent) {
        self.component = component
        
        composeController = ComposeRootControllerKt.getRootController(component: component)
        
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
