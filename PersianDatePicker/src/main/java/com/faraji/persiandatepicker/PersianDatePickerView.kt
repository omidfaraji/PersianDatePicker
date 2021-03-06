package com.faraji.persiandatepicker

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import androidx.viewpager2.widget.ViewPager2
import com.faraji.persiandatepicker.databinding.FragmentMonthBinding
import com.faraji.persiandatepicker.util.*

class PersianDatePickerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    var onSelectionChanged: (Long?, Long?) -> Unit = { _, _ -> }
) : RelativeLayout(context, attrs, defStyleAttr) {

    private val binding by lazy {
        FragmentMonthBinding.inflate(
            LayoutInflater.from(context),
            this,
            true
        )
    }

    private var firstSelectedDay: PersianCalendar? = null
    private var lastSelectedDay: PersianCalendar? = null

    private val adapter: PersianDatePickerPagerAdapter by lazy {
        PersianDatePickerPagerAdapter(makeAdapters())
    }
    private val monthAdapters by lazy { mutableListOf<PersianDatePickerAdapter>() }

    var minYear: Int = persianCalendar().persianYear
        set(value) {
            field = value
            adapter.adapters = makeAdapters()
        }
    var maxYear: Int = persianCalendar().persianYear
        set(value) {
            field = value
            adapter.adapters = makeAdapters()
        }
    var minMonth: Int = 1
        set(value) {
            field = value
            adapter.adapters = makeAdapters()
        }
    var maxMonth: Int = 12
        set(value) {
            field = value
            adapter.adapters = makeAdapters()
        }


    fun setSelection(firstDay: Long? = null, lastDay: Long? = null) {
        firstSelectedDay = firstDay?.toPersianCalendar()
        lastSelectedDay = lastDay?.toPersianCalendar()
        firstSelectedDay?.let { persianCalendar ->
            adapter
                .indexOf(persianCalendar)
                .takeIf { it != -1 }
                ?.let {
                    if (binding.pager.currentItem == it)
                        binding.pager.setCurrentItem(0, false)
                    binding.pager.setCurrentItem(it, false)
                }
        }
        applySelectionToAllMonths()
    }


    fun clearSelection() {
        firstSelectedDay = null
        lastSelectedDay = null
        applySelectionToAllMonths()
    }

    private fun makeAdapters(): List<PersianDatePickerAdapter> {
        monthAdapters.clear()
        (minYear..maxYear).forEach { year ->
            val minM = if (year == minYear) minMonth else 1
            val maxM = if (year == maxYear) maxMonth else 12
            (minM..maxM).forEach { month ->
                val persianDatePickerAdapter = PersianDatePickerAdapter(
                    year = year,
                    month = month,
                    onDateRangeListener = { first, last ->
                        firstSelectedDay = first
                        lastSelectedDay = last
                        applySelectionToAllMonths()
                        if (firstSelectedDay?.timeInMillis ?: 0 > lastSelectedDay?.timeInMillis ?: 0)
                            onSelectionChanged(
                                lastSelectedDay?.timeInMillis,
                                firstSelectedDay?.timeInMillis
                            )
                        else
                            onSelectionChanged(
                                firstSelectedDay?.timeInMillis,
                                lastSelectedDay?.timeInMillis
                            )
                    }
                )
                monthAdapters.add(persianDatePickerAdapter)
            }
        }
        return monthAdapters
    }

    private fun applySelectionToAllMonths() {
        monthAdapters.forEach {
            it.setSelection(firstSelectedDay, lastSelectedDay)
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        init()
    }

    private fun init() {
        binding.next.setOnClickListener { nextMonth() }
        binding.txtNextMonth.setOnClickListener { nextMonth() }
        binding.previous.setOnClickListener { previousMonth() }
        binding.txtPreviousMonth.setOnClickListener { previousMonth() }
        initPager()
    }

    private fun previousMonth() {
        binding.pager.setCurrentItem(binding.pager.currentItem - 1, true)
    }

    private fun nextMonth() {
        binding.pager.setCurrentItem(binding.pager.currentItem + 1, true)
    }

    val selectedRange: Pair<PersianCalendar?, PersianCalendar?>
        get() {
            return if (firstSelectedDay?.timeInMillis ?: 0 > lastSelectedDay?.timeInMillis ?: 0)
                Pair(lastSelectedDay, firstSelectedDay)
            else
                Pair(firstSelectedDay, lastSelectedDay)
        }


    private fun onMonthSelected(calendar: PersianCalendar) {
        val previousName = calendar.previousMonth().persianMonthName
        val nextName = calendar.nextMonth().persianMonthName
        PersianCalendarConstants.persianMonthNames.let {
            binding.txtPreviousMonth.text = previousName
            binding.txtNextMonth.text = nextName
        }
        binding.title.text = calendar.persianMonthName.plus(" ").plus(calendar.persianYear)

    }


    private fun initPager() {
        binding.pager.adapter = adapter
        binding.pager.offscreenPageLimit = 1
        binding.pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                adapter.adapters[position].apply {
                    onMonthSelected(persianCalendar(year = year, month = month))
                }
            }
        })
        adapter
            .indexOf(persianCalendar())
            .takeIf { it != -1 }
            ?.let {
                binding.pager.setCurrentItem(it, false)
            }
    }

}
