package com.hangman.database;

import android.database.Cursor;

import com.hangman.word.GlobalWords;
import com.hangman.word.WordStructure;

public class DbToStructure implements GlobalWords {
    private Cursor mDbWords;
    private Integer mDbWordsIndex;
    
    public DbToStructure(){
    }
    
    public Integer fillStructure(Cursor words, Integer idx){
        WordStructure word = null;
        Integer newIdx = -1;
        
        if(idx > 0){
            newIdx = fillStructure(words, idx-1);
        }
        
        if(newIdx != -1){
            words.moveToPosition(newIdx);
            word = new WordStructure(words.getString(0), words.getString(1));
            synchronized(this){
                structuredWordList.add(word);
            }
            newIdx++;
        }       
        
        return newIdx;
    }

}
