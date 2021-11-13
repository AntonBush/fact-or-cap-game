package com.tmvlg.factorcapgame.data.repository.game

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow

class GameRepositoryImpl(
    private val gameDAO: GameDAO
) : GameRepository {
    val allGames: LiveData<List<Game>> = gameDAO.getAllGames()

    @WorkerThread
    suspend fun insert(game: Game) {
        gameDAO.insert(game)
        gameDAO.deleteUnnecessary()
    }
}
