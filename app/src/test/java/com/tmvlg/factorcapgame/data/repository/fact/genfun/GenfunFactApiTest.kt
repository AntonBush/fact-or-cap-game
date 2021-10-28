package com.tmvlg.factorcapgame.data.repository.fact.genfun

import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

internal class GenfunFactApiTest {
    @Test
    fun `Get fact from genfun api should be successful`() = runBlocking {
        val expectedTruthfulness = false
        for (i in 1..5) {
            val fakeFact = GenfunFactApi.retrofitService.getFact()
            Assert.assertEquals("i:$i; $fakeFact", expectedTruthfulness, fakeFact.isTrue)
            println(fakeFact)
        }
    }
}
