package com.nacatamalitosoft.precioscan.lib.helpers
import retrofit2.HttpException
import java.io.IOException

sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val message: String, val code: Int? = null) : Result<Nothing>()
}
data class ApiError(
    val error: String
)

suspend fun <T> safeApiCall(apiCall: suspend () -> T): Result<T> {
    return try {
        val response = apiCall()
        Result.Success(response)
    } catch (e: HttpException) {
        val errorBody = e.response()?.errorBody()?.string()
        val message = try {
            val gson = com.google.gson.Gson()
            val apiError = gson.fromJson(errorBody, ApiError::class.java)
            apiError.error
        } catch (ex: Exception) {
            errorBody ?: "Error HTTP: ${e.code()}"
        }
        Result.Error(message, e.code())
    } catch (e: IOException) {
        Result.Error("Error de red: ${e.message}")
    } catch (e: Exception) {
        Result.Error("Error inesperado: ${e.message}")
    }
}