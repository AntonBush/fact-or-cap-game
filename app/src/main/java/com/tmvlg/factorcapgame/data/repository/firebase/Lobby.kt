package com.tmvlg.factorcapgame.data.repository.firebase

import android.os.Parcelable
import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.PropertyName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Lobby(
    var id: String = "0",
    var hostName: String = "",
    var started: Boolean = false,
    var players: List<Player> = emptyList(),
    var name: String = "",
    var lastTimeHostPing: Long = System.currentTimeMillis()
) : Parcelable {
    fun toMapped(): Mapped {
        return Mapped(
            hostName,
            started,
            mapOf(
                * players.map { it.id to it.toMapped() }.toTypedArray()
            ),
            name,
            lastTimeHostPing
        )
    }

    companion object {
        fun newInstance(key: String, mapped: Mapped): Lobby {
            return Lobby().apply {
                id = key
                hostName = mapped.host
                players = mapped.players.map { playerEntry ->
                    Player.newInstance(
                        playerEntry.key,
                        playerEntry.value
                    )
                }
                name = mapped.roomName
                lastTimeHostPing = mapped.lastTimeHostPing
            }
        }

        private fun IllegalFieldException(field: String): IllegalArgumentException {
            return IllegalArgumentException("map contain invalid field <$field>")
        }
    }

    @IgnoreExtraProperties
    data class Mapped(
        var host: String = "",
        var started: Boolean = false,
        var players: Map<String, Player.Mapped> = emptyMap(),
        var roomName: String = "",
        var lastTimeHostPing: Long = 0
    )
}
