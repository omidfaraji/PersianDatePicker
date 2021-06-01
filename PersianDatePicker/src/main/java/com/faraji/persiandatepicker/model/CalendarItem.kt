package com.faraji.persiandatepicker.model

data class CalendarItem(
    val name: String,
    val number: Int,
    var isSelected: Boolean,
    var backgroundResource: Int,
    var textColorResource: Int,
    val isSelectable: Boolean,
    val timeMillis: Long,
    val type: Int
)