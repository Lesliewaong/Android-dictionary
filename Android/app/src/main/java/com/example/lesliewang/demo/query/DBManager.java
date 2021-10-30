package com.example.lesliewang.demo.query;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import com.example.lesliewang.demo.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/*
打开已有的数据库查询
 */

public class DBManager {
    private static final String DB_NAME = "youdao.db"; //保存的数据库文件名
    private static final String PACKAGE_NAME = "com.example.lesliewang.demo";//包名
    private static final String DB_PATH = "/data"+ Environment.getDataDirectory().getAbsolutePath() + "/"+ PACKAGE_NAME+"/databases";  //在手机里存放数据库的位置
    private Context context;
    private SQLiteDatabase database;

    public DBManager(Context context) {
        this.context = context;
        open();
    }
    //将raw中的db文件传到手机中
    private void open() {
        try
        {
            // 获得dictionary.db文件的绝对路径
            String databaseFilename = DB_PATH + "/" + DB_NAME;
            File dir = new File(DB_PATH);
            // 如果目录中存在，创建这个目录
            if (!dir.exists())
                dir.mkdir();
            // 如果不存在 dictionary.db文件，则从res\raw目录中复制这个文件
            if (!(new File(databaseFilename)).exists())
            {
                // 获得封装dictionary.db文件的InputStream对象
                InputStream is = this.context.getResources().openRawResource(R.raw.youdao);
                FileOutputStream fos = new FileOutputStream(databaseFilename);
                int BUFFER_SIZE = 400000;
                byte[] buffer = new byte[BUFFER_SIZE];
                int count = 0;
                // 开始复制dictionary.db文件
                while ((count = is.read(buffer)) > 0)
                {
                    fos.write(buffer, 0, count);
                }
                fos.close();
                is.close();
            }
            // 打开dictionary.db文件
            database = SQLiteDatabase.openOrCreateDatabase(databaseFilename, null);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    //查询 =
    public Cursor select(String sql, String[] condition){
        return database.rawQuery(sql, condition);
    }
    //模糊查询 like
    public Cursor fuzzySelect(String sql,String[] condition){
        return database.rawQuery(sql, condition);
    }

    public void closeDatabase() {
        database.close();
    }
}
