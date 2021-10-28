package com.tmvlg.factorcapgame.data.repository.fact

import retrofit2.Retrofit

open class FactApi<T : FactApiService>(
    baseUrl: String,
    apiService: Class<T>
) {
    val retrofitService: FactApiService by lazy {
        retrofitBuilder.build()
            .create(apiService)
    }
    protected open val retrofitBuilder: Retrofit.Builder by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
    }
}
