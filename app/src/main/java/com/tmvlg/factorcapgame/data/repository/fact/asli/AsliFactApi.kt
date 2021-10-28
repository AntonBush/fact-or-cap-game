package com.tmvlg.factorcapgame.data.repository.fact.asli

import com.tmvlg.factorcapgame.data.repository.fact.FactApi
import retrofit2.converter.jackson.JacksonConverterFactory

object AsliFactApi : FactApi<AsliFactApiService>(
    "https://asli-fun-fact-api.herokuapp.com",
    AsliFactApiService::class.java
) {
    init {
        retrofitBuilder.addConverterFactory(JacksonConverterFactory.create())
    }
}
