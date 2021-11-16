package com.tmvlg.factorcapgame.data.repository.game

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow

class GameRepository(
    private val gameDAO: GameDAO
) {
    val allGames: LiveData<List<Game>> = gameDAO.getAllGames()

    @WorkerThread
    suspend fun insert(game: Game) {
        gameDAO.insert(game)
        gameDAO.deleteUnnecessary()
    }
}
