package com.info121.vms.api;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.info121.vms.App;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;

import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by KZHTUN on 2/16/2017.
 */

public class RestClient {


    private static String AuthToken = "";
    private static RestClient vmsInstance = null;
    private static RestClient photoInstance = null;
    private static int callCount = 10;
    private APIService service;


    private RestClient(String url) {

        Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();


        Retrofit retrofit = new Retrofit.Builder()
             //   .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                //.addConverterFactory(GsonConverterFactory.create(gson))
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(url)
                .client(new OkHttpClient().newBuilder()
                        .addInterceptor(new Interceptor() {
                            @Override
                            public Response intercept(Chain chain) throws IOException {
                                Request original = chain.request();
                                HttpUrl originalHttpUrl = original.url();

                                HttpUrl newUrl = originalHttpUrl.newBuilder()
                                        .build();


                                Request newRequest = original.newBuilder()
                                        .url(newUrl)
                                        .method(original.method(), original.body())
                                        .build();

                                return chain.proceed(newRequest);
                            }
                        })
                        .connectTimeout(35, TimeUnit.SECONDS)
                        .readTimeout(35, TimeUnit.SECONDS)
                        .build()
                ).build();

        service = retrofit.create(APIService.class);

    }

    public APIService getApiService() {
        return service;
    }

    public static RestClient VMS() {
        if (vmsInstance == null) {
            vmsInstance = new RestClient(App.CONST_REST_API_URL);

        }
        return vmsInstance;
    }

    public static RestClient VMSUpload() {
        if (photoInstance == null) {
            photoInstance = new RestClient(App.CONST_PHOTO_UPLOAD_URL);

        }
        return photoInstance;
    }

}
