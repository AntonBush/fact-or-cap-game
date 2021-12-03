package com.tmvlg.factorcapgame.data.repository.firebase

import android.os.Parcelable
import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.PropertyName
import kotlinx.parcelize.Parcelize
import java.lang.Exception

@Parcelize
data class Lobby(
    var id: String = "0",
    var host: String = "",
    var started: Boolean = false,
    var players: List<Player> = emptyList(),
    var roomName: String = "",
    var lastTimeHostPing: Long = System.currentTimeMillis()
) : Parcelable {
    fun toMapped(): Mapped {
        return Mapped(
            host,
            started,
            mapOf(
                * players.map { it.id to it }.toTypedArray()
            ),
            roomName,
            lastTimeHostPing
        )
    }

    companion object {
        fun newInstance(key: String, mapped: Mapped): Lobby {
            return Lobby().apply {
                id = key
                host = mapped.host
                players = mapped.players.map { playerEntry ->
                    playerEntry.value.apply {
                        id = playerEntry.key
                    }
                }
                roomName = mapped.roomName
                lastTimeHostPing = mapped.lastTimeHostPing
            }
        }

        private fun IllegalFieldException(field: String): IllegalArgumentException {
            return IllegalArgumentException("map contain invalid field <$field>")
        }
    }

    @IgnoreExtraProperties
    data class Mapped(
        @PropertyName("host")
        var host: String = "",
        @PropertyName("started")
        var started: Boolean = false,
        @PropertyName("players")
        var players: Map<String, Player> = emptyMap(),
        @PropertyName("roomName")
        var roomName: String = "",
        @PropertyName("lastTimeHostPing")
        var lastTimeHostPing: Long = 0
    )
}
