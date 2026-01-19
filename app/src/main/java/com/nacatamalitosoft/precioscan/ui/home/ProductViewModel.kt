package com.nacatamalitosoft.precioscan.ui.home
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nacatamalitosoft.precioscan.models.StoreProduct

class ProductViewModel : ViewModel() {
    private val _product = MutableLiveData<StoreProduct?>()
    val product: LiveData<StoreProduct?> = _product

    private val _isFavorite = MutableLiveData<Boolean>()
    val isFavorite: LiveData<Boolean> = _isFavorite

    private val _relatedProducts = MutableLiveData<List<StoreProduct>>()
    val relatedProducts: LiveData<List<StoreProduct>> = _relatedProducts


    fun setProduct(product: StoreProduct){
        _product.value = product
    }
    fun setIsFavorite(isFavorite: Boolean ){
        _isFavorite.value = isFavorite
    }
    fun setRelatedProducts( products: List<StoreProduct>){
        _relatedProducts.value = products
    }
}