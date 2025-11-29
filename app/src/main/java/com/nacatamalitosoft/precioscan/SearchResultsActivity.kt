package com.nacatamalitosoft.precioscan

import android.os.Bundle
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
import com.nacatamalitosoft.precioscan.ui.adapters.SearchResultsAdapter
import kotlinx.coroutines.launch

class SearchResultsActivity : AppCompatActivity(), ApiErrorHandler {

    private lateinit var binding: ActivitySearchResultsBinding
    private lateinit var repository: ProductRepository
    private lateinit var adapter: SearchResultsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchResultsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }

        val apiService = createRetrofit(this).create(ApiService::class.java)
        repository = ProductRepository(apiService, this)

        adapter = SearchResultsAdapter(emptyList())
        binding.rvSearchResults.layoutManager = LinearLayoutManager(this)
        binding.rvSearchResults.adapter = adapter

        val query = intent.getStringExtra("query") ?: ""
        val store = intent.getStringExtra("store")

        searchProducts(query, store)
    }

    private fun searchProducts(query: String, store: String?) {
        binding.progressBar.visibility = View.VISIBLE
        binding.tvEmpty.visibility = View.GONE
        
        lifecycleScope.launch {
            val products = repository.searchProducts(query, store)
            binding.progressBar.visibility = View.GONE
            
            if (products.isEmpty()) {
                binding.tvEmpty.visibility = View.VISIBLE
                adapter.updateList(emptyList())
            } else {
                adapter.updateList(products)
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