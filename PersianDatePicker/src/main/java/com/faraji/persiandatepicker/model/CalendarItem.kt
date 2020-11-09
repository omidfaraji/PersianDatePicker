package com.faraji.persiandatepicker.model

data class CalendarItem(
    val name: String,
    val number: Int,
    var isSelected: Boolean,
    val isSelectable: Boolean,
    val timeMillis: Long,
    val type: Int
)