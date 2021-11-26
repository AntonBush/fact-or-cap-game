package com.tmvlg.factorcapgame.data.repository.firebase

import android.os.Parcelable
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.parcelize.Parcelize

@Parcelize
@IgnoreExtraProperties
data class Lobby(
    var players: List<Player> = emptyList(),
    var host: String = "",
    var roomName: String = "",
    var id: String = "0",
    var isStarted: Boolean = false
) : Parcelable