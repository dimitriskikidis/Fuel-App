package com.dimitriskikidis.admin.fuelapp.presentation.brandfuellist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dimitriskikidis.admin.fuelapp.databinding.ItemBrandFuelBinding
import com.dimitriskikidis.admin.fuelapp.domain.models.BrandFuel

class BrandFuelAdapter(
    private val onBrandFuelClickListener: OnBrandFuelClickListener
) : ListAdapter<BrandFuel, BrandFuelAdapter.BrandFuelViewHolder>(BrandFuelComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BrandFuelViewHolder {
        val binding =
            ItemBrandFuelBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return BrandFuelViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BrandFuelViewHolder, position: Int) {
        val currentItem = getItem(position)
        if (currentItem != null) {
            holder.bind(currentItem)
        }
    }

    inner class BrandFuelViewHolder(private val binding: ItemBrandFuelBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
                btnEdit.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val brandFuel = getItem(position)
                        onBrandFuelClickListener.onBrandFuelEdit(brandFuel)
                    }
                }
            }
        }

        fun bind(brandFuel: BrandFuel) {
            binding.apply {
                tvName.text = brandFuel.name
                tvFuelType.text = "Fuel type: ${brandFuel.fuelTypeName}"
                val status = if (brandFuel.isEnabled) {
                    "Enabled"
                } else {
                    "Disabled"
                }
                tvStatus.text = "Status: $status"
            }
        }
    }

    class BrandFuelComparator : DiffUtil.ItemCallback<BrandFuel>() {
        override fun areItemsTheSame(oldItem: BrandFuel, newItem: BrandFuel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: BrandFuel, newItem: BrandFuel): Boolean {
            return oldItem == newItem
        }
    }

    interface OnBrandFuelClickListener {
        fun onBrandFuelEdit(brandFuel: BrandFuel)
    }
}