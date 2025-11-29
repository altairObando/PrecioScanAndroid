package com.nacatamalitosoft.precioscan.models
import com.google.gson.annotations.SerializedName

data class Store (
    val id: Int,
    val code: String,
    val name: String,
    val url: String,
    @SerializedName("logo_url")
    val logoUrl: String,
    @SerializedName("is_active")
    val isActive: Boolean
)