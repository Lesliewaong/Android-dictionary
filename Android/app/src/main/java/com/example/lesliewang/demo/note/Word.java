package com.example.lesliewang.demo.note;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
/*
实体类
*/
//使用@Entity注解的tableName属性可以自定义表
@Entity
public class Word {
    //主键
    @PrimaryKey(autoGenerate = true)
    private int id;
    //ColumnInfo不写默认变量名
    //英文
    @ColumnInfo(name = "english_word")
    private String word;
    //中文
    @ColumnInfo(name = "chinese_meaning")
    private String chineseMeaning;

    @ColumnInfo(name = "chinese_invisible")
    private boolean chineseInvisible;

    public boolean isChineseInvisible() {
        return chineseInvisible;
    }

    public void setChineseInvisible(boolean chineseInvisible) {
        this.chineseInvisible = chineseInvisible;
    }
    //    @ColumnInfo(name = "foo_data")
//    private boolean foo;
//    @ColumnInfo(name = "bar_data")
//    private boolean bar;
//
//    public boolean isBar() {
//        return bar;
//    }
//
//    public void setBar(boolean bar) {
//        this.bar = bar;
//    }
//
//    public boolean isFoo() {
//        return foo;
//    }
//
//    public void setFoo(boolean foo) {
//        this.foo = foo;
//    }

    public Word(String word, String chineseMeaning) {
        this.word = word;
        this.chineseMeaning = chineseMeaning;
    }
    //get,set
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getChineseMeaning() {
        return chineseMeaning;
    }

    public void setChineseMeaning(String chineseMeaning) {
        this.chineseMeaning = chineseMeaning;
    }
}
