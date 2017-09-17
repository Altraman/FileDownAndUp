package com.example.hu.filedownandup.retrofit;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Created by Hu on 2017/9/17.
 */

public interface ApiServer {
    @Streaming
    @GET
    Call<ResponseBody> downloadFile(@Url String url, @HeaderMap Map<String, String> headers);

    @Multipart
    @POST
    Call<ResponseBody> uploadFile(@Url String url, @Part MultipartBody.Part file);
}
