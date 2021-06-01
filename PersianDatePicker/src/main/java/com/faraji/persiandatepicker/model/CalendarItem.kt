package com.faraji.persiandatepicker.model

import com.faraji.persiandatepicker.util.PersianCalendar

data class CalendarItem(
    val name: String,
    val number: Int,
    var isSelected: Boolean,
    var backgroundResource: Int,
    var textColorResource: Int,
    val isSelectable: Boolean,
    val date: PersianCalendar?,
    val type: Int
)