package com.dimitriskikidis.owner.fuelapp.presentation.fuellist

import android.icu.text.DecimalFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dimitriskikidis.owner.fuelapp.databinding.ItemFuelBinding
import com.dimitriskikidis.owner.fuelapp.domain.models.Fuel
import java.math.RoundingMode
import java.time.format.DateTimeFormatter

class FuelAdapter(
    private val onFuelClickListener: OnFuelClickListener
) : ListAdapter<Fuel, FuelAdapter.FuelViewHolder>(FuelComparator()) {

    private val decimalFormat = DecimalFormat("0.000").apply {
        roundingMode = RoundingMode.HALF_UP.ordinal
    }

    private val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter
        .ofPattern("dd/MM/yyyy HH:mm:ss")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FuelViewHolder {
        val binding =
            ItemFuelBinding.inflate(
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

    inner class FuelViewHolder(private val binding: ItemFuelBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
                btnEdit.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val fuel = getItem(position)
                        onFuelClickListener.onEditFuel(fuel)
                    }
                }

                btnDelete.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val fuel = getItem(position)
                        onFuelClickListener.onDeleteFuel(fuel)
                    }
                }
            }
        }

        fun bind(fuel: Fuel) {
            binding.apply {
                tvName.text = fuel.name
                tvPrice.text = "${decimalFormat.format(fuel.price / 1000.0)} €"
                tvLastUpdate.text = dateTimeFormatter.format(fuel.lastUpdate)
            }
        }
    }

    class FuelComparator : DiffUtil.ItemCallback<Fuel>() {
        override fun areItemsTheSame(oldItem: Fuel, newItem: Fuel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Fuel, newItem: Fuel): Boolean {
            return oldItem == newItem
        }
    }

    interface OnFuelClickListener {
        fun onEditFuel(fuel: Fuel)
        fun onDeleteFuel(fuel: Fuel)
    }
}