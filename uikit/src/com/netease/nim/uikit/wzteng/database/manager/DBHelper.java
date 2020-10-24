package com.netease.nim.uikit.wzteng.database.manager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.netease.nim.uikit.wzteng.database.greendao.DaoMaster;

/**
 * Created by WZTENG on 2017/08/02.
 */

public class DBHelper extends DaoMaster.OpenHelper {
    public static final String DBNAME = "locationscr.db";

    public DBHelper(Context context) {
        super(context, DBNAME, null);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onUpgrade(db, oldVersion, newVersion);
//        MigrationHelper.migrate(db, xxx.class);//可以写多个Dao类

//        private SQLiteDatabase db;
//        private DaoMaster.DevOpenHelper mHelper;
//        mHelper = new DaoMaster.DevOpenHelper(this, DBNAME, null);
//        db = mHelper.getWritableDatabase();
    }
}

