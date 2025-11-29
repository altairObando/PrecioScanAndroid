package com.nacatamalitosoft.precioscan.models

data class LoginRequest (
    val username: String,
    val password: String
)

data class LoginResponse (
    val user: User,
    val tokens: Tokens
)

data class Tokens (
    val access: String,
    val refresh: String
)

data class User (
    val username: String,
    val email: String,
    val isSuperuser: Boolean,
    val groups: List<Any?>
)
data class RefreshRequest(val refresh: String)
data class RefreshResponse(val access: String, val refresh: String)
