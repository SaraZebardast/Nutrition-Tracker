package com.group20.nutritiontrackingapp.retrofitRecipe

import com.group20.nutritiontrackingapp.util.Constants
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.net.ssl.SSLSession

object ApiClient2 {
    private var retrofit: Retrofit? = null
    val okHttpClient = OkHttpClient.Builder()
        .hostnameVerifier { hostname: String, session: SSLSession -> true }  // SSL is closed
        .build()
    fun getClient(): Retrofit {
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(Constants.recipeUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit as Retrofit
    }
}