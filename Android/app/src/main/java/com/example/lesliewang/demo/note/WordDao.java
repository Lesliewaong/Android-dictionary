package com.example.lesliewang.demo.note;
import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;
/*
Database access object
访问数据库操作的接口
 */
@Dao
public interface WordDao {
    @Insert
    void insertWords(Word... words);//添加 ...可插入多个参数

    @Update
    void updateWords(Word... words);//更新

    @Delete
    void deleteWords(Word... words);//删除

    @Query("DELETE FROM WORD")
    void deleteAllWords();//清空

    //查询所有并降序排列
    @Query("SELECT * FROM WORD ORDER BY ID DESC")
    //List<Word> getAllWords();
    LiveData<List<Word>>getAllWordsLive();// LiveData系统自动将它放在副线程
    //模糊查询
    @Query("SELECT * FROM WORD WHERE english_word LIKE :pattern ORDER BY ID DESC")
    LiveData<List<Word>>findWordsWithPattern(String pattern);

}
