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
	

    private static final int DATABASE_VERSION = 1; // Database Version
    private static final String DATABASE_NAME = "deletebyhaiku_db"; // Database Name
 
	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

    // Table names
    private static final String TABLE_SENTENCE = "sentence";
    private static final String TABLE_PARTOFSPEECH = "partofspeech";
    private static final String TABLE_WORDPARTOFSPEECH = "word_partofspeech";   
    private static final String TABLE_WORD = "word";
    private static final String TABLE_WORDINSENTENCE = "wordinsentence";
    private static final String TABLE_SMSWORD = "sms_word";
    private static final String TABLE_SENTENCETHEME = "sentence_theme";    
    private static final String TABLE_THEME = "theme";
    private static final String TABLE_THEMEWORD = "theme_word";

    // Column names
    private static final String KEY_SENTENCE_ID = "_id";
    private static final String KEY_SENTENCE_TEXT = "text";
    private static final String KEY_SENTENCE_SMSID = "smsid";
    
    private static final String KEY_PARTOFSPEECH_ID = "_id";
    private static final String KEY_PARTOFSPEECH_TYPE = "type";
    
    private static final String KEY_WORDPARTOFSPEECH_ID = "_id";
    private static final String KEY_WORDPARTOFSPEECH_WORDID = "wordid";
    private static final String KEY_WORDPARTOFSPEECH_PARTOFSPEECHID = "partofspeechid";

    private static final String KEY_WORD_ID = "_id";
    private static final String KEY_WORD_TEXT = "text";
    private static final String KEY_WORD_SYLLABLES = "syllables";

    private static final String KEY_WORDINSENTENCE_ID = "_id";
    private static final String KEY_WORDINSENTENCE_POSITION = "position";
    private static final String KEY_WORDINSENTENCE_SENTENCEID = "sentenceid";
    private static final String KEY_WORDINSENTENCE_WORDID = "wordid";
    
    private static final String KEY_SMSWORD_ID = "_id";
    private static final String KEY_SMSWORD_SMSID = "smsid";
    private static final String KEY_SMSWORD_WORDID = "wordid";

    private static final String KEY_SENTENCETHEME_ID = "_id";
    private static final String KEY_SENTENCETHEME_SENTENCEID = "sentenceid";
    private static final String KEY_SENTENCETHEME_THEMEID = "themeid";

    private static final String KEY_THEME_ID = "_id"; 
    private static final String KEY_THEME_NAME = "name";

    private static final String KEY_THEMEWORD_ID = "_id";
    private static final String KEY_THEMEWORD_THEMEID = "themeid"; 
    private static final String KEY_THEMEWORD_WORDID = "wordid";

	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + TABLE_SENTENCE + "(" + KEY_SENTENCE_ID + " INTEGER PRIMARY KEY," + KEY_SENTENCE_TEXT + " TEXT," + KEY_SENTENCE_SMSID + " INTEGER)");
		db.execSQL("CREATE TABLE " + TABLE_PARTOFSPEECH + "(" + KEY_PARTOFSPEECH_ID + " INTEGER PRIMARY KEY," + KEY_PARTOFSPEECH_TYPE + " TEXT)");
		db.execSQL("CREATE TABLE " + TABLE_WORDPARTOFSPEECH + "(" + KEY_WORDPARTOFSPEECH_ID + " INTEGER PRIMARY KEY," + KEY_WORDPARTOFSPEECH_WORDID + " INTEGER," + KEY_WORDPARTOFSPEECH_PARTOFSPEECHID + " INTEGER, " +
				"FOREIGN KEY(" + KEY_WORDPARTOFSPEECH_WORDID + ") REFERENCES " + TABLE_WORD + "(" + KEY_WORD_ID + ")," +
				"FOREIGN KEY(" + KEY_WORDPARTOFSPEECH_PARTOFSPEECHID + ") REFERENCES " + TABLE_WORDPARTOFSPEECH + "(" + KEY_PARTOFSPEECH_ID + "))");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//do nothing	
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
