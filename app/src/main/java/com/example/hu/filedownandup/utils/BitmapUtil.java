package com.example.hu.filedownandup.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Created by Hu on 2017/9/28.
 */

public class BitmapUtil {
    public static Bitmap getBitmapFromString() {
        String bitmapString = "";
        byte[] bytes = Base64.decode(bitmapString, Base64.DEFAULT);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        Bitmap bitmap = BitmapFactory.decodeStream(byteArrayInputStream);
        return bitmap;
    }

    public static String bitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }
}
