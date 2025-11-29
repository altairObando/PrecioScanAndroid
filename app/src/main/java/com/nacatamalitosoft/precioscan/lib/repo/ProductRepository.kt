package com.nacatamalitosoft.precioscan.lib.repo

import com.nacatamalitosoft.precioscan.lib.ApiErrorHandler
import com.nacatamalitosoft.precioscan.lib.ApiService
import com.nacatamalitosoft.precioscan.lib.helpers.Result
import com.nacatamalitosoft.precioscan.lib.helpers.safeApiCall
import com.nacatamalitosoft.precioscan.models.StoreProduct

class ProductRepository(private val api: ApiService, private val errorHandler: ApiErrorHandler) {

    suspend fun getRandomProducts(): List<StoreProduct> {
        return when(val result = safeApiCall { api.getRandomProducts() }) {
            is Result.Success -> result.data
            is Result.Error -> {
                if(result.code == 401) errorHandler.onUnauthorized()
                else errorHandler.onNetworkError(result.message)
                emptyList()
            }
        }
    }

    suspend fun searchProducts(name: String, store: String? = null): List<StoreProduct> {
        return when(val result = safeApiCall { api.searchProducts(name, store) }) {
            is Result.Success -> result.data
            is Result.Error -> {
                if(result.code == 401) errorHandler.onUnauthorized()
                else errorHandler.onNetworkError(result.message)
                emptyList()
            }
        }
    }

    suspend fun getProductById(id: Int): StoreProduct? {
        return when(val result = safeApiCall { api.getProductById(id) }) {
            is Result.Success -> result.data
            is Result.Error -> {
                if(result.code == 401) errorHandler.onUnauthorized()
                else errorHandler.onNetworkError(result.message)
                null
            }
        }
    }

    suspend fun getRelatedProducts(id: Int): List<StoreProduct> {
        return when(val result = safeApiCall { api.getRelatedProducts(id) }) {
            is Result.Success -> result.data
            is Result.Error -> {
                if(result.code == 401) errorHandler.onUnauthorized()
                else errorHandler.onNetworkError(result.message)
                emptyList()
            }
        }
    }
}