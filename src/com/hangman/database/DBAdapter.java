package com.hangman.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBAdapter
    {

        public static final String KEY_ROWID = "_id";
        public static final String KEY_CATEGORY = "category";
        public static final String KEY_WORD = "word";
        public static final String KEY_WINS = "numwins";
        public static final String KEY_LOSSES = "numloss";

        private static final String TAG = "HangmanDbAdapter";
        private DatabaseHelper mDbHelper;
        private SQLiteDatabase mDb;

        /**
         * Database creation sql statement
         */
        private static final String DATABASE_CREATE = "create table hangmanwords (_id integer primary key autoincrement, "
                                                            + "category text not null, word text not null);";
        private static final String DATABASE_CREATE_SCORES = "create table hangmanscores(_id integer primary key autoincrement,"
                                                                + "numwins text not null, numloss text not null);";

        private static final String DATABASE_NAME = "hangmandata";
        private static final String DATABASE_TABLE = "hangmanwords";
        private static final String DATABASE_TABLE_SCORES = "hangmanscores";
        private static final int DATABASE_VERSION = 2;

        private final Context mCtx;

        private static class DatabaseHelper extends SQLiteOpenHelper
            {

                DatabaseHelper(Context context)
                    {
                        super(context, DATABASE_NAME, null, DATABASE_VERSION);
                    }

                @Override
                public void onCreate(SQLiteDatabase db)
                    {
                        db.execSQL(DATABASE_CREATE);
                        db.execSQL(DATABASE_CREATE_SCORES);
                    }

                @Override
                public void onUpgrade(SQLiteDatabase db, int oldVersion,
                        int newVersion)
                    {
                        Log.w(TAG, "Upgrading database from version "
                                + oldVersion + " to " + newVersion
                                + ", which will destroy all old data");
                        db.execSQL("DROP TABLE IF EXISTS hangmanwords");
                        db.execSQL("DROP TABLE IF ESIXTS hangmanscores;");
                        onCreate(db);
                    }
            }

        /**
         * Constructor - takes the context to allow the database to be
         * opened/created
         * 
         * @param ctx
         *            the Context within which to work
         */
        public DBAdapter(Context ctx)
            {
                this.mCtx = ctx;
            }

        /**
         * Open the words database. If it cannot be opened, try to create a new
         * instance of the database. If it cannot be created, throw an exception
         * to signal the failure
         * 
         * @return this (self reference, allowing this to be chained in an
         *         initialization call)
         * @throws SQLException
         *             if the database could be neither opened or created
         */
        public DBAdapter open() throws SQLException
            {
                mDbHelper = new DatabaseHelper(mCtx);
                mDb = mDbHelper.getWritableDatabase();
                return this;
            }

        public void close()
            {
                mDbHelper.close();
            }

        /**
         * Create a new word using the title and body provided. If the word is
         * successfully created return the new rowId for that word, otherwise
         * return a -1 to indicate failure.
         * 
         * @param category
         *            the category of the word, can be used for drill-down selection
         * @param word
         *            the word presented to the user to guess
         * @return rowId or -1 if failed
         */
        public long addWord(String category, String word)
            {
                ContentValues initialValues = new ContentValues();
                initialValues.put(KEY_CATEGORY, category);
                initialValues.put(KEY_WORD, word);

                return mDb.insert(DATABASE_TABLE, null, initialValues);
            }

        /**
         * Add initial scores to the database on the first play or
         * after a the user clears all game data.
         * 
         * @param wins
         *            the number of user wins
         * @param losses
         *            the number of user losses
         * @return rowId or -1 if failed
         */
        public long addInitialScores(String wins, String losses)
            {
                ContentValues initialValues = new ContentValues();
                initialValues.put(KEY_WINS, wins);
                initialValues.put(KEY_LOSSES, losses);

                return mDb.insert(DATABASE_TABLE_SCORES, null, initialValues);
            }

        /**
         * Delete the word with the given rowId
         * 
         * @param rowId
         *            id of word to delete
         * @return true if deleted, false otherwise
         */
        public boolean deleteWord(long rowId)
            {

                return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
            }

        /**
         * Delete the user's score data when desired
         * 
         * @param rowId
         *            id of score row
         * @return true if deleted, false otherwise
         */
        public boolean deleteScores(long rowId)
            {
                return mDb.delete(DATABASE_TABLE_SCORES, KEY_ROWID + "=" + rowId, null) > 0;
            }

        /**
         * Return a Cursor over the list of all words in the database
         * 
         * @return Cursor over all words
         */
        public Cursor fetchAllWords()
            {

                //return mDb.query(DATABASE_TABLE, new String[] { KEY_WORD }, 
                //        null, null, null, null, null);
                return mDb.rawQuery("SELECT DISTINCT " + KEY_WORD + ", " + KEY_CATEGORY + " from " + 
                        DATABASE_TABLE, null);
            }

        /**
         * Return a Cursor over the list of all scores in the database
         * 
         * @return Cursor over all scores
         */
        public Cursor fetchScores()
            {

                //return mDb.query(DATABASE_TABLE, new String[] { KEY_WORD }, 
                //        null, null, null, null, null);
                return mDb.rawQuery("SELECT DISTINCT " + KEY_ROWID + ", " + KEY_WINS + ", " +
                        KEY_LOSSES + " from " + DATABASE_TABLE_SCORES, null);
            }

        /**
         * Return a Cursor positioned at the word that matches the given rowId
         * 
         * @param rowId
         *            id of word to retrieve
         * @return Cursor positioned to matching word, if found
         * @throws SQLException
         *             if word could not be found/retrieved
         */
        public Cursor fetchWordByRowId(long rowId) throws SQLException
            {

                Cursor mCursor =

                mDb.query(true, DATABASE_TABLE, new String[] { KEY_ROWID,
                        KEY_CATEGORY, KEY_WORD }, KEY_ROWID + "=" + rowId,
                        null, null, null, null, null);
                if (mCursor != null)
                    {
                        mCursor.moveToFirst();
                    }
                return mCursor;

            }

        /**
         * Return a Cursor positioned at the word that matches the given
         * category
         * 
         * @param category
         *            category of words to retrieve
         * @return Cursor positioned to matching word, if found
         * @throws SQLException
         *             if word could not be found/retrieved
         */
        public Cursor fetchWordsByCategory(String category) throws SQLException
            {

                Cursor mCursor =

                mDb.query(true, DATABASE_TABLE, new String[] { KEY_WORD },
                        KEY_CATEGORY + "=" + category, null, null, null, null,
                        null);
                if (mCursor != null)
                    {
                        mCursor.moveToFirst();
                    }
                return mCursor;

            }

        /**
         * Update the word using the details provided. The word to be updated is
         * specified using the rowId, and it is altered to use the title and
         * body values passed in
         * 
         * @param rowId
         *            id of word to update
         * @param title
         *            value to set word title to
         * @param body
         *            value to set word body to
         * @return true if the word was successfully updated, false otherwise
         */
        public boolean updateWord(long rowId, String title, String body)
            {
                ContentValues args = new ContentValues();
                args.put(KEY_CATEGORY, title);
                args.put(KEY_WORD, body);

                return mDb.update(DATABASE_TABLE, args,
                        KEY_ROWID + "=" + rowId, null) > 0;
            }

        /**
         * Update the word using the details provided. The word to be updated is
         * specified using the rowId, and it is altered to use the title and
         * body values passed in
         * 
         * @param rowId
         *            id of scres to update
         * @param wins
         *            value to set wins to
         * @param losses
         *            value to set losses to
         * @return true if the score was successfully updated, false otherwise
         */
        public boolean updateScore(long rowId, String wins, String losses)
            {
                ContentValues args = new ContentValues();
                args.put(KEY_WINS, wins);
                args.put(KEY_LOSSES, losses);

                return mDb.update(DATABASE_TABLE_SCORES, args,
                        KEY_ROWID + "=" + rowId, null) > 0;
            }

    }
