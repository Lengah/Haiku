package haiku.top.model.sql;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import haiku.top.model.Contact;
import haiku.top.model.Haiku;
import haiku.top.model.SMS;
import haiku.top.model.Word;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHandler extends SQLiteOpenHelper{
	
	private static String DB_PATH = "/data/data/haiku.top/databases/";
	private static final String DB_NAME = "deletebyhaiku_db"; //file name
	private SQLiteDatabase myDataBase;  
	private final Context myContext;
	private static final int DATABASE_VERSION = 1; // Database Version
	private static final String DATABASE_NAME = "deletebyhaiku_db"; // Database Name
 
	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		myContext = context;
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

    /**
     * Creates a empty database on the system and rewrites it with your own database.
     * */
   public void createDataBase() throws IOException {
	   boolean dbExist = checkDataBase();    
	   if(dbExist){
		   //do nothing - database already exist
	   }
	   else {  
		   //By calling this method and empty database will be created into the default system path
		   //of your application so we are gonna be able to overwrite that database with our database.
		   this.getReadableDatabase();
		   
		   try { copyDataBase(); } catch (IOException e) {
		   throw new Error("Error copying database"); 
		   }
	   }
   }
    
   /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     * @return true if it exists, false if it doesn't
     */
   private boolean checkDataBase() {
	   SQLiteDatabase checkDB = null;
	   try {
		   String myPath = DB_PATH + DB_NAME;
		   checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
	   } catch(SQLiteException e){ //database does't exist yet.
	   }
	    
	   if(checkDB != null) { 
		   checkDB.close();
	   }
	   return checkDB != null ? true : false;
   }
    
   /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     * */
   private void copyDataBase() throws IOException {
	   //Open your local db as the input stream
	   InputStream myInput = myContext.getAssets().open(DB_NAME);
	   
	   // Path to the just created empty db
	   String outFileName = DB_PATH + DB_NAME;
	   
	   //Open the empty db as the output stream
	   OutputStream myOutput = new FileOutputStream(outFileName);
	   
	   //transfer bytes from the inputfile to the outputfile
	   byte[] buffer = new byte[1024];
	   int length;
	   while ((length = myInput.read(buffer))>0){
		   myOutput.write(buffer, 0, length);
	   }
    
	   //Close the streams
	   myOutput.flush();
	   myOutput.close();
	   myInput.close();
   }
    
   public void openDataBase() throws SQLException {
	   //Open the database
	   String myPath = DB_PATH + DB_NAME;
	   myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
   }
    
   @Override
   public synchronized void close() {   
	   if(myDataBase != null)
		   myDataBase.close(); 
	   super.close(); 
   }
    
	@Override
	public void onCreate(SQLiteDatabase db) {
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
	 
	public Word getWord(String text) { //return null if word not found
		 
	    Cursor cursor = myDataBase.query(TABLE_WORD, new String[] { KEY_WORD_ID,
	            KEY_WORD_TEXT, KEY_WORD_SYLLABLES }, KEY_WORD_TEXT + "=?",
	            new String[] { text }, null, null, null, null);
	    	    
	    if (cursor != null && cursor.getCount() > 0) { //if word found
		    cursor.moveToFirst();
		    String id = cursor.getString(0);
		    String text2 = cursor.getString(1);
		    String syllables = cursor.getString(2);
		    
		    //get partofspeech
		    ArrayList<String> wordtypes = new ArrayList<String>();
		    Cursor cursor2 = myDataBase.rawQuery("SELECT " + KEY_WORDPARTOFSPEECH_PARTOFSPEECHID + " FROM " + TABLE_WORDPARTOFSPEECH + " WHERE " + KEY_WORDPARTOFSPEECH_WORDID + " = " + id  + ";", null);  
		    if (cursor2.moveToFirst()) {
		        do {
		        	String partofspeechid = cursor2.getString(0);
		    	    Cursor cursor3 = myDataBase.query(TABLE_PARTOFSPEECH, new String[] { KEY_PARTOFSPEECH_TYPE }, KEY_PARTOFSPEECH_ID + "=?", new String[] { partofspeechid }, null, null, null, null);
		    	    if (cursor3 != null) {
		    		    cursor3.moveToFirst();
		    		    String partofspeech = cursor3.getString(0);
		    		    wordtypes.add(partofspeech);
		    	    }
		    	    	
		        } while (cursor2.moveToNext());
		    }
		    	    
		    Word word = new Word(text2, syllables, wordtypes);
		    return word;
	    }
	    else {
	    	return null;
	    }
	}
}
