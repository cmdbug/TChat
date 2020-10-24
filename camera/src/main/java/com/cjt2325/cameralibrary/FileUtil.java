package com.cjt2325.cameralibrary;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 445263848@qq.com.
 */
public class FileUtil {
    private static final String TAG = "FileUtil";
    private static final File parentPath = Environment.getExternalStorageDirectory();
    private static String storagePath = "";
    private static final String DST_FOLDER_NAME = "PlayCamera";

    private static String initPath() {
        if (storagePath.equals("")) {
            storagePath = parentPath.getAbsolutePath() + "/" + DST_FOLDER_NAME + "/PlayPicture";
            File f = new File(storagePath);
            if (!f.exists()) {
                f.mkdirs();
            }
        }
        return storagePath;
    }

    //wzteng增加返回路径并刷新系统图库
    public static String saveBitmap(Bitmap b, Context context) {

        String path = initPath();
        long dataTake = System.currentTimeMillis();
        String jpegName = path + "/" + dataTake + ".jpg";
//        Log.i(TAG, "saveBitmap:jpegName = " + jpegName);
        try {
            FileOutputStream fout = new FileOutputStream(jpegName);
            BufferedOutputStream bos = new BufferedOutputStream(fout);
            b.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
//            Log.i(TAG, "saveBitmap success");
        } catch (IOException e) {
//            Log.i(TAG, "saveBitmap:fail");
            e.printStackTrace();
        }
        // 其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    path + "/", dataTake + ".jpg", null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 最后通知图库更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + jpegName)));

        return jpegName;
    }
//    public static void saveBitmap(Bitmap b){
//
//        String path = initPath();
//        long dataTake = System.currentTimeMillis();
//        String jpegName = path + "/" + dataTake +".jpg";
//        Log.i(TAG, "saveBitmap:jpegName = " + jpegName);
//        try {
//            FileOutputStream fout = new FileOutputStream(jpegName);
//            BufferedOutputStream bos = new BufferedOutputStream(fout);
//            b.compress(Bitmap.CompressFormat.JPEG, 100, bos);
//            bos.flush();
//            bos.close();
//            Log.i(TAG, "saveBitmap success");
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            Log.i(TAG, "saveBitmap:fail");
//            e.printStackTrace();
//        }
//    }

    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }
}
