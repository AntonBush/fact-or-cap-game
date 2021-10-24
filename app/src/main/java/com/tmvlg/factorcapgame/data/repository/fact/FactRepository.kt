package com.tmvlg.factorcapgame.data.repository.fact

class FactRepository(
    private val factApiList: List<FactApi<*>>
) {
    suspend fun getFact(): Fact {
        return factApiList.random().retrofitService.getFact()
    }
}
