package com.tmvlg.factorcapgame.data.repository.firebase

data class Lobby(
    var players: List<Player> = emptyList(),
    var host: String = "",
    var roomName: String = ""
)