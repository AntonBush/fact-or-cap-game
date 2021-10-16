package com.tmvlg.factorcapgame.data.repository.fact

import retrofit2.http.GET

interface FactApiService {
    @GET("/")
    suspend fun getFact(): Fact
}
