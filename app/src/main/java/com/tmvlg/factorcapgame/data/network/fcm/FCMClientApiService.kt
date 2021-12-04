package com.tmvlg.factorcapgame.data.network.fcm

import com.tmvlg.factorcapgame.data.network.fcm.models.RootModel
import com.tmvlg.factorcapgame.internal.Keys
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

const val SERVER_KEY = Keys.FCM_SERVER_KEY

interface FCMClientApiService {

    @Headers("Authorization: key=" + SERVER_KEY, "Content-Type:application/json")
    @POST("fcm/send")
    fun sendNotification(@Body root: RootModel): Call<ResponseBody>
}
