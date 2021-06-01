package com.faraji.persiandatepicker

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.faraji.persiandatepicker.databinding.ItemDayPersianCalendarPickerBinding
import com.faraji.persiandatepicker.model.CalendarItem
import com.faraji.persiandatepicker.util.*

class PersianDatePickerAdapter(
    var year: Int = persianCalendar().persianYear,
    var month: Int = persianCalendar().persianMonth,
    private val typeface: Typeface? = null,
    var firstSelectedDay: PersianCalendar? = null,
    var lastSelectedDay: PersianCalendar? = null,
    val onMonthChangeListener: (Int) -> Unit = {},
    var onDateRangeListener: (PersianCalendar?, PersianCalendar?) -> Unit = { _, _ -> }
) : RecyclerView.Adapter<PersianDatePickerAdapter.ViewHolder>() {

    companion object {
        private const val TYPE_DAY = 0
        private const val TYPE_TITLE = 1
        private const val TYPE_NONE = 2
    }

    private var calendarItems: List<CalendarItem>

    init {
        calendarItems = calculateDays()
        if (firstSelectedDay != null)
            applySelection()
    }


    fun nextMonth() {
        persianCalendar(year, month).nextMonth().apply {
            setDate(persianYear, persianMonth)
        }
    }


    fun previousMonth() {
        persianCalendar(year, month).previousMonth().apply {
            setDate(persianYear, persianMonth)
        }
    }

    fun setDate(year: Int, month: Int) {
        this.year = year
        this.month = month
        calendarItems = calculateDays()
        notifyDataSetChanged()
        onMonthChangeListener(month)
    }

    private fun calculateDays(): List<CalendarItem> {
        val days = mutableListOf<CalendarItem>()
        PersianCalendarConstants.persianWeekDays.forEach {
            days.add(
                CalendarItem(
                    name = it[0].toString(),
                    number = -1,
                    isSelected = false,
                    backgroundResource = R.drawable.shape_unselected,
                    textColorResource = R.color.title_color,
                    isSelectable = false,
                    timeMillis = -1,
                    type = TYPE_TITLE
                )
            )
        }
        val firstDay = persianCalendar(year, month, 1)
        var day = firstDay.toFirstDayInWeek().previousDay()
        for (i in 0 until firstDay.persianWeekDayIndex) {
            day = day.nextDay().toStartDay()
            days.add(
                CalendarItem(
                    name = day.persianDay.toString(),
                    number = day.persianDay,
                    isSelected = false,
                    backgroundResource = R.drawable.shape_unselected,
                    textColorResource = R.color.unselectable_day_color,
                    isSelectable = false,
                    timeMillis = day.timeInMillis,
                    type = TYPE_NONE
                )
            )

        }

        val toDay = persianCalendar().toStartDay()
        val lastDay = firstDay.toEndMonth()
        for (i in firstDay.persianDay..lastDay.persianDay) {
            day = day.nextDay().toStartDay()
            val type = if (day.timeInMillis <= toDay.timeInMillis)
                TYPE_DAY
            else
                TYPE_NONE
            val selectable = day.timeInMillis <= toDay.timeInMillis
            val textColor =
                if (selectable) R.color.selectable_day_color else R.color.unselectable_day_color
            days.add(
                CalendarItem(
                    name = day.persianDay.toString(),
                    number = day.persianDay,
                    isSelected = false,
                    backgroundResource = R.drawable.shape_unselected,
                    textColorResource = textColor,
                    isSelectable = day.timeInMillis <= toDay.timeInMillis,
                    timeMillis = day.timeInMillis,
                    type = type
                )
            )
        }
        for (i in lastDay.persianWeekDayIndex + 1 until 7) {
            day = day.nextDay().toStartDay()
            days.add(
                CalendarItem(
                    name = day.persianDay.toString(),
                    number = day.persianDay,
                    isSelected = false,
                    backgroundResource = R.drawable.shape_unselected,
                    textColorResource = R.color.unselectable_day_color,
                    isSelectable = false,
                    timeMillis = day.timeInMillis,
                    type = TYPE_NONE
                )
            )
        }
        return days
    }


    fun setSelection(firstDay: PersianCalendar?, lastDay: PersianCalendar?) {
        firstSelectedDay = firstDay
        lastSelectedDay = lastDay
        applySelection()
        notifyDataSetChanged()
    }


    private fun applySelection() {
        calendarItems.filter { it.type != TYPE_TITLE }.forEach {
            it.isSelected = it.isInSelectionRange()
            it.setBackground()
            it.setTextColor()
        }
    }

    private fun CalendarItem.setBackground() {
        when {
            isStartOrEnd() ->
                setStartEndBackground()
            isSelected ->
                backgroundResource = R.drawable.shape_selected
            else ->
                backgroundResource = R.drawable.shape_unselected

        }
    }

    private fun CalendarItem.isStartOrEnd() =
        timeMillis == firstSelectedDay?.timeInMillis || timeMillis == lastSelectedDay?.timeInMillis

    private fun CalendarItem.setStartEndBackground() {
        val first = firstSelectedDay?.timeInMillis ?: return
        if (timeMillis == first && lastSelectedDay == null) {
            backgroundResource = R.drawable.shape_start_selection
            return
        }

        val last = lastSelectedDay?.timeInMillis ?: return
        if (first > last) {
            if (timeMillis == first)
                backgroundResource = R.drawable.shape_end_selection
            else if (timeMillis == last)
                backgroundResource = R.drawable.shape_start_selection
        } else {
            if (timeMillis == first)
                backgroundResource = R.drawable.shape_start_selection
            else if (timeMillis == last)
                backgroundResource = R.drawable.shape_end_selection
        }
    }

    private fun CalendarItem.setTextColor() {
        when (type) {
            TYPE_DAY -> textColorResource = R.color.selectable_day_color
            TYPE_TITLE -> textColorResource = R.color.title_color
            TYPE_NONE -> textColorResource = R.color.unselectable_day_color
        }
    }

    private fun clearSelection() {
        firstSelectedDay = null
        lastSelectedDay = null
        calendarItems.forEach {
            it.isSelected = false
        }
    }

    private fun CalendarItem.isInSelectionRange(): Boolean {
        val firstDay = firstSelectedDay
        val lastDay = lastSelectedDay
        if (firstDay == null)
            return false

        return if (lastDay != null) {
            if (firstDay < lastDay)
                timeMillis in firstDay.timeInMillis..lastDay.timeInMillis
            else
                timeMillis in lastDay.timeInMillis..firstDay.timeInMillis
        } else {
            firstDay.timeInMillis == timeMillis
        }

    }

    inner class ViewHolder(val binding: ItemDayPersianCalendarPickerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            typeface?.let { binding.txtItemDay.typeface = it }
            binding.root.setOnClickListener {
                val day = calendarItems[adapterPosition]
                if (day.isSelectable.not())
                    return@setOnClickListener
                handleSelection(day)
                notifyDataSetChanged()
            }

        }

        private fun handleSelection(calendarItem: CalendarItem) {
            when {
                firstSelectedDay == null -> {
                    firstSelectedDay = calendarItem.timeMillis.toPersianCalendar()
                    applySelection()
                }
                lastSelectedDay == null -> {
                    if (firstSelectedDay!!.timeInMillis == calendarItem.timeMillis)
                        return
                    lastSelectedDay = calendarItem.timeMillis.toPersianCalendar()
                    applySelection()
                }
                else -> {
                    clearSelection()
                    handleSelection(calendarItem)
                }
            }

            onDateRangeListener(firstSelectedDay, lastSelectedDay)
        }

        fun bind(calendarItem: CalendarItem) {
            binding.txtItemDay.text = calendarItem.name
            binding.txtItemDay.setBackgroundResource(calendarItem.backgroundResource)
            binding.txtItemDay.setTextColor(binding.root.context.getColorCompat(calendarItem.textColorResource))
        }


    }

    private fun Context.getColorCompat(color: Int) =
        ContextCompat.getColor(this, color)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(
            ItemDayPersianCalendarPickerBinding.inflate(inflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = calendarItems[position]
        holder.bind(item)
    }


    override fun getItemCount(): Int {
        return calendarItems.size
    }

    override fun getItemViewType(position: Int): Int {
        return calendarItems[position].type
    }
}