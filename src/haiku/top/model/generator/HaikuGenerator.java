package haiku.top.model.generator;

import haiku.top.HaikuActivity;
import haiku.top.model.PartOfSpeech;
import haiku.top.model.Theme;
import haiku.top.model.Word;
import haiku.top.model.WordAndNumber;
import haiku.top.model.date.Month;
import haiku.top.model.date.YearMonth;
import haiku.top.model.date.YearMonthConvo;
import haiku.top.model.smshandler.AddSmsThread;
import haiku.top.model.smshandler.AddSmsesThread;
import haiku.top.model.smshandler.FindValidSMSWithTheme;
import haiku.top.model.smshandler.SMS;
import haiku.top.model.sql.DatabaseHandler;
import haiku.top.view.binview.BinView;
import haiku.top.view.date.DateView;
import haiku.top.view.main.ConversationObjectView;
import haiku.top.view.main.MainView;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Semaphore;

import android.database.Cursor;
import android.net.Uri;
import android.text.AlteredCharSequence;
import android.util.Log;
import android.widget.Toast;

public class HaikuGenerator {
	public static final int THEME_WORD_MULTIPLIER = 2;
	public static final int MAX_SMS_IN_BIN = 60;
	
//	private static ArrayList<Theme_ThreadID_Tuple> themes = new ArrayList<Theme_ThreadID_Tuple>();
//	private static ArrayList<Theme> themes = new ArrayList<Theme>();
	private static ArrayList<Integer> thread_ids = new ArrayList<Integer>(); // All complete conversations added
	private static ArrayList<SMS> smses = new ArrayList<SMS>();
//	private static ArrayList<YearMonth> dates = new ArrayList<YearMonth>();
//	private static ArrayList<YearMonthConvo> datesFromThreads = new ArrayList<YearMonthConvo>();
	private static ArrayList<Theme> allThemes = new ArrayList<Theme>();
//	private static Theme theAllTheme;
//	private static ArrayList<Word> smsLogWordsWithThemes = new ArrayList<Word>();
//	private static ArrayList<Word> smsLogWordsWithTheAllTheme = new ArrayList<Word>();
	private static ArrayList<Word> allSmsLogWords = new ArrayList<Word>();
//	private static ArrayList<Long> themeWordIDs = new ArrayList<Long>();
//	private static ArrayList<Long> theAllThemeWordIDs = new ArrayList<Long>();
	private static ArrayList<Haiku> haikus = new ArrayList<Haiku>();
	private static final int NUMBER_OF_GENERATIONS = 16;
	private static int generationsCounter;
	
	private static ArrayList<PartOfSpeechList> allWordsOrderedByTypes = new ArrayList<PartOfSpeechList>();
	private static ArrayList<PartOfSpeechList> allWordsOrderedByTypesUpdated = new ArrayList<PartOfSpeechList>();
	
	
	private static ArrayList<ThemeSMS> smsAddedByTheme = new ArrayList<ThemeSMS>();
	private static ArrayList<DateSMS> smsAddedByDate = new ArrayList<DateSMS>();
	
	// for example [the(1)]
//	private static ArrayList<Word> wordsDefinedInRulesTextFile = new ArrayList<Word>();
	
	private static ArrayList<Haiku> haikusRemovedLast = new ArrayList<Haiku>();
	
	private static Semaphore threadSemaphore = new Semaphore(1);
	private static ArrayList<Thread> addingSMSThreadsRunning = new ArrayList<Thread>();
	
	// allSmsLogWords, themes, smsLogWordsWithThemes, themeWordIDs
	private static Semaphore smsSemaphore = new Semaphore(1);
	
	private static boolean inited = false;
	private static ArrayList<Word> smsLogWordsWithThemesRemoved = new ArrayList<Word>();
	private static ArrayList<Word> smsLogWordsWithTheAllThemeRemoved = new ArrayList<Word>();
	private static ArrayList<Word> allSmsLogWordsRemoved = new ArrayList<Word>();
	
	/**
	 * An array of all characters a word can contain
	 */
	public static final Character[] ALL_WORD_CHARACTERS = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 
										'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 
										's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'é', 
										'è', 'å', 'ä', 'ö', '\''}; // the character ' doesn't seem to work
	
	/**
	 * Words in rules.txt which are not words (words in [] brackets)
	 * Such as ',', '?' and '!'
	 */
	private static ArrayList<String> nonWordsInRules = new ArrayList<String>();
	
	private static String[] rules;
	
	private static ArrayList<RuleRow> rule1 = new ArrayList<RuleRow>();
	private static ArrayList<RuleRow> rule2 = new ArrayList<RuleRow>();
	private static ArrayList<RuleRow> rule3 = new ArrayList<RuleRow>();
	
	public static ArrayList<RuleRow> getRule1Copy(){
		return new ArrayList<RuleRow>(rule1);
	}
	
	public static ArrayList<RuleRow> getRule2Copy(){
		return new ArrayList<RuleRow>(rule2);
	}
	
	public static ArrayList<RuleRow> getRule3Copy(){
		return new ArrayList<RuleRow>(rule3);
	}
	
	private static void initRules(){
		try {
			InputStream rules1 = HaikuActivity.getInstance().getAssets().open("rules/row1.txt");
			BufferedReader reader1 = new BufferedReader(new InputStreamReader(rules1));
			
			InputStream rules2 = HaikuActivity.getInstance().getAssets().open("rules/row2.txt");
			BufferedReader reader2 = new BufferedReader(new InputStreamReader(rules2));
			
			InputStream rules3 = HaikuActivity.getInstance().getAssets().open("rules/row3.txt");
			BufferedReader reader3 = new BufferedReader(new InputStreamReader(rules3));
			
			String tempText;
			while ((tempText = reader1.readLine()) != null) {
				rule1.add(new RuleRow(tempText));
			}
			
			while ((tempText = reader2.readLine()) != null) {
				rule2.add(new RuleRow(tempText));
			}
			
			while ((tempText = reader3.readLine()) != null) {
				rule3.add(new RuleRow(tempText));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// old with only 1 set of rules
//		try {
//			InputStream rulesF = HaikuActivity.getInstance().getAssets().open("rules.txt");
//			BufferedReader reader = new BufferedReader(new InputStreamReader(rulesF));
//			ArrayList<String> rowsString = new ArrayList<String>();
//			String tempText;
//			while ((tempText = reader.readLine()) != null) {
//				rowsString.add(tempText);
//			}
//			rules = new String[rowsString.size()];
//			for(int i = 0; i < rowsString.size(); i++){
//				rules[i] = rowsString.get(i);
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}
	
	public static ArrayList<String> getNonWordsInRules(){
		return nonWordsInRules;
	}
	
	private static void initNonWordsCharacters(){
		String temp;
		for(RuleRow rr : rule1){
			for(String s : rr.getRuleStructs()){
				if(s.charAt(0) == '['){
					temp = s.substring(1, s.indexOf(']'));
					if(!nonWordsInRules.contains(temp)){
						nonWordsInRules.add(temp);
					}
				}
			}
		}
		
		for(RuleRow rr : rule2){
			for(String s : rr.getRuleStructs()){
				if(s.charAt(0) == '['){
					temp = s.substring(1, s.indexOf(']'));
					if(!nonWordsInRules.contains(temp)){
						nonWordsInRules.add(temp);
					}
				}
			}
		}
		
		for(RuleRow rr : rule3){
			for(String s : rr.getRuleStructs()){
				if(s.charAt(0) == '['){
					temp = s.substring(1, s.indexOf(']'));
					if(!nonWordsInRules.contains(temp)){
						nonWordsInRules.add(temp);
					}
				}
			}
		}
		
//		int startIndex;
//		int endIndex;
//		String temp;
//		String[] rulesTemp = rules.clone();
//		for(int i = 0; i < rulesTemp.length; i++){
//			while((startIndex = rulesTemp[i].indexOf('[')) != -1){
//				endIndex = rulesTemp[i].indexOf(']');
//				temp = rulesTemp[i].substring(startIndex+1, endIndex);
//				if(!nonWordsInRules.contains(temp)){
//					nonWordsInRules.add(temp);
//				}
//				rulesTemp[i] = rulesTemp[i].substring(endIndex+1);
//			}
//		}
	}
	
	public static String[] getRules(){
		return rules;
	}
	
	public static void resetHaikusRemoved(){
		haikusRemovedLast.clear();
	}
	
	public static ArrayList<SMS> getAllSMS(){
		return smses;
	}
	
	public static boolean isAWordCharacter(char c){
		for(int i = 0; i < ALL_WORD_CHARACTERS.length; i++){
			if(ALL_WORD_CHARACTERS[i].equals(c)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Should be called before a new batch of haikus are generated
	 */
	public static void updateWordsUsed(){
		ArrayList<String> wordsRemoved = BinView.getInstance().getAllWordsRemoved();
		for(int i = 0; i < wordsRemoved.size(); i++){
			for(int a = allSmsLogWords.size() - 1; a >= 0; a--){
				if(wordsRemoved.get(i).equals(allSmsLogWords.get(a).getText())){
					allSmsLogWordsRemoved.add(allSmsLogWords.get(a));
					allSmsLogWords.remove(a);
				}
			}
			for(PartOfSpeechList pl : allWordsOrderedByTypes){
				for(int a = pl.getWords().size()-1; a >= 0; a--){
					if(pl.getWords().get(a).getText().equalsIgnoreCase(wordsRemoved.get(i))){
						smsLogWordsWithThemesRemoved.add(pl.getWords().get(a));
						pl.getWords().remove(a);
					}
				}
			}
//			for(int a = smsLogWordsWithThemes.size() - 1; a >= 0; a--){
//				if(wordsRemoved.get(i).equals(smsLogWordsWithThemes.get(a).getText())){
//					smsLogWordsWithThemesRemoved.add(smsLogWordsWithThemes.get(a));
//					smsLogWordsWithThemes.remove(a);
//				}
//			}
//			for(int a = smsLogWordsWithTheAllTheme.size() - 1; a >= 0; a--){
//				if(wordsRemoved.get(i).equals(smsLogWordsWithTheAllTheme.get(a).getText())){
//					smsLogWordsWithTheAllThemeRemoved.add(smsLogWordsWithTheAllTheme.get(a));
//					smsLogWordsWithTheAllTheme.remove(a);
//				}
//			}
		}
	}
	
	public static void addHaikuRemoved(Haiku h){
		haikusRemovedLast.add(h);
	}
	
	public static void undo(){
		haikus.addAll(haikusRemovedLast);
		resetHaikusRemoved();
	}

	public static void reset() {
//		themes.clear();
		thread_ids.clear();
		smses.clear();
		smsAddedByDate.clear();
		smsAddedByTheme.clear();
//		dates.clear();
//		smsLogWordsWithTheAllTheme.clear();
		for(PartOfSpeechList pl : allWordsOrderedByTypes){
			pl.getWords().clear();
		}
//		smsLogWordsWithThemes.clear();
		allSmsLogWords.clear();
		haikus.clear();
		haikusRemovedLast.clear();
//		themeWordIDs.clear();
	}
	
	public static void checkIfHaikusAreValid(ArrayList<String> wordsRemoved){
		int size = haikus.size();
		for(int i = haikus.size() - 1; i >= 0; i--){
			for(int a = 0; a < wordsRemoved.size(); a++){
				if(haikus.get(i).getWordsUsed().contains(wordsRemoved.get(a))){// && !wordsDefinedInRulesTextFile.contains(wordsRemoved.get(a))){
					haikusRemovedLast.add(haikus.get(i));
					haikus.remove(i);
					break;
				}
			}
		}
		if(haikus.size() < size){
//			//Log.i("TAG", (size - haikus.size()) + " haikus removed! " + haikus.size() + " haikus left");
			if(haikus.isEmpty()){
//				//Log.i("TAG", "ERROR: No haikus left!");
			}
		}
	}
	
	public static void removeHaiku(Haiku haiku){
		haikus.remove(haiku);
	}
	
	public static void init(){
		if(inited){
			// The application was closed and the opened again
			// The deleting process will reset and so must the words used
			// Some words might have been deleted by updateWordsUsed()
			allSmsLogWords.addAll(allSmsLogWordsRemoved);
			addWordsWithTheme(smsLogWordsWithThemesRemoved);
//			smsLogWordsWithThemes.addAll(smsLogWordsWithThemesRemoved);
//			smsLogWordsWithTheAllTheme.addAll(smsLogWordsWithTheAllThemeRemoved);
			allSmsLogWordsRemoved.clear();
			smsLogWordsWithThemesRemoved.clear();
			smsLogWordsWithTheAllThemeRemoved.clear();
			return;
		}
		inited = true;
		initPartOfSpeech();
		initThemes();
//		initRulesWords();
		initRules();
		initNonWordsCharacters();
	}
	
	private static void addWordsWithTheme(ArrayList<Word> words){
		for(Word word : words){
			for(PartOfSpeechList pl : allWordsOrderedByTypes){
				if(word.getwordType().equalsIgnoreCase(pl.getPartOfSpeech().getType())){
					pl.getWords().add(word);
					break;
				}
			}
		}
	}
	
//	private static void addWordWithTheme(Word word){
//		for(PartOfSpeechList pl : allWordsOrderedByTypes){
//			if(word.getwordType().equalsIgnoreCase(pl.getPartOfSpeech().getType())){
//				pl.getWords().add(word);
//				break;
//			}
//		}
//	}
	
	private static void removeWordsWithTheme(ArrayList<Word> words){
		for(Word word : words){
			for(PartOfSpeechList pl : allWordsOrderedByTypes){
				pl.getWords().remove(word);
			}
		}
	}
	
	public static PartOfSpeech getPartOfSpeechWithID(long id){
		for(int i = 0; i < allWordsOrderedByTypes.size(); i++){
			if(allWordsOrderedByTypes.get(i).getPartOfSpeech().getID() == id){
				return allWordsOrderedByTypes.get(i).getPartOfSpeech();
			}
		}
		return null;
	}
	
	
	private static void initPartOfSpeech(){
		ArrayList<PartOfSpeech> pos = HaikuActivity.databaseHandler.getAllPartOfSpeeches();
		for(PartOfSpeech p : pos){
			allWordsOrderedByTypes.add(new PartOfSpeechList(p));
			allWordsOrderedByTypesUpdated.add(new PartOfSpeechList(p));
		}
	}
	
//	public static ArrayList<SMS> addThemeDuringDeletion(Theme theme){
//		try {
//			smsSemaphore.acquire();
//			BinView.getInstance().addTheme(theme);
//			// add all valid SMS
//			int threadID = MainView.getInstance().getSelectedConvoThreadID();
//			Uri uri = Uri.parse(HaikuActivity.ALLBOXES);
//			Cursor cursor = MainView.getInstance().getContext().getContentResolver().query(uri, null, "thread_id = '" + threadID + "'", null, null);
//			ArrayList<SMS> newSMSes = new ArrayList<SMS>();
//			SMS tempSMS;
//			if (cursor.moveToFirst()) {
//				do{
//					tempSMS = new SMS(cursor.getInt(cursor.getColumnIndexOrThrow("_id")), 
//							cursor.getString(cursor.getColumnIndexOrThrow("body")), 
//							cursor.getString(cursor.getColumnIndexOrThrow("date")), 
//							threadID,
//							cursor.getString(cursor.getColumnIndexOrThrow("type")));
//					if(!smses.contains(tempSMS)){
//						newSMSes.add(tempSMS);
//					}
//				}
//				while(cursor.moveToNext());
//			}
//			ArrayList<SMS> smsToAdd = FindValidSMSWithTheme.calculateSMS(newSMSes, theme);
//			smsSemaphore.release();
//			for(SMS sms : smsToAdd){
//				HaikuGenerator.addThemeSMS(sms, theme);
//			}
//			smsSemaphore.acquire();
//			HaikuGenerator.updateUseWords();
//			HaikuGenerator.updateThreadIDsADD(smsToAdd);
////			BinView.getInstance().addSMSesAtLastPosition(smsToAdd);s
//			MainView.getInstance().updateSMSView();
//			
//			themes.add(new Theme_ThreadID_Tuple(theme.getID(), threadID));
//			return smsToAdd;
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		} finally{
//			smsSemaphore.release();
//		}
//		return null;
//	}
	
	public static void addTheme(Theme theme){
		try {
			smsSemaphore.acquire();
			int threadID = MainView.getInstance().getSelectedConvoThreadID();
			ArrayList<YearMonth> binDates = new ArrayList<YearMonth>();
			for(DateSMS ds : smsAddedByDate){
				if(ds.getConversationID() == threadID || ds.getConversationID() == DateSMS.ALL_CONVERSATIONS_ID){
					binDates.add(ds.getYearMonth());
				}
			}
			BinView.getInstance().addTheme(theme);
			smsAddedByTheme.add(new ThemeSMS(theme.getID(), threadID)); // Still need to add for the theme view to update properly even if no SMS were added
			if(binDates.isEmpty()){
				// add all valid SMS
				Uri uri = Uri.parse(HaikuActivity.ALLBOXES);
				Cursor cursor = MainView.getInstance().getContext().getContentResolver().query(uri, null, "thread_id = '" + threadID + "'", null, null);
				ArrayList<SMS> newSMSes = new ArrayList<SMS>();
				SMS tempSMS;
				if (cursor.moveToFirst()) {
					do{
						tempSMS = new SMS(cursor.getInt(cursor.getColumnIndexOrThrow("_id")), 
								cursor.getString(cursor.getColumnIndexOrThrow("body")), 
								cursor.getString(cursor.getColumnIndexOrThrow("date")), 
								threadID,
								cursor.getString(cursor.getColumnIndexOrThrow("type")));
						if(!smses.contains(tempSMS)){
							newSMSes.add(tempSMS);
						}
					}
					while(cursor.moveToNext());
				}
				FindValidSMSWithTheme thread = new FindValidSMSWithTheme(newSMSes, theme, threadID);
				addThread(thread);
				thread.start();
			}
			else{
				ArrayList<Theme> validThemes = getAllAddedThemesWithConversationID(threadID);
				if(validThemes.isEmpty()){
					// There are dates in the bin and no themes -> filter the added sms
					ArrayList<SMS> smsToFilter = new ArrayList<SMS>();
					ArrayList<Long> smsIDs = new ArrayList<Long>();
					for(DateSMS ds : smsAddedByDate){
						if(ds.getConversationID() == threadID){
							smsIDs.addAll(ds.getSMSIDs());
						}
					}
					for(int i = smses.size()-1; i >= 0; i--){
						for(int a = 0; a < smsIDs.size(); a++){
							if(smses.get(i).getID() == smsIDs.get(a)){
								smsToFilter.add(smses.get(i));
								smsIDs.remove(a);
								break;
							}
						}
					}
					ArrayList<SMS> validSMS = FindValidSMSWithTheme.calculateSMS(smsToFilter, getAllAddedThemesWithConversationID(threadID));
					boolean exists;
					for(int i = smsToFilter.size()-1; i >= 0; i--){
						exists = false;
						for(int a = 0; a < validSMS.size(); a++){
							if(smsToFilter.get(i).equals(validSMS.get(a))){
								exists = true;
								break;
							}
						}
						if(exists){
							smsToFilter.remove(i);
						}
					}
					for(SMS sms : smsToFilter){
						removeSMSWithoutUpdateHoldingTheSem(sms);
					}
					BinView.getInstance().removeSMSES(smsToFilter);
					MainView.getInstance().updateSMSView();
					MainView.getInstance().updateThemeView();
				}
				else{
					// There are themes and dates in the bin -> add
					Uri uri = Uri.parse(HaikuActivity.ALLBOXES);
					Cursor cursor = MainView.getInstance().getContext().getContentResolver().query(uri, null, "thread_id = '" + threadID + "'", null, null);
					ArrayList<SMS> newSMSes = new ArrayList<SMS>();
					SMS tempSMS;
					if (cursor.moveToFirst()) {
						boolean add;
						do{
							add = false;
							tempSMS = new SMS(cursor.getInt(cursor.getColumnIndexOrThrow("_id")), 
									cursor.getString(cursor.getColumnIndexOrThrow("body")), 
									cursor.getString(cursor.getColumnIndexOrThrow("date")), 
									threadID,
									cursor.getString(cursor.getColumnIndexOrThrow("type")));
							if(!smses.contains(tempSMS)){
								for(YearMonth ds : binDates){
									if(ds.equals(tempSMS.getYearMonth())){
										add = true;
										break;
									}
								}
								if(add){
									newSMSes.add(tempSMS);
								}
							}
						}
						while(cursor.moveToNext());
					}
					FindValidSMSWithTheme thread = new FindValidSMSWithTheme(newSMSes, theme, threadID);
					addThread(thread);
					thread.start();
				}
			}
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally{
			smsSemaphore.release();
		}
	}
	
	public static ArrayList<Theme> getAllAddedThemes(){
		ArrayList<Theme> allAddedThemes = new ArrayList<Theme>();
		Theme temp = null;
		for(ThemeSMS ts : smsAddedByTheme){
			for(Theme theme : allThemes){
				if(ts.getThemeID() == theme.getID()){
					temp = theme;
					break;
				}
			}
			if(!allAddedThemes.contains(temp)){
				allAddedThemes.add(temp);
			}
		}
		return allAddedThemes;
	}
	
	public static ArrayList<Theme> getAllAddedThemesWithConversationID(long threadID){
		ArrayList<Theme> allAddedThemes = new ArrayList<Theme>();
		Theme temp = null;
		for(ThemeSMS ts : smsAddedByTheme){
			if(ts.getConversationID() == threadID){
				for(Theme theme : allThemes){
					if(ts.getThemeID() == theme.getID()){
						temp = theme;
						break;
					}
				}
				if(!allAddedThemes.contains(temp)){
					allAddedThemes.add(temp);
				}
			}
		}
		return allAddedThemes;
	}
	
	public static void removeTheme(Theme theme){
		try {
			smsSemaphore.acquire();
			ArrayList<SMS> smsToRemove = new ArrayList<SMS>();
			ArrayList<Long> smsIDs = new ArrayList<Long>();
			for(int i = smsAddedByTheme.size() - 1; i >= 0; i--){
				if(smsAddedByTheme.get(i).getThemeID() == theme.getID()){
					smsIDs.addAll(smsAddedByTheme.get(i).getSMSIDs());
					smsAddedByTheme.remove(i);
				}
			}
			for(int i = smses.size()-1; i >= 0; i--){
				for(int a = 0; a < smsIDs.size(); a++){
					if(smses.get(i).getID() == smsIDs.get(a)){
						smsToRemove.add(smses.get(i));
						smsIDs.remove(a);
						break;
					}
				}
			}
			for(SMS sms : smsToRemove){
				removeSMSWithoutUpdateHoldingTheSem(sms);
			}
			BinView.getInstance().removeSMSES(smsToRemove);
			MainView.getInstance().updateSMSView();
			MainView.getInstance().updateThemeView();
//			themeWordIDs.removeAll(theme.getWordids());
			updateUseWords();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally{
			smsSemaphore.release();
		}
	}
	
//	public static ArrayList<Theme_ThreadID_Tuple> getThemes(){
//		return themes;
//	}

	public static ArrayList<Theme> getUsedThemes(){
		ArrayList<Theme> usedThemes = new ArrayList<Theme>();
		boolean exists;
		for(ThemeSMS ts : smsAddedByTheme){
			exists = false;
			for(Theme t : usedThemes){
				if(t.getID() == ts.getThemeID()){
					exists = true;
					break;
				}
			}
			if(exists){
				for(Theme t : allThemes){
					if(ts.getThemeID() == t.getID()){
						usedThemes.add(t);
						break;
					}
				}
			}
		}
//		for(Theme_ThreadID_Tuple ttt : themes){
//			exists = false;
//			for(Theme t : usedThemes){
//				if(t.getID() == ttt.getThemeID()){
//					exists = true;
//					break;
//				}
//			}
//			if(exists){
//				for(Theme t : allThemes){
//					if(ttt.getThemeID() == t.getID()){
//						usedThemes.add(t);
//						break;
//					}
//				}
//			}
//		}
		return usedThemes;
	}
	
	/**
	 * A conversation, not an actual thread
	 * @param threadID
	 * @return The SMSes that are added
	 */
	public static ArrayList<SMS> addThread(int threadID){
		try {
			smsSemaphore.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		thread_ids.add(threadID);
		Cursor cursor = HaikuActivity.getThread(MainView.getInstance().getContext(), threadID);
		ArrayList<SMS> threadSMS = new ArrayList<SMS>();
		if (cursor.moveToFirst()) {
			do{
				threadSMS.add(new SMS(cursor.getInt(cursor.getColumnIndexOrThrow("_id")), cursor.getString(cursor.getColumnIndexOrThrow("body")), cursor.getString(cursor.getColumnIndexOrThrow("date")), threadID, cursor.getString(cursor.getColumnIndexOrThrow("type"))));
			}
			while(cursor.moveToNext());
		}
		for(int i = threadSMS.size() -1; i >= 0; i--){
			if(smses.contains(threadSMS.get(i))){
				threadSMS.remove(i);
			}
		}
		calculateSMSes(threadSMS);
		smsSemaphore.release();
		return threadSMS;
	}
	
	/**
	 * A conversation, not an actual thread
	 * @param threadID
	 */
	public static void removeThread(int threadID){
		Integer thread_id = threadID;
		if(thread_ids.remove(thread_id)){
			MainView.getInstance().updateConversationsVisibility();
		}
	}
	
	public static ArrayList<Integer> getThreadIDs(){
		return thread_ids; 
	}
	
	/**
	 * Starts a worker thread that will find the smses words and add them to the word list or return immediatly if the bin is full
	 * @param sms
	 */
	public static void calculateSMS(SMS sms){
		try {
			smsSemaphore.acquire();
			if(!addSMSToSMSList(sms)){
				return;
			}
//			smses.add(sms);
			if(!MainView.getInstance().getBinView().isDeleting()){
				BinView.getInstance().addSMSBeforeDeletion(sms);
			}
			else{
				BinView.getInstance().addSMSDuringDeletion(sms);
			}
			AddSmsThread thread = new AddSmsThread(sms);
			addThread(thread);
			thread.start();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally{
			smsSemaphore.release();
		}
	}
	
	public static void calculateSMSes(ArrayList<SMS> smses){
		ArrayList<SMS> sms2 = new ArrayList<SMS>();
//		boolean shouldSkip;
		for(int i = 0; i < smses.size(); i++){
//			shouldSkip = false;
//			for(int a = i-1; a >= 0; a--){
//				if(smses.get(a).getContactID() == smses.get(i).getContactID() 
//						&& smses.get(a).getMessage().equals(smses.get(i).getMessage()) 
//						&& MainView.getInstance().getConversationObject(smses.get(i).getContactID()) != null
//						&& MainView.getInstance().getConversationObject(smses.get(i).getContactID()).getNames().size() > i-a){
//							// probably the same...
//							shouldSkip = true;
//							break;
//						}
//			}
//			if(!shouldSkip){
				sms2.add(smses.get(i));
//			}
		}
		sms2 = addSMStoSMSList(sms2);
//		HaikuGenerator.smses.addAll(sms2);
		for(int i = 0; i < sms2.size(); i++){
			if(!MainView.getInstance().getBinView().isDeleting()){
				BinView.getInstance().addSMSBeforeDeletion(sms2.get(i));
			}
			else{
				BinView.getInstance().addSMSDuringDeletion(sms2.get(i));
			}
		}
		AddSmsesThread thread = new AddSmsesThread(sms2);
		addThread(thread);
		thread.start();
	}
	
	public static boolean threadsAreRunning(){
		return !addingSMSThreadsRunning.isEmpty();
	}
	
	/**
	 * An actual thread, not a conversation
	 * @param thread
	 */
	public static void addThread(Thread thread){
		try {
			threadSemaphore.acquire();
			addingSMSThreadsRunning.add(thread);
			threadSemaphore.release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * An actual thread, not a conversation
	 * @param thread
	 */
	public static void removeThread(Thread thread){
		try {
			threadSemaphore.acquire();
			addingSMSThreadsRunning.remove(thread);
			if(addingSMSThreadsRunning.isEmpty()){
				BinView.getInstance().allThreadsReady();
			}
			threadSemaphore.release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Just adds the SMS to the smses list if the bin isn't ful. Does NOTHING else.
	 * Returns true if the SMS was added, false otherwise
	 * @param sms
	 */
	public static boolean addSMSToSMSList(SMS sms){
		if(smses.size() < MAX_SMS_IN_BIN){
			smses.add(sms);
			return true;
		}
		HaikuActivity.getInstance().showMaxSMSView();
		return false;
	}
	
	/**
	 * Tries to add the inputed SMS into the list. The successfully added SMS are returned
	 * @param sms
	 * @return
	 */
	public static ArrayList<SMS> addSMStoSMSList(ArrayList<SMS> sms){
		ArrayList<SMS> rSMS = new ArrayList<SMS>();
		for(SMS s : sms){
			if(addSMSToSMSList(s)){
				rSMS.add(s);
			}
			else{
				break;
			}
		}
		return rSMS;
	}
	
	/**
	 * This method adds the words in the sms to the right ArrayLists. The sms itself should already be in the sms ArrayList
	 * @param sms
	 */
	public static void addSMS(SMS sms){
		try {
			smsSemaphore.acquire();
			allSmsLogWords.addAll(sms.getWords());
//			if(themes.isEmpty()){
				addWordsWithTheme(sms.getWords());
//				smsLogWordsWithThemes.addAll(sms.getWords());
//			}
//			else{
//				for(int i = 0; i < sms.getWords().size(); i++){
//					if(themeWordIDs.contains(sms.getWords().get(i).getID())){
////						smsLogWordsWithThemes.add(sms.getWords().get(i));
//						addWordWithTheme(sms.getWords().get(i));
//					}
//				}
//			}
//			for(int i = 0; i < sms.getWords().size(); i++){
//				if(theAllThemeWordIDs.contains(sms.getWords().get(i).getID())){
//					smsLogWordsWithTheAllTheme.add(sms.getWords().get(i));
//				}
//			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally{
			smsSemaphore.release();
		}
	}
	
	public static void addThemeSMS(final SMS sms, Theme theme, long threadID){
		try {
			smsSemaphore.acquire();
			boolean exists = false;
			ThemeSMS themeSMS = null;
			for(ThemeSMS ts : smsAddedByTheme){
				if(ts.getThemeID() == theme.getID() && ts.getConversationID() == threadID){
//					ts.addSMSID(sms.getID());
					themeSMS = ts;
					exists = true;
					break;
				}
			}
			if(!exists || themeSMS == null){
				themeSMS = new ThemeSMS(theme.getID(), threadID);
//				ts.addSMSID(sms.getID());
//				smsAddedByTheme.add(ts);
			}
//			smses.add(sms);
			if(addSMSToSMSList(sms)){
				themeSMS.addSMSID(sms.getID());
				smsAddedByTheme.add(themeSMS);
			}
			else{
				return;
			}
			HaikuActivity.getInstance().runOnUiThread(new Runnable(){           
		        @Override
		        public void run(){
//		        	if(!MainView.getInstance().getBinView().isDeleting()){
						BinView.getInstance().addSMSBeforeDeletion(sms);
//					}
//					else{
//						BinView.getInstance().addSMSDuringDeletion(sms);
//					}
				}
			});
			allSmsLogWords.addAll(sms.getWords());
			addWordsWithTheme(sms.getWords());
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally{
			smsSemaphore.release();
		}
	}
	
	public static void removeSMS(SMS sms){
		try {
			smsSemaphore.acquire();
			smses.remove(sms);
			MainView.getInstance().updateSMSView();
			removeThread((int)sms.getContactID()); //the contact is only saved if ALL smses of that contact is added. So if one is taken away, so is the contact.
			allSmsLogWords.removeAll(sms.getWords());
	//		smsLogWordsWithThemes.removeAll(sms.getWords());
			removeWordsWithTheme(sms.getWords());
	//		smsLogWordsWithTheAllTheme.removeAll(sms.getWords());
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally{
			smsSemaphore.release();
		}
	}
	
	private static void removeSMSWithoutUpdate(SMS sms){
		try {
			smsSemaphore.acquire();
			smses.remove(sms);
			removeThread((int)sms.getContactID()); //the contact is only saved if ALL smses of that contact is added. So if one is taken away, so is the contact.
			allSmsLogWords.removeAll(sms.getWords());
//			smsLogWordsWithThemes.removeAll(sms.getWords());
			removeWordsWithTheme(sms.getWords());
//			smsLogWordsWithTheAllTheme.removeAll(sms.getWords());
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally{
			smsSemaphore.release();
		}
	}
	
	private static void removeSMSWithoutUpdateHoldingTheSem(SMS sms){
		smses.remove(sms);
		removeThread((int)sms.getContactID()); //the contact is only saved if ALL smses of that contact is added. So if one is taken away, so is the contact.
		allSmsLogWords.removeAll(sms.getWords());
//		smsLogWordsWithThemes.removeAll(sms.getWords());
		removeWordsWithTheme(sms.getWords());
//		smsLogWordsWithTheAllTheme.removeAll(sms.getWords());
	}
	
	public static ArrayList<SMS> getAllAddedSMS(){
		return smses;
	}
	
	public static void addYear(int year){
//		YearMonth ym;
//		for(int i = 0; i < DateView.MONTHS_NAME.length; i++){
//			ym = new YearMonth(year, DateView.MONTHS_NAME[i]);
//			if(!dates.contains(ym)){
//				dates.add(ym);
//				BinView.getInstance().addDate(ym);
//			}
//		}
//		Uri uri = Uri.parse(HaikuActivity.ALLBOXES);
//		Cursor cursor = MainView.getInstance().getContext().getContentResolver().query(uri, null, null, null, null);
//		ArrayList<SMS> newSMSes = new ArrayList<SMS>();
//		SMS tempSMS;
//		if (cursor.moveToFirst()) {
//			do{
//				tempSMS = new SMS(cursor.getInt(cursor.getColumnIndexOrThrow("_id")), 
//						cursor.getString(cursor.getColumnIndexOrThrow("body")), 
//						cursor.getString(cursor.getColumnIndexOrThrow("date")), 
//						cursor.getLong(cursor.getColumnIndexOrThrow("thread_id")),
//						cursor.getString(cursor.getColumnIndexOrThrow("type")));
//				if(!smses.contains(tempSMS) && tempSMS.getYear() == year){
//					newSMSes.add(tempSMS);
//				}
//			}
//			while(cursor.moveToNext());
//		}
//		calculateSMSes(newSMSes);
//		updateThreadIDsADD(newSMSes); // must be done after smses list has been updated (done int calculateSMSes() method)
//		MainView.getInstance().updateConversationsVisibility();
//		ArrayList<SMS> newSMSes = new ArrayList<SMS>();
		YearMonth ym;
		for(int i = 0; i < DateView.MONTHS_NAME.length; i++){
			ym = new YearMonth(year, DateView.MONTHS_NAME[i]);
			if(!HaikuActivity.getInstance().isFutureDate(ym)){
//				newSMSes.addAll(addDate(ym));
				addDate(ym);
			}
		}
//		return orderSMSByDate(newSMSes);
	}
	
	public static void addYearFromSMSes(int year, int threadID){
//		YearMonthConvo ym;
//		for(int i = 0; i < DateView.MONTHS_NAME.length; i++){
//			ym = new YearMonthConvo(new YearMonth(year, DateView.MONTHS_NAME[i]), threadID);
//			if(!datesFromThreads.contains(ym)){
//				datesFromThreads.add(ym);
//				BinView.getInstance().addDate(ym.getYearMonth());
//			}
//		}
//		Uri uri = Uri.parse(HaikuActivity.ALLBOXES);
//		Cursor cursor = MainView.getInstance().getContext().getContentResolver().query(uri, null, "thread_id = '" + threadID + "'", null, null);
//		ArrayList<SMS> newSMSes = new ArrayList<SMS>();
//		SMS tempSMS;
//		if (cursor.moveToFirst()) {
//			do{
//				tempSMS = new SMS(cursor.getInt(cursor.getColumnIndexOrThrow("_id")), 
//						cursor.getString(cursor.getColumnIndexOrThrow("body")), 
//						cursor.getString(cursor.getColumnIndexOrThrow("date")), 
//						threadID,
//						cursor.getString(cursor.getColumnIndexOrThrow("type")));
//				if(!smses.contains(tempSMS) && tempSMS.getYear() == year){
//					newSMSes.add(tempSMS);
//				}
//			}
//			while(cursor.moveToNext());
//		}
////		ArrayList<Month> monthsAdded = new ArrayList<Month>();
////		for(int i = 0; i < DateView.MONTHS_NAME.length; i++){
////			monthsAdded.add(DateView.MONTHS_NAME[i]);
////		}
////		for(DateSMS ds : smsAddedByDate){
////			if(ds.getYearMonth().getYear() == year){
////				for(Month m : monthsAdded){
////					if(ds.getYearMonth().getMonth().equals(m)){
////						// mer här
////					}
////				}
////			}
////		}
//		calculateSMSes(newSMSes);
//		updateThreadIDsADD(newSMSes); // must be done after smses list has been updated (done int calculateSMSes() method)
//		MainView.getInstance().updateSMSView();
//		ArrayList<SMS> newSMSes = new ArrayList<SMS>();
		YearMonth ym;
		for(int i = 0; i < DateView.MONTHS_NAME.length; i++){
			ym = new YearMonth(year, DateView.MONTHS_NAME[i]);
			if(!HaikuActivity.getInstance().isFutureDate(ym)){
//				newSMSes.addAll(addDateFromSMSes(ym, threadID));
				addDateFromSMSes(ym, threadID);
			}
		}
//		return orderSMSByDate(newSMSes);
	}
	
	public static void addDate(YearMonth date){
//		BinView.getInstance().addDate(date);
//		Uri uri = Uri.parse(HaikuActivity.ALLBOXES);
//		Cursor cursor = MainView.getInstance().getContext().getContentResolver().query(uri, null, null, null, null);
//		ArrayList<SMS> newSMSes = new ArrayList<SMS>();
//		SMS tempSMS;
//		if (cursor.moveToFirst()) {
//			do{
//				tempSMS = new SMS(cursor.getInt(cursor.getColumnIndexOrThrow("_id")), 
//						cursor.getString(cursor.getColumnIndexOrThrow("body")), 
//						cursor.getString(cursor.getColumnIndexOrThrow("date")), 
//						cursor.getLong(cursor.getColumnIndexOrThrow("thread_id")),
//						cursor.getString(cursor.getColumnIndexOrThrow("type")));
//				if(!smses.contains(tempSMS) && tempSMS.getYearMonth().equals(date)){
//					newSMSes.add(tempSMS);
//				}
//			}
//			while(cursor.moveToNext());
//		}
//		smsAddedByDate.add(new DateSMS(date, DateSMS.ALL_CONVERSATIONS_ID));
//		calculateSMSes(newSMSes);
//		updateThreadIDsADD(newSMSes); // must be done after smses list has been updated (done int calculateSMSes() method)
//		MainView.getInstance().updateConversationsVisibility();
//		return orderSMSByDate(newSMSes);
		
		for(ConversationObjectView conversation : MainView.getInstance().getConversations()){
			addDateFromSMSes(date, conversation.getThreadID());
		}
	}
	
	public static void addDateFromSMSes(YearMonth date, int threadID){
		try {
			smsSemaphore.acquire();
			ArrayList<ThemeSMS> binThemes = new ArrayList<ThemeSMS>();
			for(ThemeSMS ts : smsAddedByTheme){
				if(ts.getConversationID() == threadID){
					binThemes.add(ts);
				}
			}
			BinView.getInstance().addDate(date);
			if(binThemes.isEmpty()){
				// Add new SMS
				Uri uri = Uri.parse(HaikuActivity.ALLBOXES);
				Cursor cursor = MainView.getInstance().getContext().getContentResolver().query(uri, null, "thread_id = '" + threadID + "'", null, null);
				ArrayList<SMS> newSMSes = new ArrayList<SMS>();
				SMS tempSMS;
				if (cursor.moveToFirst()) {
					do{
						tempSMS = new SMS(cursor.getInt(cursor.getColumnIndexOrThrow("_id")), 
								cursor.getString(cursor.getColumnIndexOrThrow("body")), 
								cursor.getString(cursor.getColumnIndexOrThrow("date")), 
								threadID,
								cursor.getString(cursor.getColumnIndexOrThrow("type")));
						if(!smses.contains(tempSMS) && tempSMS.getYearMonth().equals(date)){
							newSMSes.add(tempSMS);
						}
					}
					while(cursor.moveToNext());
				}
				smsAddedByDate.add(new DateSMS(date, newSMSes, threadID));
				calculateSMSes(newSMSes);
				updateThreadIDsADD(newSMSes); // must be done after the SMS list has been updated (done in the calculateSMSes() method)
				MainView.getInstance().updateSMSView();
			}
			else{
				ArrayList<YearMonth> validDates = new ArrayList<YearMonth>();
				for(DateSMS ds : smsAddedByDate){
					if((ds.getConversationID() == threadID || ds.getConversationID() == DateSMS.ALL_CONVERSATIONS_ID)
							&& !validDates.contains(ds.getYearMonth())){
						validDates.add(ds.getYearMonth());
					}
				}
				if(validDates.isEmpty()){
					// There are themes in the bin and no dates. Filter the SMS in the bin
					DateSMS ds = new DateSMS(date, threadID);
					smsAddedByDate.add(ds); // Still has to add so that the date view updates correctly even though no sms were added
					ArrayList<SMS> smsToFilter = new ArrayList<SMS>();
					ArrayList<Long> smsIDs = new ArrayList<Long>();
					
					for(ThemeSMS ts : binThemes){
						smsIDs.addAll(ts.getSMSIDs());
					}
					for(int i = smses.size()-1; i >= 0; i--){
						for(int a = 0; a < smsIDs.size(); a++){
							if(smses.get(i).getID() == smsIDs.get(a)){
								smsToFilter.add(smses.get(i));
								smsIDs.remove(a);
								break;
							}
						}
					}
					
					validDates.add(ds.getYearMonth());
					
					for(int i = smsToFilter.size()-1; i >= 0; i--){
						if(validDates.contains(smsToFilter.get(i).getYearMonth())){
							smsToFilter.remove(i);
						}
					}
					
					for(SMS sms : smsToFilter){
						removeSMSWithoutUpdateHoldingTheSem(sms);
					}
					BinView.getInstance().removeSMSES(smsToFilter);
					MainView.getInstance().updateSMSView();
					MainView.getInstance().updateDateView();
				}
				else{
					// There are themes and dates in the bin. All SMS in the bin will be filtered for both of those. Add SMS which fit the added date and the inputed themes
					Uri uri = Uri.parse(HaikuActivity.ALLBOXES);
					Cursor cursor = MainView.getInstance().getContext().getContentResolver().query(uri, null, "thread_id = '" + threadID + "'", null, null);
					ArrayList<SMS> newSMSes = new ArrayList<SMS>();
					SMS tempSMS;
					if (cursor.moveToFirst()) {
						do{
							tempSMS = new SMS(cursor.getInt(cursor.getColumnIndexOrThrow("_id")), 
									cursor.getString(cursor.getColumnIndexOrThrow("body")), 
									cursor.getString(cursor.getColumnIndexOrThrow("date")), 
									threadID,
									cursor.getString(cursor.getColumnIndexOrThrow("type")));
							if(!smses.contains(tempSMS) && tempSMS.getYearMonth().equals(date)){
								newSMSes.add(tempSMS);
							}
						}
						while(cursor.moveToNext());
					}
					DatabaseHandler.getInstance().initSMSES(newSMSes);
					newSMSes = FindValidSMSWithTheme.calculateSMS(newSMSes, getAllAddedThemesWithConversationID(threadID));
					smsAddedByDate.add(new DateSMS(date, newSMSes, threadID));
					calculateSMSes(newSMSes);
					updateThreadIDsADD(newSMSes); // must be done after the SMS list has been updated (done in the calculateSMSes() method)
					MainView.getInstance().updateSMSView();
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally{
			smsSemaphore.release();
		}
//		return orderSMSByDate(newSMSes);
	}
	
	public static ArrayList<SMS> orderSMSByDate(ArrayList<SMS> sms){
		ArrayList<SMS> sortedSMS = new ArrayList<SMS>();
		SMS[] smsList = new SMS[sms.size()];
		for(int i = 0; i < sms.size(); i++){
			smsList[i] = sms.get(i);
		}
		
		SMS temp;
		int location;
		for(int i = 1; i < smsList.length; i++){
			if(Long.parseLong(smsList[i].getDate()) > Long.parseLong(smsList[i-1].getDate())){
				temp = smsList[i];
				location = i;
				do {
					smsList[location] = smsList[location-1];
	                location--;
	            }
	            while (location > 0 && Long.parseLong(smsList[location-1].getDate()) < Long.parseLong(temp.getDate()));
				smsList[location] = temp;
			}
		}
		for(int i = 0; i < smsList.length; i++){
			sortedSMS.add(smsList[i]);
		}
		return sortedSMS;
	}
	
	public static ArrayList<SMS> removeDate(YearMonth date){
		ArrayList<SMS> removedSMS = new ArrayList<SMS>();
		ArrayList<Long> smsIDs = new ArrayList<Long>();
		for(int i = smsAddedByDate.size()-1; i >= 0; i--){
			if(date.equals(smsAddedByDate.get(i).getYearMonth())){
				smsIDs.addAll(smsAddedByDate.get(i).getSMSIDs());
				smsAddedByDate.remove(i);
			}
		}
		for(int i = smses.size()-1; i >= 0; i--){
			for(int a = 0; a < smsIDs.size(); a++){
				if(smses.get(i).getID() == smsIDs.get(a)){
					removedSMS.add(smses.get(i));
					removeSMSWithoutUpdate(smses.get(i));
					smsIDs.remove(a);
					break;
				}
			}
		}
		
//		dates.remove(date);
//		for(int i = datesFromThreads.size()-1; i >= 0; i--){
//			if(datesFromThreads.get(i).getYearMonth().equals(date)){
//				datesFromThreads.remove(i);
//			}
//		}
//		for(int i = smses.size()-1; i >= 0; i--){
//			if(smses.get(i).getYearMonth().equals(date)){
//				removedSMS.add(smses.get(i));
////				smses.remove(i); // old
//				removeSMSWithoutUpdate(smses.get(i));
//			}
//		}
		MainView.getInstance().updateSMSView();
		return removedSMS;
	}
	
	public static void updateThreadIDsADD(ArrayList<SMS> smsesAdded){
//		double startTime = System.currentTimeMillis();
		ArrayList<Integer> threadIDs = new ArrayList<Integer>();
		Integer thread_id;
		for(int i = 0; i < smsesAdded.size(); i++){
			if(!thread_ids.contains(smsesAdded.get(i).getContactID()) && !threadIDs.contains(smsesAdded.get(i).getContactID())){
				thread_id = (int) smsesAdded.get(i).getContactID(); // will remove the index and not the object otherwise
				threadIDs.add(thread_id);
			}
		}
		Cursor cursor;
		Uri uri = Uri.parse(HaikuActivity.ALLBOXES);
		String[] projection;
		long id;
		boolean exists;
		boolean allExists;
		boolean update = false;
		for(int i = 0; i < threadIDs.size(); i++){
			if(thread_ids.contains(threadIDs.get(i))){
				continue;
			}
			allExists = true;
			projection = new String[] {"_id"};
			cursor =  MainView.getInstance().getContext().getContentResolver().query(uri, projection, "thread_id = '" + threadIDs.get(i) + "'", null, null);
			if(cursor != null && cursor.moveToFirst()){
				do{
					exists = false;
					id = cursor.getLong(cursor.getColumnIndexOrThrow("_id"));
					for(int s = 0; s < smses.size(); s++){
						if(smses.get(s).getID() == id){
							exists = true;
							break;
						}
					}
					if(!exists){
						allExists = false;
						break;
					}
				}while(cursor.moveToNext());
			}
			if(allExists){
				thread_ids.add(threadIDs.get(i));
				update = true;
			}
		}
		if(update){
			HaikuActivity.getInstance().runOnUiThread(new Runnable(){           
		        @Override
		        public void run(){
		        	MainView.getInstance().updateConversationsVisibility();
				}
			});
		}
//		//Log.i("TAG", "Time to check if whole conversations were added: " + (System.currentTimeMillis() - startTime) + " ms");
	}
	
	public static ArrayList<YearMonth> getDates(){
		ArrayList<YearMonth> dates = new ArrayList<YearMonth>();
		for(DateSMS ds : smsAddedByDate){
			dates.add(ds.getYearMonth());
		}
		return dates;
	}
	
//	public static ArrayList<YearMonthConvo> getDateConvos(){
//		return datesFromThreads;
//	}
	
	public static ArrayList<DateSMS> getDatesAdded(){
		return smsAddedByDate;
	}
	
	public static ArrayList<ThemeSMS> getThemesAdded(){
		return smsAddedByTheme;
	}
	
	public static ArrayList<Theme> getAllThemes(){
		return allThemes;
	}
	
	private static void initThemes(){
		allThemes.addAll(HaikuActivity.databaseHandler.getAllThemes());
//		theAllTheme = HaikuActivity.databaseHandler.getTheAllTheme();
//		theAllThemeWordIDs.addAll(theAllTheme.getWordids()); // Will always be there
	}
	
	public static void updateThemeWordsInList(){
		for(PartOfSpeechList pl : allWordsOrderedByTypesUpdated){
			pl.getWords().clear();
		}
		ArrayList<Theme> themesInBin = new ArrayList<Theme>();
		for(ThemeSMS themeSMS : smsAddedByTheme){
			for(Theme theme : allThemes){
				if(theme.getID() == themeSMS.getThemeID() && !themesInBin.contains(theme)){
					themesInBin.add(theme);
				}
			}
		}
		boolean themeWord;
		for(int a = 0; a < allWordsOrderedByTypes.size(); a++){
			for(Word word : allWordsOrderedByTypes.get(a).getWords()){
				themeWord = false;
				for(Theme theme : themesInBin){
					if(theme.getWordids().contains(word.getID())){
						themeWord = true;
						break;
					}
				}
				if(themeWord){
					for(int i = 0; i < THEME_WORD_MULTIPLIER-1; i++){
						allWordsOrderedByTypesUpdated.get(a).getWords().add(word);
					}
				}
				allWordsOrderedByTypesUpdated.get(a).getWords().add(word);
			}
		}
	}
	
	public static double testStartTime;
	private static int createdHaikusCounter;
	
	public static synchronized void createHaikus(){ //TODO
		updateThemeWordsInList();
		nullHaikuGenerated = false;
		allGenerated = false;
		testStartTime = System.currentTimeMillis();
		haikus.clear();
		generationsCounter = 4;
		createdHaikusCounter = 0;
//		printAllUsableWords();
		haikus.add(new Haiku());
		haikus.add(new Haiku());
		haikus.add(new Haiku());
		haikus.add(new Haiku());
		
	}
	
	private static boolean nullHaikuGenerated = false;
	private static boolean allGenerated = false;
	
	public static synchronized void nullHaikuGenerated(){
		if(!nullHaikuGenerated){
			HaikuActivity.getInstance().runOnUiThread(new Runnable(){           
		        @Override
		        public void run(){
		        	Toast.makeText(HaikuActivity.getInstance().getApplicationContext(), "A Haiku cannot be created with the current sms input (add more!)",Toast.LENGTH_LONG).show();
				}
			});
		}
		nullHaikuGenerated = true;
	}
	
	public static synchronized void nextHaiku(){ //TODO
		createdHaikusCounter++;
		if(nullHaikuGenerated){ // Since the algorithm checks ALL possible solutions, if none is found it means that none exists.
			if(!allGenerated){
				BinView.getInstance().allHaikusAreGenerated();
				allGenerated = true;
			}
			return;
		}
		if(generationsCounter < NUMBER_OF_GENERATIONS && !BinView.getInstance().isShowingHaiku()){
			updateWordsUsed();
			generationsCounter++;
			haikus.add(new Haiku());
		}
		else if(createdHaikusCounter >= NUMBER_OF_GENERATIONS){
//			Log.i("TAG", "Time to generate all haikus: " + (System.currentTimeMillis()-testStartTime) + " ms");
			BinView.getInstance().allHaikusAreGenerated();
		}
	}
	
	public static ArrayList<Haiku> getGeneratedHaikus(){
		return haikus;
	}
	
	private static Random randomGenerator = new Random();
	
	public static Haiku getRandomReadyHaiku(){
		if(haikus.isEmpty()){
			return null;
		}
		ArrayList<Haiku> tempHaikus = new ArrayList<Haiku>();
		for(int i = 0; i < haikus.size(); i++){
			if(haikus.get(i).isHaikuFinished()){
				tempHaikus.add(haikus.get(i));
			}
		}
		if(tempHaikus.isEmpty()){
			return null;
		}
		return tempHaikus.get(randomGenerator.nextInt(tempHaikus.size()));
	}
	
	/**
	 * Updates the ArrayList with the words that will be used to create a haiku. 
	 * 
	 * @IMPORTANT The thread that calls this method MUST have the smsSemaphore!
	 */
	public static void updateUseWords(){
		for(PartOfSpeechList pl : allWordsOrderedByTypes){
			pl.getWords().clear();
		}
//		if(themes.isEmpty()){
			// no theme(s) -> use all words
			addWordsWithTheme(allSmsLogWords);
//			return;
//		}
//		for(int i = 0; i < allSmsLogWords.size(); i++){
//			if(themeWordIDs.contains(allSmsLogWords.get(i).getID())){
//				addWordWithTheme(allSmsLogWords.get(i));
//			}
//		}
	}
	
	public static ArrayList<PartOfSpeechList> getWordsUsed(){
		return allWordsOrderedByTypesUpdated;
	}
	
//	public static ArrayList<Word> getWordsUsedWithTheAllTheme(){
//		return smsLogWordsWithTheAllTheme;
//	}
	
//	public static void printAllUsableWords(){
//		for(int i = 0; i < smsLogWordsWithThemes.size(); i++){
//			smsLogWordsWithThemes.get(i).print("TAG2");
//		}
//		//Log.i("TAG2", "Number of usable words with selected themes: " + smsLogWordsWithThemes.size());
//		
//		for(int i = 0; i < smsLogWordsWithTheAllTheme.size(); i++){
//			smsLogWordsWithTheAllTheme.get(i).print("TAG2");
//		}
//		//Log.i("TAG2", "Total number of usable words with the all theme: " + smsLogWordsWithTheAllTheme.size());
//		//Log.i("TAG2", "Total number of usable words: " + (smsLogWordsWithThemes.size() + smsLogWordsWithTheAllTheme.size()));
//	}
	
	/**
	 * takes one String and divides it into smaller Strings, each with its own word
	 * @param sentence
	 * @return an ArrayList of all the words in the sentence
	 */
	public static ArrayList<String> getWords(String sentence){
		ArrayList<String> notRealWords = new ArrayList<String>();
		String textMessage = sentence.toLowerCase();
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
			notRealWords.add(word);
			if(pos2+1 <= textMessage.length()){
				textMessage = textMessage.substring(pos2+1);
			}
			else{
				break;
			}
		}
//		//Log.i("TAG2", "sentence: " + sentence);
//		for(int i = 0; i < notRealWords.size(); i++){
//			//Log.i("TAG2", notRealWords.get(i));
//		}
		return notRealWords;
	}
}
