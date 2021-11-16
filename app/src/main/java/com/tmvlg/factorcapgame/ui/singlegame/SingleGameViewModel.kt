package com.tmvlg.factorcapgame.ui.singlegame
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.tmvlg.factorcapgame.data.repository.fact.Fact
import com.tmvlg.factorcapgame.data.repository.fact.FactRepository
import com.tmvlg.factorcapgame.data.repository.game.Game
import com.tmvlg.factorcapgame.data.repository.game.GameRepository
import com.tmvlg.factorcapgame.data.repository.user.UserRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.IOException

class SingleGameViewModel(
    private val gameRepository: GameRepository,
    private val factRepository: FactRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    // exception that throws when can't fetch a fact
    private val _exception = MutableLiveData<IOException?>(null)
    val exception = _exception.map { it }

    // is game already finished
    private val _gameFinished = MutableLiveData(false)
    val gameFinished = _gameFinished.map { it }

    // is your score higher than previous scores
    private val _isHighScore = MutableLiveData(false)
    val isHighScore = _isHighScore.map { it }

    // how much answers were correct
    private val _rightAnswersCount = MutableLiveData(0)
    val rightAnswersCount = _rightAnswersCount.map { it }

    // fetches fact object from repository
    private val _fact = MutableLiveData<Fact>()
    val fact = _fact.map { it }

    // how much game longs. Stores in statistics at game history
    private var timeElapsed: Long = 0

    // check is answer is correct. If true loads new fact. If false finishes a game and saves statistics
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

    // calls to save statistics in Statistics object. Shows in statistics fragment
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

    // fetches new fact from repository
    fun getFact() = viewModelScope.launch {
        try {
            _fact.postValue(factRepository.getFact())
            _exception.postValue(null)
        } catch (e: IOException) {
            _exception.postValue(e)
        }
    }

    // saves current game to DB. Shows in game history
    private fun saveGame() = viewModelScope.launch {
        val score = (_rightAnswersCount.value ?: 0).toLong()
        val game = Game(score, timeElapsed)
        gameRepository.insert(game)
        Log.d("1", "saveGame: allGames=${gameRepository.allGames}")
    }

    // fetches first fact and starts a timer to count game duration
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
