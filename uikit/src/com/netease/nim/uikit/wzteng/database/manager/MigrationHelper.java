package com.netease.nim.uikit.wzteng.database.manager;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.internal.DaoConfig;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 类名：MigrationHelper
 * 类描述：用于数据库升级的工具类
 * 版本：V1.0
 */

public class MigrationHelper {
    /**
     * 调用升级方法
     *
     * @param db
     * @param daoClasses 一系列dao.class
     */
    public static void migrate(Database db, Class<? extends AbstractDao<?, ?>>... daoClasses) {
        //1 新建临时表
        generateTempTables(db, daoClasses);
        //2 创建新表
        createAllTables(db, false, daoClasses);
        //3 临时表数据写入新表，删除临时表
        restoreData(db, daoClasses);
    }


    /**
     * 生成临时表，存储旧的表数据
     *
     * @param db
     * @param daoClasses
     */
    private static void generateTempTables(Database db, Class<? extends AbstractDao<?, ?>>... daoClasses) {
        for (int i = 0; i < daoClasses.length; i++) {
            DaoConfig daoConfig = new DaoConfig(db, daoClasses[i]);
            String tableName = daoConfig.tablename;
            if (!checkTable(db, tableName))
                continue;
            String tempTableName = daoConfig.tablename.concat("_TEMP");
            StringBuilder insertTableStringBuilder = new StringBuilder();
            insertTableStringBuilder.append("alter table ")
                    .append(tableName)
                    .append(" rename to ")
                    .append(tempTableName)
                    .append(";");
            db.execSQL(insertTableStringBuilder.toString());
        }
    }

    /**
     * 检测table是否存在
     *
     * @param db
     * @param tableName
     */
    private static Boolean checkTable(Database db, String tableName) {
        StringBuilder query = new StringBuilder();
        query.append("SELECT count(*) FROM sqlite_master WHERE type='table' AND name='").append(tableName).append("'");
        Cursor c = db.rawQuery(query.toString(), null);
        if (c.moveToNext()) {
            int count = c.getInt(0);
            if (count > 0) {
                return true;
            }
            return false;
        }
        return false;
    }

    /**
     * 删除所有旧表
     *
     * @param db
     * @param ifExists
     * @param daoClasses
     */
    private static void dropAllTables(Database db, boolean ifExists, @NonNull Class<? extends AbstractDao<?, ?>>... daoClasses) {
        reflectMethod(db, "dropTable", ifExists, daoClasses);
    }

    /**
     * 创建新的表结构
     *
     * @param db
     * @param ifNotExists
     * @param daoClasses
     */
    private static void createAllTables(Database db, boolean ifNotExists, @NonNull Class<? extends AbstractDao<?, ?>>... daoClasses) {
        reflectMethod(db, "createTable", ifNotExists, daoClasses);
    }

    /**
     * 创建根删除都在NoteDao声明了，可以直接拿过来用
     * dao class already define the sql exec method, so just invoke it
     */
    private static void reflectMethod(Database db, String methodName, boolean isExists, @NonNull Class<? extends AbstractDao<?, ?>>... daoClasses) {
        if (daoClasses.length < 1) {
            return;
        }
        try {
            for (Class cls : daoClasses) {
                //根据方法名，找到声明的方法
                Method method = cls.getDeclaredMethod(methodName, Database.class, boolean.class);
                method.invoke(null, db, isExists);
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 临时表的数据写入新表
     *
     * @param db
     * @param daoClasses
     */
    private static void restoreData(Database db, Class<? extends AbstractDao<?, ?>>... daoClasses) {
        for (int i = 0; i < daoClasses.length; i++) {
            DaoConfig daoConfig = new DaoConfig(db, daoClasses[i]);
            String tableName = daoConfig.tablename;
            String tempTableName = daoConfig.tablename.concat("_TEMP");
            if (!checkTable(db, tempTableName))
                continue;
            // get all columns from tempTable, take careful to use the columns list
            List<String> columns = getColumns(db, tempTableName);
            //新表，临时表都包含的字段
            ArrayList<String> properties = new ArrayList<>(columns.size());
            for (int j = 0; j < daoConfig.properties.length; j++) {
                String columnName = daoConfig.properties[j].columnName;
                if (columns.contains(columnName)) {
                    properties.add(columnName);
                }
            }
            if (properties.size() > 0) {
                final String columnSQL = TextUtils.join(",", properties);

                StringBuilder insertTableStringBuilder = new StringBuilder();
                insertTableStringBuilder.append("INSERT INTO ").append(tableName).append(" (");
                insertTableStringBuilder.append(columnSQL);
                insertTableStringBuilder.append(") SELECT ");
                insertTableStringBuilder.append(columnSQL);
                insertTableStringBuilder.append(" FROM ").append(tempTableName).append(";");
                db.execSQL(insertTableStringBuilder.toString());
            }
            StringBuilder dropTableStringBuilder = new StringBuilder();
            dropTableStringBuilder.append("DROP TABLE ").append(tempTableName);
            db.execSQL(dropTableStringBuilder.toString());
        }
    }

    private static List<String> getColumns(Database db, String tableName) {
        List<String> columns = null;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM " + tableName + " limit 0", null);
            if (null != cursor && cursor.getColumnCount() > 0) {
                columns = Arrays.asList(cursor.getColumnNames());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
            if (null == columns)
                columns = new ArrayList<>();
        }
        return columns;
    }

}
