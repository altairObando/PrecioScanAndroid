package com.nacatamalitosoft.precioscan.lib.repo

import com.nacatamalitosoft.precioscan.lib.ApiErrorHandler
import com.nacatamalitosoft.precioscan.lib.ApiService
import com.nacatamalitosoft.precioscan.lib.helpers.safeApiCall
import com.nacatamalitosoft.precioscan.lib.helpers.Result
import com.nacatamalitosoft.precioscan.models.Favorites

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
}