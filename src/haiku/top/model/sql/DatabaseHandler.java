package haiku.top.model.sql;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import haiku.top.model.Contact;
import haiku.top.model.Haiku;
import haiku.top.model.SMS;
import haiku.top.model.SMSBinWord;
import haiku.top.model.Theme;
import haiku.top.model.Word;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
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
		   checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
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
	   myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
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
	
	public String getWordTextFromID(String id) {
		Cursor cursor = myDataBase.query(TABLE_WORD, new String[] { KEY_WORD_TEXT }, KEY_WORD_ID + "=?", new String[] { id }, null, null, null, null);
	    if (cursor != null && cursor.getCount() > 0) { //if word found
		    cursor.moveToFirst();
		    String text = cursor.getString(0);
		    cursor.close();
		    return text;
	    }
		else {
			cursor.close();
	    	return "";
	    }
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
		    	    cursor3.close();
		    	    	
		        } while (cursor2.moveToNext());
		    }
		    cursor2.close();
		    
		    //get themes
		    ArrayList<String> themes = new ArrayList<String>();
		    Cursor cursor4 = myDataBase.rawQuery("SELECT " + KEY_THEMEWORD_THEMEID + " FROM " + TABLE_THEMEWORD + " WHERE " + KEY_THEMEWORD_WORDID + " = " + id  + ";", null);
		    if (cursor4.moveToFirst()) {
		        do {
		        	String themeid = cursor4.getString(0);
		    	    Cursor cursor5 = myDataBase.query(TABLE_THEME, new String[] { KEY_THEME_NAME }, KEY_THEME_ID + "=?", new String[] { themeid }, null, null, null, null);
		    	    if (cursor5 != null) {
		    		    cursor5.moveToFirst();
		    		    String theme = cursor5.getString(0);
		    		    	themes.add(theme);
		    	    }
		    	    cursor5.close();
		    	    	
		        } while (cursor4.moveToNext());
		    }
		    cursor4.close();
		    	    
		    Word word = new Word(text2, syllables, wordtypes, themes);
		    cursor.close();
		    return word;
	    }
	    else {
	    	return null;
	    }

	    
	}
	
	public ArrayList<String> getWordsInSMS(String smsID) {
		ArrayList<String> words = new ArrayList<String>();
		Cursor cursor = myDataBase.rawQuery("SELECT " + KEY_SMSWORD_WORDID + " FROM " + TABLE_SMSWORD + " WHERE " + KEY_SMSWORD_SMSID + " = " + smsID  + ";", null);
	    if (cursor.moveToFirst()) {
	        do {
	        	words.add(cursor.getString(0));
	        } while (cursor.moveToNext());
	    }
	    cursor.close();
	    return words;
	}
	
	public ArrayList<Theme> getAllThemes() {
		ArrayList<Theme> themes = new ArrayList<Theme>();
		Cursor cursor = myDataBase.rawQuery("SELECT * FROM " + TABLE_THEME + ";", null);
	    if (cursor.moveToFirst()) {
	        do {
	        	String id = cursor.getString(0);
	        	String themeName = cursor.getString(1);
    		    if (!themeName.equals("all"))
    		    	themes.add(new Theme(Long.parseLong(id), themeName));
	        } while (cursor.moveToNext());
	    }
	    cursor.close();
		return themes;
	}
	
	public void setupSMSTables() { 
		//see what words (that exist in dictionary) that are in phone's SMS, add to "SMS_Word" table
		//TODO
		//see what "coherent" sentences that can be found ins SMSes, add to "Sentence" table. Also connect contained words with dictionary (Word table), add to "WordInSentence" table
		//see what "coherent" sentences that can be associated with a theme, add to "Sentence_Theme" table
		
		//done att app start, overwrite old content?
		
		Cursor cursor = myContext.getContentResolver().query(Uri.parse("content://sms"), null, null, null, null);
        
		//test
		int smsEntriesCount = cursor.getCount();
		Log.i("smsEntriesCount", "" + smsEntriesCount);
		
	    try {
	        if (cursor.moveToFirst()) {
	            do {
	        		ArrayList<String> words = new ArrayList<String>();
	                String smsid = cursor.getString(cursor.getColumnIndexOrThrow("_id")).toString();
	                String smsbody = cursor.getString(cursor.getColumnIndexOrThrow("body")).toString();
	                
	        		// not just "real" words -> can't use the database because the database only contain "real" words
	        		String textMessage = smsbody.toLowerCase();
	        		String word;
	        		int pos1;
	        		int pos2;
	        		while(textMessage.length() > 0){
	        			// Remove symbols from the start
	        			pos1 = 0;
	        			while(textMessage.charAt(pos1) != 'a' && textMessage.charAt(pos1) != 'b' && textMessage.charAt(pos1) != 'c' && textMessage.charAt(pos1) != 'd'
	        				 && textMessage.charAt(pos1) != 'e' && textMessage.charAt(pos1) != 'f' && textMessage.charAt(pos1) != 'g' && textMessage.charAt(pos1) != 'h'
	        				 && textMessage.charAt(pos1) != 'i' && textMessage.charAt(pos1) != 'j' && textMessage.charAt(pos1) != 'k' && textMessage.charAt(pos1) != 'l'
	        				 && textMessage.charAt(pos1) != 'm' && textMessage.charAt(pos1) != 'n' && textMessage.charAt(pos1) != 'o' && textMessage.charAt(pos1) != 'p'
	        				 && textMessage.charAt(pos1) != 'q' && textMessage.charAt(pos1) != 'r' && textMessage.charAt(pos1) != 's' && textMessage.charAt(pos1) != 't'
	        				 && textMessage.charAt(pos1) != 'u' && textMessage.charAt(pos1) != 'v' && textMessage.charAt(pos1) != 'w' && textMessage.charAt(pos1) != 'x'
	        			     && textMessage.charAt(pos1) != 'y' && textMessage.charAt(pos1) != 'z' && textMessage.charAt(pos1) != 'é' && textMessage.charAt(pos1) != 'è'
	        			     && textMessage.charAt(pos1) != 'å' && textMessage.charAt(pos1) != 'ä' && textMessage.charAt(pos1) != 'ö' && textMessage.charAt(pos1) != '\''){
	        				pos1++;
	        				if(pos1 >= textMessage.length()){
	        					break;
	        				}
	        			}
	        			if(pos1 >= textMessage.length()){
	        				break; // just a bunch of symbols left of the message
	        			}
	        			// find the end of the word
	        			pos2 = pos1;
	        			while(textMessage.charAt(pos2) == 'a' || textMessage.charAt(pos2) == 'b' || textMessage.charAt(pos2) == 'c' || textMessage.charAt(pos2) == 'd'
	        				 || textMessage.charAt(pos2) == 'e' || textMessage.charAt(pos2) == 'f' || textMessage.charAt(pos2) == 'g' || textMessage.charAt(pos2) == 'h'
	        				 || textMessage.charAt(pos2) == 'i' || textMessage.charAt(pos2) == 'j' || textMessage.charAt(pos2) == 'k' || textMessage.charAt(pos2) == 'l'
	        				 || textMessage.charAt(pos2) == 'm' || textMessage.charAt(pos2) == 'n' || textMessage.charAt(pos2) == 'o' || textMessage.charAt(pos2) == 'p'
	        				 || textMessage.charAt(pos2) == 'q' || textMessage.charAt(pos2) == 'r' || textMessage.charAt(pos2) == 's' || textMessage.charAt(pos2) == 't'
	        				 || textMessage.charAt(pos2) == 'u' || textMessage.charAt(pos2) == 'v' || textMessage.charAt(pos2) == 'w' || textMessage.charAt(pos2) == 'x'
	        			     || textMessage.charAt(pos2) == 'y' || textMessage.charAt(pos2) == 'z' || textMessage.charAt(pos1) == 'é' || textMessage.charAt(pos1) == 'è'
	        			     || textMessage.charAt(pos1) == 'å' || textMessage.charAt(pos1) == 'ä' || textMessage.charAt(pos1) == 'ö' || textMessage.charAt(pos1) == '\''){
	        				pos2++;
	        				if(pos1+pos2 >= textMessage.length()){
	        					break;
	        				}
	        			}
	        			// a word is found between indexes pos1 and pos2
	        			word = textMessage.substring(pos1, pos2);
	        			if(word.length() == 0){
	        				break;
	        			}
	        			words.add(word);
	        			if(pos2+1 <= textMessage.length())
	        				textMessage = textMessage.substring(pos2+1);
	        			else
	        				break;
	        		}
	        		
	        		ArrayList<String> wordids = new ArrayList<String>();
	        		for (String w : words) { //get word ids for those that exist in dictionary
		        	    Cursor cursor2 = myDataBase.query(TABLE_WORD, new String[] { KEY_WORD_ID, }, KEY_WORD_TEXT + "=?",
		        	            new String[] { w }, null, null, null, null);
		        	    if (cursor2 != null && cursor2.getCount() > 0) { //if word found
		        		    cursor2.moveToFirst();
		        		    wordids.add(cursor2.getString(0));
		        		}
		        	    cursor2.close();
	        		}

	        		for (String wid : wordids) {
		    		   ContentValues values = new ContentValues();
		    		   values.put(KEY_SMSWORD_SMSID, smsid); //the sms the word was found in
		    		   values.put(KEY_SMSWORD_WORDID, wid);  //the word
		    		   myDataBase.insert(TABLE_SMSWORD, null, values);
	        		}
	        		
	        	Log.i("setupSMSTables", smsid);
	            } while (cursor.moveToNext());
	        }
	    } catch (Exception e) {} finally { cursor.close();}
	    cursor.close();
	}
}
