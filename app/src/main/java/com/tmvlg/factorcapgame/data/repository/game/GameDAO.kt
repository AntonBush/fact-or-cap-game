package com.tmvlg.factorcapgame.data.repository.game

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDAO {
    @Query("SELECT * FROM game_table ORDER BY date ASC")
    fun getDatedGames(): Flow<List<Game>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(game: Game)

    @Query(
        "DELETE FROM game_table WHERE date NOT IN" +
            "(SELECT date from game_table ORDER BY date DESC LIMIT $gamesInTable);"
    )
    suspend fun deleteUnnecessary()

    companion object {
        const val gamesInTable = 10
    }
}
