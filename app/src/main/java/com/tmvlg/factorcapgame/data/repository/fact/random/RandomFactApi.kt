package com.tmvlg.factorcapgame.data.repository.fact.random

import com.tmvlg.factorcapgame.data.repository.fact.FactApi

object RandomFactApi : FactApi<RandomFactApiService>(
    "http://randomfactgenerator.net",
    RandomFactApiService::class.java
) {
    init {
        retrofitBuilder.addConverterFactory(ToRandomFactConverterFactory())
    }
}
