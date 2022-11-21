package co.touchlab.droidcon.domain.repository.impl.adapter

import com.squareup.sqldelight.ColumnAdapter
import kotlinx.datetime.Instant

object InstantSqlDelightAdapter : ColumnAdapter<Instant, Long> {

    override fun decode(databaseValue: Long): Instant = Instant.fromEpochMilliseconds(databaseValue)

    override fun encode(value: Instant): Long = value.toEpochMilliseconds()
}
