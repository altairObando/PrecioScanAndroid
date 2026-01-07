package com.nacatamalitosoft.precioscan.lib
import android.content.Context
import com.nacatamalitosoft.precioscan.BuildConfig
import com.nacatamalitosoft.precioscan.lib.helpers.AuthInterceptor
import com.nacatamalitosoft.precioscan.lib.helpers.TokenManager
import com.nacatamalitosoft.precioscan.models.RefreshRequest
import okhttp3.Authenticator
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

fun createRetrofit(context: Context): Retrofit {
    val baseUrl = BuildConfig.API_URL;
    val client = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor(context))
        .authenticator(object : Authenticator {
            override fun authenticate(route: Route?, response: Response): Request? {
                val refreshToken = TokenManager.getRefreshToken(context) ?: return null

                val retrofit = Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                val apiService = retrofit.create(ApiService::class.java)

                return try {
                    val call = apiService.refreshTokenSync(RefreshRequest(refreshToken))
                    val refreshResponse = call.execute().body() ?: return null

                    TokenManager.saveTokens(context, refreshResponse.access, refreshResponse.refresh)

                    response.request.newBuilder()
                        .header("Authorization", "Bearer ${refreshResponse.access}")
                        .build()

                } catch (e: Exception) {
                    TokenManager.clearTokens(context)
                    null
                }
            }
        })
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    return Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}

interface ApiErrorHandler {
    fun onUnauthorized()
    fun onNetworkError(msg: String)
}