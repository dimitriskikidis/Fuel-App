package com.dimitriskikidis.fuelapp.presentation.reviewlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dimitriskikidis.fuelapp.databinding.ItemReviewBinding
import com.dimitriskikidis.fuelapp.domain.models.Review
import java.time.format.DateTimeFormatter

class ReviewAdapter :
    ListAdapter<Review, ReviewAdapter.ReviewViewHolder>(ReviewComparator()) {

    private val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter
        .ofPattern("dd/MM/yyyy")

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ReviewViewHolder {
        val binding = ItemReviewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ReviewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val currentItem = getItem(position)
        if (currentItem != null) {
            holder.bind(currentItem)
        }
    }

    inner class ReviewViewHolder(private val binding: ItemReviewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(review: Review) {
            binding.clReview.apply {
                rbRating.rating = review.rating.toFloat()
                tvUsername.text = review.username
                tvLastUpdate.text = dateTimeFormatter.format(review.lastUpdate)
                tvText.text = review.text
                tvText.isVisible = review.text.isNotEmpty()
            }
        }
    }

    class ReviewComparator : DiffUtil.ItemCallback<Review>() {
        override fun areItemsTheSame(oldItem: Review, newItem: Review): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Review, newItem: Review): Boolean {
            return oldItem == newItem
        }
    }
}