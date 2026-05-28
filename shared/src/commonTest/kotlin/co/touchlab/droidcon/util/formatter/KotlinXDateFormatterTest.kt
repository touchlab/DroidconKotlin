package co.touchlab.droidcon.util.formatter

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.Month

class KotlinXDateFormatterTest {

    private val formatter = KotlinXDateFormatter()

    @Test
    fun monthWithDay_formatsThreeLetterUppercaseMonthAndDay() {
        assertEquals(
            "MAR 7",
            formatter.monthWithDay(LocalDate(2024, Month.MARCH, 7)),
        )
        assertEquals(
            "DEC 31",
            formatter.monthWithDay(LocalDate(2024, Month.DECEMBER, 31)),
        )
    }

    @Test
    fun timeOnly_formatsMidnightAs12Am() {
        assertEquals(
            "12:00 AM",
            formatter.timeOnly(LocalDateTime(2024, Month.JUNE, 1, 0, 0)),
        )
    }

    @Test
    fun timeOnly_formatsNoonAs12Pm() {
        assertEquals(
            "12:00 PM",
            formatter.timeOnly(LocalDateTime(2024, Month.JUNE, 1, 12, 0)),
        )
    }

    @Test
    fun timeOnly_formatsMorningAndAfternoonTimes() {
        assertEquals(
            "9:05 AM",
            formatter.timeOnly(LocalDateTime(2024, Month.JUNE, 1, 9, 5)),
        )
        assertEquals(
            "1:30 PM",
            formatter.timeOnly(LocalDateTime(2024, Month.JUNE, 1, 13, 30)),
        )
        assertEquals(
            "11:59 PM",
            formatter.timeOnly(LocalDateTime(2024, Month.JUNE, 1, 23, 59)),
        )
    }

    @Test
    fun timeOnly_padsMinutesToTwoDigits() {
        assertEquals(
            "10:09 AM",
            formatter.timeOnly(LocalDateTime(2024, Month.JUNE, 1, 10, 9)),
        )
    }

    @Test
    fun timeOnlyInterval_joinsFormattedStartAndEndTimes() {
        val from = LocalDateTime(2024, Month.JUNE, 1, 9, 0)
        val to = LocalDateTime(2024, Month.JUNE, 1, 10, 30)

        assertEquals(
            "9:00 AM - 10:30 AM",
            formatter.timeOnlyInterval(from, to),
        )
    }
}
