package com.tmvlg.factorcapgame.ui.multiplayergame

import android.os.CountDownTimer
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.tmvlg.factorcapgame.data.repository.fact.Fact
import com.tmvlg.factorcapgame.data.repository.fact.FactRepository
import com.tmvlg.factorcapgame.data.repository.game.GameRepositoryImpl
import com.tmvlg.factorcapgame.data.repository.user.UserRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.IOException

class MultiplayerGameViewModel(
    private val gameRepository: GameRepositoryImpl,
    private val factRepository: FactRepository,
    private val userRepository: UserRepository
): ViewModel() {
    private val _exception = MutableLiveData<IOException?>(null)
    val exception = _exception.map { it }

    private val _gameFinished = MutableLiveData(false)
    val gameFinished = _gameFinished.map { it }

    private val _isHighScore = MutableLiveData(false)
    val isHighScore = _isHighScore.map { it }

    private val _rightAnswersCount = MutableLiveData(0)
    val rightAnswersCount = _rightAnswersCount.map { it }

    private val _timeLeftFormatted = MutableLiveData<String>()
    val timeLeftFormatted = _timeLeftFormatted.map { it }

    private val _fact = MutableLiveData<Fact>()
    val fact = _fact.map { it }

    private var timeElapsed: Long = 0
    private var timeLeft: Long = GAME_DURATION_MS

    fun sendAnswer(answer: Boolean)= viewModelScope.launch {
        if (fact.value?.isTrue == answer) {
            _rightAnswersCount.postValue(rightAnswersCount.value?.plus(1))
            timeLeft += EXTRA_TIME_FOR_RIGHT_ANSWER
        } else {
            timeLeft -= LOST_TIME_FOR_WRONG_ANSWER
        }
        getFact()
    }

    fun getFact() = viewModelScope.launch {
        try {
            _fact.postValue(factRepository.getFact())
            _exception.postValue(null)
        } catch (e: IOException) {
            _exception.postValue(e)
        }
    }

    private fun startGame() {
        startTimer()
        startCountingGameDuration()
        getFact()
    }

    private fun startCountingGameDuration() = viewModelScope.launch {
        do {
            timeElapsed += 100
            delay(100)
        } while (!_gameFinished.value!!)
    }

    private fun startTimer() {
        viewModelScope.launch {
            do {
                timeLeft -= 1000
                _timeLeftFormatted.value = formatTime(timeLeft)
                delay(1000)
            } while (timeLeft > 0)
            _gameFinished.postValue(true)
        }
    }

    private fun formatTime(timeLeftMs: Long): String {
        val minutes = timeLeftMs / 60000
        val seconds = (timeLeftMs / 1000) - (minutes * 60)
        return String.format("%02d:%02d", minutes, seconds)
    }

    init {
        startGame()
    }

    companion object {
        const val GAME_DURATION_MS = 120000L
        const val EXTRA_TIME_FOR_RIGHT_ANSWER = 2000L
        const val LOST_TIME_FOR_WRONG_ANSWER = 5000L
    }
}