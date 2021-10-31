package com.example.lesliewang.demo.note;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
/*
数据库
 */
//entities可写多个，用，分开  version 版本
//多个Entity,要写多个Dao
@Database(entities = {Word.class},version = 5,exportSchema = false)
public abstract class WordDatabase extends RoomDatabase {

    //singleton 只允许生成一个实例
    private static WordDatabase INSTANCE;
    //synchronized 同步方法
    static synchronized WordDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),WordDatabase.class,"word_database")
                    //.fallbackToDestructiveMigration()
                    .addMigrations(MIGRATION_4_5)//不改变现有数据升级版本
                    .build();
        }
        return INSTANCE;
    }

    public abstract WordDao getWordDao();

    static final Migration MIGRATION_2_3 = new Migration(2,3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE word ADD COLUMN bar_data INTEGER NOT NULL DEFAULT 1");//添加新的列
        }
    };

    static final Migration MIGRATION_3_4 = new Migration(3,4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE word_temp (id INTEGER PRIMARY KEY NOT NULL ,english_word TEXT," +
                    "chinese_meaning TEXT)");//创建新表 TEXT字符串
            database.execSQL("INSERT INTO word_temp (id,english_word,chinese_meaning) " +
                    "SELECT id,english_word,chinese_meaning FROM word");//把旧表内容迁移到新表
            database.execSQL("DROP TABLE word");//删除旧表
            database.execSQL("ALTER TABLE word_temp RENAME to word");//将新表的名称改为旧表
        }
    };

    static final Migration MIGRATION_4_5 = new Migration(4,5) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE word ADD COLUMN  chinese_invisible INTEGER NOT NULL DEFAULT 0");
        }
    };
}
