package com.tmvlg.factorcapgame.ui.leaderboard

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.tmvlg.factorcapgame.data.repository.user.UserRepository
import kotlinx.coroutines.launch

class LeaderboardViewModel(
    userRepository: UserRepository
) : ViewModel() {
    val db = Firebase.firestore
    var allScores: MutableList<PlayerScore> = arrayListOf()
    lateinit var adapter: PlayerListAdapter
    lateinit var username: String

    init {
        viewModelScope.launch {
            username = userRepository.getUsername()!!
        }
    }

//    val docRef = db.collection("leaderboard")

    fun loadDataFromDB() {
        allScores = arrayListOf()
        var i = 1
        var isInList = false
//        setDataChangeListener()

        db.collection("leaderboard")
            .orderBy("score", Query.Direction.DESCENDING)
            .limit(100)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val curusername = document.data.getValue("username").toString()
                    val curscore = document.data.getValue("score").toString()
                    allScores.add(PlayerScore(i.toString(), curusername, curscore))
                    Log.d(TAG, "Got element" + document.id)
                    i = i.inc()
                    if (username == curusername) isInList = true
                }

                if (!isInList) addUserToList()
                else adapter.updateList(allScores)
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }
    }

    private fun addUserToList(){
        db.collection("leaderboard")
            .whereEqualTo("username", username)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val curusername = document.data.getValue("username").toString()
                    val curscore = document.data.getValue("score").toString()
                    allScores.add(PlayerScore( "", "...", ""))
                    allScores.add(PlayerScore(">100", curusername, curscore))
                }
                adapter.updateList(allScores)
            }
    }

    //    private fun setDataChangeListener() {
//        docRef.addSnapshotListener { snapshot, e ->
//            if (e != null) {
//                Log.w(TAG, "Listen failed.", e)
//                return@addSnapshotListener
//            }
//
//            if (snapshot != null) {
//                loadDataFromDB()
//            } else {
//                Log.d(TAG, "Current data: null")
//            }
//        }
//    }

    companion object {
        const val TAG = "FireStoreActivity"
    }
}
