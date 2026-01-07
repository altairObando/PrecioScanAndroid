package com.nacatamalitosoft.precioscan.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nacatamalitosoft.precioscan.databinding.ProductsCardItemBinding
import com.nacatamalitosoft.precioscan.models.StoreProduct

class ProductsAdapter(private val onProductPress: (product: StoreProduct) -> Unit ,private var products: List<StoreProduct>) :
    RecyclerView.Adapter<ProductsAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(val binding: ProductsCardItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ProductsCardItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        with(holder.binding) {
            productName.text = product.name
            ("C$" + product.price).also { productPrice.text = it }
            Glide.with(holder.itemView.context)
                .load(product.imageUrl)
                .centerCrop()
                .into(productImage)
            root.setOnClickListener {
                onProductPress(product)
            }
        }
    }

    override fun getItemCount() = products.size

    fun updateList(newProducts: List<StoreProduct>) {
        products = newProducts
        notifyDataSetChanged()
    }
}