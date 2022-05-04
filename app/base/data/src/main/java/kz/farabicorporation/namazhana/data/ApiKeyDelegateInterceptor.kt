package kz.farabicorporation.namazhana.data

import okhttp3.Interceptor
import okhttp3.Response

class ApiKeyDelegateInterceptor(
    private val apiKey: String
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder().header("nh-api-key", apiKey)
        return chain.proceed(requestBuilder.build())
    }
}