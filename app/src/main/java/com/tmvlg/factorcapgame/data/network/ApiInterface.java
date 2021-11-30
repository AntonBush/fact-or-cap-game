package com.tmvlg.factorcapgame.data.network;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiInterface {

    String SERVER_KEY = "AAAAzFlAiP8:APA91bF5l3qkFwI9BjpUN03bqd4N6yyUEjdxkDCkOy3pDUeHQFYGS0J9qgxEVI5qPr9fZZVZ3nR82gtVmm5Qa4WDvJFF8EnKiOx8H0GsfLm8FKl7n2A186J3oSmam9H8trGtZI_8M5oO";

    @Headers({"Authorization: key=" + SERVER_KEY, "Content-Type:application/json"})
    @POST("fcm/send")
    Call<ResponseBody> sendNotification(@Body RootModel root);
}