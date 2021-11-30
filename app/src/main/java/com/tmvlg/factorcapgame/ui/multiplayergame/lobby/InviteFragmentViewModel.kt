package com.tmvlg.factorcapgame.ui.multiplayergame.lobby

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.tmvlg.factorcapgame.data.network.*
import com.tmvlg.factorcapgame.data.repository.user.UserRepository
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class InviteFragmentViewModel(
    private val userRepository: UserRepository
): ViewModel() {

    fun invite(playerName: String, lobbyId: String) {
        val db = Firebase.firestore


        db.collection("users").document(playerName)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val token = document.data?.get("token").toString()
                    Log.d("1", "invite: SUCESS! token = $token")

                    val sender = userRepository.getUsername()

                    val rootModel = RootModel(
                        token,
                        NotificationModel("Invite", "${sender} invites you to lobby! Tap to join"),
                        DataModel(lobbyId),
                        "high"
                    )

                    val apiService = ApiClient.getClient().create(ApiInterface::class.java)
                    val responseBodyCall: Call<ResponseBody> = apiService.sendNotification(rootModel)

                    responseBodyCall.enqueue(object : Callback<ResponseBody?> {
                        override fun onResponse(call: Call<ResponseBody?>?, response: Response<ResponseBody?>?) {
                            Log.d("1", "Successfully notification send by using retrofit.")
                        }

                        override fun onFailure(call: Call<ResponseBody?>?, t: Throwable?) {}
                    })
                }
            }


    }

}