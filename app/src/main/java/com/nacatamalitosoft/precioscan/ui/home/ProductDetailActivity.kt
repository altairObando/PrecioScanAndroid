package com.nacatamalitosoft.precioscan.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.nacatamalitosoft.precioscan.LoginActivity
import com.nacatamalitosoft.precioscan.R
import com.nacatamalitosoft.precioscan.databinding.ActivityProductDetailBinding
import com.nacatamalitosoft.precioscan.lib.ApiErrorHandler
import com.nacatamalitosoft.precioscan.lib.ApiService
import com.nacatamalitosoft.precioscan.lib.createRetrofit
import com.nacatamalitosoft.precioscan.lib.repo.FavRepository
import com.nacatamalitosoft.precioscan.lib.repo.ProductRepository
import com.nacatamalitosoft.precioscan.models.StoreProduct
import com.nacatamalitosoft.precioscan.ui.adapters.SearchResultsAdapter
import kotlinx.coroutines.launch

class ProductDetailActivity : AppCompatActivity(), ApiErrorHandler {

    private lateinit var binding: ActivityProductDetailBinding
    private lateinit var repository: ProductRepository
    private lateinit var relatedProductsAdapter: SearchResultsAdapter
    private lateinit var favRepository: FavRepository
    private lateinit var productViewModel: ProductViewModel


    companion object {
        const val EXTRA_PRODUCT_ID = "extra_product_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        productViewModel = ViewModelProvider(this)[ProductViewModel::class.java]
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.product_detail)

        val apiService = createRetrofit(this).create(ApiService::class.java)
        repository = ProductRepository(apiService, this)
        favRepository = FavRepository(apiService, this)

        setupListeners()
        setupRecyclerView()

        binding.btnFavorite.visibility = View.GONE
        productViewModel.isFavorite.observe(this){ isFavorite ->
            binding.btnFavorite.animate()
                .withEndAction { setIcon(isFavorite) }
        }
        productViewModel.product.observe(this){ product ->
            if(product != null)
                displayProduct(product)
        }
        productViewModel.relatedProducts.observe(this){ related ->
            if( related != null && related.isNotEmpty())
                relatedProductsAdapter.updateList(related)
        }

        val productId = intent.getIntExtra(EXTRA_PRODUCT_ID, -1)
        if (productId != -1) {
            if (productViewModel.product.value == null) {
                loadProduct(productId)
            }
        } else {
            Toast.makeText(this, getString(R.string.product_not_valid), Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        //onBackPressed()
        return true
    }

    private fun setupListeners() {
        binding.btnFavorite.setOnClickListener { addOrRemoveFav() }

        binding.btnShare.setOnClickListener {
            productViewModel.product.value?.let { product ->
                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, "Mira este producto: ${product.name} en ${product.store} por $${product.price} \n ${product.url}")
                }
                startActivity(Intent.createChooser(shareIntent, "Compartir"))
            }
        }

        binding.btnNotify.setOnClickListener {
            Toast.makeText(this, "Alerta de precio activada", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupRecyclerView() {
        relatedProductsAdapter = SearchResultsAdapter({ product ->
            val intent = Intent(this, ProductDetailActivity::class.java)
            intent.putExtra(EXTRA_PRODUCT_ID, product.id)
            startActivity(intent)
        }, emptyList())
        binding.rvRelatedProducts.adapter = relatedProductsAdapter
    }

    private fun loadProduct(id: Int) {
        lifecycleScope.launch {
            val product = repository.getProductById(id)
            if (product != null) {
                productViewModel.setProduct( product )
                displayProduct(product)
                loadRelatedProducts(product.productId)
                checkIsFavorite(product.id)
            } else {
                Toast.makeText(this@ProductDetailActivity, "No se pudo cargar el producto", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadRelatedProducts(productId: Int) {
        lifecycleScope.launch {
            val relatedProducts = repository.getRelatedProducts(productId)
            if (relatedProducts.isNotEmpty()) {
                relatedProductsAdapter.updateList(relatedProducts)
            } else {
                binding.rvRelatedProducts.visibility = View.GONE
            }
        }
    }

    private fun displayProduct(product: StoreProduct) {
        binding.tvProductName.text = product.name
        binding.tvStoreName.text = product.store
        "$ ${product.price}".also { binding.tvProductPrice.text = it }
        binding.tvProductAvailability.text = if (product.isAvailable) "Disponible" else "No disponible"


        Glide.with(this)
            .load(product.imageUrl)
            .placeholder(R.mipmap.ic_launcher)
            .error(R.mipmap.ic_launcher)
            .into(binding.ivProductImage)
    }

    override fun onUnauthorized() {
        Toast.makeText(this, getString(R.string.expired_session), Toast.LENGTH_SHORT).show()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onNetworkError(msg: String) {
        Toast.makeText(this, "Error de red: $msg", Toast.LENGTH_SHORT).show()
    }
    private fun addOrRemoveFav(){
        val productId: Int = productViewModel.product.value?.id ?: return
        lifecycleScope.launch {
            val added = favRepository.add( productId )
            productViewModel.setIsFavorite(added)
        }
    }
    private fun setIcon(isFavorite: Boolean){
        val iconId = if (isFavorite) android.R.drawable.btn_star_big_on else android.R.drawable.btn_star_big_off
        binding.btnFavorite.setIconResource(iconId)
    }
    private fun checkIsFavorite( productId: Int){
        binding.btnFavorite.visibility = View.VISIBLE
        lifecycleScope.launch {
            val isFav = favRepository.isFavorite(productId)
            productViewModel.setIsFavorite(isFav)
        }
    }
}