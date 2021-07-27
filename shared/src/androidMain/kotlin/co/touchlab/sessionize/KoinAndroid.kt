package co.touchlab.sessionize

import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

actual val platformModule: Module
    get() = module {
        single(qualifier = named("nnnnnn")) {"arst"}
    }