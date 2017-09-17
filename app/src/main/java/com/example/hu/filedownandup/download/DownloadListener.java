package com.example.hu.filedownandup.download;

/**
 * Created by Hu on 2017/9/17.
 */

public interface DownloadListener {
    void onProgress(int progress);

    void onSuccess();

    void onFailed();

    void onPause();

    void onCancel();
}
