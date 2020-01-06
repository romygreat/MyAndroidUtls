package cn.gddiyi.api;

import com.google.gson.JsonObject;

import java.util.HashMap;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class ApiSn<T> extends Api {
    @Override
    Call createCall() {
        ApiService apiService = getDefaultApiService();
        Call<ResponseBody> call = apiService.getSnTestActivity(createRequestBody());
        return call;
    }

    @Override
    RequestBody createRequestBody() {
        //请求参数，参数较多的话可以使用builder模式设置
        HashMap hashMap=new HashMap(1);
        hashMap.put("sn","sn666666");
        JsonObject jsonObject= Api.newReuqest(hashMap);
        return createRequestBody(jsonObject);
    }

    @Override
    Object getResponse(String response, Object clazz) {

        return null;
    }































































}
