package com.tmvlg.factorcapgame.ui.multiplayergame

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.tmvlg.factorcapgame.data.repository.fact.Fact
import com.tmvlg.factorcapgame.data.repository.fact.FactRepository
import com.tmvlg.factorcapgame.data.repository.firebase.FirebaseLobbyRepository
import com.tmvlg.factorcapgame.data.repository.game.GameRepository
import com.tmvlg.factorcapgame.data.repository.user.UserRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.*
import java.util.ArrayDeque
import kotlin.NoSuchElementException

class MultiplayerGameViewModel(
    private val gameRepository: GameRepository,
    private val factRepository: FactRepository,
    private val userRepository: UserRepository,
    private val firebaseLobbyRepository: FirebaseLobbyRepository
) : ViewModel() {
    private val _exception = MutableLiveData<IOException?>(null)
    val exception = _exception.map { it }

    private val _gameFinished = MutableLiveData(false)
    val gameFinished = _gameFinished.map { it }

    private val _rightAnswersCount = MutableLiveData(0)
    val rightAnswersCount = _rightAnswersCount.map { it }

    private val _timeLeftFormatted = MutableLiveData<String>()
    val timeLeftFormatted = _timeLeftFormatted.map { it }

    private val _fact = MutableLiveData<Fact>()
    val fact = _fact.map { it }

    private val _isAnswerCorrect = MutableLiveData<Boolean>()
    val isAnswerCorrect = _isAnswerCorrect.map { it }

    private var _factsList = ArrayDeque<Fact>()

    private val _factsLoadingState = MutableLiveData<Boolean>(true)
    val factsLoadingState = _factsLoadingState.map { it }

    private var timeElapsed: Long = 0
    private var timeLeft: Long = GAME_DURATION_MS

    fun sendAnswer(answer: Boolean) = viewModelScope.launch {
        if (fact.value?.isTrue == answer) {
            _rightAnswersCount.postValue(rightAnswersCount.value?.plus(1))
            timeLeft += EXTRA_TIME_FOR_RIGHT_ANSWER
            _isAnswerCorrect.value = false
        } else {
            timeLeft -= LOST_TIME_FOR_WRONG_ANSWER
            _isAnswerCorrect.value = true
        }
//        _timeLeftFormatted.value = formatTime(timeLeft)
        getFact()
        loadFactsList()
    }

    fun getFact() = viewModelScope.launch {
        while (true) {
            Log.d("1", "loadFactsList: ${_factsList.size}")
            try {
                _fact.postValue(_factsList.pop())
                break
            } catch (e: NoSuchElementException) {
                waitForFactToBeLoaded().join()
            }
        }
    }

    private fun connectToLobby(lobbyId: String) = viewModelScope.launch {
        firebaseLobbyRepository.listenLobbyPlayers(lobbyId)
    }

    fun loadFactsList() = viewModelScope.launch {
        while (_factsList.size < FACTS_TO_BE_LOADED_COUNT) {
            try {
                _factsList.push(factRepository.getFact())
                _exception.postValue(null)
            }
            catch (e: IOException) {
                _exception.postValue(e)
            }
        }
    }

    fun waitForFactToBeLoaded() = viewModelScope.launch {
        delay(CHECK_TIME_IS_FACT_LOADED)
    }

    fun startGame(lobbyId: String) = viewModelScope.launch {
        connectToLobby(lobbyId)
        loadFactsList().join()
        setUserReady(lobbyId)
        waitForAllPlayersLoaded(lobbyId).join()
        startTimer()
        startCountingGameDuration()
    }

    fun setUserReady(lobbyId: String) {
        val username = userRepository.getUsername()
        firebaseLobbyRepository.setPlayerLoaded(lobbyId, username)
    }

    fun waitForAllPlayersLoaded(lobbyId: String) = viewModelScope.launch {
        while (!firebaseLobbyRepository.isAllPlayersLoaded(lobbyId)) {
            delay(CHECK_TIME_IS_PLAYERS_LOADED)
        }
        Log.d("1", "waitForAllPlayersLoaded: ALL PLAYERS LOADED")
        if (_factsLoadingState.value == true) {
            getFact()
            _factsLoadingState.postValue(false)
        }
    }

    private fun startCountingGameDuration() = viewModelScope.launch {
        do {
            timeElapsed += MS_TENTH_DELAY
            delay(MS_TENTH_DELAY.toLong())
        } while (!_gameFinished.value!!)
    }

    private fun startTimer() {
        viewModelScope.launch {
            do {
                timeLeft -= MS_DELAY
                _timeLeftFormatted.value = formatTime(timeLeft)
                delay(MS_DELAY.toLong())
            } while (timeLeft > 0)
            _gameFinished.postValue(true)
        }
    }

    private fun formatTime(timeLeftMs: Long): String {
        val minutes = timeLeftMs / MS_IN_MINUTE
        val seconds = (timeLeftMs / MS_IN_SECOND) - (minutes * SECONDS_IN_MINUTE)
        return String.format("%02d:%02d", minutes, seconds)
    }

    companion object {
        const val MS_TENTH_DELAY = 100
        const val MS_DELAY = 1000
        const val MS_IN_MINUTE = 60000
        const val MS_IN_SECOND = 1000
        const val SECONDS_IN_MINUTE = 60
        const val GAME_DURATION_MS = 120000L
        const val EXTRA_TIME_FOR_RIGHT_ANSWER = 3000L
        const val LOST_TIME_FOR_WRONG_ANSWER = 15000L
        const val FACTS_TO_BE_LOADED_COUNT = 20
        const val CHECK_TIME_IS_PLAYERS_LOADED = 2000L
        const val CHECK_TIME_IS_FACT_LOADED = 300L
    }
}
