package com.tmvlg.factorcapgame.data.repository.firebase

import android.util.Log
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import java.lang.RuntimeException

class FirebaseLobbyRepository {

    private val database = Firebase.database
    private val lobbiesRef = database.getReference("Lobbies")

    private var players: List<Player> = emptyList()
    private val playersLD = MutableLiveData<List<Player>>()

    suspend fun listenLobbyPlayers(lobbyId: String) {
        lobbiesRef.child(lobbyId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lobby: Lobby =
                    snapshot.getValue<Lobby>() ?: throw RuntimeException("no such lobby")
                players = lobby.players
                updateList()

                Log.d("1", "lobby players: ${lobby.players}")
                Log.d("1", "non-lobby players: ${players}")
                Log.d("1", "ld-lobby players: ${playersLD.value}")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun updateList() {
        playersLD.value = players.toList()
        Log.d("1", "ld2-lobby players: ${playersLD.value}")

    }

//    fun getLobbyPlayers(lobbyId: String): LiveData<List<Player>> {
//        listenLobbyPlayers(lobbyId)
//        Log.d("1", "ld3-lobby players: ${playersLD}")
//        return playersLD
//    }

    fun subscribeOnPlayersLD(): LiveData<List<Player>> {
        return playersLD
    }

    fun updatePlayerScore(score: Int, lobbyId: String, username: String) {
        for (player in players) {
            if (player.playerName == username) {
                lobbiesRef.child(lobbyId)
                    .child("players")
                    .child(player.userId)
                    .child("score")
                    .setValue(score)
            }
        }
    }
}