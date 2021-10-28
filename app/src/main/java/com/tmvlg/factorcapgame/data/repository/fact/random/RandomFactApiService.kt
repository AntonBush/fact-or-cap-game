package com.tmvlg.factorcapgame.data.repository.fact.random

import com.tmvlg.factorcapgame.data.repository.fact.FactApiService
import retrofit2.http.GET

interface RandomFactApiService : FactApiService {
    @GET("/")
    override suspend fun getFact(): RandomFact
}
