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
    private static final String KEY_SENTENCE_ID = "_id"; //INTEGER PRIMARY KEY
    private static final String KEY_SENTENCE_TEXT = "text"; //TEXT
    private static final String KEY_SENTENCE_SMSID = "smsid"; //INTEGER (foreign key to predefined table "SMS")
    
    private static final String KEY_PARTOFSPEECH_ID = "_id"; //INTEGER PRIMARY KEY
    private static final String KEY_PARTOFSPEECH_TYPE = "type"; //TEXT
    
    private static final String KEY_WORDPARTOFSPEECH_ID = "_id"; //INTEGER PRIMARY KEY
    private static final String KEY_WORDPARTOFSPEECH_WORDID = "wordid"; //INTEGER, Foreign key word(_id)
    private static final String KEY_WORDPARTOFSPEECH_PARTOFSPEECHID = "partofspeechid"; //INTEGER, Foreign key partofspeech(_id)

    private static final String KEY_WORD_ID = "_id"; //INTEGER PRIMARY KEY
    private static final String KEY_WORD_TEXT = "text"; //TEXT
    private static final String KEY_WORD_SYLLABLES = "syllables"; //TEXT

    private static final String KEY_WORDINSENTENCE_ID = "_id"; //INTEGER PRIMARY KEY
    private static final String KEY_WORDINSENTENCE_POSITION = "position"; //INTEGER
    private static final String KEY_WORDINSENTENCE_SENTENCEID = "sentenceid"; //INTEGER, Foreign key sentence(_id)
    private static final String KEY_WORDINSENTENCE_WORDID = "wordid"; //INTEGER, Foreign key word(_id)
    
    private static final String KEY_SMSWORD_ID = "_id"; //INTEGER PRIMARY KEY
    private static final String KEY_SMSWORD_SMSID = "smsid"; //foreign key to predefined table "SMS"
    private static final String KEY_SMSWORD_WORDID = "wordid"; //INTEGER, Foreign key word(_id)

    private static final String KEY_SENTENCETHEME_ID = "_id"; //INTEGER PRIMARY KEY
    private static final String KEY_SENTENCETHEME_SENTENCEID = "sentenceid"; //INTEGER, Foreign key sentence(_id)
    private static final String KEY_SENTENCETHEME_THEMEID = "themeid"; //INTEGER, Foreign key theme(_id)

    private static final String KEY_THEME_ID = "_id"; //INTEGER PRIMARY KEY
    private static final String KEY_THEME_NAME = "name"; //TEXT

    private static final String KEY_THEMEWORD_ID = "_id"; //INTEGER PRIMARY KEY
    private static final String KEY_THEMEWORD_THEMEID = "themeid"; //INTEGER, Foreign key theme(_id)
    private static final String KEY_THEMEWORD_WORDID = "wordid"; //INTEGER, Foreign key word(_id)

	
	@Override
	public void onCreate(SQLiteDatabase db) {

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
	
	public void addWord(Word word) {
		SQLiteDatabase db = this.getWritableDatabase();
	    ContentValues valuesWord = new ContentValues();
	    
	    valuesWord.put(KEY_WORD_TEXT, word.getText());
	    valuesWord.put(KEY_WORD_SYLLABLES, word.getSyllables());
	    db.insert(TABLE_WORD, null, valuesWord);
	    
	    //update all foreign tables
	    //serach through all sms
	    
	    //ContentValues valuesWord_partofspeech = new ContentValues();
	    //valuesWord_partofspeech.put(KEY_WORD_TEXT, word.text);
	    //db.insert(TABLE_WORDPARTOFSPEECH, null, valuesWord_partofspeech);
	    
	    db.close(); // Closing database connection
	}
	 
	public Word getWord(int id) {
		SQLiteDatabase db = this.getReadableDatabase();
		 
	    Cursor cursor = db.query(TABLE_WORD, new String[] { KEY_WORD_ID,
	            KEY_WORD_TEXT, KEY_WORD_SYLLABLES }, KEY_WORD_ID + "=?",
	            new String[] { String.valueOf(id) }, null, null, null, null);
	    	    
	    if (cursor != null)
	        cursor.moveToFirst();  
	 
	    String text = cursor.getString(0);
	    String syllables = cursor.getString(1);
	    String wordtypes = "";
	    
	    Word word = new Word(text, syllables, wordtypes);
	    return word;
	}
	 
//	public ArrayList<Word> getAllWords() {
//		ArrayList<Word> wordList = new ArrayList<Word>();
//	    String selectQuery = "SELECT  * FROM " + TABLE_WORD; // Select All Query
//	    SQLiteDatabase db = this.getWritableDatabase();
//	    Cursor cursor = db.rawQuery(selectQuery, null);
//	 
//	    // looping through all rows and adding to list
//	    if (cursor.moveToFirst()) {
//	        do {
//	        	Word word = new Word();
//	            word.setID(Integer.parseInt(cursor.getString(0)));
//	            word.setText(cursor.getString(1));
//	            word.setSyllables(cursor.getString(2));
//	            wordList.add(word);
//	        } while (cursor.moveToNext());
//	    }
//	 
//	    return wordList;
//	}
}
