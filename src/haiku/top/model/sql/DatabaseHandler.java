package haiku.top.model.sql;

import java.util.ArrayList;

import haiku.top.model.Contact;
import haiku.top.model.Haiku;
import haiku.top.model.SMS;
import haiku.top.model.Word;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper{
	
	// Database Version
    private static final int DATABASE_VERSION = 1;
 
    // Database Name
    private static final String DATABASE_NAME = "Database";
 
    // Table names
    private static final String TABLE_WORD = "Word";
    private static final String TABLE_THEME = "Theme";
    private static final String TABLE_PARTOFSPEECH = "Part of speech";
    private static final String TABLE_PARTOFSPEECHWORD = "Part of speech word";
    private static final String TABLE_SMSWORD = "SMS word";
    private static final String TABLE_HAIKU = "Haiku";
    private static final String TABLE_HAIKUSMS = "Haiku SMS";
    private static final String TABLE_THEMEWORD = "Theme word";
    private static final String TABLE_HAIKUTHEME = "Haiku theme";
    private static final String TABLE_DATEINTERVAL = "Date interval";
    private static final String TABLE_SENTENCE = "Sentence";
    private static final String TABLE_WORDINSENTENCE = "Word in sentence";
 
    // Column names
    private static final String KEY_WORD_ID = "id";
    private static final String KEY_WORD_TEXT = "text";
    private static final String KEY_WORD_SYLLABLES = "syllables";
    
    private static final String KEY_THEME_ID = "id";
    private static final String KEY_THEME_NAME = "name";
    
    private static final String KEY_PARTOFSPEECH_ID = "id";
    private static final String KEY_PARTOFSPEECH_TYPE = "type";
    
    private static final String KEY_PARTOFSPEECHWORD_ID = "id";
    private static final String KEY_PARTOFSPEECHWORD_PARTOFSPEECH = "part of speech";
    private static final String KEY_PARTOFSPEECHWORD_WORD = "word";
    
    private static final String KEY_SMSWORD_ID = "id";
    private static final String KEY_SMSWORD_SMS = "sms";
    private static final String KEY_SMSWORD_WORD = "word";
    
    private static final String KEY_HAIKU_ID = "id";
    private static final String KEY_HAIKU_DATECREATED = "date created";
    private static final String KEY_HAIKU_RATING = "rating";
    
    private static final String KEY_HAIKUSMS_ID = "id";
    private static final String KEY_HAIKUSMS_HAIKU = "haiku";
    private static final String KEY_HAIKUSMS_SMS = "sms";
    
    private static final String KEY_THEMEWORD_ID = "id";
    private static final String KEY_THEMEWORD_HAIKU = "haiku";
    private static final String KEY_THEMEWORD_THEME = "theme";
    
    private static final String KEY_HAIKUTHEME_ID = "id";
    private static final String KEY_HAIKUTHEME_HAIKU = "haiku";
    private static final String KEY_HAIKUTHEME_THEME = "theme";
    
    private static final String KEY_DATEINTERVAL_ID = "id";
    private static final String KEY_DATEINTERVAL_HAIKU = "haiku";
    private static final String KEY_DATEINTERVAL_STARTDATE = "start date";
    private static final String KEY_DATEINTERVAL_ENDDATE = "end date";
    
    private static final String KEY_SENTENCE_ID = "id";
    private static final String KEY_SENTENCE_HAIKU = "haiku";
    private static final String KEY_SENTENCE_POSITION = "position";
    private static final String KEY_SENTENCE_TEXT = "text";
    
    private static final String KEY_WORDINSENTENCE_ID = "id";
    private static final String KEY_WORDINSENTENCE_SENTENCE = "sentence";
    private static final String KEY_WORDINSENTENCE_WORD = "word";
    private static final String KEY_WORDINSENTENCE_POSITION = "position";
    
	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
//		String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "("
//                + KEY_CONTACT_ID + " INTEGER PRIMARY KEY," + KEY_CONTACT_NAME + " TEXT,"
//                + KEY_CONTACT_PH_NO + " TEXT" + ")";
//        db.execSQL(CREATE_CONTACTS_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
 
        // Create tables again
        onCreate(db);
	}
	
	public void addWord(Word word) {
		SQLiteDatabase db = this.getWritableDatabase();
		 
	    ContentValues values = new ContentValues();
	    values.put(KEY_WORD_TEXT, word.getText());
	    values.put(KEY_WORD_SYLLABLES, word.getSyllables());
	    
	 
	    // Inserting Row
	    db.insert(TABLE_WORD, null, values);
	    db.close(); // Closing database connection
	}
	 
	public Word getWord(int id) {
		SQLiteDatabase db = this.getReadableDatabase();
		 
	    Cursor cursor = db.query(TABLE_WORD, new String[] { KEY_WORD_ID,
	            KEY_WORD_TEXT, KEY_WORD_SYLLABLES }, KEY_WORD_ID + "=?",
	            new String[] { String.valueOf(id) }, null, null, null, null);
	    if (cursor != null)
	        cursor.moveToFirst();
	 
	    Word word = new Word(cursor.getString(0), cursor.getString(1));
	    return word;
	}
	 
	public ArrayList<Word> getAllWords() {
		ArrayList<Word> wordList = new ArrayList<Word>();
	    // Select All Query
	    String selectQuery = "SELECT  * FROM " + TABLE_WORD;
	 
	    SQLiteDatabase db = this.getWritableDatabase();
	    Cursor cursor = db.rawQuery(selectQuery, null);
	 
	    // looping through all rows and adding to list
	    if (cursor.moveToFirst()) {
	        do {
	        	Word word = new Word();
	            word.setID(Integer.parseInt(cursor.getString(0)));
	            word.setText(cursor.getString(1));
	            word.setSyllables(cursor.getString(2));
	            wordList.add(word);
	        } while (cursor.moveToNext());
	    }
	 
	    return wordList;
	}
	 
	public int getWordsCount() {
		String countQuery = "SELECT  * FROM " + TABLE_WORD;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();
        return cursor.getCount();
	}
	
	public int updateWord(Word word) {
		SQLiteDatabase db = this.getWritableDatabase();
		 
	    ContentValues values = new ContentValues();
	    values.put(KEY_WORD_TEXT, word.getText());
	    values.put(KEY_WORD_SYLLABLES, word.getSyllables());
	 
	    // updating row
	    return db.update(TABLE_WORD, values, KEY_WORD_ID + " = ?",
	            new String[] { String.valueOf(word.getID()) });
	}
	 
	public void deleteWord(Word word) {
		SQLiteDatabase db = this.getWritableDatabase();
	    db.delete(TABLE_WORD, KEY_WORD_ID + " = ?",
	            new String[] { String.valueOf(word.getID()) });
	    db.close();
	}
}
