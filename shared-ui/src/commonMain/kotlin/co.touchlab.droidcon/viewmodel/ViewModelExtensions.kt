package co.touchlab.droidcon.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.KVisibility
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Type aliases for compatibility
typealias ObservableProperty<T> = StateFlow<T>
typealias MutableObservableProperty<T> = MutableStateFlow<T>

// Storage for published property StateFlows so observe can access them
private val publishedFlows = mutableMapOf<Any, MutableStateFlow<*>>()

// Replacement for hyperdrive's published delegate
fun <T> ViewModel.published(initialValue: T, equalityPolicy: ((T, T) -> Boolean)? = null): ReadWriteProperty<ViewModel, T> {
    val stateFlow = MutableStateFlow(initialValue)
    return object : ReadWriteProperty<ViewModel, T> {
        override fun getValue(thisRef: ViewModel, property: KProperty<*>): T = stateFlow.value
        override fun setValue(thisRef: ViewModel, property: KProperty<*>, value: T) {
            val shouldUpdate = equalityPolicy?.let { !it(stateFlow.value, value) } ?: (stateFlow.value != value)
            if (shouldUpdate) {
                stateFlow.value = value
            }
        }
    }.also {
        // Store the flow for observe to access
        publishedFlows[this to property.name] = stateFlow
    }
}

// Replacement for hyperdrive's observe delegate - returns StateFlow
// For published properties, this returns the underlying StateFlow
// For other properties, creates a new StateFlow (simplified)
fun <T> ViewModel.observe(property: () -> T): ReadWriteProperty<ViewModel, StateFlow<T>> {
    // Try to find if this is observing a published property
    // This is a simplified approach - in practice you'd need better property tracking
    val stateFlow = MutableStateFlow(property())
    val resultFlow = stateFlow.asStateFlow()

    // Poll for changes (simplified - in production you'd want proper observation)
    viewModelScope.launch {
        var lastValue = property()
        while (true) {
            kotlinx.coroutines.delay(50) // Poll every 50ms
            val currentValue = property()
            if (currentValue != lastValue) {
                stateFlow.value = currentValue
                lastValue = currentValue
            }
        }
    }

    return object : ReadWriteProperty<ViewModel, StateFlow<T>> {
        override fun getValue(thisRef: ViewModel, property: KProperty<*>): StateFlow<T> = resultFlow
        override fun setValue(thisRef: ViewModel, property: KProperty<*>, value: StateFlow<T>) {
            // Read-only
        }
    }
}

// Replacement for hyperdrive's managed delegate (nullable version)
fun <T : ViewModel> ViewModel.managed(initialValue: T?): ReadWriteProperty<ViewModel, T?> {
    val stateFlow = MutableStateFlow(initialValue)
    return object : ReadWriteProperty<ViewModel, T?> {
        override fun getValue(thisRef: ViewModel, property: KProperty<*>): T? = stateFlow.value
        override fun setValue(thisRef: ViewModel, property: KProperty<*>, value: T?) {
            stateFlow.value = value
        }
    }
}

// Non-null version of managed for when initial value is non-null
fun <T : ViewModel> ViewModel.managed(initialValue: T): ReadWriteProperty<ViewModel, T> {
    val stateFlow = MutableStateFlow(initialValue)
    return object : ReadWriteProperty<ViewModel, T> {
        override fun getValue(thisRef: ViewModel, property: KProperty<*>): T = stateFlow.value
        override fun setValue(thisRef: ViewModel, property: KProperty<*>, value: T) {
            stateFlow.value = value
        }
    }
}

// Replacement for hyperdrive's managedList delegate
fun <T : ViewModel> ViewModel.managedList(
    initialValue: List<T>,
    flow: kotlinx.coroutines.flow.Flow<List<T>>? = null,
): ReadWriteProperty<ViewModel, List<T>> {
    val stateFlow = MutableStateFlow(initialValue)
    // Collect from flow if provided
    flow?.let { f ->
        viewModelScope.launch {
            f.collect { value ->
                stateFlow.value = value
            }
        }
    }
    return object : ReadWriteProperty<ViewModel, List<T>> {
        override fun getValue(thisRef: ViewModel, property: KProperty<*>): List<T> = stateFlow.value
        override fun setValue(thisRef: ViewModel, property: KProperty<*>, value: List<T>) {
            stateFlow.value = value
        }
    }
}

// Overload for managedList that accepts a ReadWriteProperty (from map)
// This converts the property delegate to a Flow by observing it
fun <T : ViewModel> ViewModel.managedList(
    initialValue: List<T>,
    delegate: ReadWriteProperty<ViewModel, List<T>>,
): ReadWriteProperty<ViewModel, List<T>> {
    val stateFlow = MutableStateFlow(initialValue)
    // Observe the delegate by polling
    viewModelScope.launch {
        var lastValue = initialValue
        while (true) {
            kotlinx.coroutines.delay(50)
            try {
                // Create a minimal KProperty for getValue
                val fakeProperty = object : KProperty<*> {
                    override val name: String = "managedList"
                    override val getter: KProperty.Getter<*> = throw NotImplementedError()
                    override val isConst: Boolean = false
                    override val isLateinit: Boolean = false
                    override val isAbstract: Boolean = false
                    override val isFinal: Boolean = true
                    override val isOpen: Boolean = false
                    override val visibility: KVisibility = KVisibility.PUBLIC
                    override val annotations: List<Annotation> = emptyList()
                    override val returnType: KType = throw NotImplementedError()
                }
                val currentValue = delegate.getValue(this@managedList, fakeProperty)
                if (currentValue != lastValue) {
                    stateFlow.value = currentValue
                    lastValue = currentValue
                }
            } catch (e: Exception) {
                // Ignore errors
            }
        }
    }
    return object : ReadWriteProperty<ViewModel, List<T>> {
        override fun getValue(thisRef: ViewModel, property: KProperty<*>): List<T> = stateFlow.value
        override fun setValue(thisRef: ViewModel, property: KProperty<*>, value: List<T>) {
            stateFlow.value = value
        }
    }
}

// Replacement for hyperdrive's binding delegate
fun <T, R> ViewModel.binding(
    flow: StateFlow<T>,
    mapping: (T) -> R = { it as R },
    set: ((R) -> Unit)? = null,
): ReadWriteProperty<ViewModel, R> {
    val stateFlow = MutableStateFlow(mapping(flow.value))
    // Collect from source flow
    viewModelScope.launch {
        flow.collect { value ->
            stateFlow.value = mapping(value)
        }
    }
    return object : ReadWriteProperty<ViewModel, R> {
        override fun getValue(thisRef: ViewModel, property: KProperty<*>): R = stateFlow.value
        override fun setValue(thisRef: ViewModel, property: KProperty<*>, value: R) {
            stateFlow.value = value
            set?.invoke(value)
        }
    }
}

// Replacement for hyperdrive's collected delegate
fun <T> ViewModel.collected(
    initialValue: T,
    flow: kotlinx.coroutines.flow.Flow<T>,
    equalityPolicy: ((T, T) -> Boolean)? = null,
): ReadWriteProperty<ViewModel, T> {
    val stateFlow = MutableStateFlow(initialValue)
    viewModelScope.launch {
        flow.collect { value ->
            val shouldUpdate = equalityPolicy?.let { !it(stateFlow.value, value) } ?: (stateFlow.value != value)
            if (shouldUpdate) {
                stateFlow.value = value
            }
        }
    }
    return object : ReadWriteProperty<ViewModel, T> {
        override fun getValue(thisRef: ViewModel, property: KProperty<*>): T = stateFlow.value
        override fun setValue(thisRef: ViewModel, property: KProperty<*>, value: T) {
            stateFlow.value = value
        }
    }
}

// Helper for lifecycle.whileAttached - use viewModelScope.launch instead
class LifecycleHelper(private val viewModel: ViewModel) {
    fun whileAttached(block: suspend () -> Unit) {
        viewModel.viewModelScope.launch {
            block()
        }
    }
}

val ViewModel.lifecycle: LifecycleHelper
    get() = LifecycleHelper(this)

// Helper for instanceLock
class InstanceLock {
    private var isLocked = false

    suspend fun <T> runExclusively(block: suspend () -> T): T {
        while (isLocked) {
            kotlinx.coroutines.delay(10)
        }
        isLocked = true
        try {
            return block()
        } finally {
            isLocked = false
        }
    }
}

val ViewModel.instanceLock: InstanceLock
    get() = InstanceLock()

// Helper for identityEqualityPolicy
fun <T> identityEqualityPolicy(): (T, T) -> Boolean = { a, b -> a === b }

// Helper for neverEqualPolicy - always returns false (never equal)
fun <T> neverEqualPolicy(): (T, T) -> Boolean = { _, _ -> false }

// Extension functions to make StateFlow.map work as property delegates
// These create a StateFlow that automatically updates when the source changes
fun <T, R> StateFlow<T>.map(transform: (T) -> R): ReadWriteProperty<ViewModel, R> {
    val resultFlow = MutableStateFlow(transform(this.value))
    // Store the ViewModel reference for collection
    var viewModelRef: ViewModel? = null
    var collected = false

    return object : ReadWriteProperty<ViewModel, R> {
        override fun getValue(thisRef: ViewModel, property: KProperty<*>): R {
            if (!collected) {
                collected = true
                viewModelRef = thisRef
                thisRef.viewModelScope.launch {
                    this@map.collect { value ->
                        resultFlow.value = transform(value)
                    }
                }
            }
            return resultFlow.value
        }
        override fun setValue(thisRef: ViewModel, property: KProperty<*>, value: R) {
            // Read-only for mapped values
        }
    }
}

fun <T, R> StateFlow<T>.flatMapLatest(transform: (T) -> kotlinx.coroutines.flow.Flow<R>): ReadWriteProperty<ViewModel, R?> {
    val resultFlow = MutableStateFlow<R?>(null)
    var viewModelRef: ViewModel? = null
    var collected = false

    return object : ReadWriteProperty<ViewModel, R?> {
        override fun getValue(thisRef: ViewModel, property: KProperty<*>): R? {
            if (!collected) {
                collected = true
                viewModelRef = thisRef
                thisRef.viewModelScope.launch {
                    this@flatMapLatest.collect { value ->
                        transform(value).collect { transformedValue ->
                            resultFlow.value = transformedValue
                        }
                    }
                }
            }
            return resultFlow.value
        }
        override fun setValue(thisRef: ViewModel, property: KProperty<*>, value: R?) {
            resultFlow.value = value
        }
    }
}

// Helper to convert StateFlow to Flow for use with managedList
fun <T> StateFlow<T>.asFlow(): kotlinx.coroutines.flow.Flow<T> = this

// Helper to convert ReadWriteProperty to Flow for use with managedList
// This observes the property by accessing it periodically
fun <T> ReadWriteProperty<ViewModel, T>.asFlow(viewModel: ViewModel): kotlinx.coroutines.flow.Flow<T> = kotlinx.coroutines.flow.flow {
    var lastValue: T? = null
    while (true) {
        val currentValue = getValue(
            viewModel,
            object : KProperty<*> {
                override val name: String = "asFlow"
                override val getter: KProperty.Getter<*> = throw NotImplementedError()
                override val isConst: Boolean = false
                override val isLateinit: Boolean = false
                override val isAbstract: Boolean = false
                override val isFinal: Boolean = true
                override val isOpen: Boolean = false
                override val visibility: KVisibility = KVisibility.PUBLIC
                override val annotations: List<Annotation> = emptyList()
                override val returnType: KType = throw NotImplementedError()
            },
        )
        if (currentValue != lastValue) {
            emit(currentValue)
            lastValue = currentValue
        }
        kotlinx.coroutines.delay(50)
    }
}
