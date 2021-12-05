package com.tmvlg.factorcapgame.ui.multiplayergame.lobby

import androidx.lifecycle.LiveData
import com.tmvlg.factorcapgame.data.repository.firebase.FirebaseLobbyRepository
import com.tmvlg.factorcapgame.data.repository.firebase.Lobby
import com.tmvlg.factorcapgame.data.repository.firebase.Player

class PingThread(
    private val firebaseLobbyRepository: FirebaseLobbyRepository,
    private val username: String,
    private val lobbyLiveData: LiveData<Lobby?>
) : SoftInterruptThread() {
    override fun run() {
        while (!interrupted) {
            val lobby = lobbyLiveData.value
            val player = lobby?.players?.find { it.name == username }

            if (lobby == null || player == null) {
                continue
            }

            firebaseLobbyRepository.updatePing(lobby.id, player.id)

            if (isTimeout(lobby.lastTimeHostPing)) {
                firebaseLobbyRepository.removePlayer(lobby.id, player.id)
            }

            doHostStuff(lobby, player)

            sleep(SLEEP_TIME_MILLIS)
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
