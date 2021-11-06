package com.tmvlg.factorcapgame.ui.singlegame

import android.util.Log
import androidx.lifecycle.*
import com.tmvlg.factorcapgame.data.repository.fact.Fact
import com.tmvlg.factorcapgame.data.repository.fact.FactRepository
import com.tmvlg.factorcapgame.data.repository.game.Game
import com.tmvlg.factorcapgame.data.repository.game.GameRepositoryImpl
import com.tmvlg.factorcapgame.data.repository.user.UserRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SingleGameViewModel(
    private val gameRepository: GameRepositoryImpl,
    private val factRepository: FactRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _gameFinished = MutableLiveData(false)
    val gameFinished: LiveData<Boolean> = _gameFinished.map { it }

//    NOT WORKS
//    private val _isHighScore = MutableLiveData(false)
//    val isHighScore: LiveData<Boolean> = _isHighScore.map { it }

//    WORKS
    private val _isHighScore = MutableLiveData(false)
    val isHighScore: LiveData<Boolean>
        get() = _isHighScore

    private val _rightAnswersCount = MutableLiveData(0)
    val rightAnswersCount: LiveData<Int> = _rightAnswersCount.map { it }

    private val _fact = MutableLiveData<Fact>()
    val fact = _fact.map { it }

    private var timeElapsed: Long = 0

    fun sendAnswer(answer: Boolean) {
        if (fact.value?.isTrue == answer) {
            getFact()
            _rightAnswersCount.postValue(_rightAnswersCount.value?.plus(1))
        } else {
            _gameFinished.postValue(true)
        }
    }

    fun saveStats() = viewModelScope.launch {
        val stats = userRepository.getStats()
        val score = _rightAnswersCount.value ?: 0
        stats.last_score = score
        stats.all_scores += score
        stats.total_games += 1
        stats.average_score = stats.all_scores / stats.total_games
        if (score > stats.highest_score) {
            stats.highest_score = score
            _isHighScore.value = true
        }
        userRepository.saveGame(stats)
    }

    fun saveGame() = viewModelScope.launch {
        val score = (_rightAnswersCount.value ?: 0).toLong()
        val game = Game(score, timeElapsed)
        gameRepository.insert(game)
        Log.d("1", "saveGame: allGames=${gameRepository.allGames}")
    }

    private fun getFact() = viewModelScope.launch {
        _fact.postValue(factRepository.getFact())
    }

    private fun startGame() {
        getFact()
        viewModelScope.launch {
            do {
                timeElapsed += 100
                delay(100)
            } while (!_gameFinished.value!!)
        }
    }


    init {
        startGame()
    }
}
