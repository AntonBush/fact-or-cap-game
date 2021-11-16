package com.tmvlg.factorcapgame.ui.statisitics

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.tmvlg.factorcapgame.data.repository.game.GameRepository
import com.tmvlg.factorcapgame.data.repository.user.UserRepository
import kotlinx.coroutines.launch

class StatisticsViewModel(userRepository: UserRepository, gameRepository: GameRepository) : ViewModel() {

    val allGames = gameRepository.allGames
    private val _totalGames = MutableLiveData<Int>()
    val totalGames = _totalGames.map { it }

    private val _highestScore = MutableLiveData<Int>()
    val highestScore = _highestScore.map { it }

    private val _lastScore = MutableLiveData<Int>()
    val lastScore = _lastScore.map { it }

    private val _averageScore = MutableLiveData<Int>()
    val averageScore = _averageScore.map { it }

    private val _allScores = MutableLiveData<Int>()
    val allScores = _allScores.map { it }

    init {
        viewModelScope.launch {
            val statistics = userRepository.getStats()
            _totalGames.postValue(statistics.totalGames)
            _highestScore.postValue(statistics.highestScore)
            _lastScore.postValue(statistics.lastScore)
            _averageScore.postValue(statistics.averageScore)
            _allScores.postValue(statistics.allScores)
        }
    }
}
