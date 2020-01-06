package cn.gddiyi.api;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface ApiService {
    @POST("device/Verify/checkDevice")
    @Headers({"Content-Type: application/json;charset=UTF-8","Behavior: api"})
    Call<ResponseBody> getSnTestActivity(@Body RequestBody rb);
}
