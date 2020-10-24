package com.netease.nim.uikit.wzteng.locationscreenshot;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by WZTENG on 2017/06/27 0027.
 */

public class AMapScreenShotUtils {

    private static final String TAG = AMapScreenShotUtils.class.getSimpleName();

    /**
     * 外部获取图片用
     */
    public static final String SOURCE_BITMAP_PNG = "sourceBitmap.png";
    /**
     * 内部保存图片用
     */
    public static final String SOURCE_BITMAP = "sourceBitmap";
    /**
     * 外部获取图片用
     */
    public static final String SMALL_BITMAP_PNG = "smallBitmap.png";
    /**
     * 内部保存图片用
     */
    public static final String SMALL_BITMAP = "smallBitmap";

    /**
     * dp转px
     *
     * @param context context
     * @param dpValue dp值
     * @return
     */
    public static int dp2px(Context context, float dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, context.getResources().getDisplayMetrics());
    }

    public static int px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * sp转px
     *
     * @param context context
     * @param spValue sp值
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue, context.getResources().getDisplayMetrics());
    }

    /**
     * 获取屏幕分辨率
     *
     * @param context context
     * @return
     */
    public static Point getScreenResolution(Context context) {
//        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//        Display display = wm.getDefaultDisplay();
//        Point screenResolution = new Point();
//        if (android.os.Build.VERSION.SDK_INT >= 13) {
//            display.getSize(screenResolution);
//        } else {
//            screenResolution.set(display.getWidth(), display.getHeight());
//        }
//        return screenResolution;

        //反射获取
        Point point = new Point();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        @SuppressWarnings("rawtypes")
        Class c;
        try {
            c = Class.forName("android.view.Display");
            @SuppressWarnings("unchecked")
            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
            method.invoke(display, displayMetrics);
            point.x = displayMetrics.widthPixels;
            point.y = displayMetrics.heightPixels;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return point;
    }

    //扩展名
    public enum Extension {
        png, jpeg
    }

    /**
     * 将图像保存到Data目录
     *
     * @param context                  context
     * @param bmpToSave                要保存的图片
     * @param fileNameWithoutExtension 文件名不包含扩展名
     * @param ext                      扩展名
     * @param quality                  保存的质量 1-100
     * @return
     */
    public static boolean saveBitmapToData(Context context, Bitmap bmpToSave, String fileNameWithoutExtension, Extension ext, int quality) {
        try {
            if (quality > 100) {
                quality = 100;
            } else if (quality < 1) {
                quality = 1;
            }
            FileOutputStream fos = context.openFileOutput(fileNameWithoutExtension + "." + ext.toString(), Context.MODE_PRIVATE);
            if (ext == Extension.png) {
                bmpToSave.compress(Bitmap.CompressFormat.PNG, quality, fos);
            } else if (ext == Extension.jpeg) {
                bmpToSave.compress(Bitmap.CompressFormat.JPEG, quality, fos);
            }
            //写入文件
            fos.flush();
            fos.close();
            return true;
        } catch (Exception e) {
            if (e.getMessage() != null) {
                Log.w(TAG, e.getMessage());
            } else {
                e.printStackTrace();
            }
            return false;
        }
    }

    /**
     * 从Data目录读取图像
     *
     * @param context  context
     * @param fileName 文件名包含扩展名
     * @return
     */
    public static Bitmap getBitmapFromData(Context context, String fileName) {
        FileInputStream fis = null;
        try {
            fis = context.openFileInput(fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedInputStream bis = new BufferedInputStream(fis);
        Bitmap bmpRet = BitmapFactory.decodeStream(bis);
        try {
            bis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpRet;
    }

}
