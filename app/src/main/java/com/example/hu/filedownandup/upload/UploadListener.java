package com.example.hu.filedownandup.upload;

/**
 * Created by Hu on 2017/9/17.
 */

public interface UploadListener {
    void onProgress(int progress, long contentLength);

    void onSuccess();

    void onFailed();

    void onPause();

    void onCancel();
}
