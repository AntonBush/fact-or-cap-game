package com.tmvlg.factorcapgame.ui.multiplayergame.lobby

import android.util.Log
import com.tmvlg.factorcapgame.data.repository.firebase.FirebaseLobbyRepository
import com.tmvlg.factorcapgame.data.repository.firebase.Lobby
import com.tmvlg.factorcapgame.data.repository.firebase.Player
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicReference

class PingThread(
    private val firebaseLobbyRepository: FirebaseLobbyRepository,
    private val username: String
) : SoftInterruptThread() {
    var lobby = AtomicReference<Lobby?>(null)
    override fun run() = runBlocking {
        withContext(Dispatchers.IO) {
            while (!interrupted) {
                delay(SLEEP_TIME_MILLIS)

                val lobby = lobby.get() ?: continue

                val player = lobby.players.find { it.name == username } ?: continue

                firebaseLobbyRepository.updatePing(lobby.id, player.id)

                if (isTimeout(lobby.lastTimeHostPing)) {
                    firebaseLobbyRepository.removePlayer(lobby.id, player.id)
                }

                doHostStuff(lobby, player)
            }
        }
    }

    private fun doHostStuff(lobby: Lobby, player: Player) {
        if (lobby.hostName != player.name) {
            return
        }

        firebaseLobbyRepository.updateLobbyPing(lobby.id)

        val currentMillis = System.currentTimeMillis()
        lobby.players
            .filter { p ->
                isTimeout(
                    p.lastTimePing,
                    currentMillis
                ) && p.name != lobby.hostName
            }
            .forEach { p ->
                firebaseLobbyRepository.removePlayer(lobby.id, p.id)
            }
    }

    companion object {
        fun isTimeout(
            examined: Long,
            current: Long = System.currentTimeMillis()
        ): Boolean {
            return current - examined > PLAYER_TIMEOUT_MILLIS
        }

        const val SLEEP_TIME_MILLIS = 1_000L
        const val PLAYER_TIMEOUT_MILLIS = 10_000L
    }
}
