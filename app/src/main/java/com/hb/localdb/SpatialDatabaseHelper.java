package com.hb.localdb;

import android.os.Environment;

import java.io.File;

import jsqlite.Constants;
import jsqlite.Database;
import jsqlite.Exception;

public class SpatialDatabaseHelper {
    private static String TAG="空间数据库";
    private static String path= Environment.getExternalStorageDirectory().getPath()+"/test.sqlite";


    private static class ClassHolder{
      private static final SpatialDatabaseHelper INSTANCE=new SpatialDatabaseHelper();
    }
    private SpatialDatabaseHelper (){
        Database database= OpenDatabase(path);

        if(database!=null){

        }

    }

    public static final SpatialDatabaseHelper getInstance(){
       return ClassHolder.INSTANCE;
    }

    /**
     * 打开Spatialite数据库
     * @param path 数据库路径和名称
     * @return Spatialite数据库
     */
    private Database OpenDatabase(String path){
        Database database=new Database();
        File dbFile=new File(path);
        try {
            //不存在就创建数据库
            database.open(dbFile.toString(), Constants.SQLITE_OPEN_READWRITE|Constants.SQLITE_OPEN_CREATE);
            return database;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  null;
    }

}
