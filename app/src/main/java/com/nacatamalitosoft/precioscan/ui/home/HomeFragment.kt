package com.nacatamalitosoft.precioscan.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.nacatamalitosoft.precioscan.LoginActivity
import com.nacatamalitosoft.precioscan.R
import com.nacatamalitosoft.precioscan.ui.home.ProductDetailActivity
import com.nacatamalitosoft.precioscan.ui.home.SearchResultsActivity
import com.nacatamalitosoft.precioscan.databinding.FragmentHomeBinding
import com.nacatamalitosoft.precioscan.lib.ApiErrorHandler
import com.nacatamalitosoft.precioscan.lib.ApiService
import com.nacatamalitosoft.precioscan.lib.createRetrofit
import com.nacatamalitosoft.precioscan.lib.repo.ProductRepository
import com.nacatamalitosoft.precioscan.lib.repo.StoreRepository
import com.nacatamalitosoft.precioscan.ui.adapters.ProductsAdapter
import com.nacatamalitosoft.precioscan.ui.adapters.StoreAdapter
import kotlinx.coroutines.launch

class HomeFragment : Fragment(), ApiErrorHandler {
    private var _binding: FragmentHomeBinding? = null
    private lateinit var productRepository: ProductRepository
    private lateinit var storeRepository: StoreRepository
    private lateinit var productsAdapter: ProductsAdapter
    private lateinit var storeAdapter: StoreAdapter

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val homeViewModel =
            ViewModelProvider(this)[HomeViewModel::class.java]

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val apiService = createRetrofit(requireContext()).create(ApiService::class.java)
        // Ocultar tienda seleccionada
        binding.homeSelectedStore.visibility = View.GONE

        productRepository = ProductRepository(apiService, this)
        storeRepository = StoreRepository(apiService, this)

        // Configurar RecyclerView Productos
        productsAdapter = ProductsAdapter({ product ->
            val intent = Intent(requireContext(), ProductDetailActivity::class.java)
            intent.putExtra(ProductDetailActivity.EXTRA_PRODUCT_ID, product.id);
            startActivity(intent)
        },emptyList())
        binding.rvHomeProducts.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvHomeProducts.adapter = productsAdapter

        // Configurar RecyclerView Tiendas
        storeAdapter = StoreAdapter{ store ->
            homeViewModel.setStore(store.name)
        }
        binding.rvStores.layoutManager = GridLayoutManager(context, 2)
        binding.rvStores.adapter = storeAdapter

        loadData()
        homeViewModel.store.observe(viewLifecycleOwner) { store ->
            binding.homeSelectedStore.animate()
                .alpha(if (store.isNullOrEmpty()) 0f else 1f)
                .setDuration(200)
                .withEndAction {
                    binding.homeSelectedStore.visibility = if (store.isNullOrEmpty()) View.GONE else View.VISIBLE
                    binding.selectedStore.text = getString(R.string.buscando_en, store)
                }
        }
        binding.txtRemoverTienda.setOnClickListener {
            homeViewModel.setStore(String())
        }
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { q ->
                    val intent = Intent(requireContext(), SearchResultsActivity::class.java)
                    intent.putExtra("query", q)
                    homeViewModel.store.value?.let { storeName ->
                        if (storeName.isNotEmpty()) {
                            intent.putExtra("store", storeName)
                        }
                    }
                    startActivity(intent)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
        return root
    }

    private fun loadData() {
        binding.loadingProducts.visibility = View.VISIBLE
        lifecycleScope.launch {
            // Cargar productos
            val products = productRepository.getRandomProducts()
            productsAdapter.updateList(products)

            // Cargar tiendas
            val stores = storeRepository.getStores()
            storeAdapter.updateList(stores)
            binding.loadingProducts.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onUnauthorized() {
        Toast.makeText(context, getString(R.string.expired_session), Toast.LENGTH_SHORT).show()
        val intent = Intent(requireContext(), LoginActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onNetworkError(msg: String) {
        Toast.makeText(context, "Error de red: $msg", Toast.LENGTH_SHORT).show()
    }
}