package com.tmvlg.factorcapgame.data.repository.game

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

class GameRepository(
    private val gameDAO: GameDAO
) {
    //loads game history from DB
    val allGames: Flow<List<Game>> = gameDAO.getDatedGames()

    //inserts game data to DB to store game history
    @WorkerThread
    suspend fun insert(game: Game) {
        gameDAO.insert(game)
        gameDAO.deleteUnnecessary()
    }
}
