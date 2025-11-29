package com.nacatamalitosoft.precioscan.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nacatamalitosoft.precioscan.databinding.StoreCardItemBinding
import com.nacatamalitosoft.precioscan.models.Store

class StoreAdapter(
    private val onStoreClick: (Store) -> Unit
) : ListAdapter<Store, StoreAdapter.StoreViewHolder>(StoreDiffCallback()) {

    inner class StoreViewHolder(val binding: StoreCardItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(store: Store) {
            binding.storeName.text = store.name
            Glide.with(binding.root.context)
                .load(store.logoUrl)
                .centerCrop()
                .into(binding.storeLogo)

            binding.root.setOnClickListener {
                onStoreClick(store)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreViewHolder {
        val binding = StoreCardItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return StoreViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoreViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun updateList(newStores: List<Store>) {
        submitList(newStores)
    }
}

class StoreDiffCallback : DiffUtil.ItemCallback<Store>() {

    override fun areItemsTheSame(oldItem: Store, newItem: Store): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Store, newItem: Store): Boolean {
        return oldItem == newItem
    }
}