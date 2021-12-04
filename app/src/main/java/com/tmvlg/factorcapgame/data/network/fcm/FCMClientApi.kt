package com.tmvlg.factorcapgame.data.network.fcm

import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory

private const val BASE_URL = "https://fcm.googleapis.com/"

private var retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(JacksonConverterFactory.create())
    .build()

object FCMClientApi {
    val retrofitService: FCMClientApiService by lazy {
        retrofit.create(FCMClientApiService::class.java)
    }
}
