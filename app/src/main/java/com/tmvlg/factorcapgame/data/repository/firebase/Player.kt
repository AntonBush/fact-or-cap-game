package com.tmvlg.factorcapgame.data.repository.firebase

import android.os.Parcelable
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.parcelize.Parcelize

@Parcelize
@IgnoreExtraProperties
data class Player(
    val playerName: String = "",
    var score: Int = 0,
    var timeElapsed: Long = 0,
    var waiting: Boolean = false,
    var id: String = "0",
    var isWinner: Boolean = false,
    var loaded: Boolean = false
) : Parcelable {
    enum class Type {
        PLAYER, HOST
    }

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "loaded" to loaded,
            "playerName" to playerName,
            "score" to score,
            "timeElapsed" to timeElapsed,
            "waiting" to waiting
        )
    }
}