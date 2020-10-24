package com.netease.nim.uikit.wzteng.database.manager;

import android.content.Context;

import com.netease.nim.uikit.wzteng.database.greendao.DaoMaster;
import com.netease.nim.uikit.wzteng.database.greendao.DaoSession;

/**
 * Created by WZTENG on 2017/08/02.
 */

public class GetDaoMaster {
    public static DaoMaster daoMaster;
    public static DaoSession daoSession;
    public static Context context;
    public static DBHelper devOpenHelper;
    public static DaoMaster.DevOpenHelper helper;

    public static DaoMaster getDaoMaster() {
        if (daoMaster == null) {
            //使用封装好的helper
            devOpenHelper = getDBHelper(context);
            daoMaster = new DaoMaster(devOpenHelper.getWritableDb());

            //使用原来的helper
          /*  helper = new DaoMaster.DevOpenHelper(context,"leno");
            daoMaster=new DaoMaster(helper.getWritableDatabase());*/
        }
        return daoMaster;
    }

    public static DaoSession getDaoSession(Context context) {
        GetDaoMaster.context = context;
        if (daoSession == null) {
            if (daoMaster == null) {
                daoMaster = getDaoMaster();
            }
            daoSession = daoMaster.newSession();
        }
        return daoSession;
    }

    public static DBHelper getDBHelper(Context context) {
        if (devOpenHelper == null) {
            devOpenHelper = new DBHelper(context);
        }
        return devOpenHelper;
    }

    public static DaoMaster.DevOpenHelper getDvHelper(Context context) {
        if (helper == null) {
            helper = new DaoMaster.DevOpenHelper(context, DBHelper.DBNAME);
        }
        return helper;
    }
}
