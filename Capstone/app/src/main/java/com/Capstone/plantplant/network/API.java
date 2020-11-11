package com.capstone.plantplant.network;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;


public interface API {
     @Headers({
             "Content-Type: application/json",
             "Accept: */*"
     })
     @GET("")
     Call<ResponseBody> LoadAPI();

}
