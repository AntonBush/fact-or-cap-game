package com.tmvlg.factorcapgame.data.repository.fact.asli

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.tmvlg.factorcapgame.data.repository.fact.Fact

@JsonDeserialize(using = AsliFactJsonDeserializer::class)
data class AsliFact(
    override val isTrue: Boolean,
    override val text: String
) : Fact
