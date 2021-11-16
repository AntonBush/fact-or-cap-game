package com.tmvlg.factorcapgame.data.repository.fact.random

import com.tmvlg.factorcapgame.data.repository.fact.FactApi

/**
 * Api for false facts
 */
object RandomFactApi : FactApi<RandomFactApiService>(
    "http://randomfactgenerator.net",
    RandomFactApiService::class.java
) {
    init {
        retrofitBuilder.addConverterFactory(ToRandomFactConverterFactory())
    }
}
