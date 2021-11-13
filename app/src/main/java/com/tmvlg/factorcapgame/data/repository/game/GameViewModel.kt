package com.tmvlg.factorcapgame.data.repository.game

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: GameRepositoryImpl

    val allGames: LiveData<List<Game>>

    init {
        val gamesDao = GameRoomDatabase.getDatabase(application, viewModelScope).gameDao()
        repository = GameRepositoryImpl(gamesDao)
        allGames = repository.allGames
    }

    fun insertGame(game: Game) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(game)
    }
}