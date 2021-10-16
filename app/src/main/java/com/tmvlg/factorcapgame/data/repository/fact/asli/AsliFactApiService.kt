package com.tmvlg.factorcapgame.data.repository.fact.asli

import com.tmvlg.factorcapgame.data.repository.fact.FactApiService
import retrofit2.http.GET

interface AsliFactApiService : FactApiService {
    @GET("/")
    override suspend fun getFact(): AsliFact
}