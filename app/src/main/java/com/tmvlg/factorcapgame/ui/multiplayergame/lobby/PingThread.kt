package com.tmvlg.factorcapgame.ui.multiplayergame.lobby

import android.util.Log
import com.tmvlg.factorcapgame.data.repository.firebase.FirebaseLobbyRepository
import com.tmvlg.factorcapgame.data.repository.firebase.Lobby
import com.tmvlg.factorcapgame.data.repository.firebase.Player
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicReference

class PingThread(
    private val firebaseLobbyRepository: FirebaseLobbyRepository,
    private val username: String
) {
    var interrupted = false
    var isStarted = AtomicReference(false)
    var lobby = AtomicReference<Lobby?>(null)

    private suspend fun run() {
        while (!interrupted) {
            val lobby = lobby.get()
            val player = lobby?.players?.find { it.name == username }
            if (lobby != null && player != null) {
//                Log.d("PING_THREAD", "NON_NULL")
                doStuff(lobby, player)
//                Log.d("PING_THREAD", "IF")
                if (!isStarted.get()) {
                    isStarted.set(true)
                }
                doHostStuff(lobby, player)
            } else {
//                Log.d("PING_THREAD", "SHORT")
                delay(SHORT_SLEEP_TIME_MILLIS)
                continue
            }

            delay(SLEEP_TIME_MILLIS)
        }
        isStarted.set(false)
    }

    suspend fun start() {
        withContext(Dispatchers.IO) {
            run()
        }
    }

    private fun doStuff(lobby: Lobby, player: Player) {
        firebaseLobbyRepository.updatePing(lobby.id, player.id)

        if (isTimeout(lobby.lastTimeHostPing)) {
            firebaseLobbyRepository.removePlayer(lobby.id, player.id)
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

    fun interrupt() {
        interrupted = true
    }

    companion object {
        fun isTimeout(
            examined: Long,
            current: Long = System.currentTimeMillis()
        ): Boolean {
            return current - examined > PLAYER_TIMEOUT_MILLIS
        }

        const val SLEEP_TIME_MILLIS = 1_000L
        const val SHORT_SLEEP_TIME_MILLIS = 50L
        const val PLAYER_TIMEOUT_MILLIS = 10_000L
    }
}
