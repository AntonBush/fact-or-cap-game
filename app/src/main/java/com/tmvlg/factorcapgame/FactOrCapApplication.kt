package com.tmvlg.factorcapgame

import com.tmvlg.factorcapgame.data.repository.fact.FactRepository
import com.tmvlg.factorcapgame.data.repository.fact.asli.AsliFactApi

class FactOrCapApplication {
    val factRepository by lazy { FactRepository(listOf(AsliFactApi)) }
}