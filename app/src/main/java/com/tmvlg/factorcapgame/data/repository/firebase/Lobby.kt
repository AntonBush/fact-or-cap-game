package com.tmvlg.factorcapgame.data.repository.firebase

import android.os.Parcelable
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
                    ?: throw IllegalArgumentException("map does not contain field <host>")
                started = map["started"] as Boolean?
                    ?: throw IllegalArgumentException("map does not contain field <started>")
                players = try {
                    val players_map = map["players"] as Map<*, *>?
                        ?: throw IllegalArgumentException()
                    (players_map as Map<String, Map<String, Any?>>).map { playerEntry ->
                        Player.newInstance(playerEntry.key, playerEntry.value)
                    }
                } catch (e: Exception) {
                    throw IllegalArgumentException("map does not contain field <players> or it is invalid")
                }
                roomName = map["roomName"] as String?
                    ?: throw IllegalArgumentException("map does not contain field <roomName>")
            }
        }
    }
}