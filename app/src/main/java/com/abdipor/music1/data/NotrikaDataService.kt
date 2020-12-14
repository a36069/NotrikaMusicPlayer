package com.abdipor.music1.data

import com.abdipor.music1.model.Contributor
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

private const val BASE_URL = "https://github.com/a36069/NotrikaMusicPlayer/blob/dev/data/"

interface NotrikaDataService {

    @GET("translators.json")
    suspend fun getContributors(): List<Contributor>

    @GET("translators.json")
    suspend fun getTranslators(): List<Contributor>

    companion object {
        val retoService: NotrikaDataService = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
            .create(NotrikaDataService::class.java)
    }
}
