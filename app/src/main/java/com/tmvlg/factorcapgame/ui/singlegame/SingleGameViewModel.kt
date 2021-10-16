package com.tmvlg.factorcapgame.ui.singlegame

import androidx.lifecycle.ViewModel
import com.tmvlg.factorcapgame.data.repository.game.GameRepositoryImpl

class SingleGameViewModel : ViewModel() {
    val repository = GameRepositoryImpl()
}
