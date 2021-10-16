package com.tmvlg.factorcapgame.data.repository.fact.asli

import kotlinx.coroutines.runBlocking
import org.junit.*

internal class AsliFactApiTest {
    @Test
    fun `Get fact from asli api should be successful`() = runBlocking {
        for (i in 1..5) {
            AsliFactApi.retrofitService.getFact()
        }
    }
}