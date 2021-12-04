package com.tmvlg.factorcapgame.data.repository.firebase

import android.os.Parcelable
import android.util.Log
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.parcelize.Parcelize

@Parcelize
data class Player(
    var id: String = "0",
    var loaded: Boolean = false,
    var name: String = "",
    var score: Int = 0,
    var timeElapsed: Long = 0,
    var waiting: Boolean = false,
    var isWinner: Boolean = false,
    var lastTimePing: Long = System.currentTimeMillis()
) : Parcelable {
    fun toMapped(): Mapped {
        return Mapped(
            loaded,
            name,
            score,
            timeElapsed,
            waiting,
            lastTimePing
        )
    }

    companion object {
        fun newInstance(key: String, mapped: Mapped): Player {
            return Player().apply {
                Log.d("Player.newInstance", "$key|$mapped")
                id = key
                loaded = mapped.loaded
                name = mapped.playerName
                score = mapped.score
                timeElapsed = mapped.timeElapsed
                waiting = mapped.waiting
                lastTimePing = mapped.lastTimePing
            }
        }
    }

    @IgnoreExtraProperties
    data class Mapped(
        var loaded: Boolean = false,
        var playerName: String = "",
        var score: Int = 0,
        var timeElapsed: Long = 0,
        var waiting: Boolean = false,
        var lastTimePing: Long = 0
    )
}
