package com.tmvlg.factorcapgame.data.repository.fact.asli

import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

internal class AsliFactApiTest {
    @Test
    fun `Get fact from asli api should be successful`() = runBlocking {
        val expectedTruthfulness = true;
        for (i in 1..5) {
            val trueFact = AsliFactApi.retrofitService.getFact()
            Assert.assertEquals("i:$i; $trueFact", expectedTruthfulness, trueFact.isTrue)
            println(trueFact)
        }
    }
}
