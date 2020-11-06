package com.capstone.plantplant.network;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitService {
    //물주기 목록
    public static String WATERCYCLE_LIST_URL ="http://api.nongsaro.go.kr/service/garden/waterCycleList";
    public static String WATERCYCLE_LIST_APIKEY;

    //실내정원용 식물 목록
    public static String GARDEN_LIST_URL ="http://api.nongsaro.go.kr/service/garden/waterCycleList";
    public static String GARDEN_LIST_APIKEY = "20201102QJ9WDI6LGLCMHSR5GQOG";

    //실내정원용 식물상세
    public static String GARDEN_DTL_URL ="http://api.nongsaro.go.kr/service/garden/gardenDtl";
    public static String GARDEN_DTL_APIKEY = "20201102QJ9WDI6LGLCMHSR5GQOG";

    public static Retrofit getAPIWaterCycleList() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        Retrofit retrofit = new Retrofit.Builder().
                baseUrl(WATERCYCLE_LIST_URL+WATERCYCLE_LIST_APIKEY).
                addConverterFactory(GsonConverterFactory.create())
                .client(client).build();

        return retrofit;
    }
    public static Retrofit getAPIGardenList() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        Retrofit retrofit = new Retrofit.Builder().
                baseUrl(GARDEN_LIST_URL+GARDEN_LIST_APIKEY).
                addConverterFactory(GsonConverterFactory.create())
                .client(client).build();

        return retrofit;
    }

}
