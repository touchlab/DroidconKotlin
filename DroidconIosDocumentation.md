# DroidconKit iOS Framework Documentation

## Overview

The DroidconKit framework is a Kotlin Multiplatform library that exposes a native iOS API for the Droidcon conference application. It provides a comprehensive set of functionality for managing conference data, user interactions, and UI presentation through Compose Multiplatform.

## Getting Started

### Initialization

To use DroidconKit, you need to initialize it with your iOS-specific dependencies:

```swift
// Initialize the Koin dependency injection container
func startKoin() {
    // Create UserDefaults for app settings
    let userDefaults = UserDefaults(suiteName: "DROIDCON2024_SETTINGS")!
    
    // Initialize Koin with iOS-specific implementations
    let koinApplication = DependencyInjectionKt.doInitKoinIos(
        userDefaults: userDefaults, 
        analyticsService: IOSAnalyticsService()
    )
    
    // Store the Koin reference
    _koin = koinApplication.koin
}

// Set up Kermit logging
AppInitKt.setupKermit()
```

### Main Application Entry Point

```swift
// In DroidconApp.swift
var body: some Scene {
    WindowGroup {
        // Get the main view model from Koin
        let viewModel = koin.waitForLoadedContextModel
        
        // Create the Compose UI controller
        ComposeController(viewModel: viewModel)
            .ignoresSafeArea()
            .attach(viewModel: viewModel)
            .environmentObject(lifecycleManager)
    }
}
```

## Key Classes and Interfaces

### WaitForLoadedContextModel

The primary entry point for the application, responsible for loading conference data and initializing the application state.

```swift
class DroidconKitWaitForLoadedContextModel : DroidconKitBaseViewModel {
    // Access to the main application view model
    var applicationViewModel: DroidconKitApplicationViewModel { get }
    
    // State flow that indicates loading status
    var state: KotlinxCoroutinesStateFlow { get }
    
    // Starts monitoring conference changes
    func monitorConferenceChanges(completionHandler: @escaping (Error?) -> Void)
    
    // Watches for conference data changes
    func watchConferenceChanges(completionHandler: @escaping (Error?) -> Void)
}
```

The state of the model is represented by `DroidconKitWaitForLoadedContextModelState` with two variations:

```swift
// Loading state
class DroidconKitWaitForLoadedContextModelStateLoading

// Ready state with loaded conference
class DroidconKitWaitForLoadedContextModelStateReady {
    var conference: DroidconKitConference { get }
}
```

### ApplicationViewModel

Manages the main application flow, providing access to various features like schedule, agenda, etc.

```swift
class DroidconKitApplicationViewModel : DroidconKitBaseViewModel {
    // Access to specific feature view models
    var agenda: DroidconKitAgendaViewModel { get }
    var schedule: DroidconKitScheduleViewModel { get }
    var sponsors: DroidconKitSponsorListViewModel { get }
    var settings: DroidconKitSettingsViewModel { get }
    
    // State properties
    var allConferences: ObservableProperty { get }
    var isFirstRun: ObservableProperty { get }
    var showSplashScreen: KotlinxCoroutinesMutableStateFlow { get }
    
    // Navigation state
    var selectedTab: DroidconKitApplicationViewModelTab { get set }
    var observeSelectedTab: MutableObservableProperty { get }
    
    // Feedback management
    var presentedFeedback: DroidconKitFeedbackDialogViewModel? { get set }
    var observePresentedFeedback: MutableObservableProperty { get }
    
    // Get available tabs for a conference
    func listTabs(conference: DroidconKitConference?) -> [DroidconKitApplicationViewModelTab]
    
    // Select a conference by its ID
    func selectConference(conferenceId: Int64) 
    
    // Run all live tasks such as syncing and notifications
    func runAllLiveTasks(conference: DroidconKitConference, completionHandler: @escaping (Error?) -> Void)
}
```

The available tabs are represented by the enum:

```swift
enum DroidconKitApplicationViewModelTab {
    case schedule
    case myagenda
    case venue
    case sponsors
    case settings
}
```

### Navigation and Compose Integration

The framework provides integration with Compose Multiplatform for UI rendering:

```swift
// Creates a UIViewController with the Compose UI
func getRootController(viewModel: DroidconKitWaitForLoadedContextModel) -> UIViewController

// Creates a specific view controller for the venue screen
func venueBodyViewController() -> UIViewController
```

### Session Management

Session data is handled through view models:

```swift
// Displays a list of sessions
class DroidconKitScheduleViewModel : DroidconKitBaseSessionListViewModel {
    // Open a session detail by ID
    func openSessionDetail(sessionId: DroidconKitSessionId)
    
    // Close the detail view
    func closeSessionDetail()
}

// Shows personal agenda
class DroidconKitAgendaViewModel : DroidconKitBaseSessionListViewModel {
    // Similar functionality to ScheduleViewModel
}

// Session detail screen
class DroidconKitSessionDetailViewModel : DroidconKitBaseViewModel {
    // Session properties
    var title: String { get }
    var description: String { get }
    var roomName: String { get }
    var startTime: Date { get }
    var endTime: Date { get }
    var speakers: [DroidconKitSpeakerListItemViewModel] { get }
    
    // Attendance state
    var isAttending: Bool { get }
    var isInConflict: Bool { get }
    var isPast: Bool { get }
    
    // Toggle attendance for the session
    func toggleAttending()
}
```

### Services and Interfaces

The framework defines several service interfaces that can be implemented on iOS:

```swift
// Analytics service for tracking events
protocol DroidconKitAnalyticsService {
    func logEvent(name: String, params: [String: Any])
}

// Notification handling
protocol DroidconKitNotificationService {
    // Cancel notifications for sessions
    func cancel(sessionIds: [DroidconKitSessionId], completionHandler: @escaping (Error?) -> Void)
    
    // Handle notification responses
    func didReceiveNotificationResponse(userInfo: [AnyHashable: Any], completionHandler: @escaping (Error?) -> Void)
    
    // Handle remote notifications
    func didReceiveRemoteNotification(userInfo: [AnyHashable: Any], completionHandler: @escaping (Error?) -> Void) -> Bool
}
```

### Model Classes

Key model classes include:

```swift
// Conference data
class DroidconKitConference {
    var id: Int64 { get }
    var name: String { get }
    var startDate: Date { get }
    var endDate: Date { get }
    var venue: String { get }
    var venueAddress: String { get }
    var venueCity: String { get }
    var showVenueMap: Bool { get }
    var timezoneId: String { get }
}

// Session information
class DroidconKitSession {
    var id: DroidconKitSessionId { get }
    var title: String { get }
    var description: String { get }
    var startsAt: Date { get }
    var endsAt: Date { get }
    var roomId: DroidconKitRoomId { get }
    var isServiceSession: Bool { get }
    var rsvp: DroidconKitSessionRSVP { get }
    var feedbackId: String? { get }
    var feedback: DroidconKitSessionFeedback? { get }
}

// Sponsor details
class DroidconKitSponsor {
    var id: DroidconKitSponsorId { get }
    var name: String { get }
    var groupName: String { get }
    var description: String? { get }
    var website: String? { get }
    var imageUrl: String? { get }
    var level: Int32 { get }
}
```

## Usage Examples

### Setting Up the Application

```swift
class AppDelegate: UIApplicationDelegate {
    // Lazy-loaded services from Koin
    lazy var log: Logger = koin.get(parameters: "AppDelegate")
    lazy var analytics: AnalyticsService = koin.get()
    lazy var notificationService: IOSNotificationService = koin.get()
    
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        // Configure Firebase
        FirebaseApp.configure()
        
        // Set up Kermit logging
        AppInitKt.setupKermit()
        
        // Initialize Koin
        startKoin()
        
        // Log app start event
        analytics.logEvent(name: AnalyticsServiceCompanion().EVENT_STARTED, params: [:])
        
        // Set up notifications
        UNUserNotificationCenter.current().delegate = self
        Messaging.messaging().delegate = self
        
        return true
    }
}
```

### Implementing Services

```swift
// Analytics implementation using Firebase
final class IOSAnalyticsService: AnalyticsService {
    func logEvent(name: String, params: [String: Any]) {
        Analytics.logEvent(name, parameters: params)
    }
}
```

### Handling Compose UI

```swift
// SwiftUI wrapper for Compose UI
struct ComposeController: UIViewControllerRepresentable {
    let viewModel: WaitForLoadedContextModel
    
    func makeUIViewController(context: Context) -> some UIViewController {
        getRootController(viewModel: viewModel)
    }
    
    func updateUIViewController(_ uiViewController: UIViewControllerType, context: Context) {}
}
```

### Managing Lifecycle

```swift
// View modifier to attach view model lifecycle
struct ManagedLifecycle: ViewModifier {
    private let viewModel: BaseViewModel
    
    @EnvironmentObject
    private var lifecycleManager: LifecycleManager
    
    func body(content: Content) -> some View {
        content
            .onChange(of: viewModel) { vm in
                lifecycleManager.managedViewModel = vm
            }
            .onAppear {
                lifecycleManager.managedViewModel = viewModel
            }
            .onDisappear {
                lifecycleManager.managedViewModel = nil
            }
    }
}

// Extension to easily apply lifecycle management
extension View {
    func attach(viewModel: BaseViewModel) -> some View {
        self.modifier(ManagedLifecycle(viewModel: viewModel))
    }
}
```

### Working with Koin Dependency Injection

```swift
// Extension to make Koin more Swift-friendly
extension Koin_coreKoin {
    func get<T: AnyObject>(_ type: T.Type = T.self, qualifier: Koin_coreQualifier? = nil, parameters: Any...) -> T {
        return getAny(
            objCObject: type,
            qualifier: qualifier,
            parameters: parameters.isEmpty ? nil : {
                Koin_coreParametersHolder(_values: NSMutableArray(array: parameters), useIndexedValues: KotlinBoolean(bool: false))
            }
        ) as! T
    }
}

// Get an instance from Koin
let viewModel = koin.get(ApplicationViewModel.self)
```

## Handling Notifications

```swift
func application(_ application: UIApplication, didReceiveRemoteNotification userInfo: [AnyHashable: Any]) async -> UIBackgroundFetchResult {
    log.d { "application(_:didReceiveRemoteNotification:)" }
    Messaging.messaging().appDidReceiveMessage(userInfo)
    
    do {
        let hasNewData = try await notificationService.didReceiveRemoteNotification(userInfo: userInfo)
        
        return if hasNewData.boolValue {
            .newData
        } else {
            .noData
        }
    } catch {
        return .failed
    }
}
```

## Best Practices

1. **Lifecycle Management**: Always use the `attach(viewModel:)` modifier when using view models to ensure proper lifecycle handling.

2. **Asynchronous Operations**: Use Swift's async/await when working with Kotlin coroutines for cleaner code:
   ```swift
   Task {
       do {
           try await viewModel.monitorConferenceChanges()
       } catch {
           print("Error: \(error)")
       }
   }
   ```

3. **Error Handling**: Always handle exceptions from Kotlin code using try/catch blocks.

4. **Memory Management**: Be aware of retain cycles when working with closures that reference Kotlin objects.

5. **UI Rendering**: Use the provided Compose integration rather than trying to build custom UI for shared components.

## Conclusion

DroidconKit provides a comprehensive framework for building the Droidcon iOS app with a consistent experience across platforms. By leveraging Kotlin Multiplatform and Compose Multiplatform, it enables a shared codebase while still allowing iOS-specific customization where needed.