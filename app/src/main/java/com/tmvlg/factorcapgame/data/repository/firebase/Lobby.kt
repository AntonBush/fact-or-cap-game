package com.tmvlg.factorcapgame.data.repository.firebase

import android.os.Parcelable
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.parcelize.Parcelize
import java.lang.Exception

@Parcelize
data class Lobby(
    var id: String = "0",
    var host: String = "",
    var started: Boolean = false,
    var players: List<Player> = emptyList(),
    var roomName: String = ""
) : Parcelable {
    fun toMapped(): Mapped {
        return Mapped(
            host,
            started,
            mapOf(
                * players.map { it.id to it }.toTypedArray()
            ),
            roomName
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
        var players: Map<String, Player> = emptyMap(),
        var roomName: String = ""
    )
}
