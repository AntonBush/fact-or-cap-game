package com.tmvlg.factorcapgame.data.repository.fact

import androidx.annotation.WorkerThread
import com.tmvlg.factorcapgame.data.repository.fact.genfun.GenfunFactApi
import com.tmvlg.factorcapgame.data.repository.fact.random.RandomFactApi
import java.io.IOException

class FactRepository(
    private val factApiList: List<FactApi<*>>
) {

    /**
     * Tries to load fact from net [ATTEMPTS_COUNT] times
     * @throws IOException if fails to load fact from net [ATTEMPTS_COUNT] times
     * @return loaded fact
     */
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

    /**
     * Tries load random fact
     * @return loaded fact
     */
    @WorkerThread
    private suspend fun loadFact(): Fact {
        return factApiList.random()
            .retrofitService
            .getFact()
    }

    companion object {
        /**
         * Fabric method
         * @return new FactRepository
         */
        fun newInstance(): FactRepository {
            return FactRepository(listOf(GenfunFactApi, RandomFactApi)) // api list
        }

        /**
         * Count of attempts to load fact
         * @see getFact
         */
        private const val ATTEMPTS_COUNT = 3
    }
}
