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
    private static final String DATABASE_NAME = "haiku_db"; // Database Name
 
	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

    // Table names
    private static final String TABLE_SENTENCE = "sentence";
    private static final String TABLE_SENTENCEINSMS = "sentenceinsms";
    private static final String TABLE_SENTENCEINHAIKU = "sentenceinhaiku";
    private static final String TABLE_HAIKU = "haiku";
    private static final String TABLE_PARTOFSPEECH = "partofspeech";
    private static final String TABLE_WORDPARTOFSPEECH = "word_partofspeech";   
    private static final String TABLE_WORD = "word";
    private static final String TABLE_WORDINSENTENCE = "wordinsentence";
    private static final String TABLE_WORDINSMS = "wordinsms";
    private static final String TABLE_SENTENCEINSMSTHEME = "sentenceinsms_theme";    
    private static final String TABLE_THEME = "theme";
    private static final String TABLE_THEMEWORD = "theme_word";

    // Column names
    private static final String KEY_SENTENCE_ID = "id";
    private static final String KEY_SENTENCE_TEXT = "text";
    
    private static final String KEY_SENTENCEINSMS_ID = "id";
    private static final String KEY_SENTENCEINSMS_SMS = "sms"; //foreign key
    
    private static final String KEY_SENTENCEINHAIKU_ID = "id";
    private static final String KEY_SENTENCEINHAIKU_POSITION = "position";
    private static final String KEY_SENTENCEINHAIKU_HAIKU = "haiku";
    
    private static final String KEY_HAIKU_ID = "id";
    private static final String KEY_HAIKU_DATECREATED = "dateCreated";

    private static final String KEY_PARTOFSPEECH_ID = "id";
    private static final String KEY_PARTOFSPEECH_TYPE = "type";
    
    private static final String KEY_WORDPARTOFSPEECH_ID = "id";
    private static final String KEY_WORDPARTOFSPEECH_WORD = "word";
    private static final String KEY_WORDPARTOFSPEECH_PARTOFSPEECH = "partofspeech";

    private static final String KEY_WORD_ID = "id";
    private static final String KEY_WORD_TEXT = "text";
    private static final String KEY_WORD_SYLLABLES = "syllables";

    private static final String KEY_WORDINSENTENCE_ID = "id";
    private static final String KEY_WORDINSENTENCE_POSITION = "position";
    private static final String KEY_WORDINSENTENCE_SENTENCE = "sentence";
    private static final String KEY_WORDINSENTENCE_WORD = "word";    
    
    private static final String KEY_WORDINSMS_ID = "id";
    private static final String KEY_WORDINSMS_AMOUNT = "amount";
    private static final String KEY_WORDINSMS_SMS = "sms"; //foreign key
    private static final String KEY_WORDINSMS_WORD = "word";    
 
    private static final String KEY_SENTENCEINSMSTHEME_ID = "id";
    private static final String KEY_SENTENCEINSMSTHEME_SENTENCEINSMS = "sentenceinsms";
    private static final String KEY_SENTENCEINSMSTHEME_THEME = "theme";

    private static final String KEY_THEME_ID = "id";
    private static final String KEY_THEME_NAME = "name";

    private static final String KEY_THEMEWORD_ID = "id";
    private static final String KEY_THEMEWORD_WORD = "word";
    private static final String KEY_THEMEWORD_THEME = "theme"; 
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		
/*		db.execSQL("CREATE TABLE sentence(id INTEGER PRIMARY KEY, text TEXT)");
        db.execSQL("CREATE TABLE sentenceinsms(id INTEGER PRIMARY KEY, sms INTEGER)");
        db.execSQL("CREATE TABLE sentenceinhaiku(id INTEGER PRIMARY KEY, position INTEGER, haiku, INTEGER, FOREIGN KEY(haiku) REFERENCES haiku(id)");
        db.execSQL("CREATE TABLE haiku(id INTEGER PRIMARY KEY, datecreated INTEGER)");
        db.execSQL("CREATE TABLE partofspeech(id INTEGER PRIMARY KEY, type TEXT)");
        db.execSQL("CREATE TABLE word_partofspeech(id INTEGER PRIMARY KEY, word INTEGER, partofspeech INTEGER, FOREIGN KEY(word) REFERENCES word(id), FOREIGN KEY(partofspeech) REFERENCES partofspeech(id))");
        db.execSQL("CREATE TABLE word(id INTEGER PRIMARY KEY, text TEXT, syllables TEXT)");
        db.execSQL("CREATE TABLE wordinsentence(id INTEGER PRIMARY KEY, position INTEGER, sentence INTEGER, word INTEGER, FOREIGN KEY(sentence) REFERENCES sentence(id), FOREIGN KEY(word) REFERENCES word(id))");
        db.execSQL("CREATE TABLE wordinsms(id INTEGER PRIMARY KEY, amount INTEGER, sms INTEGER, word INTEGER, FOREIGN KEY(word) REFERENCES word(id))");
        db.execSQL("CREATE TABLE sentenceinsms_theme(id INTEGER PRIMARY KEY, sentenceinsms INTEGER, theme INTEGER, FOREIGN KEY(sentenceinsms) REFERENCES sentenceinsms(id), FOREIGN KEY(theme) REFERENCES theme(id))");     
        db.execSQL("CREATE TABLE theme(id INTEGER PRIMARY KEY, name TEXT)");
        db.execSQL("CREATE TABLE theme_word(id INTEGER PRIMARY KEY word INTEGER, theme INTEGER, FOREIGN KEY(word) REFERENCES word(id), FOREIGN KEY(theme) REFERENCES theme(id))");*/
	
        db.execSQL("CREATE TABLE " + TABLE_SENTENCE + "("
        		+ KEY_SENTENCE_ID + " INTEGER PRIMARY KEY," + KEY_SENTENCE_TEXT + " TEXT" + ")");
        db.execSQL("CREATE TABLE " + TABLE_SENTENCEINSMS + "(" + KEY_SENTENCEINSMS_ID + " INTEGER PRIMARY KEY, " + KEY_SENTENCEINSMS_SMS + " INTEGER)");
	
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//do nothing
	}
	
	public void addWord(Word word) {
		SQLiteDatabase db = this.getWritableDatabase();
		 
	    ContentValues values = new ContentValues();
	    
	    //values.put(KEY_WORD_TEXT, word.getText());
	    //values.put(KEY_WORD_SYLLABLES, word.getSyllables());
	    
	    values.put("text", word.getText());
	    values.put("syllables", word.getSyllables());	    
	    
	    // Inserting Row
	    //db.insert(TABLE_WORD, null, values);
	    
	    db.insert("word", null, values);
	    
	    db.close(); // Closing database connection
	}
	 
	public Word getWord(int id) {
		SQLiteDatabase db = this.getReadableDatabase();
		 
	    Cursor cursor = db.query(TABLE_WORD, new String[] { KEY_WORD_ID,
	            KEY_WORD_TEXT, KEY_WORD_SYLLABLES }, KEY_WORD_ID + "=?",
	            new String[] { String.valueOf(id) }, null, null, null, null);
	    
	    //Cursor cursor = db.query("word", new String[] { "id", "text", "syllables" }, "id" + "=?", new String[] { String.valueOf(id) }, null, null, null, null);
	    
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
