package com.example.hu.filedownandup.download;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.example.hu.filedownandup.retrofit.ApiServer;
import com.example.hu.filedownandup.retrofit.RetrofitClient;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;

/**
 * Created by Hu on 2017/9/17.
 */

public class DownloadTask extends AsyncTask<String, Integer, Integer> {

    public static final int TYPE_SUCCESS = 0;
    public static final int TYPE_FAILED = 1;
    public static final int TYPE_PAUSE = 2;
    public static final int TYPE_CANCEL = 3;

    private int lastProgress;
    private DownloadListener listener;

    private boolean isCancel = false;
    private boolean isPause = false;

    public DownloadTask(DownloadListener listener) {
        this.listener = listener;
    }

    @Override
    protected Integer doInBackground(String... strings) {
        RandomAccessFile saveFile = null;
        File file = null;
        int status = -1;
        try {
            long downloadedLength = 0;
            String downloadUrl = strings[0];
            String fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/"));
            String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
            Log.d("tag", filePath);
            file = new File(filePath + fileName);
            if (file.exists()) {
                downloadedLength = file.length();
            }
            long contentLength = getContentLength(downloadUrl);
            if (contentLength == 0) {
                return TYPE_FAILED;
            } else if (contentLength == downloadedLength) {
                return TYPE_SUCCESS;
            }
            saveFile = new RandomAccessFile(file, "rw");
            saveFile.seek(downloadedLength);
            Map<String, String> header = new HashMap<>();
            header.put("RANGE", "bytes=" + downloadedLength + "-");
            status = downloadFile(downloadUrl, header, downloadedLength, contentLength, saveFile);
            if (isCancel && file != null) {
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return status;
    }

    private int downloadFile(String downloadUrl, Map<String, String> header, long downloadedLength, long contentLength, RandomAccessFile saveFile) {
        InputStream is = null;
        try {
            ResponseBody responseBody = RetrofitClient.getInstance()
                    .create(ApiServer.class)
                    .downloadFile(downloadUrl, header)
                    .execute().body();
            if (responseBody != null) {
                is = responseBody.byteStream();
                byte[] bytes = new byte[1024];
                int total = 0;
                int len;
                while ((len = is.read(bytes)) != -1) {
                    if (isCancel) {
                        return TYPE_CANCEL;
                    } else if (isPause) {
                        return TYPE_PAUSE;
                    }
                    total += len;
                    saveFile.write(bytes, 0, len);
                    int progress = (int) ((downloadedLength + total) * 100 / contentLength);
                    publishProgress(progress);
                }
                return TYPE_SUCCESS;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (saveFile != null) {
                    saveFile.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return TYPE_FAILED;
    }

    private long getContentLength(String downloadUrl) {
        Map<String, String> header = new HashMap<>();
        header.put("platform", "Android");
        long length = 0;
        try {
            length = RetrofitClient.getInstance()
                    .create(ApiServer.class)
                    .downloadFile(downloadUrl, header)
                    .execute().body().contentLength();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return length;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        int progress = values[0];
        if (progress > lastProgress) {
            lastProgress = progress;
            listener.onProgress(progress);
        }
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
            case TYPE_PAUSE:
                listener.onPause();
                break;
            case TYPE_CANCEL:
                listener.onCancel();
                break;
            default:
                break;
        }
    }

    public void pauseDownload() {
        isPause = true;
    }

    public void cancelDownload() {
        isCancel = true;
    }
}
