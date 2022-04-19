package com.capstone.plantplant.network;

import com.capstone.plantplant.model.PlantToServer;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface API {
    //서버로부터 기기목록을 받기 위한 함수
    @Headers({
            "Content-Type: application/json",
            "Accept: */*"
    })
    @POST("comments")
    Call<ResponseBody> postUserDevice(@Body PlantToServer plantToServer);

    //서버로부터 식물정보 데이터베이스 값 받기위한
    @Headers({
            "Content-Type: application/json",
            "Accept: */*"
    })
    @GET("getalldata")
    Call<ResponseBody> getAllData();

    //서버에 사용자가 등록하고자하는 식물데이터를 전송
    @Headers({
            "Content-Type: application/json",
            "Accept: */*"
    })
    @GET("getdevice")
    Call<ResponseBody> getDeviceList();

    //서버에서 식물 정보를 불러옴
    @Headers({
            "Content-Type: application/json",
            "Accept: */*"
    })
    @GET("getdata")
    Call<ResponseBody> getPlantData(@Query("plant_name") String plant_name);

    //자동급수여부
    @Headers({
            "Content-Type: application/json",
            "Accept: */*"
    })
    @POST("controlwater ")
    Call<ResponseBody> onControlMoter(@Body PlantToServer plantToServer);

    //아이템 삭제
    @Headers({
            "Content-Type: application/json",
            "Accept: */*"
    })
    @POST("deleteplant")
    Call<ResponseBody> onDeleteData(@Body PlantToServer plant_name);


    @POST("watering")
    Call<ResponseBody> onDirectWater(@Body PlantToServer plant_name);

}
