package com.nacatamalitosoft.precioscan.models

import com.google.gson.annotations.SerializedName

data class Favorites(
    val id: Int,
    @SerializedName("product_detail")
    val productDetail: StoreProduct,
    val user: String,
    @SerializedName("created_at")
    val createdAt: String
)