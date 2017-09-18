package com.example.hu.filedownandup.upload;

import android.os.AsyncTask;

import com.example.hu.filedownandup.retrofit.ApiServer;
import com.example.hu.filedownandup.retrofit.RetrofitClient;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by Hu on 2017/9/17.
 */

public class UploadTask extends AsyncTask<String, Integer, Integer> {
    public static final int TYPE_SUCCESS = 0;
    public static final int TYPE_FAILED = 1;
    public static final int TYPE_PAUSE = 2;
    public static final int TYPE_CANCEL = 3;

    private UploadListener listener;

    public UploadTask(UploadListener listener) {
        this.listener = listener;
    }

    @Override
    protected Integer doInBackground(String... strings) {
        String uploadUrl = strings[0];
        String filePath = strings[1];
        File file = new File(filePath);
        if (file.exists()) {
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
            UploadRequestBody uploadRequestBody = new UploadRequestBody(requestBody, listener);
            MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), uploadRequestBody);
            try {
                RetrofitClient.getInstance()
                        .create(ApiServer.class)
                        .uploadFile(uploadUrl, part)
                        .execute();
                return TYPE_SUCCESS;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return TYPE_FAILED;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Integer integer) {
        switch (integer) {
            case TYPE_SUCCESS:
                listener.onSuccess();
                break;
            case TYPE_FAILED:
                listener.onFailed();
                break;
            default:
                break;
        }
    }
}
