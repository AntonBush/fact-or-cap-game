package com.tmvlg.factorcapgame.data.repository.fact

import androidx.annotation.WorkerThread
import com.tmvlg.factorcapgame.data.repository.fact.genfun.GenfunFactApi
import com.tmvlg.factorcapgame.data.repository.fact.random.RandomFactApi
import java.io.IOException

class FactRepository(
    private val factApiList: List<FactApi<*>>
) {

    //returns new fact
    @WorkerThread
    suspend fun getFact(): Fact {
        var fact: Fact? = null
        var errors = 0
        val exception = IOException("Can't load fact")
        while (fact == null) {
            if (errors > ATTEMPTS_COUNT) {
                throw exception
            }

            try {
                fact = loadFact()
            } catch (e: IOException) {
                exception.addSuppressed(e)
                ++errors
            }
        }

        return fact
    }

    //fetches new fact from apis
    @WorkerThread
    private suspend fun loadFact(): Fact {
        return factApiList.random()
            .retrofitService
            .getFact()
    }

    companion object {

        //calls at application level to init repository
        fun newInstance(): FactRepository {
            return FactRepository(listOf(GenfunFactApi, RandomFactApi)) //api list
        }

        private const val ATTEMPTS_COUNT = 3
    }
}
