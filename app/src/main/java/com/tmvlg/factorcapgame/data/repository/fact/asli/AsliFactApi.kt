package com.tmvlg.factorcapgame.data.repository.fact.asli

import com.tmvlg.factorcapgame.data.repository.fact.FactApi

object AsliFactApi : FactApi<AsliFactApiService>(
    "https://asli-fun-fact-api.herokuapp.com",
    AsliFactApiService::class.java
)
