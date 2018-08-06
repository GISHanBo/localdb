package com.hb.localdb;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.File;

import jsqlite.Database;
import jsqlite.Stmt;

public class MainActivity extends AppCompatActivity {
    private Database db;
    private static String TAG="测试";
    SpatialDatabaseHelper spatialDatabaseHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //单例模式
        spatialDatabaseHelper=SpatialDatabaseHelper.getInstance();
        db=new Database();
        try {
            File dbFile=new File(Environment.getExternalStorageDirectory().getPath()+"/test.sqlite");
            db.open(dbFile.toString(), jsqlite.Constants.SQLITE_OPEN_READONLY);
            String query = "SELECT name, peoples, AsText(Geometry) from Towns where peoples > 350000";
            Stmt stmt = db.prepare(query);
            stmt.step();
            while (stmt.step()) {
                String tableName = stmt.column_string(0);
                String type = stmt.column_string(1);
                String srid = stmt.column_string(2);
                Log.e(TAG, tableName+type+srid);
            }
            stmt.close();
            db.close();
        } catch (Exception e){
            Log.e(TAG, String.valueOf(e));
            e.printStackTrace();
        }

    }
}
