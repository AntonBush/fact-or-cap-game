package com.tmvlg.factorcapgame.ui.singlegame

import androidx.lifecycle.*
import com.tmvlg.factorcapgame.data.repository.fact.Fact
import com.tmvlg.factorcapgame.data.repository.fact.FactRepository
import com.tmvlg.factorcapgame.data.repository.game.GameRepositoryImpl
import com.tmvlg.factorcapgame.data.repository.user.UserRepository
import kotlinx.coroutines.launch

class SingleGameViewModel(
    private val gameRepository: GameRepositoryImpl,
    private val factRepository: FactRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _gameFinished = MutableLiveData(false)
    val gameFinished = _gameFinished.map { it }

    private val _isHighScore = MutableLiveData(false)
    val isHighScore = _isHighScore.map { it }

    private val _rightAnswersCount = MutableLiveData(0)
    val rightAnswersCount = _rightAnswersCount.map { it }

    private val _fact = MutableLiveData<Fact>()
    val fact = _fact.map { it }

    fun sendAnswer(answer: Boolean) = viewModelScope.launch {
        if (fact.value?.isTrue == answer) {
            _rightAnswersCount.postValue(rightAnswersCount.value?.plus(1))
            getFact()
        } else {
            saveStats().join()
            _gameFinished.postValue(true)
        }
    }

    fun saveStats() = viewModelScope.launch {
        val stats = userRepository.getStats()
        val score = rightAnswersCount.value ?: 0
        stats.last_score = score
        stats.all_scores += score
        stats.total_games += 1
        stats.average_score = stats.all_scores / stats.total_games
        if (score > stats.highest_score) {
            stats.highest_score = score
            _isHighScore.postValue(true)
        }
        userRepository.saveGame(stats)
    }

    private fun getFact() = viewModelScope.launch {
        _fact.postValue(factRepository.getFact())
    }

    init {
        getFact()
    }
}
