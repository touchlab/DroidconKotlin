package co.touchlab.droidcon.util

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class DcDispatchers(
    val main: CoroutineDispatcher = Dispatchers.Main.immediate,
)
