package com.nacatamalitosoft.precioscan.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nacatamalitosoft.precioscan.R
import com.nacatamalitosoft.precioscan.databinding.ItemSearchProductBinding
import com.nacatamalitosoft.precioscan.models.StoreProduct
import java.text.NumberFormat
import java.util.Locale

class SearchResultsAdapter(
    private val onProductClicked: (product: StoreProduct) -> Unit
) : ListAdapter<StoreProduct, SearchResultsAdapter.ViewHolder>(ProductDiffCallback()) {


    private val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-NI"))

    class ViewHolder(val binding: ItemSearchProductBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSearchProductBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = getItem(position)

        with(holder.binding) {
            tvProductName.text = product.name
            tvStoreName.text = product.store
            "C$ ${currencyFormatter.format(product.price)}".also { tvPrice.text = it }

            Glide.with(imgProduct.context)
                .load(product.imageUrl)
                .placeholder(R.drawable.ic_box)
                .error(R.drawable.ic_warning)
                .centerCrop()
                .into(imgProduct)

            root.setOnClickListener { onProductClicked(product) }
        }
    }

    class ProductDiffCallback : DiffUtil.ItemCallback<StoreProduct>() {
        override fun areItemsTheSame(oldItem: StoreProduct, newItem: StoreProduct): Boolean {
            return oldItem.imageUrl == newItem.imageUrl
        }

        override fun areContentsTheSame(oldItem: StoreProduct, newItem: StoreProduct): Boolean {
            return oldItem == newItem
        }
    }
}