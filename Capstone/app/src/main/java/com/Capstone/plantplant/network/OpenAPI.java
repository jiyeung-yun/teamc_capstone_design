package com.capstone.plantplant.network;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface OpenAPI {
     @Headers({
             "Content-Type: application/json",
             "Accept: */*"
     })
     @POST("Auth/authenticate")
     Call<ResponseBody> getOpenAPIREST(@Body String string);

}
