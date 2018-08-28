package com.hb.localdb;

import android.content.Context;
import android.util.Log;

import com.hb.entity.Heat;
import com.hb.entity.Marker;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import jsqlite.Constants;
import jsqlite.Database;
import jsqlite.Exception;
import jsqlite.Stmt;

public class SpatialDatabaseHelper {
    private static String TAG = "空间数据库";
    private static String path ;
    private Database database;


    private static class ClassHolder {
        private static final SpatialDatabaseHelper INSTANCE = new SpatialDatabaseHelper();
    }

    private SpatialDatabaseHelper() {
//        String dirPath=context.getFilesDir().getPath()+"/"+ context.getPackageName()+ "/databases";
        Log.e(TAG,3+path);
        database = OpenDatabase(path);

        Log.e(TAG, "创建数据库");
        close();

    }


    /**
     * 向marker表中插入数据
     * @param marker 图标和坐标信息
     */
    public void addMarker(Marker marker){
        doSQL(database,"insert into marker (serial, icon, geom)values('"+marker.getSerial()+"', '"+marker.getIcon()+"', MakePoint("+marker.getLat()+" ,"+marker.getLng()+", 4326))");
    }

    /**
     * 向heat表中插入热力数据
     * @param database 空间上数据库
     * @param heat 热力点信息
     */
    public void addHeat(Database database,Heat heat){
        doSQL(database,"insert into heat (value, geom)values('"+heat.getValue()+"', MakePoint("+heat.getLat()+" ,"+heat.getLng()+", 4326))");
    }

    public  static final SpatialDatabaseHelper getInstance(Context context) {

        if(path==null){
            path=createDB(context);
        }
        Log.e(TAG,2+path);
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
        Log.e(TAG,"打开数据库失败");
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

    /**
     * 每次自动从Assets复制空间数据库
     * @param context
     * @return 生成的数据库路径
     */
    public static String createDB(Context context){
        String filePath = context.getFilesDir().getPath()+"/"+ context.getPackageName()+ "/databases/";
//        String filePath=Environment.getExternalStorageDirectory()+"/tempDB/";
        String dbName="db.sqlite";
        File dir = new File(filePath);
        // 如果目录不中存在，创建这个目录
        if (!dir.exists())dir.mkdirs();
        try {
            File target=new File(filePath+dbName);
            if(target.exists()){
                target.delete();
            }
            InputStream is = context.getResources().getAssets()
                    .open(dbName);
            FileOutputStream fos = new FileOutputStream(filePath+dbName);
            byte[] buffer = new byte[7168];
            int count;
            while ((count = is.read(buffer)) > 0) {
                fos.write(buffer, 0, count);
            }
            fos.close();
            is.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return filePath+dbName;
    }

}
