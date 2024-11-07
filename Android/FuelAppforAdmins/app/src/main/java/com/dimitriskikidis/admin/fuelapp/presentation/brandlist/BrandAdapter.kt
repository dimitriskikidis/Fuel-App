package com.dimitriskikidis.admin.fuelapp.presentation.brandlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dimitriskikidis.admin.fuelapp.databinding.ItemBrandBinding
import com.dimitriskikidis.admin.fuelapp.domain.models.Brand

class BrandAdapter(
    private val onBrandClickListener: OnBrandClickListener
) : ListAdapter<Brand, BrandAdapter.BrandViewHolder>(BrandComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BrandViewHolder {
        val binding =
            ItemBrandBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return BrandViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BrandViewHolder, position: Int) {
        val currentItem = getItem(position)
        if (currentItem != null) {
            holder.bind(currentItem)
        }
    }

    inner class BrandViewHolder(private val binding: ItemBrandBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
                btnEdit.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val brand = getItem(position)
                        onBrandClickListener.onBrandEdit(brand)
                    }
                }

                btnDelete.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val brand = getItem(position)
                        onBrandClickListener.onBrandDelete(brand)
                    }
                }
            }
        }

        fun bind(brand: Brand) {
            binding.apply {
                tvBrandName.text = brand.name
                ivBrandIcon.setImageBitmap(brand.iconBitmap)
            }
        }
    }

    class BrandComparator : DiffUtil.ItemCallback<Brand>() {
        override fun areItemsTheSame(oldItem: Brand, newItem: Brand): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Brand, newItem: Brand): Boolean {
            return oldItem == newItem
        }
    }

    interface OnBrandClickListener {
        fun onBrandEdit(brand: Brand)
        fun onBrandDelete(brand: Brand)
    }
}