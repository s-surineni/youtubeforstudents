package com.example.youtubeforstudents

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkModule {
    
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()
    
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://www.googleapis.com/youtube/v3/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    val youtubeApiService: YouTubeApiService = retrofit.create(YouTubeApiService::class.java)
    
    val youtubeRepository: YouTubeRepository = YouTubeRepository(youtubeApiService)
    
    val youtubeViewModel: YouTubeViewModel = YouTubeViewModel(youtubeRepository)
} 