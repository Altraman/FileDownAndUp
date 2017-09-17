package com.example.hu.filedownandup;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.hu.filedownandup.download.DownloadListener;
import com.example.hu.filedownandup.download.DownloadService;
import com.example.hu.filedownandup.download.DownloadTask;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;

import io.reactivex.functions.Consumer;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button start;
    private Button pause;
    private Button cancel;
    private Button fore_start;
    private Button fore_pause;
    private Button fore_cancel;
    private ProgressBar progressBar;

    private String url = "http://bunnyhsu.oss-cn-shanghai.aliyuncs.com/figure_2.jpg";
    private DownloadService downloadService = new DownloadService();
    private DownloadService.DownloadBinder binder;
    private DownloadTask downloadTask;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            binder = (DownloadService.DownloadBinder) iBinder;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        start = (Button) findViewById(R.id.start);
        pause = (Button) findViewById(R.id.pause);
        cancel = (Button) findViewById(R.id.cancel);
        fore_start = (Button) findViewById(R.id.fore_start);
        fore_pause = (Button) findViewById(R.id.fore_pause);
        fore_cancel = (Button) findViewById(R.id.fore_cancel);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        start.setOnClickListener(this);
        pause.setOnClickListener(this);
        cancel.setOnClickListener(this);
        fore_start.setOnClickListener(this);
        fore_pause.setOnClickListener(this);
        fore_cancel.setOnClickListener(this);

        Intent intent = new Intent(this, DownloadService.class);
        startService(intent);
        bindService(intent, connection, BIND_AUTO_CREATE);

        new Thread(new Runnable() {
            @Override
            public void run() {
                int progress = downloadService.getFore_progress();
                progressBar.setProgress(progress);
            }
        }).start();
        RxPermissions rx = new RxPermissions(this);
        rx.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {

                        } else {
                            Toast.makeText(MainActivity.this, "拒绝权限将无法使用程序", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.start:
                binder.startDownload(url);
                break;
            case R.id.pause:
                binder.pauseDownload();
                break;
            case R.id.cancel:
                binder.cancelDownload();
                break;
            case R.id.fore_start:
                if (downloadTask == null) {
                    downloadTask = new DownloadTask(new DownloadListener() {
                        @Override
                        public void onProgress(int progress) {
                            progressBar.setProgress(progress);
                        }

                        @Override
                        public void onSuccess() {
                            Toast.makeText(MainActivity.this, "Download Success", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailed() {
                            Toast.makeText(MainActivity.this, "Download Failed", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onPause() {
                            Toast.makeText(MainActivity.this, "Download Pause", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancel() {
                            progressBar.setProgress(0);
                            Toast.makeText(MainActivity.this, "Download Cancel", Toast.LENGTH_SHORT).show();
                        }
                    });
                    downloadTask.execute(url);
                }
                break;
            case R.id.fore_pause:
                if (downloadTask != null) {
                    downloadTask.pauseDownload();
                }
                break;
            case R.id.fore_cancel:
                if (downloadTask != null) {
                    downloadTask.cancelDownload();
                } else {
                    if (url != null) {
                        String fileName = url.substring(url.lastIndexOf("/"));
                        String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
                        File file = new File(filePath + fileName);
                        if (file.exists()) {
                            file.delete();
                        }
                        Toast.makeText(MainActivity.this, "Canceled", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }
}
