package com.tmvlg.factorcapgame.ui.singlegame

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tmvlg.factorcapgame.data.repository.fact.FactRepository
import com.tmvlg.factorcapgame.data.repository.game.GameRepositoryImpl
import com.tmvlg.factorcapgame.data.repository.user.UserRepository
import kotlinx.coroutines.launch

class SingleGameViewModel(
    private val gameRepository: GameRepositoryImpl,
    private val factRepository: FactRepository,
    private val userRepository: UserRepository
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
        saveStats()
    }

    private fun saveStats() {

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
