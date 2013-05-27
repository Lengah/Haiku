package haiku.top.model;

import haiku.top.HaikuActivity;
import haiku.top.view.BinView;
import haiku.top.view.ConversationObjectView;
import haiku.top.view.DateView;
import haiku.top.view.MainView;
import haiku.top.view.SMSObjectView;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class HaikuGenerator {
	private static BufferedReader readerTheme;
	private static ArrayList<String> smsLog = new ArrayList<String>();
	private static ArrayList<Haiku> generatedHaikus = new ArrayList<Haiku>();
	private static ArrayList<Theme> themes = new ArrayList<Theme>();
	private static ArrayList<Integer> thread_ids = new ArrayList<Integer>(); // All complete conversations added
	private static ArrayList<SMS> smses = new ArrayList<SMS>();
	private static ArrayList<YearMonth> dates = new ArrayList<YearMonth>();
	private static ArrayList<Theme> allThemes = new ArrayList<Theme>();
	private static ArrayList<Word> smsLogWords = new ArrayList<Word>();
	
	/**
	 *  A test. Contains the words that wasn't found 
	 */
	private static ArrayList<String> wordsNotFound = new ArrayList<String>();
	
	public static void addTheme(Theme theme){
		themes.add(theme);
		BinView.getInstance().addTheme(theme);
	}
	
	public static void removeTheme(Theme theme){
		themes.remove(theme);
		MainView.getInstance().updateThemeView();
	}
	
	public static ArrayList<Theme> getThemes(){
		return themes;
	}
	
	public static void addThread(int threadID){
		thread_ids.add(threadID);
		Cursor cursor = HaikuActivity.getThread(MainView.getInstance().getContext(), threadID);
		ArrayList<SMS> threadSMS = new ArrayList<SMS>();
		if (cursor.moveToFirst()) {
			do{
				threadSMS.add(new SMS(cursor.getInt(cursor.getColumnIndexOrThrow("_id")), cursor.getString(cursor.getColumnIndexOrThrow("body")), cursor.getString(cursor.getColumnIndexOrThrow("date")), threadID));
			}
			while(cursor.moveToNext());
		}
		for(int i = 0; i < threadSMS.size(); i++){
			if(!smses.contains(threadSMS.get(i))){
				addSMS(threadSMS.get(i));
			}
		}
	}
	
	public static void removeThread(int threadID){
		Integer thread_id = threadID;
		if(thread_ids.remove(thread_id)){
			MainView.getInstance().updateConversations();
		}
	}
	
	public static ArrayList<Integer> getThreadIDs(){
		return thread_ids; 
	}
	
	public static void addSMS(SMS sms){
		smses.add(sms);
		BinView.getInstance().addSMS(sms);
	}
	
	public static void removeSMS(SMS sms){
		smses.remove(sms);
		MainView.getInstance().updateSMSView();
		removeThread((int)sms.getContactID()); //the contact is only saved if ALL smses of that contact is added. So if one is taken away, so is the contact.
	}
	
	public static ArrayList<SMS> getAllAddedSMS(){
		return smses;
	}
	
	public static void addYear(int year){
		YearMonth ym;
		for(int i = 0; i < DateView.MONTHS_NAME.length; i++){
			ym = new YearMonth(year, DateView.MONTHS_NAME[i]);
			if(!dates.contains(ym)){
				dates.add(ym);
				BinView.getInstance().addDate(ym);
			}
		}
		Uri uri = Uri.parse(HaikuActivity.ALLBOXES);
		Cursor cursor = MainView.getInstance().getContext().getContentResolver().query(uri, null, null, null, null);
		SMS tempSMS;
		if (cursor.moveToFirst()) {
			do{
				tempSMS = new SMS(cursor.getInt(cursor.getColumnIndexOrThrow("_id")), 
						cursor.getString(cursor.getColumnIndexOrThrow("body")), 
						cursor.getString(cursor.getColumnIndexOrThrow("date")), 
						cursor.getLong(cursor.getColumnIndexOrThrow("thread_id")));
				if(!smses.contains(tempSMS) && tempSMS.getYear() == year){
					addSMS(tempSMS);
				}
			}
			while(cursor.moveToNext());
		}
	}
	
	public static void addDate(YearMonth date){
		dates.add(date);
		BinView.getInstance().addDate(date);
		Uri uri = Uri.parse(HaikuActivity.ALLBOXES);
		Cursor cursor = MainView.getInstance().getContext().getContentResolver().query(uri, null, null, null, null);
		SMS tempSMS;
		if (cursor.moveToFirst()) {
			do{
				tempSMS = new SMS(cursor.getInt(cursor.getColumnIndexOrThrow("_id")), 
						cursor.getString(cursor.getColumnIndexOrThrow("body")), 
						cursor.getString(cursor.getColumnIndexOrThrow("date")), 
						cursor.getLong(cursor.getColumnIndexOrThrow("thread_id")));
				if(!smses.contains(tempSMS) && tempSMS.getYearMonth().equals(date)){
					addSMS(tempSMS);
				}
			}
			while(cursor.moveToNext());
		}
		
	}
	
	public static ArrayList<SMS> removeDate(YearMonth date){
		ArrayList<SMS> removedSMS = new ArrayList<SMS>();
		dates.remove(date);
		for(int i = smses.size()-1; i >= 0; i--){
			if(smses.get(i).getYearMonth().equals(date)){
				removedSMS.add(smses.get(i));
				smses.remove(i);
			}
		}
		return removedSMS;
	}
	
	public static ArrayList<YearMonth> getDates(){
		return dates;
	}
	
	public static ArrayList<Theme> getAllThemes(){
		return allThemes;
	}
	
	public void initThemes(){
		//TODO hämta alla themes från databasen och lägg in dom i arraylisten allThemes.
	}
		
	public static void createHaiku(){
		Haiku haiku = new Haiku();
		haiku.generate();
		generatedHaikus.add(haiku);
	}
	
	public static Haiku getHaiku(int index){
		return generatedHaikus.get(index);
	}
	
	public static Haiku getNewestHaiku(){
		return generatedHaikus.get(generatedHaikus.size()-1);
	}
	
	public static void updateLogs(){
		smsLogWords.clear();
		Word tempWord;
		String textMessage;
		String word;
		int pos1;
		int pos2;
		for(int i = 0; i < smsLog.size(); i++){
			textMessage = smsLog.get(i).toLowerCase();
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
				     && textMessage.charAt(pos1) != '\''){
					pos1++;
					if(pos1 > textMessage.length()){
						break;
					}
				}
				if(pos1 > textMessage.length()){
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
				     || textMessage.charAt(pos1) == '\''){
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
				tempWord = lookUpWord(word);
				if(tempWord != null){
					boolean exists = false;
					for(int w = 0; w < smsLogWords.size(); w++){
						if(smsLogWords.get(i).equals(word)){
							exists = true;
							break;
						}
					}
					if(!exists){
						smsLogWords.add(tempWord);
//						Log.i("TAG", "The word " + word + " was added");
					}
					else{
						Log.i("TAG", "The word " + word + " was already added");
					}
				}
				else{
					Log.i("TAG", "The word " + word + " didn't exist");
				}
				if(pos2+1 <= textMessage.length()){
					textMessage = textMessage.substring(pos2+1);
				}
				else{
					break;
				}
			}
		}
		Log.i("TAG", "Number of words found: " + smsLogWords.size());
	}
	
	/**
	 * Returns the word with its info (word, syllables, part-of-speech).
	 * If the word isn't found it returns null.
	 * @param word - just the word (not syllables or part-of-speech)
	 * @return - A Word object (word, syllables, part-of-speech) or null if doesn't
	 * find the word
	 */
	public static Word lookUpWord(String word){
		try {
			if(word.length() == 0){
				return null;
			}
			word = word.toLowerCase();
			String text;
			String wordText;
			Word returnWord = null;
			while ((text = readerTheme.readLine()) != null) { // or until the word is found
				if((wordText = text.substring(0,text.indexOf('|'))).equals(word)){
					text = text.substring(text.indexOf('|')+1);
//					returnWord = new Word(wordText, text.substring(0, text.indexOf('|')),text.substring(text.lastIndexOf('|')+1));
					break;
				}
			}
			//TODO close the stream?
			if(returnWord == null){ // used to debug
				wordsNotFound.add(word);
			}
			return returnWord;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		wordsNotFound.add(word);
		return null; // If something went wrong
	}
	
	private static Random randomGenerator = new Random();
	private static int index;
	
	/**
	 * 
	 * @return A random word with the right part-of-speech(es)
	 */
	public static Word getWord(String wordTypes){
		ArrayList<Word> words = new ArrayList<Word>();
		ArrayList<Character> types = new ArrayList<Character>();
		while(wordTypes.length() > 0){
			types.add(wordTypes.charAt(0));
			wordTypes = wordTypes.substring(1);
		}
		boolean exists;
		for(int i = 0; i < smsLogWords.size(); i++){
			exists = true;
			for(int t = 0; t < types.size(); t++){
				if(types.get(t) == 'U' && smsLogWords.get(i).getwordTypes().indexOf('p') != -1){
					// needs to be a singular noun, but the word is a plural noun
					exists = false;
					break;
				}
				if(types.get(t) == 'J' && smsLogWords.get(i).getwordTypes().indexOf('s') != -1){
					// needs to be a plural verb, but the word is a singular verb
					exists = false;
					break;
				}
				if(types.get(t) != 'U' && types.get(t) != 'J'  && smsLogWords.get(i).getwordTypes().indexOf(types.get(t)) == -1){
					// U and J does not exist in the dictionaries
					exists = false;
					break;
				}
			}
			if(exists){
				words.add(smsLogWords.get(i));
			}
		}
		if(words.isEmpty()){
			// finns inget sådant ord
			return null;
		}
		index = randomGenerator.nextInt(words.size());
		return words.get(index);
	}
	
	/**
	 * Used by getPartOfSentence
	 * @param structure - How the sentence looks
	 * @return
	 */
	public static String getSentence(String structure){
		Log.i("TAG", structure);
		ArrayList<String> parts = new ArrayList<String>();
		String returnString = "";
		String tempString;
		int index;
		
		while(structure.contains("<") || structure.contains("(") || structure.contains("[")){
			if(structure.indexOf('<') == -1 && structure.indexOf("[") == -1){
				index = structure.indexOf(")");
			}
			else if(structure.indexOf('(') == -1 && structure.indexOf("[") == -1){
				index = structure.indexOf(">");
			}
			else if(structure.indexOf('(') == -1 && structure.indexOf('<') == -1){
				index = structure.indexOf("]");
			}
			else if(structure.indexOf('<') == -1){
				index = Math.min(structure.indexOf(")"), structure.indexOf("]"));
			}
			else if(structure.indexOf('(') == -1){
				index = Math.min(structure.indexOf(">"), structure.indexOf("]"));
			}
			else if(structure.indexOf("[") == -1){
				index = Math.min(structure.indexOf(")"), structure.indexOf(">"));
			}
			else{
				index = Math.min(structure.indexOf(")"), Math.min(structure.indexOf(">") ,structure.indexOf("]")));
			}
			tempString = structure.substring(0, index+1);
			structure = structure.substring(index+1);
			parts.add(getPartOfSentence(tempString));
		}
		returnString = parts.get(0);
		for(int i = 1; i < parts.size(); i++){
			returnString = returnString + " " + parts.get(i);
		}
		return returnString;
}
	
	/**
	 * Only one(!) object surrounded by (), <> or []
	 * @param structure
	 * @return
	 */
	public static String getPartOfSentence(String structure){
		if(structure.contains("<")){
			try {
				// needs to read from text file
				InputStream rules = HaikuActivity.getInstance().getAssets().open("rules.txt");
				BufferedReader reader = new BufferedReader(new InputStreamReader(rules));
				String tempText;
				while ((tempText = reader.readLine()) != null) {// or until the part is found
					if(tempText.contains(structure + "=")){
						// The right line is found!
						int rows = Integer.parseInt(tempText.substring(tempText.indexOf('=')+1));
						index = randomGenerator.nextInt(rows);
						while(index > 0){
							reader.readLine();
							index--;
						}
						return getSentence(reader.readLine());
					}
				}
				Log.i("TAG", "null in getPartOfSentence()");
				return null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else if(structure.contains("(")){
			String wordTypes = "";
			Word word;
			structure = structure.substring(1, structure.length()-1);
			String temp = structure;
			while(temp.contains(" ")){
				// 2 or more words
				wordTypes = wordTypes + getCharOfWordType(structure.substring(0, structure.indexOf(' ')));
				temp = structure.substring(structure.indexOf(' ')+1);
			}
			// 1 word (left)
			wordTypes = wordTypes + getCharOfWordType(temp);
			if(wordTypes == null || (word = getWord(wordTypes)) == null){
				Log.i("TAG","WordTypes: " + wordTypes);
				return "(" + structure + ")";
			}
			else{
				//ord hittat!
				return word.getText();
			}
		}
		// just a string with the following structure: [the string]	
		return structure.substring(1, structure.length()-1);
	}
	
	/**
	 * A finished sentence does not contain any (,),<, >, [ or ]
	 * @param sentence
	 * @return
	 */
	public static boolean finishedSentence(String sentence){
		if(sentence.contains("(") || sentence.contains(")") || sentence.contains("<") || sentence.contains(">") || sentence.contains("[") || sentence.contains("]")){
			return false;
		}
		return true;
	}
	
	public static char getCharOfWordType(String wordType){
		if(wordType.equals("propernoun")){
			return 'N';
		}
		if(wordType.equals("impropernoun")){
			return 'N';
		}
		if(wordType.equals("noun")){
			return 'N';
		}
		if(wordType.equals("singular-noun")){
			return 'U';
		}
		if(wordType.equals("plural-noun")){
			return 'p';
		}
		if(wordType.equals("conjunction")){
			return 'C';
		}
		if(wordType.equals("pronoun")){
			return 'r';
		}
		if(wordType.equals("positive-adjective")){
			return 'A';
		}
		if(wordType.equals("comparative-adjective")){
			return 'c';
		}
		if(wordType.equals("superlative-adjective")){
			return 'S';
		}
		if(wordType.equals("adverb")){
			return 'v';
		}
		if(wordType.equals("preposition")){
			return 'P';
		}
		if(wordType.equals("intransitiveverb")){
			return 'i';
		}
		if(wordType.equals("transitiveverb")){
			return 't';
		}
		if(wordType.equals("singular-verb")){
			return 's';
		}
		if(wordType.equals("plural-verb")){
			return 'J';
		}
		if(wordType.equals("past-tense-verb")){
			return 'T';
		}
		if(wordType.equals("gerund")){
			return 'G';
		}
		if(wordType.equals("interjection")){
			return '!';
		}
		if(wordType.equals("definitearticle")){
			return 'D';
		}
		Log.i("TAG", "wordType that gives null: " + wordType);
		return (Character) null;
	}
}
