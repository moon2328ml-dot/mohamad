package ir.bordermanager.util

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime

object Jalali {
    val IRAN_ZONE: ZoneId = ZoneId.of("Asia/Tehran")
    private val monthNames = listOf(
        "فروردین", "اردیبهشت", "خرداد", "تیر", "مرداد", "شهریور",
        "مهر", "آبان", "آذر", "دی", "بهمن", "اسفند"
    )

    data class Date(val year: Int, val month: Int, val day: Int) {
        fun format(): String = "%04d/%02d/%02d".format(year, month, day)
        fun monthTitle(): String = "${monthNames.getOrElse(month - 1) { "" }} $year"
    }

    data class DateTime(val date: Date, val hour: Int, val minute: Int) {
        fun format(): String = "${date.format()} - %02d:%02d".format(hour, minute)
    }

    fun now(): DateTime = fromEpoch(System.currentTimeMillis())

    fun fromEpoch(epochMillis: Long): DateTime {
        val z = Instant.ofEpochMilli(epochMillis).atZone(IRAN_ZONE)
        val j = gregorianToJalali(z.year, z.monthValue, z.dayOfMonth)
        return DateTime(j, z.hour, z.minute)
    }

    fun monthRangeEpoch(year: Int, month: Int): LongRange {
        val start = jalaliToGregorian(year, month, 1).atStartOfDay(IRAN_ZONE).toInstant().toEpochMilli()
        val next = if (month == 12) jalaliToGregorian(year + 1, 1, 1) else jalaliToGregorian(year, month + 1, 1)
        val endExclusive = next.atStartOfDay(IRAN_ZONE).toInstant().toEpochMilli()
        return start until endExclusive
    }

    fun dayRangeEpoch(year: Int, month: Int, day: Int): LongRange {
        val startDate = jalaliToGregorian(year, month, day)
        val start = startDate.atStartOfDay(IRAN_ZONE).toInstant().toEpochMilli()
        val end = startDate.plusDays(1).atStartOfDay(IRAN_ZONE).toInstant().toEpochMilli()
        return start until end
    }

    fun daysInMonth(year: Int, month: Int): Int = when {
        month <= 6 -> 31
        month <= 11 -> 30
        isLeap(year) -> 30
        else -> 29
    }

    fun isLeap(jy: Int): Boolean =
        jalaliToGregorian(jy + 1, 1, 1).toEpochDay() - jalaliToGregorian(jy, 1, 1).toEpochDay() == 366L

    fun dayOfWeekIndex(year: Int, month: Int, day: Int): Int {
        val g = jalaliToGregorian(year, month, day)
        // Persian calendar columns: شنبه=0 ... جمعه=6
        return (g.dayOfWeek.value + 1) % 7
    }

    fun gregorianToJalali(gy: Int, gm: Int, gd: Int): Date {
        val gDayNo = gregorianDayNumber(gy, gm, gd)
        val jDayNo = gDayNo - gregorianDayNumber(1600, 3, 20)
        var days = jDayNo
        val cycles = Math.floorDiv(days, 12053)
        var jy = 979 + 33 * cycles
        days = Math.floorMod(days, 12053)
        jy += 4 * (days / 1461)
        days %= 1461
        if (days >= 366) {
            jy += (days - 1) / 365
            days = (days - 1) % 365
        }
        val jm: Int
        val jd: Int
        if (days < 186) {
            jm = 1 + days / 31
            jd = 1 + days % 31
        } else {
            jm = 7 + (days - 186) / 30
            jd = 1 + (days - 186) % 30
        }
        return Date(jy, jm, jd)
    }

    fun jalaliToGregorian(jy: Int, jm: Int, jd: Int): LocalDate {
        val jYear = jy + 1595
        var days = -355668 + 365 * jYear + (jYear / 33) * 8 + ((jYear % 33) + 3) / 4 + jd
        days += if (jm < 7) (jm - 1) * 31 else (jm - 7) * 30 + 186
        var gy = 400 * (days / 146097)
        days %= 146097
        if (days > 36524) {
            gy += 100 * ((days - 1) / 36524)
            days = (days - 1) % 36524
            if (days >= 365) days++
        }
        gy += 4 * (days / 1461)
        days %= 1461
        if (days > 365) {
            gy += (days - 1) / 365
            days = (days - 1) % 365
        }
        var gd = days + 1
        val leap = (gy % 4 == 0 && gy % 100 != 0) || gy % 400 == 0
        val monthDays = intArrayOf(0, 31, if (leap) 29 else 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
        var gm = 1
        while (gm <= 12 && gd > monthDays[gm]) {
            gd -= monthDays[gm]
            gm++
        }
        return LocalDate.of(gy, gm, gd)
    }

    private fun gregorianDayNumber(gy: Int, gm: Int, gd: Int): Int {
        val a = (14 - gm) / 12
        val y = gy + 4800 - a
        val m = gm + 12 * a - 3
        return gd + (153 * m + 2) / 5 + 365 * y + y / 4 - y / 100 + y / 400 - 32045
    }

}
