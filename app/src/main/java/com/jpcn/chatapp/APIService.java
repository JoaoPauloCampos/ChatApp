package com.jpcn.chatapp;

import com.jpcn.chatapp.Notifications.MyResponse;
import com.jpcn.chatapp.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers({"Content-Type:application/json"})
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body, @Header("Authorization") String authorization);
}
