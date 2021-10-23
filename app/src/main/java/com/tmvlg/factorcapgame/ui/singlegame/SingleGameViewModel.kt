package com.tmvlg.factorcapgame.ui.singlegame

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tmvlg.factorcapgame.data.repository.game.GameRepositoryImpl

class SingleGameViewModel : ViewModel() {

    private val repository = GameRepositoryImpl()

    private val _gameFinished = MutableLiveData<Boolean>()
    val gameFinished: LiveData<Boolean>
        get() = _gameFinished

    private var rightAnswersCount = 0

    fun sendAnswer(answer: Boolean) {
        // TODO
    }

    private fun endGame() {
        _gameFinished.value = true
    }

    private fun getFact() {
        // TODO
    }
}
