package com.nacatamalitosoft.precioscan.ui.favorites

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
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
    private lateinit var repo: FavRepository;
    private lateinit var adapter: FavsAdapter;
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        val root: View = binding.root;
        val apiService = createRetrofit(requireContext()).create(ApiService::class.java)
        repo = FavRepository(apiService, this);
        adapter = FavsAdapter(){ fav ->
            val intent = Intent(requireContext(), ProductDetailActivity::class.java);
            intent.putExtra(ProductDetailActivity.EXTRA_PRODUCT_ID, fav.productDetail.id);
            intent.putExtra(ProductDetailActivity.EXTRA_FAV_ID, fav.id);
            startActivity(intent)
        }
        loadFavs()
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun loadFavs (){
        lifecycleScope.launch {
            val myFavs = repo.get()
            adapter.updateList(myFavs)
        }
    }

    override fun onUnauthorized() {
        binding.notLoggedInLayout.root.visibility = View.VISIBLE;
    }

    override fun onNetworkError(msg: String) {
        binding.notLoggedInLayout.root.visibility = View.VISIBLE;
    }
}