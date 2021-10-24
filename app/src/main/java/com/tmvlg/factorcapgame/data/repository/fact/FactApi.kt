package com.tmvlg.factorcapgame.data.repository.fact

import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory

open class FactApi<T : FactApiService>(
    baseUrl: String,
    apiService: Class<T>
) {
    val retrofitService: FactApiService by lazy {
        Retrofit.Builder()
            .addConverterFactory(JacksonConverterFactory.create())
            .baseUrl(baseUrl)
            .build()
            .create(apiService)
    }
}
