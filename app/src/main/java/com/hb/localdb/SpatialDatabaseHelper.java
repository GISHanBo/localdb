package com.hb.localdb;

import android.os.Environment;
import android.util.Log;

import com.hb.entity.Heat;
import com.hb.entity.Marker;

import java.io.File;

import jsqlite.Constants;
import jsqlite.Database;
import jsqlite.Exception;
import jsqlite.Stmt;

public class SpatialDatabaseHelper {
    private static String TAG = "空间数据库";
    private static String path = Environment.getExternalStorageDirectory().getPath() + "/test.sqlite";
    private Database database;


    private static class ClassHolder {
        private static final SpatialDatabaseHelper INSTANCE = new SpatialDatabaseHelper();
    }

    private SpatialDatabaseHelper() {
        database = OpenDatabase(path);
        //检测数据表是否存在,存在则清空数据
        Log.e(TAG, "创建数据库");
        if (!isTableExit(database, "marker")) {
            createTableMarker(database);
            Log.e(TAG, "创建表");
        }else {
            clearTable(database,"marker");
            Log.e(TAG, "清除表");
        }
        if (!isTableExit(database, "heat")) {
            createTableHeat(database);
            Log.e(TAG, "创建表");
        }else {
            clearTable(database,"heat");
            Log.e(TAG, "清除表");
        }
    }

    /**
     * 添加一个点名为marker的表,并添加geom字段
     *
     * @param database 数据库
     */
    private void createTableMarker(Database database) {
        doSQL(database, "create table marker(id integer primary key, icon text,serial integer);");
        //添加geometry字段
        doSQL(database, "select AddGeometryColumn('marker', 'geom', 4326, 'POINT', 'XY');");
    }

    /**
     * 向marker表中插入数据
     * @param database 空间上数据库
     * @param marker 图标和坐标信息
     */
    public void addMarker(Database database,Marker marker){
        doSQL(database,"insert into marker (serial, icon, geom)values('"+marker.getSerial()+"', '"+marker.getIcon()+"', MakePoint("+marker.getLat()+" ,"+marker.getLng()+", 4326))");
    }
    /**
     * 添加一个点名为heat的表,并添加geom字段
     *
     * @param database 空间数据库
     */
    private void createTableHeat(Database database) {
        doSQL(database, "create table heat(id integer primary key, value float);");
        //添加geometry字段
        doSQL(database, "select AddGeometryColumn('heat', 'geom', 4326, 'POINT', 'XY');");
    }
    /**
     * 向heat表中插入热力数据
     * @param database 空间上数据库
     * @param heat 热力点信息
     */
    public void addHeat(Database database,Heat heat){
        doSQL(database,"insert into heat (value, geom)values('"+heat.getValue()+"', MakePoint("+heat.getLat()+" ,"+heat.getLng()+", 4326))");
    }

    public static final SpatialDatabaseHelper getInstance() {
        return ClassHolder.INSTANCE;
    }

    /**
     * 打开Spatialite数据库
     *
     * @param path 数据库路径和名称
     * @return Spatialite数据库
     */
    private Database OpenDatabase(String path) {
        Database database = new Database();
        File dbFile = new File(path);
        try {
            //不存在就创建数据库，以读写模式打开
            database.open(dbFile.toString(), Constants.SQLITE_OPEN_READWRITE | Constants.SQLITE_OPEN_CREATE);
            return database;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 判断数据库中是否已经存在某些表
     *
     * @param database  目标数据库
     * @param tableName 表名
     * @return 存在返回true, 不存在返回false
     */
    private boolean isTableExit(Database database, String tableName) {
        String query = "select count(*)  from sqlite_master where type='table' and name = '" + tableName + "';";
        try {
            Stmt stmt = database.prepare(query);
            stmt.step();
            int count = stmt.column_int(0);
            Log.e(TAG, String.valueOf(count));
            if (count != 0) {
                return true;
            }
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 无需返回值的SQL操作
     *
     * @param database 目标数据库
     * @param sql      sql语句
     */
    private void doSQL(Database database, String sql) {
        try {
            Stmt stmt=database.prepare(sql);
            stmt.step();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 清空表中的记录
     * @param database 空间数据库
     * @param tableName 表名
     */
    private void clearTable(Database database,String tableName){
        doSQL(database,"delete from "+tableName+";");
    }

    /**
     * 关闭数据库
     */
    public void close(){
        try {
            database.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
