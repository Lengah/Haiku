package haiku.top.model.sql;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;

import haiku.top.HaikuActivity;
import haiku.top.model.PartOfSpeech;
import haiku.top.model.Theme;
import haiku.top.model.Word;
import haiku.top.model.generator.HaikuGenerator;
import haiku.top.model.smshandler.SMS;
import haiku.top.view.main.MainView;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.TextView;

public class DatabaseHandler extends SQLiteOpenHelper{
	private static DatabaseHandler dh;
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
    private static final String TABLE_PARTOFSPEECH = "partofspeech";
    private static final String TABLE_WORD = "word";
    private static final String TABLE_THEME = "theme";
    private static final String TABLE_THEMEWORD = "theme_word";

    // Column names
    private static final String KEY_PARTOFSPEECH_ID = "_id"; //INTEGER PRIMARY KEY
    private static final String KEY_PARTOFSPEECH_TYPE = "type"; //TEXT
    
    private static final String KEY_WORD_ID = "_id"; //INTEGER PRIMARY KEY
    private static final String KEY_WORD_TEXT = "text"; //TEXT
    private static final String KEY_WORD_SYLLABLES = "syllables"; //TEXT
    private static final String KEY_WORD_PARTOFSPEECHID = "partofspeechid";
    private static final String KEY_WORD_CUEWORDSIDS = "cuewords";

    private static final String KEY_THEME_ID = "_id"; //INTEGER PRIMARY KEY
    private static final String KEY_THEME_NAME = "name"; //TEXT

    private static final String KEY_THEMEWORD_THEMEID = "themeid"; //INTEGER, Foreign key theme(_id)
    private static final String KEY_THEMEWORD_WORDID = "wordid"; //INTEGER, Foreign key word(_id)

//    public void test(){
//    	Cursor cursor = myDataBase.query(TABLE_WORD, null, null, null, null, null, null);
//    	Log.i("TAG", "" + cursor.getCount());
//    	cursor.close();
//    }
    
    //test 30/1/2014
//    public void rebuildWordTable(){
//    	double startTime = System.currentTimeMillis();
//    	Log.i("TAG", "Updating the database");
//    	myDataBase.execSQL("DROP TABLE IF EXISTS " + TABLE_WORD);
//    	Log.i("TAG", "creating new");
//    	myDataBase.execSQL("CREATE TABLE " + TABLE_WORD + " (" 
//    			+ KEY_WORD_ID + " INTEGER not NULL, "
//    			+ KEY_WORD_TEXT + " VARCHAR(255), "
//    			+ KEY_WORD_SYLLABLES + " VARCHAR(255), "
//    			+ KEY_WORD_PARTOFSPEECHID + " INTEGER not NULL, "
//    			+ KEY_WORD_CUEWORDSIDS + " VARCHAR(255))");
//    	
//    	Log.i("TAG", "created");
//		try {
//			InputStream rules = HaikuActivity.getInstance().getAssets().open("deletebyhaiku_db_sql_word_dictionary_full_updated2_added_words_and_cue_words.txt");
//			BufferedReader reader = new BufferedReader(new InputStreamReader(rules));
////			File sqlWords = new File("assets/deletebyhaiku_db_sql_word_dictionary_full_updated2_added_words_and_cue_words.txt");
////			BufferedReader reader = new BufferedReader(new FileReader(sqlWords));
//			int rows = 170329;
//			int c = 0;
//			int currentP = -1;
//			int temp;
//			String text;
//			while ((text = reader.readLine()) != null) {
//				c++;
//				temp = c*100/rows;
//				if(temp != currentP){
//					Log.i("TAG", temp + "% done.");
//					currentP = temp;
//				}
//				myDataBase.execSQL(text);
//			}
//			
//		} catch (Exception e) {
//			Log.i("TAG", "exception: " + e);
//			e.printStackTrace();
//		}
//		double execTime = System.currentTimeMillis()-startTime;
//		int seconds = (int)execTime/1000;
//		int minutes = seconds/60;
//		seconds = seconds%60;
//		int hours = minutes/60;
//		minutes = minutes%60;
//		Log.i("TAG", "Time (HH:MM:SS): " + hours + ":" + minutes + ":" + seconds);
//    }
    
    /**
     * Creates a empty database on the system and rewrites it with your own database.
     * */
   public void createDataBase() throws IOException {
	   boolean dbExist = checkDataBase();    
	   if(dbExist){
		   //do nothing - database already exist
	   }
	   else {  
		   //By calling this method an empty database will be created into the default system path
		   //of your application so we are going to be able to overwrite that database with our database.
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
//	   Log.i("TAG", "copyDataBase");
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
   
   public static DatabaseHandler getInstance(){
	   if(dh == null){
		   dh = new DatabaseHandler(MainView.getInstance().getContext());
		   dh.openReadOnlyDataBase();
	   }
	   return dh;
   }
   
//   public SQLiteDatabase getDatabase(){
//	   if(myDataBase == null){
//		   openReadOnlyDataBase();
//	   }
//	   return myDataBase;
//   }
    
   public void openDataBase() throws SQLException {
	   //Open the database
	   String myPath = DB_PATH + DB_NAME;
	   myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
   }
   
   private void openReadOnlyDataBase() throws SQLException {
	   //Open the database
	   String myPath = DB_PATH + DB_NAME;
	   myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
   }
    
   @Override
   public synchronized void close() {   
	   if(myDataBase != null)
//		   Log.i("TAG", "CLOSE DB!!!!!!!!!!!!!!!");
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
		Cursor cursor = null;
		try{
			cursor = myDataBase.query(TABLE_WORD, new String[] { KEY_WORD_TEXT }, KEY_WORD_ID + "=?", new String[] { id }, null, null, null, null);
		    if (cursor != null && cursor.getCount() > 0) { //if word found
			    cursor.moveToFirst();
			    String text = cursor.getString(0);
			    return text;
		    }
			else {
		    	return "";
		    }
		}
		finally{
			cursor.close();
		}
	}
	
	public ArrayList<PartOfSpeech> getAllPartOfSpeeches(){
		Cursor cursor = null;
		try{
			ArrayList<PartOfSpeech> partOfSpeeches = new ArrayList<PartOfSpeech>();
			cursor = myDataBase.query(TABLE_PARTOFSPEECH, new String[] {KEY_PARTOFSPEECH_ID, KEY_PARTOFSPEECH_TYPE }, null, null, null, null, null, null);
			if(cursor.moveToFirst()){
		    	long id;
		    	String type;
		    	do {
		    		id = cursor.getLong(0);
		    		type = cursor.getString(1);
		    		partOfSpeeches.add(new PartOfSpeech(id, type));
		    	}while(cursor.moveToNext());
		    }
			return partOfSpeeches;
		}
		finally{
			cursor.close();
		}
	}
	
	public ArrayList<Long> getIDs(String idsString){
		ArrayList<Long> ra = new ArrayList<Long>();
		int index;
		while(idsString.contains(".")){
			idsString = idsString.substring(1); // remove the first dot
			index = idsString.indexOf(".");
			if(index == -1){
				ra.add(Long.parseLong(idsString));
				break;
			}
			else{
				ra.add(Long.parseLong(idsString.substring(0, index)));
				idsString = idsString.substring(index);
			}
		}
		return ra;
	}
	
	//TODO causes massive lag when many (1000+) words are added simultaneously
	public ArrayList<Word> getWords(ArrayList<String> texts){
		int textCounter = 0;
		int maxQuery = 500; // cap the query at this number. If the query is too big, the program will crash.
		ArrayList<Word> words = new ArrayList<Word>();
//		double startTime = System.currentTimeMillis();
//		Log.i("TAG", "getWords(): check " + texts.size() + " words");
		while(textCounter < texts.size()){
			String selection = "";
			selection += KEY_WORD_TEXT + " IN ( ?";
			for(int i = textCounter + 1; i < texts.size() && i < maxQuery + textCounter; i++){ // Can not call the database with unlimited amount of words! It will crash if to many are used.
				selection += ", ?";
			}
			selection += ")";
			String[] selectionArg = new String[Math.min(texts.size() - textCounter, maxQuery)];
//			Log.i("TAG", "getWords() inner: check " + selectionArg.length + " words");
			for(int i = 0; i < selectionArg.length; i++){
				selectionArg[i] = texts.get(textCounter + i);
			}
			textCounter += selectionArg.length;
			Cursor cursor = null;
			try{
			    cursor = myDataBase.query(TABLE_WORD, new String[] { KEY_WORD_ID, KEY_WORD_TEXT, KEY_WORD_SYLLABLES,  KEY_WORD_PARTOFSPEECHID, KEY_WORD_CUEWORDSIDS}, selection, selectionArg, null, null, null, null);
			    
			    if(cursor.moveToFirst()){
			    	long id;
			    	String syllables;
			    	String text;
			    	long partOfSpeechID;
			    	do {
			    		id = cursor.getLong(cursor.getColumnIndexOrThrow(KEY_WORD_ID));
			    		text = cursor.getString(cursor.getColumnIndexOrThrow(KEY_WORD_SYLLABLES)); // TODO text and syllables are swapped for some reason (there is a test a few rows down)
			    		syllables = cursor.getString(cursor.getColumnIndexOrThrow(KEY_WORD_TEXT));
			    		partOfSpeechID = cursor.getLong(cursor.getColumnIndexOrThrow(KEY_WORD_PARTOFSPEECHID));
			    		words.add(new Word(id, syllables, text, HaikuGenerator.getPartOfSpeechWithID(partOfSpeechID).getType(), getIDs(cursor.getString(cursor.getColumnIndexOrThrow(KEY_WORD_CUEWORDSIDS)))));
			    	}while(cursor.moveToNext());
			    }
			}
			finally{
				cursor.close();
			}
		}
//	    Log.i("TAG", "getWords(): Getting all the words: " + (System.currentTimeMillis() - startTime));
//		Log.i("TAG3", "Number of words not found: " + (texts.size()-words.size()));
//		Log.i("TAG3", "Words to look for");
//		for(int i = 0; i < texts.size(); i++){
//			Log.i("TAG3", texts.get(i));
//		}
//		Log.i("TAG3", "--------------------------");
//		Log.i("TAG3", "Words found");
//		for(int i = 0; i < words.size(); i++){
//			Log.i("TAG3", words.get(i).getText() + ", " + words.get(i).getwordType());
//		}
//		Log.i("TAG3", "--------------------------");
//		Log.i("TAG3", "Words not found");
//		boolean found;
//		for(int t = 0; t < texts.size(); t++){
//			found = false;
//			for(int i = 0; i < words.size(); i++){
//				if(texts.get(t).equalsIgnoreCase(words.get(i).getText())){
//					found = true;
//					break;
//				}
//			}
//			if(!found){
//				Log.i("TAG3", texts.get(t));
//			}
//		}
//		Log.i("TAG3", "--------------------------");
//		Log.i("TAG3", "--------------------------");
//		Log.i("TAG3", "--------------------------");
//		Log.i("TAG3", "--------------------------");
//		Log.i("TAG3", "--------------------------");
//		Log.i("TAG3", "--------------------------");
//		Log.i("TAG3", "--------------------------");
//		Log.i("TAG3", "--------------------------");
//		Log.i("TAG3", "--------------------------");
//		Log.i("TAG3", "--------------------------");
	    return words;
	}
	
	public ArrayList<Long> initTheme(Theme t) {
		Cursor cursor = null;
		try{
			ArrayList<Long> wordids = new ArrayList<Long>();
			
		    cursor = myDataBase.rawQuery("SELECT " + KEY_THEMEWORD_WORDID + " FROM " + TABLE_THEMEWORD + " WHERE " + KEY_THEMEWORD_THEMEID + " = " + t.getID() + ";", null);
		    if (cursor.moveToFirst()) {
		        do {
		    		wordids.add(cursor.getLong(0));
		        } while (cursor.moveToNext());
		    }
			return wordids;
		}
		finally{
			cursor.close();
		}
	}
	
	public Theme getTheAllTheme() {
		Cursor cursor = null;
		try{
		    cursor = myDataBase.query(TABLE_THEME, new String[] { KEY_THEME_ID }, KEY_THEME_NAME + "=?", new String[] { "all" }, null, null, null, null);
	    	cursor.moveToFirst();
	    	long id = cursor.getLong(0);
	    	return new Theme(id, "all");
		}
		finally{
			cursor.close();
		}
	}
	
	public ArrayList<Theme> getAllThemes() {
		Cursor cursor = null;
		try{
			ArrayList<Theme> themes = new ArrayList<Theme>();
			cursor = myDataBase.rawQuery("SELECT * FROM " + TABLE_THEME + ";", null);
		    if (cursor.moveToFirst()) {
		        do {
		        	String id = cursor.getString(0);
		        	String themeName = cursor.getString(1);
	    		    if (!themeName.equals("all"))
	    		    	themes.add(new Theme(Long.parseLong(id), themeName));
		        } while (cursor.moveToNext());
		    }
			return themes;
		}
		finally{
			cursor.close();
		}
	}
	
	public void initSMSES(ArrayList<SMS> smses){
//		double startTime = System.currentTimeMillis();
		ArrayList<String> wordsToFind = new ArrayList<String>();
		ArrayList<String> tempList;
		for(int i = 0; i < smses.size(); i++){
			tempList = smses.get(i).getNotRealWords();
			for(int a = 0; a < tempList.size(); a++){
				if(!wordsToFind.contains(tempList.get(a))){
					wordsToFind.add(tempList.get(a));
				}
			}
		}
		ArrayList<Word> allUniqueWordsInAllSmses = getWords(wordsToFind);
		for(int i = 0; i < allUniqueWordsInAllSmses.size(); i++){
			for(int a = 0; a < smses.size(); a++){
				smses.get(a).addWord(null); // creates the arraylist so that the program knows that the sms has been initiated even if there are no words in the sms
				for(int b = 0; b < smses.get(a).getNotRealWords().size(); b++){
					if(smses.get(a).getNotRealWords().get(b).equals(allUniqueWordsInAllSmses.get(i).getText())){
						smses.get(a).addWord(allUniqueWordsInAllSmses.get(i)); // Can have several instances of the same word (that word will have a bigger chance of being picked for the haiku)
					}
				}
			}
		}
//		Log.i("TAG", "initSMSES(): finding all the real words in the smses: " + (System.currentTimeMillis()-startTime));
	}

	public ArrayList<Word> initSMS(SMS sms) {
//		double startTime = System.currentTimeMillis();
		ArrayList<String> words = sms.getNotRealWords();
		ArrayList<String> wordsUnique = new ArrayList<String>(); // doing this so the sql request goes faster
		for(int i = 0; i < words.size(); i++){
			if(!wordsUnique.contains(words.get(i))){
				wordsUnique.add(words.get(i));
			}
		}
		ArrayList<Word> realWordsUnique = getWords(wordsUnique);
		ArrayList<Word> realWords = new ArrayList<Word>();
		for(int i = 0; i < realWordsUnique.size(); i++){
			for(int a = 0; a < sms.getNotRealWords().size(); a++){
				if(sms.getNotRealWords().get(a).equals(realWordsUnique.get(i).getText())){
					realWords.add(realWordsUnique.get(i)); // Can have several instances of the same word (that word will have a bigger chance of being picked for the haiku)
				}
			}
		}
//		Log.i("TAG", "initSMS(): finding all the real words from the strings: " + (System.currentTimeMillis()-startTime));
		return realWords;
	}
	

	
/*	public void setupSMSTables() { 
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
	        			     && textMessage.charAt(pos1) != 'y' && textMessage.charAt(pos1) != 'z' && textMessage.charAt(pos1) != '�' && textMessage.charAt(pos1) != '�'
	        			     && textMessage.charAt(pos1) != '�' && textMessage.charAt(pos1) != '�' && textMessage.charAt(pos1) != '�' && textMessage.charAt(pos1) != '\''){
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
	        			     || textMessage.charAt(pos2) == 'y' || textMessage.charAt(pos2) == 'z' || textMessage.charAt(pos1) == '�' || textMessage.charAt(pos1) == '�'
	        			     || textMessage.charAt(pos1) == '�' || textMessage.charAt(pos1) == '�' || textMessage.charAt(pos1) == '�' || textMessage.charAt(pos1) == '\''){
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
	}*/
}
