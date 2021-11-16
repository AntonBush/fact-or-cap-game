package com.tmvlg.factorcapgame.data.repository.game

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData

class GameRepository(
    private val gameDAO: GameDAO
) {
    // loads game history from DB
    val allGames: LiveData<List<Game>> = gameDAO.getAllGames()

    // inserts game data to DB to store game history
    @WorkerThread
    suspend fun insert(game: Game) {
        gameDAO.insert(game)
        gameDAO.deleteUnnecessary()
    }
}
