package com.tmvlg.factorcapgame.data.repository.firebase

data class Player (
    val playerName: String = "",
    var score: Int = 0,
    var timeElapsed: Long = 0,
    var isFinished: Boolean = false,
    var userId:String = "0"
)