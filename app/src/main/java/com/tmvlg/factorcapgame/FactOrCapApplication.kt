package com.tmvlg.factorcapgame

import com.tmvlg.factorcapgame.data.repository.fact.FactRepository

class FactOrCapApplication {
    val factRepository by lazy { FactRepository.newInstance() }
}
