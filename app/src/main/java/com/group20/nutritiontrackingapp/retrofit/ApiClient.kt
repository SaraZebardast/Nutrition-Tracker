package com.group20.nutritiontrackingapp.retrofit

import com.group20.nutritiontrackingapp.util.Constants
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.net.ssl.SSLSession

object ApiClient {
    private var retrofit: Retrofit? = null
    // Some PCs cannot access json.keeper so with this, We remove the SSL Check
    val okHttpClient = OkHttpClient.Builder()
        .hostnameVerifier { hostname: String, session: SSLSession -> true }  // SSL is closed
        .build()
    fun getClient(): Retrofit {
        if (retrofit == null)
            retrofit = Retrofit.Builder()
                .baseUrl(Constants.baseUrl)
                .client(okHttpClient) // implement removing SSL Check
                .addConverterFactory(GsonConverterFactory.create()) //retrofit will understand as a converter GSON converter will be used
                .build()
        return retrofit as Retrofit
    }
}