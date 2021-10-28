package com.tmvlg.factorcapgame.ui.singlegame

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tmvlg.factorcapgame.data.repository.fact.FactRepository
import com.tmvlg.factorcapgame.data.repository.game.GameRepositoryImpl
import kotlinx.coroutines.launch

class SingleGameViewModel(
    private val gameRepository: GameRepositoryImpl,
    private var factRepository: FactRepository
) : ViewModel() {

    private val _gameFinished = MutableLiveData<Boolean>()
    val gameFinished: LiveData<Boolean>
        get() = _gameFinished


    private val _rightAnswersCount = MutableLiveData<Int>()
    val rightAnswersCount: LiveData<Int>
        get() = _rightAnswersCount

    var fact = factRepository.getFact()

    fun sendAnswer(answer: Boolean) {
        when (factRepository.isAnswerCorrect(answer)) {
            true -> {
                getFact()
                _rightAnswersCount.value = _rightAnswersCount.value?.plus(1)
            }
            false -> {
                endGame()
            }
        }
    }

    private fun endGame() {
        _gameFinished.value = true
    }

    private fun getFact() {
        viewModelScope.launch {
            factRepository.loadFact()
        }
    }

    init {
        _rightAnswersCount.value = 0
        getFact()
    }
}
