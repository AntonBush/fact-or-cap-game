package com.tmvlg.factorcapgame.data.repository.game

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDAO {
    /**
     * @return the flow of all games sorted by date in ascending order
     */
    @Query("SELECT * FROM game_table ORDER BY date ASC")
    fun getDatedGames(): Flow<List<Game>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(game: Game)

    /**
     * Deletes all except last [GAMES_IN_TABLE] games
     */
    @Query(
        "DELETE FROM game_table WHERE date NOT IN" +
            "(SELECT date from game_table ORDER BY date DESC LIMIT $GAMES_IN_TABLE);"
    )
    suspend fun deleteUnnecessary()

    companion object {
        /**
         * Count of games that table should contain
         */
        const val GAMES_IN_TABLE = 10
    }
}
