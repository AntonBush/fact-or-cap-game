package com.tmvlg.factorcapgame.data.repository.fact

import com.tmvlg.factorcapgame.data.repository.fact.asli.AsliFactApi
import com.tmvlg.factorcapgame.data.repository.fact.genfun.GenfunFactApi

class FactRepository(
    private val factApiList: List<FactApi<*>>
) {
    suspend fun getFact(): Fact {
        return factApiList.random().retrofitService.getFact()
    }

    companion object {
        fun newInstance(): FactRepository {
            return FactRepository(listOf(AsliFactApi, GenfunFactApi))
        }
    }
}
