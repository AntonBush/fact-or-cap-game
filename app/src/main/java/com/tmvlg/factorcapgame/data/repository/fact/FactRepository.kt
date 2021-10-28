package com.tmvlg.factorcapgame.data.repository.fact

import androidx.annotation.WorkerThread
import com.tmvlg.factorcapgame.data.repository.fact.genfun.GenfunFactApi
import com.tmvlg.factorcapgame.data.repository.fact.random.RandomFactApi

class FactRepository(
    private val factApiList: List<FactApi<*>>
) {
    @WorkerThread
    suspend fun getFact(): Fact {
        return factApiList.random().retrofitService.getFact()
    }

    companion object {
        fun newInstance(): FactRepository {
            return FactRepository(listOf(GenfunFactApi, RandomFactApi))
        }
    }
}
