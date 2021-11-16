package com.tmvlg.factorcapgame.data.repository.game

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = arrayOf(Game::class), version = 1, exportSchema = false)
abstract class GameRoomDatabase : RoomDatabase() {
    abstract fun gameDao(): GameDAO
    private class GameDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            INSTANCE?.let { database ->
                scope.launch {
                    var gameDao = database.gameDao()
                    gameDao.deleteUnnecessary()
                    // Add sample game.
//                    var game = Game(10, 3, 1636652224779)
//                    gameDao.insert(game)
//                    game = Game(3, 1, 1636652245730)
//                    gameDao.insert(game)
                }
            }
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: GameRoomDatabase? = null
        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): GameRoomDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GameRoomDatabase::class.java,
                    "game_database"
                )
                    .addCallback(GameDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}