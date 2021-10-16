package com.tmvlg.factorcapgame.data.repository.fact.asli

import com.tmvlg.factorcapgame.data.repository.fact.FactApiService

interface AsliFactApiService : FactApiService {
    override suspend fun getFact(): AsliFact
}