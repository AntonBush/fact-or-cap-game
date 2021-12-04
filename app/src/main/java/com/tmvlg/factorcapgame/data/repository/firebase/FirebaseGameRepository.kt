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

class FirebaseGameRepository {
    private val database = Firebase.database
    private val lobbiesRef = database.getReference("Lobbies")
    private lateinit var valueEventListener: ValueEventListener

    private var players = mutableListOf<Player>()
    private val playersLD = MutableLiveData<List<Player>>()

    private fun addPlayer(player: Player, playerId: String) {
        player.id = playerId
        players.add(player)
    }

    private fun clearList() {
        players.clear()
        updateList()
    }

    private fun updateList() {
        playersLD.postValue(players.toList())
        Log.d("1", "updateList: ${playersLD.value}")
    }

    @WorkerThread
    fun listenLobbyPlayers(lobbyId: String) {
        valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                clearList()
                for (child in snapshot.children) {
                    if (child.key == "players") {
                        for (firebasePlayer in child.children) {
                            val playerId = firebasePlayer.key
                                ?: throw IllegalStateException("can't find playerId")
                            val playerBody = firebasePlayer.getValue<Player.Mapped>()
                                ?: throw IllegalStateException("can't find playerBody")
                            val player = Player.newInstance(
                                playerId,
                                playerBody
                            )
                            Log.d(
                                "1",
                                "onDataChange: ${player.name} is finished ${player.waiting}"
                            )
                            addPlayer(player, playerId)
                        }
                    }
                }
                updateList()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("FirebaseLobbyRepository", "${error.message}|${error.details}")
            }
        }
        lobbiesRef.child(lobbyId).addValueEventListener(valueEventListener)
    }

    @WorkerThread
    fun stopListeningPlayers(lobbyId: String) {
        lobbiesRef.child(lobbyId).removeEventListener(valueEventListener)
        updateList()
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
            if (it.name == username) {
                lobbiesRef.child(lobbyId)
                    .child("players")
                    .child(it.id)
                    .child("score")
                    .setValue(score)
                lobbiesRef.child(lobbyId)
                    .child("players")
                    .child(it.id)
                    .child("waiting")
                    .setValue(true)
            }
        }
    }

    fun setPlayerLoaded(lobbyId: String, username: String) {
        Log.d("1", "setPlayerLoaded: $lobbyId, $username")
        players.forEach {
            if (it.name == username) {
                lobbiesRef.child(lobbyId)
                    .child("players")
                    .child(it.id)
                    .child("loaded")
                    .setValue(true)
            }
        }
    }

    fun isAllPlayersLoaded(): Boolean {
        Log.d("1", "isAllPlayersLoaded: $players")
        players.forEach {
            if (!it.loaded) {
                return false
            }
        }
        return true
    }

    @WorkerThread
    fun calculateWinner(): Player {
        var highScore = 0 to players[0]
        var timeElapsed = FirebaseLobbyRepository.MAX_TIME_ELAPSED to players[0]
        Log.d("1", "calculateWinner: players = $players")
        players.forEach {
            Log.d("1", "calculateWinner: start for ${it.name}")
            if (it.score > highScore.first) {
                Log.d("1", "calculateWinner: ${it.name} = ${it.score}")
                highScore = it.score to it
                timeElapsed = it.timeElapsed to it
            } else if (it.score == highScore.first) {
                if (it.timeElapsed < timeElapsed.first) {
                    Log.d("1", "calculateWinner: ${it.name} = ${it.score}")
                    highScore = it.score to it
                    timeElapsed = it.timeElapsed to it
                }
            }
        }
        return highScore.second
    }
}
