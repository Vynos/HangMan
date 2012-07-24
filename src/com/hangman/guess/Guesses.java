package com.hangman.guess;

import java.util.Random;

import com.hangman.word.WordStructure;

import android.view.View;
import android.widget.TextView;

public class Guesses {
    private View view;
    private View catView;
    private char[] word;
    private String selectedWord;
    private String selectedCategory;
    private TextView guessedLetters;
    private TextView category;
    private Integer strikes = 0;
    
    public Guesses(){
        
    }
    
    public char[] assignWord(String[] words, View v){
        this.setView(v);
        guessedLetters = (TextView)v;
        
        char[] localWord;
        Random randomGenerator = new Random();
        int randomInt = randomGenerator.nextInt(words.length);
        selectedWord = words[randomInt];
        String spacedWord = "";
        
        localWord = new char[selectedWord.length()];
        selectedWord.getChars(0, selectedWord.length(), localWord, 0);
        
        for(char ltr : localWord){
            guessedLetters.append("_ ");
            spacedWord = spacedWord + ltr + " ";
        }
        
        localWord = new char[spacedWord.length()];
        spacedWord.getChars(0, spacedWord.length(), localWord, 0);
        this.word = localWord;
                
        return localWord;
    }

    public char[] assignWord(WordStructure[] words, View gl, View cat){
        this.setView(gl);
        guessedLetters = (TextView)gl;
        this.setCatView(cat);
        category = (TextView)cat;
        
        char[] localWord;
        Random randomGenerator = new Random();
        int randomInt = randomGenerator.nextInt(words.length);
        selectedWord = words[randomInt].getWord();
        selectedCategory = words[randomInt].getCategory();
        String spacedWord = "";
        
        localWord = new char[selectedWord.length()];
        selectedWord.getChars(0, selectedWord.length(), localWord, 0);
        
        for(char ltr : localWord){
            guessedLetters.append("_ ");
            spacedWord = spacedWord + ltr + " ";
        }
        
        localWord = new char[spacedWord.length()];
        spacedWord.getChars(0, spacedWord.length(), localWord, 0);
        category.setText(selectedCategory);
        this.word = localWord;
                
        return localWord;
    }
    
    public Integer letterGuess(char letter){
        CharSequence original = guessedLetters.getText();
        guessedLetters.setText("");
        
        boolean found = false;
        
        for(int x = 0; x < word.length; x++){
            char wordLtr = word[x];
            
            if(Character.toUpperCase(wordLtr) == Character.toUpperCase(letter)){
                guessedLetters.append(Character.toUpperCase(letter) + "");
                found = true;
            } else {
                guessedLetters.append(Character.toUpperCase(original.charAt(x)) + "");
            }
        }

        if(!found)
            strikes++;
        
        return strikes;
    }
    
    public View getView(){
        return this.view;
    }

    public void setView(View v) {
        this.view = v;        
    }
    public View getCatView(){
        return this.catView;
    }

    public void setCatView(View v) {
        this.catView = v;        
    }
}
