package cn.gddiyi.api;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import cn.gddiyi.cash.constant.VSConstances;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;

import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public abstract class Api<T>{
   public static final String TAG="Api";

    static Gson instanceGson;
    /**
     *
     * @param
     * @return
     */
    abstract retrofit2.Call<ResponseBody> createCall();
    abstract Object createRequestBody();
    abstract T getResponse(String response,T clazz);
    static Retrofit getDefaultRetrfit(){
        Retrofit retrofit = Api.createRetrofit(VSConstances.getUrl());
        return retrofit;
    }


   static RequestBody createRequestBody(Object o){
        if (o instanceof String){
            return RequestBody.create(MEDIA_TYPE_APPLICATION,(String) o);
        }else {
            String requestString=toJonString(o);
            Log.d(TAG, "createRequestBody: "+requestString);
            return RequestBody.create(MEDIA_TYPE_APPLICATION,requestString);
        }
    }


    public static final MediaType MEDIA_TYPE_APPLICATION = MediaType.parse("application/json; charset=utf-8");
     static Retrofit  createRetrofit(String requestUrl){
         Retrofit retrofit2 = new Retrofit.Builder()
                 .baseUrl(requestUrl)
                 .addConverterFactory(GsonConverterFactory.create())
                 .client(new OkHttpClient())
                 .build();
         return retrofit2;}

    public static String toJonString(Object  o ) {
        String jsonString = getInstanceGson().toJson(o);
        return jsonString;
    }

  public   void enqueue(Callback<ResponseBody> callback) {
        createCall().enqueue(callback);
    }
public  static ApiService getDefaultApiService(){
    Retrofit retrofit = getDefaultRetrfit();
    ApiService apiService = retrofit.create(ApiService.class);
    return apiService;
}
    public static Gson getInstanceGson() {
        if (instanceGson == null) {
            instanceGson = new Gson();
        }
        return instanceGson;
    }

    public String newJsonOjectString(HashMap<String,String> hashMap){
        JsonObject jsonObject=new JsonObject();
        hashMap.forEach((key,value)->jsonObject.addProperty(key,value));
         return jsonObject.toString();
    };
    public static JsonObject newReuqest(HashMap<String,String> hashMap){
        JsonObject jsonObject=new JsonObject();
        hashMap.forEach((key,value)->jsonObject.addProperty(key,value));
        Log.d(TAG, "newReuqest: "+jsonObject.toString());
        return jsonObject;
    };
}
