package com.nacatamalitosoft.precioscan.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nacatamalitosoft.precioscan.models.Store
import com.nacatamalitosoft.precioscan.models.StoreProduct

class HomeViewModel : ViewModel() {
    private val _suggestedProducts = MutableLiveData<List<StoreProduct>>()
    val suggestedProducts: LiveData<List<StoreProduct>> = _suggestedProducts
    private val _stores = MutableLiveData<List<Store>>()
    val stores: LiveData<List<Store>> = _stores
    private val _store = MutableLiveData<String>()
    val store: LiveData<String> = _store

    fun setStore(storeName: String){
        _store.value = storeName
    }
    fun setProducts(products: List<StoreProduct>){
        _suggestedProducts.value = products
    }
    fun setStores(stores: List<Store>){
        _stores.value = stores
    }
}