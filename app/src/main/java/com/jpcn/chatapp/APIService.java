package com.jpcn.chatapp;

import com.jpcn.chatapp.Notifications.MyResponse;
import com.jpcn.chatapp.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAbvDZ-pk:APA91bFT2gq88iZCLeo8Yl-hxiShUYUIPvGgs6CPh_s6FlipCu-4CumvLkicdjxZPQO_oWdSy9I4SodKvNWA1N_jenoNeNi-Pr1FgrWo0ad5GrKSJY8mwom8NDr8H-UvT7Dmau04gUC9"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
