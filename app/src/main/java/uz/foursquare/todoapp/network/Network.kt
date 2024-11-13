package uz.foursquare.todoapp.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Network {

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val original = chain.request()
            val request = original.newBuilder()
                .header(NetworkConstants.AUTHORIZATION_HEADER, "Bearer Thranduil")
                .method(original.method, original.body)
                .build()
            chain.proceed(request)
        }
        .build()

    private val retrofit: Retrofit = try {
        Retrofit.Builder()
            .baseUrl("https://hive.mrdekk.ru/todo/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    } catch (e: Exception) {
        throw RuntimeException("Retrofit initialization failed", e)
    }

    fun <T> buildService(service: Class<T>): T {
        return retrofit.create(service)
    }

}

object NetworkConstants {
    const val AUTHORIZATION_HEADER = "Authorization"
    var LAST_KNOWN_REVISION_HEADER = ""
}