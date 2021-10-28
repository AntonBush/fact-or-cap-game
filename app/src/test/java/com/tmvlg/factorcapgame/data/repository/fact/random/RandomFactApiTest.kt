package com.tmvlg.factorcapgame.data.repository.fact.random

import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

internal class RandomFactApiTest {
    @Test
    fun `Get fact from genfun api should be successful`() = runBlocking {
        val expectedTruthfulness = true
        for (i in 1..5) {
            val trueFact = RandomFactApi.retrofitService.getFact()
            Assert.assertEquals("i:$i; $trueFact", expectedTruthfulness, trueFact.isTrue)
            println(trueFact)
        }
    }
}
