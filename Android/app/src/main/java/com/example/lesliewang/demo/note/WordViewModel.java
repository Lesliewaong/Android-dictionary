package com.example.lesliewang.demo.note;
import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import java.util.List;
/*
存储和管理界面相关的数据
 */
public class WordViewModel extends AndroidViewModel {
    private WordRepository wordRepository;
    public WordViewModel(@NonNull Application application) {
        super(application);
        wordRepository = new WordRepository(application);
    }
    //通过仓库类wordRepository实现功能
    LiveData<List<Word>> getAllWordsLive() {
        return wordRepository.getAllWordsLive();
    }
    LiveData<List<Word>> findWordsWithPattern(String patten) {
        return wordRepository.findWordsWithPattern(patten);
    }

    public void insertWords(Word... words) {
        wordRepository.insertWords(words);
    }
    void updateWords(Word... words) {
        wordRepository.updateWords(words);
    }
    void deleteWords(Word... words) {
        wordRepository.deleteWords(words);
    }
    void deleteAllWords() {
        wordRepository.deleteAllWords();
    }


}
