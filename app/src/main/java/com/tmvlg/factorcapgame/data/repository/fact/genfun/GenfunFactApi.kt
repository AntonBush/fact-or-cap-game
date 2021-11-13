package com.tmvlg.factorcapgame.data.repository.fact.genfun

import com.tmvlg.factorcapgame.data.repository.fact.FactApi

/**
 * Api for false facts
 */
object GenfunFactApi : FactApi<GenfunFactApiService>(
    "https://generatorfun.com",
    GenfunFactApiService::class.java
) {
    init {
        retrofitBuilder.addConverterFactory(ToGenfunFactConverterFactory())
    }
}
