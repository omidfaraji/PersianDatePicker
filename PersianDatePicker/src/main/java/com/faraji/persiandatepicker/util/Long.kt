package com.faraji.persiandatepicker.util

import android.content.res.Resources
import android.util.TypedValue
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar.*

val PersianCalendar.shortFormattedDate: String
    get() = "$persianDay $persianMonthName $persianYear"


fun Long.toFormattedTime(): String {
    val formatter = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    return formatter.format(Date().apply { time = this@toFormattedTime })
}

fun Long.toCalendar() = getInstance().apply {
    timeInMillis = this@toCalendar
}

fun Long.toPersianCalendar() = PersianCalendar(this)


fun PersianCalendar.toStartMonth() = copy().apply {
    setPersianDate(persianYear, persianMonth, 1)
}

fun PersianCalendar.toStartYear() = copy().apply {
    setPersianDate(persianYear, 1, 1)
}

fun PersianCalendar.toEndMonth() = copy().apply {
    if (persianMonth >= 12)
        setPersianDate(persianYear + 1, 1, 1)
    else
        setPersianDate(persianYear, persianMonth + 1, 1)
}.previousDay()


fun PersianCalendar.copy() = timeInMillis.toPersianCalendar()
fun PersianCalendar.toFirstDayInWeek() = copy()
    .apply { setPersianDate(persianYear, persianMonth, persianDay - persianWeekDayIndex) }

fun PersianCalendar.nextDay() = persianCalendar(persianYear, persianMonth, persianDay + 1)
fun PersianCalendar.previousDay() = persianCalendar(persianYear, persianMonth, persianDay - 1)

fun PersianCalendar.nextMonth() = let {
    if (persianMonth == 12)
        persianCalendar(persianYear + 1, 1, 1)
    else
        persianCalendar(persianYear, persianMonth + 1, 1)
}

fun PersianCalendar.previousMonth() = let {
    if (persianMonth == 1)
        persianCalendar(persianYear - 1, 12, 1)
    else
        persianCalendar(persianYear, persianMonth - 1, 1)
}


fun persianCalendar(year: Int? = null, month: Int? = null, day: Int? = null) = PersianCalendar().apply {
    if (year == null && month == null && day == null)
        return@apply
    setPersianDate(
        year ?: persianYear,
        month ?: persianMonth,
        day ?: persianDay
    )
}

fun Long.formatNumber() = NumberFormat.getInstance().format(this)

fun Long.removeMilliseconds() = this / 1000

fun PersianCalendar.isInSameDay(persianCalendar: PersianCalendar) =
    get(DAY_OF_YEAR) == persianCalendar.get(DAY_OF_YEAR) && get(YEAR) == persianCalendar.get(YEAR)

val Number.dp: Float
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        toFloat(),
        Resources.getSystem().displayMetrics
    )
