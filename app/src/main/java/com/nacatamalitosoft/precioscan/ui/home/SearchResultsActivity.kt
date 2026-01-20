package com.nacatamalitosoft.precioscan.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.nacatamalitosoft.precioscan.databinding.ActivitySearchResultsBinding
import com.nacatamalitosoft.precioscan.lib.ApiErrorHandler
import com.nacatamalitosoft.precioscan.lib.ApiService
import com.nacatamalitosoft.precioscan.lib.createRetrofit
import com.nacatamalitosoft.precioscan.lib.repo.ProductRepository
import com.nacatamalitosoft.precioscan.models.StoreProduct
import com.nacatamalitosoft.precioscan.ui.adapters.SearchResultsAdapter
import kotlinx.coroutines.launch

class SearchResultsActivity : AppCompatActivity(), ApiErrorHandler {

    private lateinit var binding: ActivitySearchResultsBinding
    private lateinit var repository: ProductRepository
    private lateinit var adapter: SearchResultsAdapter

    companion object {
        const val EXTRA_QUERY = "extra_query"
        const val EXTRA_STORE = "extra_store"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchResultsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }

        val apiService = createRetrofit(this).create(ApiService::class.java)
        repository = ProductRepository(apiService, this)

        adapter = SearchResultsAdapter({ product ->
            val intent = Intent(this, ProductDetailActivity::class.java)
            intent.putExtra(ProductDetailActivity.EXTRA_PRODUCT_ID, product.id)
            startActivity(intent)
        })
        binding.rvSearchResults.layoutManager = LinearLayoutManager(this)
        binding.rvSearchResults.adapter = adapter

        val query = intent.getStringExtra(EXTRA_QUERY) ?: ""
        val store = intent.getStringExtra(EXTRA_STORE)

        searchProducts(query, store)
    }

    private fun searchProducts(query: String, stores: String?) {
        binding.progressBar.visibility = View.VISIBLE
        binding.tvEmpty.visibility = View.GONE

        val listaAcumulada = mutableListOf<StoreProduct>()
        adapter.submitList(emptyList())


        val storeList : List<String> = (stores?:"").split(",")
        var peticionesFinalizadas = 0

        storeList.forEach { storeName ->
            lifecycleScope.launch {
                try {
                    val products = repository.searchProducts(query, storeName.trim())
                    if (products.isNotEmpty()) {
                        listaAcumulada.addAll(products)
                        adapter.submitList(listaAcumulada.toList())
                    }
                } catch (e: Exception) {
                    Log.e("Search", "Error en tienda $storeName: ${e.message}")
                } finally {
                    peticionesFinalizadas++
                    if (peticionesFinalizadas == storeList.size) {
                        binding.progressBar.visibility = View.GONE
                        if (listaAcumulada.isEmpty()) binding.tvEmpty.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    override fun onUnauthorized() {
        Toast.makeText(this, "Sesión expirada", Toast.LENGTH_SHORT).show()
    }

    override fun onNetworkError(msg: String) {
        Toast.makeText(this, "Error de red: $msg", Toast.LENGTH_SHORT).show()
    }
}