package com.faraji.persiandatepicker

import android.graphics.Color
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

    private lateinit var calendarItems: List<CalendarItem>

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
            days.add(
                    CalendarItem(
                            name = day.persianDay.toString(),
                            number = day.persianDay,
                            isSelected = false,
                            isSelectable = day.timeInMillis <= toDay.timeInMillis,
                            timeMillis = day.timeInMillis,
                            type = type
                    )
            )
        }
        for (i in lastDay.persianWeekDayIndex until 7) {
            day = day.nextDay().toStartDay()
            days.add(
                    CalendarItem(
                            name = day.persianDay.toString(),
                            number = day.persianDay,
                            isSelected = false,
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

        return if (lastDay != null)
            if (firstDay < lastDay)
                timeMillis in firstDay.timeInMillis..lastDay.timeInMillis
            else
                timeMillis in lastDay.timeInMillis..firstDay.timeInMillis
        else firstDay.timeInMillis == timeMillis


    }

    inner class ViewHolder(val binding: ItemDayPersianCalendarPickerBinding) :
            RecyclerView.ViewHolder(binding.root) {

        init {
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
            typeface?.let { binding.txtItemDay.typeface = it }
            setBackground(calendarItem)
            setTextColor(calendarItem)
        }

        private fun setBackground(calendarItem: CalendarItem) {
            when {
                calendarItem.timeMillis == firstSelectedDay?.timeInMillis && lastSelectedDay != null ->
                    setStartEndBackground(calendarItem)
                calendarItem.timeMillis == lastSelectedDay?.timeInMillis ->
                    setStartEndBackground(calendarItem)
                calendarItem.isSelected ->
                    binding.txtItemDay.setBackgroundColor(getColorCompat(R.color.paleGreyTwo))
                else ->
                    binding.txtItemDay.setBackgroundColor(Color.parseColor("#ffffff"))
            }
        }

        private fun setStartEndBackground(calendarItem: CalendarItem) {
            val first = firstSelectedDay ?: return
            val last = lastSelectedDay ?: return
            if (first.timeInMillis > last.timeInMillis) {
                if (calendarItem.timeMillis == first.timeInMillis)
                    binding.txtItemDay.setBackgroundResource(R.drawable.shape_end_selection)
                else if (calendarItem.timeMillis == last.timeInMillis)
                    binding.txtItemDay.setBackgroundResource(R.drawable.shape_start_selection)
            } else {
                if (calendarItem.timeMillis == first.timeInMillis)
                    binding.txtItemDay.setBackgroundResource(R.drawable.shape_start_selection)
                else if (calendarItem.timeMillis == last.timeInMillis)
                    binding.txtItemDay.setBackgroundResource(R.drawable.shape_end_selection)
            }
        }

        private fun setTextColor(calendarItem: CalendarItem) {
            when (calendarItem.type) {
                TYPE_DAY -> binding.txtItemDay.setTextColor(getColorCompat(R.color.darkBlueGrey))
                TYPE_TITLE -> binding.txtItemDay.setTextColor(getColorCompat(R.color.bluishGrey))
                else -> binding.txtItemDay.setTextColor(Color.parseColor("#dddddd"))
            }
        }
    }

    private fun ViewHolder.getColorCompat(color: Int) =
            ContextCompat.getColor(binding.root.context, color)

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
        return 6 * 7 // days of week * month view rows
    }

    override fun getItemViewType(position: Int): Int {
        return calendarItems[position].type
    }
}