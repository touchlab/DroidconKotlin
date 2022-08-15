import SwiftUI
import DroidconKit

@main
struct DroidconApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self)
    var appDelegate
    
    init() {
        setupNavBarAppearance()
        setupTabBarAppearance()
    }

    var body: some Scene {
        WindowGroup {
            SwitchingRootView(viewModel: koin.applicationViewModel)
        }
    }

    private func setupNavBarAppearance() {
        let appearance = UINavigationBarAppearance()
        appearance.backgroundColor = UIColor(named: "NavBar_Background")
        appearance.titleTextAttributes = UIColor(named: "NavBar_Foreground").map { [.foregroundColor: $0] } ?? [:]
        UINavigationBar.appearance().tintColor = UIColor(named: "NavBar_Foreground")
        UINavigationBar.appearance().standardAppearance = appearance
        UINavigationBar.appearance().compactAppearance = appearance
        UINavigationBar.appearance().scrollEdgeAppearance = appearance
    }

    private func setupTabBarAppearance() {
        let itemAppearance = UITabBarItemAppearance()
        itemAppearance.configureWithDefault(for: .inline)
        itemAppearance.normal.iconColor = UIColor(named: "TabBar_Foreground")
        itemAppearance.normal.titleTextAttributes = UIColor(named: "TabBar_Foreground").map { [.foregroundColor: $0] } ?? [:]
        itemAppearance.selected.iconColor = UIColor(named: "TabBar_Foreground_Selected")
        itemAppearance.selected.titleTextAttributes = UIColor(named: "TabBar_Foreground_Selected").map { [.foregroundColor: $0] } ?? [:]
        let appearance = UITabBarAppearance()
        appearance.configureWithOpaqueBackground()
        appearance.backgroundColor = UIColor(named: "TabBar_Background")
        appearance.inlineLayoutAppearance = itemAppearance
        appearance.compactInlineLayoutAppearance = itemAppearance
        appearance.stackedLayoutAppearance = itemAppearance

        UITabBar.appearance().standardAppearance = appearance
        if #available(iOS 15.0, *) {
            UITabBar.appearance().scrollEdgeAppearance = UITabBarAppearance(barAppearance: appearance)
        }
    }
}

struct SwitchingRootView: View {
    
    @ObservedObject
    var viewModel: ApplicationViewModel
    
    private let userDefaultsPublisher = NotificationCenter.default.publisher(for: UserDefaults.didChangeNotification)
    
    var body: some View {
        Group {
            if viewModel.useCompose {
                ZStack {
                    Color("NavBar_Background")
                        .ignoresSafeArea()
                    
                    ComposeController(viewModel: viewModel)
                }
            } else {
                MainView(viewModel: viewModel)
            }
        }
        .attach(viewModel: viewModel)
        .onAppear(perform: viewModel.onAppear)
        .onReceive(userDefaultsPublisher) { _ in
            viewModel.useCompose = SettingsBundleHelper.getUseComposeValue()
        }
        .onChange(of: viewModel.useCompose) { newValue in
            SettingsBundleHelper.setUseComposeValue(newValue: newValue)
        }
    }
}

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

struct ComposeController: UIViewControllerRepresentable {
    
    let viewModel: ApplicationViewModel
    
    func makeUIViewController(context: Context) -> some UIViewController {
        BackgroundCrashWorkaroundController(viewModel: viewModel)
    }

    func updateUIViewController(_ uiViewController: UIViewControllerType, context: Context) {
        uiViewController.view.setNeedsLayout()
    }
}
