package com.tmvlg.factorcapgame.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.tmvlg.factorcapgame.data.repository.game.Game
import com.tmvlg.factorcapgame.data.repository.game.GameDAO
import kotlinx.coroutines.CoroutineScope

@Database(entities = arrayOf(Game::class), version = 1, exportSchema = false)
abstract class FactOrCapDatabase : RoomDatabase() {
    abstract fun gameDao(): GameDAO

    companion object {
        @Volatile
        private var INSTANCE: FactOrCapDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): FactOrCapDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FactOrCapDatabase::class.java,
                    "word_database"
                ).build()
                INSTANCE = instance
                return@synchronized instance
            }
        }
    }
}
