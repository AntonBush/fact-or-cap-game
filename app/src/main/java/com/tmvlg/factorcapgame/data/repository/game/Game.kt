package com.tmvlg.factorcapgame.data.repository.game

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

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
