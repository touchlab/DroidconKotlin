package co.touchlab.droidcon

import app.cash.sqldelight.db.SqlDriver
import co.touchlab.droidcon.db.ConferenceTable
import co.touchlab.droidcon.db.DroidconDatabase
import co.touchlab.droidcon.db.SessionTable
import co.touchlab.droidcon.db.SponsorGroupTable
import co.touchlab.droidcon.domain.repository.impl.adapter.InstantSqlDelightAdapter

internal fun createDroidconDatabase(driver: SqlDriver): DroidconDatabase = DroidconDatabase(
    driver = driver,
    sessionTableAdapter = SessionTable.Adapter(
        startsAtAdapter = InstantSqlDelightAdapter,
        endsAtAdapter = InstantSqlDelightAdapter,
        feedbackRatingAdapter = intToLongAdapter,
    ),
    sponsorGroupTableAdapter = SponsorGroupTable.Adapter(
        intToLongAdapter,
    ),
    conferenceTableAdapter = ConferenceTable.Adapter(
        conferenceTimeZoneAdapter = timeZoneAdapter,
    ),
)
