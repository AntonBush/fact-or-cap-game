package com.tmvlg.factorcapgame.data.repository.firebase

import android.util.Log
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.IOException
import java.lang.RuntimeException

class FirebaseLobbyRepository {

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
                                ?: throw RuntimeException("can't find playerId")
                            val player: Player = firebasePlayer.getValue<Player>()
                                ?: throw RuntimeException("can't find player")
                            Log.d(
                                "1",
                                "onDataChange: ${player.playerName} is finished ${player.waiting}"
                            )
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
            if (it.playerName == username) {
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

    @WorkerThread
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
            } else if (it.score == highScore.first) {
                if (it.timeElapsed < timeElapsed.first) {
                    Log.d("1", "calculateWinner: ${it.playerName} = ${it.score}")
                    highScore = it.score to it
                    timeElapsed = it.timeElapsed to it
                }
            }
        }
        return highScore.second
    }

    // ----------------------------- NEW PHILOSOPHY -------------------------

    // ----------------------------- LIST ROOMS --------------------------------

    private val _lobbies = MutableLiveData<List<Lobby>>(emptyList())
    val lobbies = _lobbies.map { it }

    private var lobbiesEventListener: ValueEventListener? = null

    @WorkerThread
    fun listenLobbies() {
        stopListenLobbies()
        val newLobbiesEventListener = object : ValueEventListener {
            override fun onDataChange(lobbies: DataSnapshot) {
                val newLobbies = mutableListOf<Lobby>()
                for (lobby in lobbies.children) {
                    val newLobbyPlayerList = mutableListOf<Player>()
                    val newLobbyHost = lobby.child("host").getValue<String>()
                        ?: throw IOException("can't find host")
                    val newLobbyRoomName = lobby.child("roomName").getValue<String>()
                        ?: throw IOException("can't find roomName")
                    for (firebasePlayer in lobby.child("players").children) {
                        val player: Player = firebasePlayer.getValue<Player>()
                            ?: throw IOException("can't find player")
                        player.id = firebasePlayer.key
                            ?: throw IOException("can't find playerId")
                        Log.d(
                            "1",
                            "ListenLobbiesOnDataChange: ${player.playerName} is finished ${player.waiting}"
                        )
                        newLobbyPlayerList.add(player)
                    }
                    newLobbies.add(
                        Lobby(
                            newLobbyPlayerList,
                            newLobbyHost,
                            newLobbyRoomName,
                            lobby.key!!
                        )
                    )
                }
                _lobbies.postValue(newLobbies)
            }

            override fun onCancelled(error: DatabaseError) {}
        }
        lobbiesRef.addValueEventListener(newLobbiesEventListener)
    }

    @WorkerThread
    fun stopListenLobbies() {
        if (lobbiesEventListener != null) {
            lobbiesRef.removeEventListener(valueEventListener)
            _lobbies.postValue(emptyList())
        }
    }

    // ------------------------------ JOIN ROOM -------------------------------

    @WorkerThread
    suspend fun addPlayerToLobby(
        username: String,
        lobbyId: String
    ) = withContext(Dispatchers.IO) {
        var isPlayerInRoom = false

        val lobbyPlayersRef = lobbiesRef.child(lobbyId).child("players")
        val lobbyPlayersTask = lobbyPlayersRef.get()

        while (!lobbyPlayersTask.isComplete) {
            delay(1)
        }

        val lobbyPlayers = lobbyPlayersTask.result.children
        lobbyPlayers.forEach { firebasePlayer ->
            val player = firebasePlayer.getValue<Player>()
                ?: throw IOException("can't find player")
            if (!isPlayerInRoom && player.playerName == username) {
                isPlayerInRoom = true
            }
        }

        if (!isPlayerInRoom) {
            val newPlayerKey = lobbyPlayersRef.push().key
                ?: throw IOException("can't add new player to lobby")

            val newPlayer = Player(
                playerName = username,
                id = newPlayerKey
            )

            lobbyPlayersRef.child(newPlayerKey).setValue(newPlayer)
        }
    }

    // -------------------------------- LOOK ROOM -----------------------------------

    private val _lobby = MutableLiveData<Lobby?>(null)
    val lobby = _lobby.map { it }

    private var lobbyId: String = ""
    private var lobbyEventListener: ValueEventListener? = null

    @WorkerThread
    fun listenLobby(lobbyId: String) {
        stopListenLobby()
        val newLobbiesEventListener = object : ValueEventListener {
            override fun onDataChange(lobby: DataSnapshot) {
                val newLobby = Lobby()
                newLobby.id = lobby.key!!
                newLobby.host = lobby.child("host").getValue<String>()!!
                newLobby.isStarted = lobby.child("isStarted").getValue<Boolean>()!!
                newLobby.roomName = lobby.child("roomName").getValue<String>()!!
                newLobby.players = lobby.child("players").children.map { firebasePlayer ->
                    val player: Player = firebasePlayer.getValue<Player>()
                        ?: throw IOException("can't find player")
                    player.id = firebasePlayer.key
                        ?: throw IOException("can't find playerId")
                    return@map player
                }
                _lobby.postValue(newLobby)
            }

            override fun onCancelled(error: DatabaseError) {}
        }
        lobbiesRef.child(lobbyId).addValueEventListener(newLobbiesEventListener)
        this.lobbyId = lobbyId
    }

    @WorkerThread
    fun stopListenLobby() {
        if (lobbyEventListener != null) {
            lobbiesRef.child(lobbyId).removeEventListener(valueEventListener)
            _lobby.postValue(null)
        }
    }
}