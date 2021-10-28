package com.tmvlg.factorcapgame.ui.statisitics

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tmvlg.factorcapgame.data.preferences.PreferenceProvider
import com.tmvlg.factorcapgame.data.repository.user.Statistics
import com.tmvlg.factorcapgame.data.repository.user.UserRepository

class StatisticsViewModel(userRepository: UserRepository) : ViewModel() {

    private val statistics : Statistics = userRepository.getStats()
    private val _totalGames = MutableLiveData<Int>()
    val totalGames: LiveData<Int>
        get() = _totalGames
    private val _highestScore = MutableLiveData<Int>()
    val highestScore: LiveData<Int>
        get() = _highestScore
    private val _lastScore = MutableLiveData<Int>()
    val lastScore: LiveData<Int>
        get() = _lastScore
    private val _averageScore = MutableLiveData<Int>()
    val averageScore: LiveData<Int>
        get() = _averageScore
    private val _allScores = MutableLiveData<Int>()
    val allScores: LiveData<Int>
        get() = _allScores

    init {
        _totalGames.value = statistics.total_games
        _highestScore.value = statistics.highest_score
        _lastScore.value = statistics.last_score
        _averageScore.value = statistics.average_score
        _allScores.value = statistics.all_scores

    }
}
