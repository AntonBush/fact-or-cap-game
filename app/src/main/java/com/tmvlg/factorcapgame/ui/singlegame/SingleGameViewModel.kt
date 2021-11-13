package com.tmvlg.factorcapgame.ui.singlegame

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import android.util.Log
import com.tmvlg.factorcapgame.data.repository.fact.Fact
import com.tmvlg.factorcapgame.data.repository.fact.FactRepository
import com.tmvlg.factorcapgame.data.repository.game.Game
import com.tmvlg.factorcapgame.data.repository.game.GameRepositoryImpl
import com.tmvlg.factorcapgame.data.repository.user.UserRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.IOException

class SingleGameViewModel(
    private val gameRepository: GameRepositoryImpl,
    private val factRepository: FactRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private val _exception = MutableLiveData<IOException?>(null)
    val exception = _exception.map { it }

    private val _gameFinished = MutableLiveData(false)
    val gameFinished = _gameFinished.map { it }

    private val _isHighScore = MutableLiveData(false)
    val isHighScore = _isHighScore.map { it }

    private val _rightAnswersCount = MutableLiveData(0)
    val rightAnswersCount = _rightAnswersCount.map { it }

    private val _fact = MutableLiveData<Fact>()
    val fact = _fact.map { it }

    private var timeElapsed: Long = 0

    fun sendAnswer(answer: Boolean) = viewModelScope.launch {
        if (fact.value?.isTrue == answer) {
            _rightAnswersCount.postValue(rightAnswersCount.value?.plus(1))
            getFact()
        } else {
            saveStats().join()
            saveGame().join()
            _gameFinished.postValue(true)
        }
    }

    fun saveStats() = viewModelScope.launch {
        val stats = userRepository.getStats()
        val score = rightAnswersCount.value ?: 0
        stats.lastScore = score
        stats.allScores += score
        stats.totalGames += 1
        stats.averageScore = stats.allScores / stats.totalGames
        if (score > stats.highestScore) {
            stats.highestScore = score
            _isHighScore.postValue(true)
        }
        userRepository.saveGame(stats)
    }

    fun getFact() = viewModelScope.launch {
        try {
            _fact.postValue(factRepository.getFact())
            _exception.postValue(null)
        } catch (e: IOException) {
            _exception.postValue(e)
        }
    }
    fun saveGame() = viewModelScope.launch {
        val score = (_rightAnswersCount.value ?: 0).toLong()
        val game = Game(score, timeElapsed)
        gameRepository.insert(game)
        Log.d("1", "saveGame: allGames=${gameRepository.allGames}")
    }

    private fun startGame() {
        getFact()
        viewModelScope.launch {
            do {
                timeElapsed += DELAY_MILLIS
                delay(DELAY_MILLIS)
            } while (!_gameFinished.value!!)
        }
    }

    init {
        startGame()
    }

    companion object {
        private const val DELAY_MILLIS = 100L
    }
}
