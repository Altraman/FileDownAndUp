package com.example.hu.filedownandup;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.hu.filedownandup.upload.UploadListener;
import com.example.hu.filedownandup.upload.UploadTask;

import java.io.File;

public class UploadActivity extends AppCompatActivity {
    private Button button;
    private ProgressBar progressBar;
    private UploadTask uploadTask;

    private UploadListener listener = new UploadListener() {
        @Override
        public void onProgress(int progress, long contentLength) {
            progressBar.setProgress((int) (progress * 100 / contentLength));
        }

        @Override
        public void onSuccess() {
            Toast.makeText(UploadActivity.this, "上传成功了", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onFailed() {
            Toast.makeText(UploadActivity.this, "上传失败了", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPause() {

        }

        @Override
        public void onCancel() {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        button = (Button) findViewById(R.id.upload);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (uploadTask == null) {
                    String uploadUrl = "http://10.7.90.191:8011/AndroidFileTest/UploadHandleServlet";
                    String fileName = Environment.
                            getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).
                            getPath() + File.separator + "figure_2.jpg";
                    uploadTask = new UploadTask(listener);
                    uploadTask.execute(uploadUrl, fileName);

                }
            }
        });
    }
}
