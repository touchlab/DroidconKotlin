# Dynamic Conference Display Implementation Plan

## Overview

This document outlines the plan to refactor the DroidconKotlin app to support multiple conferences. Currently, the app is hardcoded to display data for a single conference defined in `Constants.kt`. The goal is to make the app capable of displaying data for any conference stored in the database, allowing users to switch between conferences via the Settings screen.

## Current State Analysis

The app currently:
1. Uses hardcoded conference information in `Constants.kt`
2. Has a database schema for conferences (`Conference.sq`)
3. Has UI components for conference selection already implemented
4. Has repository implementations for conference management
5. The app lacks the connection between selected conference and the rest of the app

## Implementation Plan

### 1. Create Conference Config Provider

Create a new class to abstract conference configuration and replace direct references to `Constants`:

```kotlin
// In shared/src/commonMain/kotlin/co/touchlab/droidcon/domain/service/ConferenceConfigProvider.kt
interface ConferenceConfigProvider {
    fun getConferenceId(): Long
    fun getConferenceTimeZone(): TimeZone
    fun getProjectId(): String
    fun getCollectionName(): String
    fun getApiKey(): String
    fun getScheduleId(): String
    fun observeChanges(): Flow<Conference>
}

// In shared/src/commonMain/kotlin/co/touchlab/droidcon/domain/service/impl/DefaultConferenceConfigProvider.kt
class DefaultConferenceConfigProvider(
    private val conferenceRepository: ConferenceRepository
) : ConferenceConfigProvider {
    private var currentConference: Conference? = null
    
    init {
        // Initialize with a blocking call to get the current conference
        runBlocking {
            currentConference = conferenceRepository.getSelected()
        }
    }
    
    override fun getConferenceId(): Long = currentConference?.id ?: Constants.conferenceId
    
    override fun getConferenceTimeZone(): TimeZone = currentConference?.timeZone ?: Constants.conferenceTimeZone
    
    override fun getProjectId(): String = currentConference?.projectId ?: Constants.Firestore.projectId
    
    override fun getCollectionName(): String = currentConference?.collectionName ?: Constants.Firestore.collectionName
    
    override fun getApiKey(): String = currentConference?.apiKey ?: Constants.Firestore.apiKey
    
    override fun getScheduleId(): String = currentConference?.scheduleId ?: Constants.Sessionize.scheduleId
    
    override fun observeChanges(): Flow<Conference> = conferenceRepository.observeSelected()
}
```

### 2. Modify Koin Module for Dependency Injection

Update the Koin module to provide the ConferenceConfigProvider:

```kotlin
// In shared/src/commonMain/kotlin/co/touchlab/droidcon/Koin.kt
fun appModule() = module {
    // Existing dependencies...
    
    // Add ConferenceConfigProvider
    single<ConferenceConfigProvider> { DefaultConferenceConfigProvider(get()) }
    
    // Update existing dependencies that depend on Constants
    // ...
}
```

### 3. Modify ApplicationViewModel to Handle Conference Changes

Update the ApplicationViewModel to respond to conference changes:

```kotlin
// In shared-ui/src/commonMain/kotlin/co.touchlab.droidcon/viewmodel/ApplicationViewModel.kt
class ApplicationViewModel(
    // Existing parameters...
    private val conferenceConfigProvider: ConferenceConfigProvider,
) : BaseViewModel(), DeepLinkNotificationHandler {
    
    // Existing properties...
    
    // Add a state property to track the current conference
    private val _currentConference = MutableStateFlow<Conference?>(null)
    val currentConference = _currentConference.asStateFlow()
    
    init {
        // Existing init code...
        
        // Observe conference changes
        lifecycle.whileAttached {
            conferenceConfigProvider.observeChanges().collect { conference ->
                _currentConference.value = conference
                // Force a data reload when conference changes
                refreshData()
            }
        }
    }
    
    // Function to refresh data when conference changes
    private fun refreshData() {
        lifecycle.whileAttached {
            try {
                // Clear splash screen if it's still showing
                showSplashScreen.value = false
                
                // Force sync to reload data for the new conference
                syncService.forceSynchronize()
                
                // Reset the selected tab to Schedule
                selectedTab = Tab.Schedule
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
    
    // Add a function to check if this is the first run
    fun checkFirstRun() {
        lifecycle.whileAttached {
            val isFirstRun = settingsGateway.settings().value.isFirstRun
            if (isFirstRun) {
                // Show conference selection dialog on first run
                selectedTab = Tab.Settings
                // Update settings to indicate it's not the first run anymore
                settingsGateway.setFirstRun(false)
            }
        }
    }
}
```

### 4. Update Settings Class to Track First Run

Modify the Settings class to track first app run for conference selection:

```kotlin
// In shared/src/commonMain/kotlin/co/touchlab/droidcon/application/composite/Settings.kt
data class Settings(
    val isFeedbackEnabled: Boolean, 
    val isRemindersEnabled: Boolean,
    val isFirstRun: Boolean = true
)

// In shared/src/commonMain/kotlin/co/touchlab/droidcon/application/repository/impl/DefaultSettingsRepository.kt
class DefaultSettingsRepository(private val observableSettings: ObservableSettings) : SettingsRepository {
    private companion object {
        private const val SETTINGS_FEEDBACK_ENABLED_KEY = "SETTINGS_FEEDBACK_ENABLED"
        private const val SETTINGS_REMINDERS_ENABLED_KEY = "SETTINGS_REMINDERS_ENABLED"
        private const val SETTINGS_FIRST_RUN_KEY = "SETTINGS_FIRST_RUN"
    }
    
    // Existing properties...
    
    private var isFirstRun: Boolean
        get() = observableSettings[SETTINGS_FIRST_RUN_KEY, true]
        set(value) {
            observableSettings[SETTINGS_FIRST_RUN_KEY] = value
        }
    
    override val settings: MutableStateFlow<Settings> = MutableStateFlow(
        Settings(
            isFeedbackEnabled = isFeedbackEnabled,
            isRemindersEnabled = isRemindersEnabled,
            isFirstRun = isFirstRun,
        ),
    )
    
    // Existing methods...
    
    override suspend fun setFirstRun(isFirstRun: Boolean) {
        this.isFirstRun = isFirstRun
        this.settings.value = this.settings.value.copy(
            isFirstRun = isFirstRun,
        )
    }
}

// In shared/src/commonMain/kotlin/co/touchlab/droidcon/application/gateway/SettingsGateway.kt
interface SettingsGateway {
    // Existing methods...
    
    suspend fun setFirstRun(isFirstRun: Boolean)
}

// In shared/src/commonMain/kotlin/co/touchlab/droidcon/application/gateway/impl/DefaultSettingsGateway.kt
class DefaultSettingsGateway(private val settingsRepository: SettingsRepository) : SettingsGateway {
    // Existing methods...
    
    override suspend fun setFirstRun(isFirstRun: Boolean) {
        settingsRepository.setFirstRun(isFirstRun)
    }
}
```

### 5. Update SyncService to Use ConferenceConfigProvider

Modify the DefaultSyncService to use the ConferenceConfigProvider instead of Constants:

```kotlin
// In shared/src/commonMain/kotlin/co/touchlab/droidcon/domain/service/impl/DefaultSyncService.kt
class DefaultSyncService(
    // Existing parameters...
    private val conferenceConfigProvider: ConferenceConfigProvider,
) : SyncService {
    // Replace all references to Constants with calls to conferenceConfigProvider
    
    // For example:
    // Change: sessionRepository.observeAll(Constants.conferenceId)
    // To: sessionRepository.observeAll(conferenceConfigProvider.getConferenceId())
    
    // Change sync logic to respect the current conference configuration
    private suspend fun updateRepositoriesFromDataSource(dataSource: DataSource) {
        val conferenceId = conferenceConfigProvider.getConferenceId()
        // Use conferenceId in all repository operations
        // ...
    }
}
```

### 6. Add First-Run Conference Selection Dialog

Create a component to show on first run:

```kotlin
// In shared-ui/src/commonMain/kotlin/co.touchlab.droidcon/ui/FirstRunConferenceSelector.kt
@Composable
fun FirstRunConferenceSelector(
    conferences: List<Conference>,
    onConferenceSelected: (Conference) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(16.dp)
        ) {
            Text(
                text = "Welcome to Droidcon!",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Text(
                text = "Please select a conference to get started:",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            LazyColumn {
                items(conferences) { conference ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onConferenceSelected(conference) }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = false,
                            onClick = { onConferenceSelected(conference) }
                        )
                        Text(
                            text = conference.name,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        }
    }
}
```

### 7. Update MainComposeView

Update the MainComposeView to handle first run:

```kotlin
// In shared-ui/src/commonMain/kotlin/co.touchlab.droidcon/ui/MainComposeView.kt

@Composable
fun MainComposeView(viewModel: ApplicationViewModel) {
    // Existing code...
    
    val isFirstRun by viewModel.observeIsFirstRun
    val conferences by viewModel.observeAllConferences
    
    // Check if this is the first run
    LaunchedEffect(Unit) {
        viewModel.checkFirstRun()
    }
    
    // Show first run dialog if needed
    if (isFirstRun && conferences.isNotEmpty()) {
        FirstRunConferenceSelector(
            conferences = conferences,
            onConferenceSelected = { conference ->
                viewModel.selectConference(conference.id)
                // Navigate to the schedule tab after selection
                viewModel.selectedTab = ApplicationViewModel.Tab.Schedule
            },
            onDismiss = {
                // Use the first conference as default if user dismisses
                if (conferences.isNotEmpty()) {
                    viewModel.selectConference(conferences.first().id)
                }
                // Navigate to the schedule tab
                viewModel.selectedTab = ApplicationViewModel.Tab.Schedule
            }
        )
    }
    
    // Existing UI code...
}
```

### 8. Modify Repository Methods to Use Conference ID Parameter

Update any repository methods that don't currently include conference ID parameter:

```kotlin
// For example, in shared/src/commonMain/kotlin/co/touchlab/droidcon/domain/repository/SessionRepository.kt
interface SessionRepository {
    // Change methods to accept conferenceId
    fun observeAll(conferenceId: Long): Flow<List<Session>>
    // ...
}
```

### 9. Testing Plan

1. Unit Tests:
   - Test ConferenceConfigProvider functionality
   - Test conference switching in repositories
   - Test Settings with first run flag

2. Integration Tests:
   - Test end-to-end conference switching
   - Test data reload on conference change
   - Test first run experience

3. Manual Tests:
   - Verify conference data displays correctly
   - Test switching between conferences
   - Ensure RSVP and favorites persist correctly per conference
   - Test installation on a new device to verify first run experience

## Migration Plan

1. Implement ConferenceConfigProvider
2. Update Settings for first run tracking
3. Update SyncService
4. Update ApplicationViewModel
5. Add first run UI
6. Update repositories
7. Test thoroughly

## Timeline Estimate

- Phase 1 (Core Services): 3 days
- Phase 2 (UI Components): 2 days
- Phase 3 (Testing & Bug Fixes): 2 days
- Total: ~7 days