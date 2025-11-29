package com.nacatamalitosoft.precioscan.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nacatamalitosoft.precioscan.databinding.ItemSearchProductBinding
import com.nacatamalitosoft.precioscan.models.StoreProduct

class SearchResultsAdapter(private var products: List<StoreProduct>) :
    RecyclerView.Adapter<SearchResultsAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemSearchProductBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSearchProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = products[position]
        with(holder.binding) {
            tvProductName.text = product.name
            tvStoreName.text = product.store
            tvPrice.text = "$${product.price}"
            
            Glide.with(root.context)
                .load(product.imageUrl)
                .centerCrop()
                .into(imgProduct)
        }
    }

    override fun getItemCount() = products.size

    fun updateList(newProducts: List<StoreProduct>) {
        products = newProducts
        notifyDataSetChanged()
    }
}