package com.tmvlg.factorcapgame.data.repository.fact.genfun

import kotlinx.coroutines.runBlocking
import org.junit.Test

internal class GenfunFactApiTest {
    @Test
    fun `Get fact from genfun api should be successful`() = runBlocking {
        for (i in 1..5) {
            println(GenfunFactApi.retrofitService.getFact())
        }
    }
}
