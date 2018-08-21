package co.touchlab.sessionize.platform

import kotlinx.coroutines.experimental.CoroutineDispatcher

internal expect val ApplicationDispatcher: CoroutineDispatcher
internal expect val MainDispatcher: CoroutineDispatcher