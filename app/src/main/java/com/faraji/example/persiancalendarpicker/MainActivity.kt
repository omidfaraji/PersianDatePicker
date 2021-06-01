package com.faraji.example.persiancalendarpicker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.faraji.persiandatepicker.PersianDatePickerView
import com.faraji.persiandatepicker.util.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        handleWeekly()

    }
    private fun handleWeekly() {
        val end = persianCalendar().toStartDay()
        val start = end.daysAgo(6).toStartDay()
        findViewById<PersianDatePickerView>(R.id.datePicker).setSelection(start.timeInMillis, end.timeInMillis)
    }

    fun PersianCalendar.daysAgo(count: Int) =
        (toStartDay().timeInMillis - TimeUnit.DAYS.toMillis(count.toLong()))
            .toPersianCalendar()

}