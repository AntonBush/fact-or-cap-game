package com.tmvlg.factorcapgame.data.repository.firebase

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Lobby(
    var players: List<Player> = emptyList(),
    var host: String = "",
    var roomName: String = ""
) : Parcelable