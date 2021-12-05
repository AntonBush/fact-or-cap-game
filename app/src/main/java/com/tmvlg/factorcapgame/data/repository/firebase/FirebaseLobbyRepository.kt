package com.tmvlg.factorcapgame.data.repository.firebase

import android.util.Log
import androidx.annotation.WorkerThread
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
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.concurrent.atomic.AtomicReference

class FirebaseLobbyRepository {
    private val database = Firebase.database
    private val lobbiesRef = database.getReference("Lobbies")

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
                        Map<String, Lobby.Mapped>
                        >()
                    ?: throw IOException("lobby does not contain value")
                _lobbyList.postValue(
                    mappedLobbyList.map { lobbyEntry ->
                        Lobby.newInstance(lobbyEntry.key, lobbyEntry.value)
                    }
                )
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("FirebaseLobbyRepository", "${error.message}|${error.details}")
            }
        }
        lobbiesRef.addValueEventListener(newLobbiesEventListener)
    }

    @WorkerThread
    fun stopListenLobbies() {
        val localLobbiesEventListener = lobbiesEventListener
        if (localLobbiesEventListener != null) {
            lobbiesRef.removeEventListener(localLobbiesEventListener)
            _lobbyList.postValue(emptyList())
        }
    }

    // ------------------------------ JOIN ROOM -------------------------------

    @WorkerThread
    suspend fun addPlayerToLobby(
        username: String,
        lobbyId: String
    ) = withContext(Dispatchers.IO) {
        val lobbyRef = lobbiesRef.child(lobbyId)
        val lobbyPlayersRef = lobbyRef.child("players")
        val task = lobbyRef.get().addOnSuccessListener { lobbyData ->
            val lobbyPlayers = lobbyData.child("players").getValue<Map<String, Player.Mapped>>()
                ?.map { Player.newInstance(it.key, it.value) }
                ?: emptyList()

            val isPlayerInRoom = lobbyPlayers.any { it.name == username }
            val isGameStarted = lobbyData.child("started").getValue<Boolean>() == true
            if (isPlayerInRoom || isGameStarted) {
                return@addOnSuccessListener
            }

            val newPlayer = Player(
                name = username
            )
            val newPlayerKey = lobbyPlayersRef.push().key
                ?: throw IOException("can't add new player to lobby")
            newPlayer.id = newPlayerKey
            lobbyPlayersRef.child(newPlayerKey).setValue(newPlayer.toMapped())
        }

        while (!task.isComplete || !task.isComplete) {
            delay(1)
        }
    }

// -------------------------------- LOOK ROOM -----------------------------------

    val lobby = AtomicReference<Lobby?>(null)

    private var lobbyRef: DatabaseReference? = null
    private var lobbyEventListener: ValueEventListener? = null

    @WorkerThread
    fun listenLobby(lobbyId: String) {
        stopListenLobby()
        val newLobbyEventListener = object : ValueEventListener {
            override fun onDataChange(firebaseLobby: DataSnapshot) {
                val firebaseLobbyId = firebaseLobby.key
                    ?: throw IOException("lobby does not contain key")
                val firebaseLobbyValue = firebaseLobby.getValue<Lobby.Mapped>()
                    ?: throw IOException("lobby does not contain value")

                Log.d(TAG, "${Thread.currentThread()}")
                Log.d(TAG, "Before send")
                Log.d(TAG, firebaseLobbyId)
                lobby.set(Lobby.newInstance(firebaseLobbyId, firebaseLobbyValue))
                Log.d(TAG, "After send")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("FirebaseLobbyRepository", "${error.message}|${error.details}")
            }
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
            lobby.set(null)
        }
    }

// --------------------------------- CREATE ---------------------------------------

    @WorkerThread
    suspend fun createLobby(
        username: String,
        roomName: String = "Room name"
    ): String = withContext(Dispatchers.IO) {
        val localRoomName = if (roomName.isEmpty()) {
            "Room name"
        } else {
            roomName
        }
        val newLobby = Lobby(
            hostName = username,
            name = localRoomName
        )
        val newLobbyKey = lobbiesRef.push().key ?: throw IOException("can't add new lobby")
        lobbiesRef.updateChildren(mapOf(newLobbyKey to newLobby.toMapped()))
        return@withContext newLobbyKey
    }

    @WorkerThread
    suspend fun startGame(lobbyId: String) = withContext(Dispatchers.IO) {
        lobbiesRef.child(lobbyId).child("started").setValue(true)
    }

    @WorkerThread
    fun updatePing(lobbyId: String, userId: String) {
        lobbiesRef.child(lobbyId)
            .child("players")
            .child(userId)
            .child("lastTimePing")
            .setValue(System.currentTimeMillis())
    }

    @WorkerThread
    fun updateLobbyPing(lobbyId: String) {
        lobbiesRef.child(lobbyId)
            .child("lastTimeHostPing")
            .setValue(System.currentTimeMillis())
    }

    @WorkerThread
    fun removePlayer(lobbyId: String, userId: String) {
        lobbiesRef.child(lobbyId)
            .child("players")
            .child(userId)
            .removeValue()
    }

    companion object {
        const val TAG = "FirebaseLobbyRepository"
        const val MAX_TIME_ELAPSED = 10_000_000L
    }
}
