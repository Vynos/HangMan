package com.hangman;

import android.app.*;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.hangman.database.DBAdapter;
import com.hangman.guess.Guesses;
import com.hangman.word.GlobalWords;
import com.hangman.word.WordStructure;

public class HangManMain extends Activity implements GlobalWords {
    //The below array will be used to initialize the database if it is empty
    private WordStructure[] structuredWords = {new WordStructure("Hi", "Greet"),
                                               new WordStructure("Tornado", "Thing"),
                                               new WordStructure("Flower", "Flora"),
                                               new WordStructure("Pizza", "Food"),
                                               new WordStructure("Muffin", "Food"),
                                               new WordStructure("Aquarium", "Place"),
                                               new WordStructure("Toad", "Animal"),
                                               new WordStructure("Alabama", "Place"),
                                               new WordStructure("Mermaid", "Mythical"),
                                               new WordStructure("Android", "Mobile"),
                                               new WordStructure("Game", "This"),
                                               new WordStructure("Arkansas", "Place"),
                                               new WordStructure("Test", "Test"),
                                               new WordStructure("Word", "Test")};
    
    //fetchedWords will hold the results of our database query.
    private Cursor fetchedWords;
    
    private Cursor fetchedScores;
    
    private char[] lastWord;
    
    //array "word" used to test individual letters.
    private char[] word;
    
    private TextView guessedLetters;
    private TextView txtWins;
    private TextView txtLosses;
    private TextView txtCategory;
    
    //ImageView to hold the main game screen where the gallows is displayed.
    private ImageView hangmanImage;
    
    //class to handle processing of guesses.
    private Guesses guess;
    
    //class used to handle all database actions
    private DBAdapter db;
    
    //strikeNumber will hold the resource value of the gallows images.
    static int[] strikeNumber = new int[8];
    
    Integer strikes = 0;
    Integer wins = 0;
    Integer losses = 0;
    long rowId = 0;

    //static block to initialize the chalk board image array for guesses.
    static{
        strikeNumber[0] = R.drawable.base;
        strikeNumber[1] = R.drawable.strike1;
        strikeNumber[2] = R.drawable.strike2;
        strikeNumber[3] = R.drawable.strike3;
        strikeNumber[4] = R.drawable.strike4;
        strikeNumber[5] = R.drawable.strike5;
        strikeNumber[6] = R.drawable.gameover;
        strikeNumber[7] = R.drawable.youwin;
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        db = new DBAdapter(this.getApplicationContext());

        //pull text views for updating as the user plays guesses/games.
        guessedLetters = (TextView) findViewById(R.id.GuessedLetters);
        txtWins = (TextView) findViewById(R.id.Win);
        txtLosses = (TextView) findViewById(R.id.Loss);
        txtCategory = (TextView) findViewById(R.id.Category);

        //assign the comic font used in the letter images to the below textviews.
        //can't assign custom fonts in layout xml except for custom controls.        
        Typeface myTypeface = Typeface.createFromAsset(this.getAssets(),
        "comic.ttf");
        guessedLetters.setTypeface(myTypeface);
        txtWins.setTypeface(myTypeface);
        txtLosses.setTypeface(myTypeface);
        txtCategory.setTypeface(myTypeface);

        if(fetchedWords == null || fetchedWords.isClosed()){ //only allows db query once per application run
            db.open();
            
            fetchedWords = db.fetchAllWords();    
            fetchedScores = db.fetchScores();
            
            //add the words array to the database if it is empty 
            //if not, prevents adding redundant rows to the db.
            if(fetchedWords.getCount() == 0){
                for(WordStructure wrd : structuredWordList){
                    db.addWord(wrd.getCategory(), wrd.getWord());
                }
                
                fetchedWords = db.fetchAllWords();
            }
            
            if(fetchedScores.getCount() <= 0){
                db.addInitialScores(wins.toString(), losses.toString());
            }
            
            if(fetchedScores != null && fetchedScores.getCount() > 0){
                fetchedScores.moveToFirst();
                rowId = fetchedScores.getInt(0);
                wins = Integer.parseInt(fetchedScores.getString(1));
                losses = Integer.parseInt(fetchedScores.getString(2));
            }

            //if db pull was successful, replace words array for use in game play. 
            if(fetchedWords != null && fetchedWords.getCount() > 0){
                //words = new String[fetchedWords.getCount()];
                structuredWords = new WordStructure[fetchedWords.getCount()];
                fetchedWords.moveToFirst();
                while(!fetchedWords.isAfterLast()){
                    //words[fetchedWords.getPosition()] = fetchedWords.getString(0);
                    structuredWords[fetchedWords.getPosition()] = 
                        new WordStructure(fetchedWords.getString(0), fetchedWords.getString(1));
                    fetchedWords.move(1);
                }                
                
//                Thread thd = new Thread(){
//                  public void run(){
//                      structuredWords = new WordStructure[fetchedWords.getCount()];
//                      fetchedWords.moveToFirst();
//                      DbToStructure dts = new DbToStructure();
//                      int i = dts.fillStructure(fetchedWords, fetchedWords.getCount());
//                      notify();
//                  }
//                };
//                
//                thd.start();
//                if(thd.isAlive()){
//                    try {
//                        this.wait();
//                    } catch (InterruptedException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }
//                }
            }

            db.close();
        }

        //set category and stats
        txtWins.setText(wins.toString());
        txtLosses.setText(losses.toString());
        txtCategory.setText("misc");

        //set initial gallows image
        hangmanImage = (ImageView) findViewById(R.id.HangManImage);
        hangmanImage.setImageResource(strikeNumber[strikes]);
        
        guess = new Guesses();
        
        //set the word to be guessed.
        if(lastWord == null || !getLastWord().toString().equals(getWord().toString())){
            setWord(guess.assignWord(structuredWords, guessedLetters, txtCategory));
            setLastWord(getWord());
        }else{
            while(getLastWord().toString().equals(getWord().toString())){
                setWord(guess.assignWord(structuredWords, guessedLetters, txtCategory));
            }
            setLastWord(getWord());
        }        
    }

    private void checkScore() {
        hangmanImage.setImageResource(strikeNumber[strikes]);
        Boolean win = true;
        
        for (int x = 0; x < guessedLetters.length(); x++) {
            if (guessedLetters.getText().charAt(x) == '_')
                win = false;
        }

        if (win) {
            wins += 1;
            txtWins.setText(wins.toString());
            hangmanImage.setImageResource(strikeNumber[7]);
            userContinue("You won! Congrats!");
        }

        if (strikes == 6) {
            char[] word = this.getWord();
            String losingText = "The word was only " + Integer.toString(word.length/2) +
            " letters long!!";
            losses += 1;
            txtLosses.setText(losses.toString());
            userContinue(losingText);
        }
    }

    public char[] getWord() {
        return word;
    }
    
    public char[] getLastWord(){
        return lastWord;
    }

    public void letterClick(View view) {
        Button btn = (Button) findViewById(view.getId());
        btn.setEnabled(false);
        // btn.setVisibility(4);
        strikes = guess.letterGuess(btn.getText().charAt(0));
        checkScore();
    }

    private void newGame() {
        strikes = 0;

        Bundle Bundle = new Bundle();
        onCreate(Bundle);
    }

    public void setWord(char[] word) {
        this.word = word;
    }
    
    public void setLastWord(char[] word){
        this.lastWord = word;
    }

    
    //handle the popup used to ask if the user wants to play again or quit.
    public void userContinue(String message) {
        if(!fetchedScores.isClosed())
            fetchedScores.close();
        if(!fetchedWords.isClosed())
            fetchedWords.close();
        
        db.open();        
        db.updateScore(rowId, wins.toString(), losses.toString());        
        db.close();
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message + "\n Play again?")
        .setCancelable(false)
        .setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog,
                    int which) {
                newGame();
            }
        })
        .setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(!fetchedWords.isClosed())
                    fetchedWords.close();

                HangManMain.this.finish();
            }
        });

        AlertDialog endGame = builder.create();
        endGame.show();
    }
}