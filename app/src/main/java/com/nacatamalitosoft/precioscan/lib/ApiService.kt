package com.nacatamalitosoft.precioscan.lib
import com.nacatamalitosoft.precioscan.models.Favorites
import com.nacatamalitosoft.precioscan.models.FavoritesPost
import com.nacatamalitosoft.precioscan.models.IsFavoriteResponse
import com.nacatamalitosoft.precioscan.models.LoginRequest
import com.nacatamalitosoft.precioscan.models.LoginResponse
import com.nacatamalitosoft.precioscan.models.RefreshRequest
import com.nacatamalitosoft.precioscan.models.RefreshResponse
import com.nacatamalitosoft.precioscan.models.Store
import com.nacatamalitosoft.precioscan.models.StoreProduct
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @POST("auth/login/")
    suspend fun login(@Body loginRequest: LoginRequest): LoginResponse
    @POST("auth/refresh/")
    fun refreshTokenSync(@Body request: RefreshRequest): retrofit2.Call<RefreshResponse>

    /// Products
    @GET("store-products/random/")
    suspend fun getRandomProducts(): List<StoreProduct>
    @GET("store-products/search/")
    suspend fun searchProducts(@Query("name") name: String, @Query("store") store: String? = null): List<StoreProduct>
    @GET("store-products/{id}/")
    suspend fun getProductById(@Path("id") id: Int): StoreProduct
    @GET("store-products/related/")
    suspend fun getRelatedProducts(@Query("pk") id: Int): List<StoreProduct>
    // Stores
    @GET("stores/")
    suspend fun getStores(): List<Store>
    // Favorites
    @POST("favorites/")
    suspend fun addFavorite(@Body product: FavoritesPost): Favorites
    @DELETE("favorites/{id}/")
    suspend fun deleteFavorite(@Path("id") id: Int)
    @GET("favorites/isFavorite/")
    suspend fun isFavorite(@Query("pk") pk: Int) : IsFavoriteResponse
    @GET("favorites/")
    suspend fun getFavorite(): List<Favorites>
}