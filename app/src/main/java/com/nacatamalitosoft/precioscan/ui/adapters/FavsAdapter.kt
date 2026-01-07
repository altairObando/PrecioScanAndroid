package com.nacatamalitosoft.precioscan.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nacatamalitosoft.precioscan.databinding.ItemSearchProductBinding
import com.nacatamalitosoft.precioscan.models.Favorites

class FavsAdapter ( private val onFavClick: (Favorites) -> Unit) :
    ListAdapter<Favorites, FavsAdapter.FavViewHolder>( FavsDiffCallback() ) {
    inner class FavViewHolder(val binding: ItemSearchProductBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(fav: Favorites){
            binding.tvProductName.text = fav.productDetail.name;
            binding.tvStoreName.text = fav.productDetail.store;
            ("C$ " + fav.productDetail.price).also {
                binding.tvPrice.text = it
            }
            Glide.with(binding.root.context)
                .load(fav.productDetail.imageUrl)
                .centerInside()
                .into(binding.imgProduct)
            binding.root.setOnClickListener {
                onFavClick(fav)
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavViewHolder {
        val binding = ItemSearchProductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FavViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    fun updateList(newFavs: List<Favorites>) {
        submitList(newFavs)
    }

}

class FavsDiffCallback : DiffUtil.ItemCallback<Favorites>(){
    override fun areItemsTheSame(oldItem: Favorites, newItem: Favorites): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Favorites, newItem: Favorites): Boolean {
        return oldItem == newItem
    }
}
