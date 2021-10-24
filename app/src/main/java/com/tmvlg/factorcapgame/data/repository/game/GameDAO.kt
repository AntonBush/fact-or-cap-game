package com.tmvlg.factorcapgame.data.repository.game

import android.arch.persistence.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDAO {
    @Query("SELECT * FROM game_table ORDER BY date ASC")
    fun getDatedGames(): Flow<List<Game>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(game: Game)

    @Query("DELETE FROM game_table WHERE date NOT IN (SELECT date from game_table LIMIT 10);")
    suspend fun deleteUnnecessary()
}
