package com.tmvlg.factorcapgame.data.repository.fact.random

import com.tmvlg.factorcapgame.data.repository.fact.Fact

data class RandomFact(
    override val isTrue: Boolean,
    override val text: String
) : Fact
