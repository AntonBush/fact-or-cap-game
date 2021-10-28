package com.tmvlg.factorcapgame.data.repository.fact

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.tmvlg.factorcapgame.data.repository.fact.asli.AsliFactApi
import com.tmvlg.factorcapgame.data.repository.fact.genfun.GenfunFactApi
import java.lang.RuntimeException

class FactRepository(
    private val factApiList: List<FactApi<*>>
) {

    private val factLD = MutableLiveData<Fact>()

    private var fact: Fact? = null

    fun getFact(): LiveData<Fact> {
        return factLD
    }

    fun isAnswerCorrect(answer: Boolean): Boolean {
        return answer == fact?.isTrue
    }

    @WorkerThread
    suspend fun loadFact() {
//        fact = factApiList.random().retrofitService.getFact() // RIP ASLI API
        fact = factApiList[1].retrofitService.getFact() //LOADS ONLY FAKE FACTS
        factLD.postValue(fact ?: throw RuntimeException("Cannot load fact"))
    }

    companion object {
        fun newInstance(): FactRepository {
            return FactRepository(listOf(AsliFactApi, GenfunFactApi))
        }
    }
}
