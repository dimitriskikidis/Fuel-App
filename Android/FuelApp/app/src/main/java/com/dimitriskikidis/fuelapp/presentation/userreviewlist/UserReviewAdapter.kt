package com.dimitriskikidis.fuelapp.presentation.userreviewlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dimitriskikidis.fuelapp.databinding.ItemUserReviewBinding
import com.dimitriskikidis.fuelapp.domain.models.UserReview
import java.time.format.DateTimeFormatter

class UserReviewAdapter(
    private val onUserReviewClickListener: OnUserReviewClickListener
) : ListAdapter<UserReview, UserReviewAdapter.UserReviewViewHolder>(UserReviewComparator()) {

    private val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter
        .ofPattern("dd/MM/yyyy")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserReviewViewHolder {
        val binding =
            ItemUserReviewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return UserReviewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserReviewViewHolder, position: Int) {
        val currentItem = getItem(position)
        if (currentItem != null) {
            holder.bind(currentItem)
        }
    }

    inner class UserReviewViewHolder(private val binding: ItemUserReviewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
                root.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val userReview = getItem(position)
                        onUserReviewClickListener.onUserReviewClick(userReview)
                    }
                }

                btnEdit.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val userReview = getItem(position)
                        onUserReviewClickListener.onUserReviewEdit(userReview)
                    }
                }

                btnDelete.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val userReview = getItem(position)
                        onUserReviewClickListener.onUserReviewDelete(userReview)
                    }
                }
            }
        }

        fun bind(userReview: UserReview) {
            binding.apply {
                tvFuelStationName.text = userReview.fuelStation.name
                tvLocationInfo.text =
                    "${userReview.fuelStation.address}, ${userReview.fuelStation.city}"
                rbRating.rating = userReview.rating.toFloat()
                tvReviewLastUpdate.text = dateTimeFormatter.format(userReview.lastUpdate)
                tvText.text = userReview.text
                tvText.isVisible = userReview.text.isNotEmpty()
            }
        }
    }

    class UserReviewComparator:DiffUtil.ItemCallback<UserReview>() {
        override fun areItemsTheSame(oldItem: UserReview, newItem: UserReview): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: UserReview, newItem: UserReview): Boolean {
            return oldItem == newItem
        }
    }

    interface OnUserReviewClickListener {
        fun onUserReviewClick(userReview: UserReview)
        fun onUserReviewEdit(userReview: UserReview)
        fun onUserReviewDelete(userReview: UserReview)
    }
}