package com.tmvlg.factorcapgame.data.repository.firebase

import android.os.Parcelable
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.parcelize.Parcelize
import java.lang.Exception

@Parcelize
data class Player(
    var id: String = "0",
    var loaded: Boolean = false,
    val playerName: String = "",
    var score: Int = 0,
    var timeElapsed: Long = 0,
    var waiting: Boolean = false,
    var isWinner: Boolean = false
) : Parcelable {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "loaded" to loaded,
            "playerName" to playerName,
            "score" to score,
            "timeElapsed" to timeElapsed,
            "waiting" to waiting
        )
    }

    companion object {
        fun newInstance(key: String, map: Map<String, Any?>): Player {
            /*

                var id: String = "0",
    var loaded: Boolean = false,
    val playerName: String = "",
    var score: Int = 0,
    var timeElapsed: Long = 0,
    var waiting: Boolean = false,
    var isWinner: Boolean = false

             */
            return Player().apply {
                id = key
                loaded = map["loaded"] as Boolean?
                    ?: throw IllegalFieldException("loaded")
                started = map["started"] as Boolean?
                    ?: throw IllegalFieldException("loaded")
                players = try {
                    val players_map = map["players"] as Map<*, *>?
                        ?: throw IllegalArgumentException()
                    (players_map as Map<String, Map<String, Any?>>).map { playerEntry ->
                        newInstance(playerEntry.key, playerEntry.value)
                    }
                } catch (e: Exception) {
                    throw IllegalArgumentException("map does not contain field <players> or it is invalid")
                }
                roomName = map["roomName"] as String?
                    ?: throw IllegalArgumentException("map does not contain field <roomName>")
            }
        }

        private fun IllegalFieldException(field: String): IllegalArgumentException {
            return IllegalArgumentException("map does not contain field<$field>")
        }
    }
}