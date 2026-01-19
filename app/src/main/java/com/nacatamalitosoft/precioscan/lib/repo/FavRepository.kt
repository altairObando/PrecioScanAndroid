package com.nacatamalitosoft.precioscan.lib.repo

import com.nacatamalitosoft.precioscan.lib.ApiErrorHandler
import com.nacatamalitosoft.precioscan.lib.ApiService
import com.nacatamalitosoft.precioscan.lib.helpers.safeApiCall
import com.nacatamalitosoft.precioscan.lib.helpers.Result
import com.nacatamalitosoft.precioscan.models.Favorites
import com.nacatamalitosoft.precioscan.models.FavoritesPost

class FavRepository (private val api: ApiService, private val errorHandler: ApiErrorHandler) {
    suspend fun get() : List<Favorites> {
        return when(val result = safeApiCall { api.getFavorite() }) {
            is Result.Success -> result.data
            is Result.Error -> {
                if (result.code == 401) errorHandler.onUnauthorized()
                else errorHandler.onNetworkError(result.message)
                emptyList()
            }
        }
    }
    suspend fun add( product: Int): Boolean {
        val fav = FavoritesPost(product = product)
        return when(val result = safeApiCall { api.addFavorite(fav) }) {
            is Result.Success -> {
                result.data
                true
            }
            is Result.Error -> {
                errorHandler.onNetworkError(result.message)
                print(result.message)
                false
            }
        }
    }
    suspend fun remove(id: Int){
        return when(val result = safeApiCall { api.deleteFavorite(id) }){
            is Result.Success -> result.data
            is Result.Error -> errorHandler.onNetworkError(result.message)
        }
    }
    suspend fun isFavorite( product: Int): Boolean {
        return when(val result = safeApiCall { api.isFavorite(product) }){
            is Result.Success -> result.data.isFavorite
            is Result.Error ->  false
        }
    }
}