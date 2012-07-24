package com.hangman.word;

public class WordStructure {
    private String mWord = "";
    private String mCategory = "";
    private String mId = "";
    private char[] mSelectedWord;
    
    public WordStructure() {
    }
    
    public WordStructure(String word, String category){
        //setId(id);
        setWord(word);
        setCategory(category);
    }
    
//    public void setId(String id){
//        this.mId = id;
//    }
//    
//    public String getId(){
//        return this.mId;
//    }
    
    public void setWord(String word){
        this.mWord = word;
        this.mSelectedWord = new char[word.length()];
        this.mSelectedWord = word.toCharArray();
    }
    
    public String getWord(){
        return this.mWord;
    }
    
    public char[] getSelectedWord(){
        return this.mSelectedWord;
    }
    
    public void setCategory(String category){
        this.mCategory = category;
    }
    
    public String getCategory(){
        return this.mCategory;
    }
}
