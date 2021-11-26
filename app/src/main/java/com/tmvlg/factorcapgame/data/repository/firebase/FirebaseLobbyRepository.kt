package com.tmvlg.factorcapgame.data.repository.firebase

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import java.lang.RuntimeException

class FirebaseLobbyRepository {

    private val database = Firebase.database
    private val lobbiesRef = database.getReference("Lobbies")
    private lateinit var valueEventListener: ValueEventListener

    private var players = mutableListOf<Player>()
    private val playersLD = MutableLiveData<List<Player>>()

    private fun addPlayer(player: Player, playerId: String) {
        player.userId = playerId
        players.add(player)
    }

    private fun clearList() {
        players.clear()
        updateList()
    }

    suspend fun listenLobbyPlayers(lobbyId: String) {
        valueEventListener = object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                clearList()
                for (child in snapshot.children) {
                    if (child.key == "players") {
                        for (firebasePlayer in child.children) {
                            val playerId = firebasePlayer.key
                                ?: throw RuntimeException("can't find playerId")
                            val player: Player = firebasePlayer.getValue<Player>()
                                ?: throw RuntimeException("can't find player")
                            Log.d("1", "onDataChange: ${player.playerName} is finished ${player.waiting}")
                            addPlayer(player, playerId)
                        }
                    }
                }
                updateList()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        }
        lobbiesRef.child(lobbyId).addValueEventListener(valueEventListener)
    }

    fun stopListeningPlayers(lobbyId: String) {
        lobbiesRef.child(lobbyId).removeEventListener(valueEventListener)
        updateList()
    }
    private fun updateList() {
        playersLD.value = players.toList()
        Log.d("1", "updateList: ${playersLD.value}")
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
        players.forEach {
            if (it.playerName == username) {
                lobbiesRef.child(lobbyId)
                    .child("players")
                    .child(it.userId)
                    .child("score")
                    .setValue(score)
                lobbiesRef.child(lobbyId)
                    .child("players")
                    .child(it.userId)
                    .child("waiting")
                    .setValue(true)
            }
        }
    }

    fun setPlayerLoaded(lobbyId: String, username: String) {
        Log.d("1", "setPlayerLoaded: $lobbyId, $username")
        players.forEach {
            if (it.playerName == username) {
                lobbiesRef.child(lobbyId)
                    .child("players")
                    .child(it.userId)
                    .child("loaded")
                    .setValue(true)
            }
        }
    }

    fun isAllPlayersLoaded(lobbyId: String): Boolean {
        Log.d("1", "isAllPlayersLoaded: $players")
        players.forEach {
            if (!it.loaded) {
                return false
            }
        }
        return true
    }

    fun calculateWinner(): Player {
        var highScore = 0 to players[0]
        var timeElapsed = 10000000L to players[0]
        Log.d("1", "calculateWinner: players = $players")
        players.forEach {
            Log.d("1", "calculateWinner: start for ${it.playerName}")
            if (it.score > highScore.first) {
                Log.d("1", "calculateWinner: ${it.playerName} = ${it.score}")
                highScore = it.score to it
                timeElapsed = it.timeElapsed to it
            }
            else if (it.score == highScore.first) {
                if (it.timeElapsed < timeElapsed.first) {
                    Log.d("1", "calculateWinner: ${it.playerName} = ${it.score}")
                    highScore = it.score to it
                    timeElapsed = it.timeElapsed to it
                }
            }
        }
        return highScore.second
    }
}