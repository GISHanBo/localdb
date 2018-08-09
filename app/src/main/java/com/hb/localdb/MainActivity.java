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
    }

    @Override
    protected void onDestroy() {
        spatialDatabaseHelper.close();
        super.onDestroy();
    }
}
