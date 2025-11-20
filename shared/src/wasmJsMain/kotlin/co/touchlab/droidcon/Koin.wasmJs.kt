package co.touchlab.droidcon

import app.cash.sqldelight.db.SqlDriver
import co.touchlab.droidcon.domain.repository.impl.SqlDelightDriverFactory
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.js.Js
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single<SqlDriver> {
        SqlDelightDriverFactory().createDriver()
    }

    single<HttpClientEngine> {
        Js.create()
    }
}
