# Hyperdrive - Kotlin Multiplatform Architecture Library

## Overview

Hyperdrive is a Kotlin Multiplatform (KMP) architecture library focused on seamless integration between iOS and Android platforms. It provides infrastructure for creating reactive view models that can be used in both SwiftUI and Jetpack Compose. The library consists of two main components:

1. **MultiplatformX**: View model architecture for sharing business logic across platforms
2. **Compiler Plugin**: Automatically generates boilerplate code for property observation and factory creation

## Core Components

### BaseViewModel

The central class in Hyperdrive is `BaseViewModel`, which serves as the foundation for all view models. It inherits from `BaseObservableManageableObject` and implements `ManageableViewModel`.

```kotlin
@ViewModel
class MyViewModel: BaseViewModel() {
    // View model implementation
}
```

Key features:
- Provides property observation mechanisms
- Manages lifecycle for proper resource cleanup
- Includes locking mechanisms for managing concurrent operations
- Integrates with SwiftUI and Jetpack Compose

### Property Delegates

Hyperdrive offers several property delegates to make properties observable:

#### `published`

The most basic delegate for tracking property changes and notifying UI:

```kotlin
var name by published("Default")
var isEnabled by published(false)
```

#### `collected`

Used to observe `Flow`, `StateFlow`, or `ObservableProperty`:

```kotlin
// Collect from StateFlow
val count by collected(counterStateFlow)

// Collect from Flow with initial value
val messages by collected(emptyList(), messagesFlow)

// Map values
val formattedCount by collected(counterStateFlow) { "Count: $it" }
```

#### `managed` and `managedList`

Used for child view model composition, ensuring proper lifecycle management:

```kotlin
// Single child view model
var detailViewModel by managed<DetailViewModel?>(null)

// List of child view models
var itemViewModels by managedList(emptyList<ItemViewModel>())

// From StateFlow
val childViewModel by managed(childStateFlow)
```

#### `binding`

Creates two-way binding to `StateFlow` or custom setters:

```kotlin
// Two-way binding with StateFlow
var text by binding(textStateFlow)

// With mapping
var formattedText by binding(
    textStateFlow,
    readMapping = { it.uppercase() },
    writeMapping = { it.lowercase() },
    defaultEqualityPolicy()
)

// With custom setter
var searchTerm by binding(
    searchTermStateFlow,
    defaultEqualityPolicy()
) { newTerm -> 
    performSearch(newTerm)
}
```

### Lifecycle Management

Hyperdrive provides a lifecycle system to manage resources properly:

```kotlin
// All view models have a lifecycle
val lifecycle: Lifecycle

// Execute code while view model is attached
lifecycle.whileAttached {
    // Collect flows, perform periodic tasks, etc.
    // This block is cancelled when the view model detaches
}

// Alternative to override in BaseViewModel
override suspend fun whileAttached() {
    // Same as above
}
```

### Interface Locks

Prevent duplicate actions with built-in locking mechanism:

```kotlin
// Use the default instance lock
fun submitForm() = instanceLock.runExclusively {
    // This code won't run again until it completes
    // Great for preventing double-clicks, etc.
    api.submitForm()
}

// Create custom locks
private val refreshLock = createLock()
fun refresh() = refreshLock.runExclusively {
    // Refresh operation
}

// Check lock status
val isLoading = instanceLock.observeIsLocked
```

### Observable Properties

Properties support reactive operations:

```kotlin
// Map a property
val greeting by observeName.map { "Hello, $it!" }

// Combine properties
val fullName by combine(observeFirstName, observeLastName) { first, last ->
    "$first $last"
}

// Filter, flatMap, etc. are also available
```

## Annotations

### @ViewModel

Marks a class as a view model and enables property observation:

```kotlin
@ViewModel
class ProfileViewModel: BaseViewModel() {
    var name by published("")
    
    // The compiler plugin automatically generates:
    // val observeName: StateFlow<String>
}
```

### @AutoFactory

Generates a factory class for dependency injection:

```kotlin
@ViewModel
@AutoFactory
class UserViewModel(
    private val userRepository: UserRepository,
    @Provided
    private val userId: String
): BaseViewModel() {
    // Implementation
}

// The plugin generates:
class Factory(
    private val userRepository: UserRepository
) {
    fun create(userId: String): UserViewModel {
        return UserViewModel(userRepository, userId)
    }
}
```

- Regular parameters are considered injectable dependencies
- Parameters marked with `@Provided` are passed to the factory's `create` method

## Platform Integration

### SwiftUI Integration

In Swift, add this extension:

```swift
import Combine

extension BaseViewModel: ObservableObject {
    private static var objectWillChangeKey: UInt8 = 0
    public var objectWillChange: ObservableObjectPublisher {
        if let publisher = objc_getAssociatedObject(self, &Self.objectWillChangeKey) as? ObservableObjectPublisher {
            return publisher
        }
        let publisher = ObjectWillChangePublisher()
        objc_setAssociatedObject(self, &Self.objectWillChangeKey, publisher, objc_AssociationPolicy.OBJC_ASSOCIATION_RETAIN)
        willChange.addObserver {
            publisher.send()
        }
        return publisher
    }
}

extension BaseViewModel: Identifiable { }
```

Then use the view model in SwiftUI:

```swift
struct UserProfileView: View {
    @ObservedObject
    var viewModel: UserProfileViewModel
    
    var body: some View {
        VStack {
            Text(viewModel.displayName)
            TextField("Email", text: $viewModel.email)
            Button("Save", action: viewModel.save)
        }
    }
}
```

### Jetpack Compose Integration

With the Hyperdrive Gradle plugin, Compose can directly use view model properties:

```kotlin
@Composable
fun UserProfileScreen(viewModel: UserProfileViewModel) {
    Column {
        Text(viewModel.displayName)
        TextField(
            value = viewModel.email,
            onValueChange = { viewModel.email = it }
        )
        Button(onClick = viewModel.save) {
            Text("Save")
        }
    }
}
```

Without the plugin, use `observeAsState()`:

```kotlin
@Composable
fun UserProfileScreen(viewModel: UserProfileViewModel) {
    val vm by viewModel.observeAsState()
    // or observe specific properties:
    val name by viewModel.observeDisplayName.observeAsState()
    
    Column {
        Text(vm.displayName)
        // Rest of UI
    }
}
```

## Complete Example

```kotlin
@ViewModel
@AutoFactory
class ContactViewModel(
    private val contactRepository: ContactRepository,
    @Provided
    private val contactId: String? = null
): BaseViewModel() {
    // Properties
    var firstName by published("")
    var lastName by published("")
    var phoneNumber by published("")
    var isEditable by published(contactId == null)
    
    // Computed properties
    val fullName by combine(observeFirstName, observeLastName) { first, last ->
        if (first.isEmpty() && last.isEmpty()) "" else "$first $last"
    }
    
    val isValid by combine(observeFirstName, observePhoneNumber) { name, phone ->
        name.isNotEmpty() && phone.isNotEmpty()
    }
    
    // Child view models
    var addressViewModel by managed(AddressViewModel())
    
    // Lock for save operation
    private val saveLock = createLock()
    val isSaving = saveLock.observeIsLocked
    
    init {
        if (contactId != null) {
            loadContact()
        }
    }
    
    private fun loadContact() = instanceLock.runExclusively {
        val contact = contactRepository.getContact(contactId!!)
        firstName = contact.firstName
        lastName = contact.lastName
        phoneNumber = contact.phoneNumber
        addressViewModel.setAddress(contact.address)
    }
    
    fun save() = saveLock.runExclusively {
        if (!isValid) return@runExclusively
        
        val contact = Contact(
            id = contactId,
            firstName = firstName,
            lastName = lastName,
            phoneNumber = phoneNumber,
            address = addressViewModel.createAddress()
        )
        
        contactRepository.saveContact(contact)
    }
    
    override suspend fun whileAttached() {
        // Example of using the lifecycle
        contactRepository.observeChanges(contactId)
            .collect { contact ->
                // Update UI when contact changes in repository
                firstName = contact.firstName
                lastName = contact.lastName
                phoneNumber = contact.phoneNumber
            }
    }
}
```

## Notes on Compiler Plugin Magic

The Hyperdrive Kotlin compiler plugin performs several transformations that may not be immediately visible in your code:

1. For properties declared with delegates like `published`, `collected`, etc., the plugin generates corresponding `observeX` properties that expose `StateFlow<T>` for each property.

2. The `@AutoFactory` annotation generates a factory class with injectable dependencies.

3. For Compose, the plugin modifies `@Composable` functions to automatically observe view model properties, removing the need for explicit `.collectAsState()` calls.

4. The plugin manages lifecycle connections between parent and child view models.

Remember that when you see code that doesn't seem to work by standard Kotlin rules, the compiler plugin is likely generating the necessary code behind the scenes.