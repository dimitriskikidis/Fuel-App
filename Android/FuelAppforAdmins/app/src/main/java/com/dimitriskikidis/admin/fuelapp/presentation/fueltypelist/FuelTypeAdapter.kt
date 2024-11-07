package com.dimitriskikidis.admin.fuelapp.presentation.fueltypelist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dimitriskikidis.admin.fuelapp.databinding.ItemFuelTypeBinding
import com.dimitriskikidis.admin.fuelapp.domain.models.FuelType

class FuelTypeAdapter(
    private val onFuelTypeClickListener: OnFuelTypeClickListener
) :
    ListAdapter<FuelType, FuelTypeAdapter.FuelTypeViewHolder>(FuelTypeComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FuelTypeViewHolder {
        val binding =
            ItemFuelTypeBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return FuelTypeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FuelTypeViewHolder, position: Int) {
        val currentItem = getItem(position)
        if (currentItem != null) {
            holder.bind(currentItem)
        }
    }

    inner class FuelTypeViewHolder(private val binding: ItemFuelTypeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
                btnEdit.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val brand = getItem(position)
                        onFuelTypeClickListener.onFuelTypeEdit(brand)
                    }
                }

                btnDelete.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val brand = getItem(position)
                        onFuelTypeClickListener.onFuelTypeDelete(brand)
                    }
                }
            }
        }

        fun bind(fuelType: FuelType) {
            binding.apply {
                tvFuelTypeName.text = fuelType.name
            }
        }
    }

    class FuelTypeComparator : DiffUtil.ItemCallback<FuelType>() {
        override fun areItemsTheSame(oldItem: FuelType, newItem: FuelType): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: FuelType, newItem: FuelType): Boolean {
            return oldItem == newItem
        }
    }

    interface OnFuelTypeClickListener {
        fun onFuelTypeEdit(fuelType: FuelType)
        fun onFuelTypeDelete(fuelType: FuelType)
    }
}