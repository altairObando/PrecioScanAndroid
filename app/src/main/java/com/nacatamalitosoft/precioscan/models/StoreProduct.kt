package com.nacatamalitosoft.precioscan.models

import com.google.gson.annotations.SerializedName

data class StoreProduct(
    val id: Int,
    val productId: Int,
    val name: String,
    val description: String,
    @SerializedName("image_url") val imageUrl: String,
    val url: String,
    @SerializedName("is_available") val isAvailable: Boolean,
    @SerializedName("is_active") val isActive: Boolean,
    val store: String,
    val price: Double
)
