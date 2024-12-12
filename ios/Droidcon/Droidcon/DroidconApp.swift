import SwiftUI
import DroidconKit

@main
struct DroidconApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self)
    var appDelegate

    @StateObject
    private var lifecycleManager = LifecycleManager()

    init() {
        setupNavBarAppearance()
        setupTabBarAppearance()
        
        SettingsBundleHelper.initialize()
    }

    var body: some Scene {
        WindowGroup {
            SwitchingRootView(viewModel: koin.applicationViewModel)
                .environmentObject(lifecycleManager)
        }
    }

    private func setupNavBarAppearance() {
        let appearance = UINavigationBarAppearance()
        appearance.configureWithDefaultBackground()
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
        appearance.configureWithDefaultBackground()
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
