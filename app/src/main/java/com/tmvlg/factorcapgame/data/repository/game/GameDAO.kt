package com.tmvlg.factorcapgame.data.repository.game

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface GameDAO {
    /**
     * @return the live data of all games sorted by date in descending order
     */
    @Query("SELECT * FROM game_table ORDER BY date DESC")
    fun getAllGames(): LiveData<List<Game>>
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(game: Game)

    /**
     * Deletes all except last [GAMES_IN_TABLE] games
     */
    @Query(
        "DELETE FROM game_table WHERE date NOT IN" +
            "(SELECT date from game_table ORDER BY date DESC LIMIT $gamesInTable);"
    )
    suspend fun deleteUnnecessary()

    companion object {
        /**
         * Count of games that table should contain
         */
        const val gamesInTable = 5
    }
}
