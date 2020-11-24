package com.faraji.persiandatepicker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.faraji.persiandatepicker.databinding.LayoutRecyclerViewBinding
import com.faraji.persiandatepicker.util.PersianCalendar
import com.faraji.persiandatepicker.util.dp

class PersianDatePickerPagerAdapter(
    adapters: List<PersianDatePickerAdapter> = listOf()
) : RecyclerView.Adapter<PersianDatePickerPagerAdapter.ViewHolder>() {

    var adapters: List<PersianDatePickerAdapter> = adapters
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    fun indexOf(persianCalendar: PersianCalendar): Int {
        return adapters
            .indexOfFirst {
                it.year == persianCalendar.persianYear && it.month == persianCalendar.persianMonth
            }
    }

    private val viewPool = RecyclerView.RecycledViewPool()


    inner class ViewHolder(val binding: LayoutRecyclerViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setRecycledViewPool(viewPool)
            binding.root.layoutManager = GridLayoutManager(binding.root.context, 7)
            binding.root.setHasFixedSize(true)
            binding.root.isNestedScrollingEnabled = false
            binding.root.addItemDecoration(
                SpaceItemDecoration(5.dp.toInt(), SpaceItemDecoration.GRID_LAYOUT_MANAGER)
            )
        }


        fun bind(adapter: PersianDatePickerAdapter) {
            binding.root.adapter = adapter
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(
            LayoutRecyclerViewBinding.inflate(inflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val adapter = adapters[position]
        holder.bind(adapter)
    }


    override fun getItemCount(): Int {
        return adapters.size
    }

}