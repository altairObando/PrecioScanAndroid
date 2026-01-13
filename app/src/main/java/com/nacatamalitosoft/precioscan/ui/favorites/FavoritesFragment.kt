package com.nacatamalitosoft.precioscan.ui.favorites

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.nacatamalitosoft.precioscan.LoginActivity
import com.nacatamalitosoft.precioscan.databinding.FragmentFavoritesBinding
import com.nacatamalitosoft.precioscan.lib.ApiErrorHandler
import com.nacatamalitosoft.precioscan.lib.ApiService
import com.nacatamalitosoft.precioscan.lib.createRetrofit
import com.nacatamalitosoft.precioscan.lib.repo.FavRepository
import com.nacatamalitosoft.precioscan.ui.adapters.FavsAdapter
import com.nacatamalitosoft.precioscan.ui.home.ProductDetailActivity
import kotlinx.coroutines.launch

class FavoritesFragment : Fragment(), ApiErrorHandler {

    private var _binding: FragmentFavoritesBinding? = null
    private lateinit var repo: FavRepository
    private lateinit var adapter: FavsAdapter

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val apiService = createRetrofit(requireContext()).create(ApiService::class.java)
        repo = FavRepository(apiService, this)

        adapter = FavsAdapter { fav ->
            val intent = Intent(requireContext(), ProductDetailActivity::class.java)
            intent.putExtra(ProductDetailActivity.EXTRA_PRODUCT_ID, fav.productDetail.id)
            startActivity(intent)
        }

        binding.rvFavorites.layoutManager = LinearLayoutManager(requireContext())
        binding.rvFavorites.adapter = adapter

        loadFavs()

        binding.notLoggedInLayout.btnGoToLogin.setOnClickListener { openLogin() }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadFavs() {
        lifecycleScope.launch {
            val myFavs = repo.get()

            // Hide everything first
            binding.notLoggedInLayout.root.visibility = View.GONE

            if (myFavs.isEmpty()) {
                binding.emptyFavoritesLayout.visibility = View.VISIBLE
                binding.contentLayout.visibility = View.GONE
            } else {
                binding.emptyFavoritesLayout.visibility = View.GONE
                binding.contentLayout.visibility = View.VISIBLE
                adapter.updateList(myFavs)
            }
        }
    }

    private fun openLogin() {
        val intent = Intent(requireContext(), LoginActivity::class.java)
        startActivity(intent)
    }

    override fun onUnauthorized() {
        binding.notLoggedInLayout.root.visibility = View.VISIBLE
        binding.contentLayout.visibility = View.GONE
        binding.emptyFavoritesLayout.visibility = View.GONE
    }

    override fun onNetworkError(msg: String) {
        // You might want to show a specific network error UI here
        binding.notLoggedInLayout.root.visibility = View.VISIBLE
        binding.contentLayout.visibility = View.GONE
        binding.emptyFavoritesLayout.visibility = View.GONE
    }
}