package com.dimitriskikidis.fuelapp.presentation.fuellist

import android.icu.text.DecimalFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dimitriskikidis.fuelapp.databinding.ItemFuelSearchResultBinding
import com.dimitriskikidis.fuelapp.domain.models.Brand
import com.dimitriskikidis.fuelapp.domain.models.FuelSearchResult
import com.dimitriskikidis.fuelapp.domain.models.FuelStation
import java.math.RoundingMode
import java.time.format.DateTimeFormatter

class FuelSearchResultAdapter(
    private val onFuelClickListener: OnFuelClickListener
) : ListAdapter<FuelSearchResult, FuelSearchResultAdapter.FuelSearchResultViewHolder>(
    FuelSearchResultComparator()
) {

    private val priceFormatter = DecimalFormat("0.000").apply {
        roundingMode = RoundingMode.HALF_UP.ordinal
    }

    private val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter
        .ofPattern("dd/MM/yyyy HH:mm")

    private val distanceFormatter = DecimalFormat("0.00").apply {
        roundingMode = RoundingMode.HALF_UP.ordinal
    }

    private val ratingFormatter = DecimalFormat("0.0").apply {
        roundingMode = RoundingMode.HALF_UP.ordinal
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FuelSearchResultViewHolder {
        val binding =
            ItemFuelSearchResultBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return FuelSearchResultViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FuelSearchResultViewHolder, position: Int) {
        val currentItem = getItem(position)
        if (currentItem != null) {
            holder.bind(currentItem)
        }
    }

    inner class FuelSearchResultViewHolder(private val binding: ItemFuelSearchResultBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
                root.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val fuelStation = getItem(position).fuelStation
                        onFuelClickListener.onFuelClick(fuelStation)
                    }
                }
            }
        }

        fun bind(fuelSearchResult: FuelSearchResult) {
            val fuelStation = fuelSearchResult.fuelStation
            val fuel = fuelSearchResult.fuel
            val brand = fuelStation.brand!!
            binding.apply {
                tvFuelName.text = fuel.name
                ivBrandIcon.setImageBitmap(brand.iconBitmap)
                tvBrandName.text = brand.name
                val price = priceFormatter.format(fuel.price / 1000.0)
                tvFuelPrice.text = "$price €"
                tvFuelLastUpdate.text = dateTimeFormatter.format(fuel.lastUpdate)
                tvFuelStationName.text = fuelStation.name
                val distance = fuelStation.distance
                tvFuelStationDistance.isVisible = distance != null
                if (distance != null) {
                    tvFuelStationDistance.text = "${distanceFormatter.format(distance)} km"
                }
                val hasReviews = fuelStation.rating != null
                tvReviewSummary.isVisible = hasReviews
                if (hasReviews) {
                    val rating = fuelStation.rating!!
                    rbRating.rating = rating
                    tvReviewSummary.text =
                        "${ratingFormatter.format(rating)} (${fuelStation.reviewCount})"
                }
            }
        }
    }

    class FuelSearchResultComparator : DiffUtil.ItemCallback<FuelSearchResult>() {
        override fun areItemsTheSame(
            oldItem: FuelSearchResult,
            newItem: FuelSearchResult
        ): Boolean {
            return oldItem.fuelStation.id == newItem.fuelStation.id
        }

        override fun areContentsTheSame(
            oldItem: FuelSearchResult,
            newItem: FuelSearchResult
        ): Boolean {
            return oldItem == newItem
        }
    }

    interface OnFuelClickListener {
        fun onFuelClick(fuelStation: FuelStation)
    }
}