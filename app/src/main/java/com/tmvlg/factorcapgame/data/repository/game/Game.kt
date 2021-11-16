package com.tmvlg.factorcapgame.data.repository.game

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Date
import java.util.*

@Entity(tableName = "game_table")
data class Game(
    @ColumnInfo(name = "score")
    val score: Long,
    @ColumnInfo(name = "duration")
    val duration: Long,
    @PrimaryKey
    @ColumnInfo(name = "date")
    val date: Long = System.currentTimeMillis()
)
