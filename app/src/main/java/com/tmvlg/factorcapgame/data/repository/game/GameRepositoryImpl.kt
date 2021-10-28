package com.tmvlg.factorcapgame.data.repository.game

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

class GameRepositoryImpl(
    private val gameDAO: GameDAO
) : GameRepository {
    val allGames: Flow<List<Game>> = gameDAO.getDatedGames()

    @WorkerThread
    suspend fun insert(game: Game) {
        gameDAO.insert(game)
        gameDAO.deleteUnnecessary()
    }
}
