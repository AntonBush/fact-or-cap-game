package com.tmvlg.factorcapgame.data.repository.firebase

import android.os.Parcelable
import android.util.Log
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.parcelize.Parcelize
import java.lang.Exception

@Parcelize
data class Lobby(
    var id: String = "0",
    var host: String = "",
    var started: Boolean = false,
    var players: List<Player> = emptyList(),
    var roomName: String = "",
) : Parcelable {
    fun toMutableMap(): MutableMap<String, Any?> {
        return mutableMapOf(
            "host" to host,
            "started" to started,
            "score" to mutableMapOf(* players.map { it.id to it }.toTypedArray()),
            "roomName" to roomName
        )
    }

    companion object {
        fun newInstance(key: String, map: Map<String, Any?>): Lobby {
            return Lobby().apply {
                id = key
                host = map["host"] as String?
                    ?: throw IllegalFieldException("host")
                started = map["started"] as Boolean?
                    ?: throw IllegalFieldException("started")
                players = try {
                    val playersMap = map["players"] as Any?
                        ?: throw IllegalArgumentException()
                    Log.d("Lobby.newInstance", "${playersMap::class.simpleName}")
                    Log.d("Lobby.newInstance", "${(playersMap as HashMap<String, Any?>).size}")
                    Log.d("Lobby.newInstance", "$playersMap")
                    (playersMap as Map<String, Map<String, Any?>>).map { playerEntry ->
                        Player.newInstance(playerEntry.key, playerEntry.value)
                    }
                    emptyList()
                } catch (e: Exception) {
                    throw IllegalFieldException("players").apply {
                        addSuppressed(e)
                    }
                }
                roomName = map["roomName"] as String?
                    ?: throw IllegalFieldException("roomName")
            }
        }

        private fun IllegalFieldException(field: String): IllegalArgumentException {
            return IllegalArgumentException("map contain invalid field<$field>")
        }
    }
}