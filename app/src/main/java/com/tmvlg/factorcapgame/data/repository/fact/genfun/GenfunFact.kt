package com.tmvlg.factorcapgame.data.repository.fact.genfun

import com.tmvlg.factorcapgame.data.repository.fact.Fact

data class GenfunFact(
    override val isTrue: Boolean,
    override val text: String
) : Fact
