package com.tmvlg.factorcapgame.data.repository.firebase

import android.util.Log
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.IOException
import java.lang.Exception
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
                            val playerBody = firebasePlayer.getValue<Map<String, Any?>>()
                                ?: throw RuntimeException("can't find playerBody")
                            val player = Player.newInstance(
                                playerId,
                                playerBody
                            )
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

    fun setPlayerLoaded(lobbyId: String, username: String) {
        Log.d("1", "setPlayerLoaded: $lobbyId, $username")
        players.forEach {
            if (it.playerName == username) {
                lobbiesRef.child(lobbyId)
                    .child("players")
                    .child(it.id)
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

    private val _lobbyList = MutableLiveData<List<Lobby>>(emptyList())
    val lobbyList = _lobbyList.map { it }

    private var lobbiesEventListener: ValueEventListener? = null

    @WorkerThread
    fun listenLobbies() {
        stopListenLobbies()
        val newLobbiesEventListener = object : ValueEventListener {
            override fun onDataChange(firebaseLobbyList: DataSnapshot) {
                // Player = String + Map<String, Any?>
                // PlayerList = Map<String, Map<String, Any?>>
                // Lobby = String + Map<String, Map<String, Any?>>
                // LobbyList = Map<String, Map<String, Map<String, Any?>>>
                val mappedLobbyList = firebaseLobbyList.getValue<
                    Map<String, Map<String, Any?>>
                    >()
                    ?: throw IOException("lobby does not contain value")
                _lobbyList.postValue(
                    mappedLobbyList.map { lobbyEntry ->
                        return@map try {
                            Lobby.newInstance(lobbyEntry.key, lobbyEntry.value)
                        } catch (e: Exception) {
                            Log.w("Firebase.listenLobbies", e.message.toString())
                            null
                        }
                    }.filterNotNull()
                )
            }

            override fun onCancelled(error: DatabaseError) {}
        }
        lobbiesRef.addValueEventListener(newLobbiesEventListener)
    }

    @WorkerThread
    fun stopListenLobbies() {
        if (lobbiesEventListener != null) {
            lobbiesRef.removeEventListener(valueEventListener)
            _lobbyList.postValue(emptyList())
        }
    }

    // ------------------------------ JOIN ROOM -------------------------------

    @WorkerThread
    suspend fun addPlayerToLobby(
        username: String,
        lobbyId: String
    ) = withContext(Dispatchers.IO) {
        val lobbyPlayersRef = lobbiesRef.child(lobbyId).child("players")
        val lobbyPlayersTask = lobbyPlayersRef.get().addOnSuccessListener {
            val lobbyPlayers = it.children

            val isPlayerInRoom = lobbyPlayers.any { firebasePlayer ->
                val playerId = firebasePlayer.key
                    ?: throw IOException("can't find playerId")
                val mappedPlayer = firebasePlayer.getValue<Map<String, Any?>>()
                    ?: throw IOException("can't find playerBody")
                val player = Player.newInstance(playerId, mappedPlayer)

                return@any player.playerName == username
            }

            if (!isPlayerInRoom) {
                val newPlayer = Player(
                    playerName = username
                )

                val newPlayerKey = lobbyPlayersRef.push().key
                    ?: throw IOException("can't add new player to lobby")

                newPlayer.id = newPlayerKey

                lobbyPlayersRef.child(newPlayerKey).setValue(newPlayer.toMutableMap())
            }
        }



    }

    // -------------------------------- LOOK ROOM -----------------------------------

    private val _lobby = MutableLiveData<Lobby?>(null)
    val lobby = _lobby.map { it }

    private var lobbyRef: DatabaseReference? = null
    private var lobbyEventListener: ValueEventListener? = null

    @WorkerThread
    fun listenLobby(lobbyId: String) {
        stopListenLobby()
        val newLobbyEventListener = object : ValueEventListener {
            override fun onDataChange(firebaseLobby: DataSnapshot) {
                val firebaseLobbyId = firebaseLobby.key
                    ?: throw IOException("lobby does not contain key")
                val firebaseLobbyValue = firebaseLobby.getValue<Map<String, Any?>>()
                    ?: throw IOException("lobby does not contain value")
                _lobby.postValue(
                    Lobby.newInstance(firebaseLobbyId, firebaseLobbyValue)
                )
            }

            override fun onCancelled(error: DatabaseError) {}
        }
        lobbyEventListener = newLobbyEventListener
        lobbyRef = lobbiesRef.child(lobbyId).apply {
            addValueEventListener(newLobbyEventListener)
        }
    }

    @WorkerThread
    fun stopListenLobby() {
        val localLobbyEventListener = lobbyEventListener
        if (localLobbyEventListener != null) {
            val lr = lobbyRef ?: throw IllegalStateException("lobbyRef is null")
            lr.removeEventListener(localLobbyEventListener)
            _lobby.postValue(null)
        }
    }

    // --------------------------------- CREATE ---------------------------------------

    @WorkerThread
    suspend fun createLobby(
        username: String,
        roomName: String = "Room name"
    ): String = withContext(Dispatchers.IO) {
        val _roomName = if (roomName.isEmpty()) {
            "Room name"
        } else {
            roomName
        }
        val newLobby = Lobby(
            host = username,
            roomName = _roomName
        )
        val newLobbyKey = lobbiesRef.push().key ?: throw IOException("can't add new lobby")
        lobbiesRef.updateChildren(mapOf(newLobbyKey to newLobby.toMutableMap()))
        addPlayerToLobby(username, newLobbyKey)
        return@withContext newLobbyKey
    }

    @WorkerThread
    suspend fun startGame(
        lobbyId: String
    ) = withContext(Dispatchers.IO) {
        lobbiesRef.child(lobbyId).child("started").setValue(true)
        val resetListener = object : ValueEventListener {
            override fun onDataChange(firebaseLobby: DataSnapshot) {
                val firebaseLobbyId = firebaseLobby.key
                    ?: throw IOException("lobby does not contain key")
                val firebaseLobbyValue = firebaseLobby.getValue<Map<String, Any?>>()
                    ?: throw IOException("lobby does not contain value")
                _lobby.postValue(
                    Lobby.newInstance(firebaseLobbyId, firebaseLobbyValue)
                )
            }

            override fun onCancelled(error: DatabaseError) {}
        }
    }

    companion object {
        const val TAG = "FirebaseLobbyRepository"
    }
}
