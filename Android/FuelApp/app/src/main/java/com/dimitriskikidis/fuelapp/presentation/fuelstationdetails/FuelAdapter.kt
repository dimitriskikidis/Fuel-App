package com.dimitriskikidis.fuelapp.presentation.fuelstationdetails

import android.icu.text.DecimalFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dimitriskikidis.fuelapp.databinding.ItemFuelBinding
import com.dimitriskikidis.fuelapp.domain.models.Fuel
import java.math.RoundingMode
import java.time.format.DateTimeFormatter

class FuelAdapter :
    ListAdapter<Fuel, FuelAdapter.FuelViewHolder>(FuelComparator()) {

    private val priceFormatter = DecimalFormat("0.000").apply {
        roundingMode = RoundingMode.HALF_UP.ordinal
    }

    private val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter
        .ofPattern("dd/MM/yyyy HH:mm")

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FuelViewHolder {
        val binding = ItemFuelBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FuelViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FuelViewHolder, position: Int) {
        val currentItem = getItem(position)
        if (currentItem != null) {
            holder.bind(currentItem)
        }
    }

    inner class FuelViewHolder(
        private val binding: ItemFuelBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(fuel: Fuel) {
            binding.apply {
                tvName.text = fuel.name
                tvName.isSelected = true
                val price = priceFormatter.format(fuel.price / 1000.0)
                tvPrice.text = "$price €"
                tvLastUpdate.text = dateTimeFormatter.format(fuel.lastUpdate)
            }
        }
    }

    private class FuelComparator : DiffUtil.ItemCallback<Fuel>() {
        override fun areItemsTheSame(oldItem: Fuel, newItem: Fuel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Fuel, newItem: Fuel): Boolean {
            return oldItem == newItem
        }
    }
}