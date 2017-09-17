package com.example.hu.filedownandup.download;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.example.hu.filedownandup.MainActivity;
import com.example.hu.filedownandup.R;

import java.io.File;

/**
 * Created by Hu on 2017/9/17.
 */

public class DownloadService extends Service {
    private DownloadBinder mBinder = new DownloadBinder();

    private DownloadTask downloadTask;
    private String downloadUrl;
    private int fore_progress = 0;

    public int getFore_progress() {
        return fore_progress;
    }

    public void setFore_progress(int fore_progress) {
        this.fore_progress = fore_progress;
    }

    private DownloadListener listener = new DownloadListener() {
        @Override
        public void onProgress(int progress) {
            getNotificationManager().notify(1, getNotification("Downloading...", progress));
        }

        @Override
        public void onSuccess() {
            downloadTask = null;
            stopForeground(true);
            getNotificationManager().notify(1, getNotification("Download Success", -1));
            Toast.makeText(DownloadService.this, "Download Success", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onFailed() {
            downloadTask = null;
            stopForeground(true);
            getNotificationManager().notify(1, getNotification("Download Failed", -1));
            Toast.makeText(DownloadService.this, "Download Failed", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPause() {
            downloadTask = null;
            Toast.makeText(DownloadService.this, "Download Pause", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel() {
            downloadTask = null;
            Toast.makeText(DownloadService.this, "Download Cancel", Toast.LENGTH_SHORT).show();
        }
    };

    private DownloadListener fore_listener = new DownloadListener() {
        @Override
        public void onProgress(int progress) {
            setFore_progress(progress);
        }

        @Override
        public void onSuccess() {
            downloadTask = null;
            stopForeground(true);
            Toast.makeText(DownloadService.this, "Download Success", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onFailed() {
            downloadTask = null;
            stopForeground(true);
            Toast.makeText(DownloadService.this, "Download Failed", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPause() {
            downloadTask = null;
            Toast.makeText(DownloadService.this, "Download Pause", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel() {
            downloadTask = null;
            Toast.makeText(DownloadService.this, "Download Cancel", Toast.LENGTH_SHORT).show();
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class DownloadBinder extends Binder {
        public void startDownload(String url) {
            if (downloadTask == null) {
                downloadUrl = url;
                downloadTask = new DownloadTask(listener);
                downloadTask.execute(downloadUrl);
                startForeground(1, getNotification("Downloading...", 0));
                Toast.makeText(DownloadService.this, "Downloading...", Toast.LENGTH_SHORT).show();
            }
        }

        public void pauseDownload() {
            if (downloadTask != null) {
                downloadTask.pauseDownload();
            }
        }

        public void cancelDownload() {
            if (downloadTask != null) {
                downloadTask.cancelDownload();
            } else {
                if (downloadUrl != null) {
                    String fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/"));
                    String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
                    File file = new File(filePath + fileName);
                    if (file.exists()) {
                        file.delete();
                    }
                    getNotificationManager().cancel(1);
                    stopForeground(true);
                    Toast.makeText(DownloadService.this, "Canceled", Toast.LENGTH_SHORT).show();
                }
            }
        }

        public void start_fore(String url) {
            if (downloadTask == null) {
                downloadUrl = url;
                downloadTask = new DownloadTask(fore_listener);
                downloadTask.execute(downloadUrl);
            }
        }

        public void pause_fore() {
            if (downloadTask != null) {
                downloadTask.pauseDownload();
            }
        }

        public void cancel_fore() {
            if (downloadTask != null) {
                downloadTask.cancelDownload();
            } else {
                String fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/"));
                String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
                File file = new File(filePath + fileName);
                if (file.exists()) {
                    file.delete();
                }
                setFore_progress(0);
                Toast.makeText(DownloadService.this, "Canceled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private NotificationManager getNotificationManager() {
        return (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    private Notification getNotification(String title, int progress) {
        Intent intent = new Intent(DownloadService.this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(DownloadService.this, 0, intent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher_round);
        builder.setContentIntent(pendingIntent);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round));
        builder.setContentTitle(title);
        if (progress > 0) {
            builder.setContentText(progress + "%");
            builder.setProgress(100, progress, false);
        }
        return builder.build();
    }
}
