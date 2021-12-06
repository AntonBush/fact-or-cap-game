//package com.tmvlg.factorcapgame.data.repository.game
//
//import androidx.room.Room
//import androidx.test.core.app.ApplicationProvider
//import androidx.test.ext.junit.runners.AndroidJUnit4
//import com.tmvlg.factorcapgame.data.FactOrCapDatabase
//import kotlinx.coroutines.flow.first
//import kotlinx.coroutines.runBlocking
//import org.junit.After
//import org.junit.Assert.assertEquals
//import org.junit.Before
//import org.junit.Test
//import org.junit.runner.RunWith
//
//@RunWith(AndroidJUnit4::class)
//class GameDAOTest {
//    private lateinit var db: FactOrCapDatabase
//    private lateinit var gameDAO: GameDAO
//
//    @Before
//    fun createDb() {
//        db = Room.inMemoryDatabaseBuilder(
//            ApplicationProvider.getApplicationContext(),
//            FactOrCapDatabase::class.java
//        )
//            .build()
//        gameDAO = db.gameDao()
//    }
//
//    @Test
//    fun whenInsertGameThenReadTheSameOne(): Unit = runBlocking {
//        val expected = listOf(Game(0, 0))
//        gameDAO.insert(expected.first())
//        val actual = gameDAO.getDatedGames().first()
//        assertEquals(expected, actual)
//    }
//
//    @Test
//    fun whenDeleteUnnecessaryThenReadOthers(): Unit = runBlocking {
//        val games = List(20) { i ->
//            val l = i.toLong()
//            return@List Game(l, l, l)
//        }
//        val expected = games.takeLast(GameDAO.gamesInTable)
//        games.forEach { game -> gameDAO.insert(game) }
//        gameDAO.deleteUnnecessary()
//        val actual = gameDAO.getDatedGames().first()
//        assertEquals(expected, actual)
//    }
//
//    @After
//    fun closeDb() {
//        db.close()
//    }
//}
