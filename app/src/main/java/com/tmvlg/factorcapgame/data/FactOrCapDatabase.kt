package com.tmvlg.factorcapgame.data

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
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
