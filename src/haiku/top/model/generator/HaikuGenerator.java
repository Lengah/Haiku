package haiku.top.model.generator;

import haiku.top.HaikuActivity;
import haiku.top.model.PartOfSpeech;
import haiku.top.model.Theme;
import haiku.top.model.Word;
import haiku.top.model.WordAndNumber;
import haiku.top.model.date.YearMonth;
import haiku.top.model.date.YearMonthConvo;
import haiku.top.model.smshandler.AddSmsThread;
import haiku.top.model.smshandler.AddSmsesThread;
import haiku.top.model.smshandler.SMS;
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

public class HaikuGenerator {
	private static ArrayList<Theme> themes = new ArrayList<Theme>();
	private static ArrayList<Integer> thread_ids = new ArrayList<Integer>(); // All complete conversations added
	private static ArrayList<SMS> smses = new ArrayList<SMS>();
	private static ArrayList<YearMonth> dates = new ArrayList<YearMonth>();
	private static ArrayList<YearMonthConvo> datesFromThreads = new ArrayList<YearMonthConvo>();
	private static ArrayList<Theme> allThemes = new ArrayList<Theme>();
	private static Theme theAllTheme;
//	private static ArrayList<Word> smsLogWordsWithThemes = new ArrayList<Word>();
	private static ArrayList<Word> smsLogWordsWithTheAllTheme = new ArrayList<Word>();
	private static ArrayList<Word> allSmsLogWords = new ArrayList<Word>();
	private static ArrayList<Long> themeWordIDs = new ArrayList<Long>();
	private static ArrayList<Long> theAllThemeWordIDs = new ArrayList<Long>();
	private static ArrayList<Haiku> haikus = new ArrayList<Haiku>();
	private static final int NUMBER_OF_GENERATIONS = 4;
	private static int generationsCounter;
	
	private static ArrayList<PartOfSpeechList> allWordsUsedWithThemesOrderedByTypes = new ArrayList<PartOfSpeechList>();
	
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
										'è', 'å', 'ä', 'ö'};//, '\''}; // ' doesn't seem to work
	
	/**
	 * Words in rules.txt which are not words (words in [] brackets)
	 * Such as ',', '?' and '!'
	 */
	private static ArrayList<String> nonWordsInRules = new ArrayList<String>();
	
	private static String[] rules;
	
	private static void initRules(){
		try {
			InputStream rulesF = HaikuActivity.getInstance().getAssets().open("rules.txt");
			BufferedReader reader = new BufferedReader(new InputStreamReader(rulesF));
			ArrayList<String> rowsString = new ArrayList<String>();
			String tempText;
			while ((tempText = reader.readLine()) != null) {
				rowsString.add(tempText);
			}
			rules = new String[rowsString.size()];
			for(int i = 0; i < rowsString.size(); i++){
				rules[i] = rowsString.get(i);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static ArrayList<String> getNonWordsInRules(){
		return nonWordsInRules;
	}
	
	private static void initNonWordsCharacters(){
		int startIndex;
		int endIndex;
		String temp;
		String[] rulesTemp = rules.clone();
		for(int i = 0; i < rulesTemp.length; i++){
			while((startIndex = rulesTemp[i].indexOf('[')) != -1){
				endIndex = rulesTemp[i].indexOf(']');
				temp = rulesTemp[i].substring(startIndex+1, endIndex);
				if(!nonWordsInRules.contains(temp)){
					nonWordsInRules.add(temp);
				}
				rulesTemp[i] = rulesTemp[i].substring(endIndex+1);
			}
		}
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
			for(PartOfSpeechList pl : allWordsUsedWithThemesOrderedByTypes){
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
			for(int a = smsLogWordsWithTheAllTheme.size() - 1; a >= 0; a--){
				if(wordsRemoved.get(i).equals(smsLogWordsWithTheAllTheme.get(a).getText())){
					smsLogWordsWithTheAllThemeRemoved.add(smsLogWordsWithTheAllTheme.get(a));
					smsLogWordsWithTheAllTheme.remove(a);
				}
			}
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
		themes.clear();
		thread_ids.clear();
		smses.clear();
		dates.clear();
		smsLogWordsWithTheAllTheme.clear();
		for(PartOfSpeechList pl : allWordsUsedWithThemesOrderedByTypes){
			pl.getWords().clear();
		}
//		smsLogWordsWithThemes.clear();
		allSmsLogWords.clear();
		haikus.clear();
		haikusRemovedLast.clear();
		themeWordIDs.clear();
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
			Log.i("TAG", (size - haikus.size()) + " haikus removed! " + haikus.size() + " haikus left");
			if(haikus.isEmpty()){
				Log.i("TAG", "ERROR: No haikus left!");
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
			smsLogWordsWithTheAllTheme.addAll(smsLogWordsWithTheAllThemeRemoved);
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
			for(PartOfSpeechList pl : allWordsUsedWithThemesOrderedByTypes){
				if(word.getwordType().equalsIgnoreCase(pl.getPartOfSpeech().getType())){
					pl.getWords().add(word);
					break;
				}
			}
		}
	}
	
	private static void addWordWithTheme(Word word){
		for(PartOfSpeechList pl : allWordsUsedWithThemesOrderedByTypes){
			if(word.getwordType().equalsIgnoreCase(pl.getPartOfSpeech().getType())){
				pl.getWords().add(word);
				break;
			}
		}
	}
	
	private static void removeWordsWithTheme(ArrayList<Word> words){
		for(Word word : words){
			for(PartOfSpeechList pl : allWordsUsedWithThemesOrderedByTypes){
				pl.getWords().remove(word);
			}
		}
	}
	
	public static PartOfSpeech getPartOfSpeechWithID(long id){
		for(int i = 0; i < allWordsUsedWithThemesOrderedByTypes.size(); i++){
			if(allWordsUsedWithThemesOrderedByTypes.get(i).getPartOfSpeech().getID() == id){
				return allWordsUsedWithThemesOrderedByTypes.get(i).getPartOfSpeech();
			}
		}
		return null;
	}
	
	
	private static void initPartOfSpeech(){
		ArrayList<PartOfSpeech> pos = HaikuActivity.databaseHandler.getAllPartOfSpeeches();
		for(PartOfSpeech p : pos){
			allWordsUsedWithThemesOrderedByTypes.add(new PartOfSpeechList(p));
		}
	}
	
	public static void addTheme(Theme theme){
		if(themes.contains(theme)){
			// no duplicates allowed!
			return;
		}
		BinView.getInstance().addTheme(theme);
		try {
			smsSemaphore.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		themes.add(theme);
		themeWordIDs.addAll(theme.getWordids());
		updateUseWords();
		smsSemaphore.release();
	}
	
	public static void removeTheme(Theme theme){
		try {
			smsSemaphore.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		themes.remove(theme);
		MainView.getInstance().updateThemeView();
		themeWordIDs.removeAll(theme.getWordids());
		updateUseWords();
		smsSemaphore.release();
	}
	
	public static ArrayList<Theme> getThemes(){
		return themes;
	}
	
	/**
	 * A conversation, not an actual thread
	 * @param threadID
	 * @return The SMSes that are added
	 */
	public static ArrayList<SMS> addThread(int threadID){
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
	 * Starts a worker thread that will find the smses words and add them to the word list
	 * @param sms
	 */
	public static void calculateSMS(SMS sms){
		smses.add(sms);
		if(!MainView.getInstance().getBinView().isDeleting()){
			BinView.getInstance().addSMSBeforeDeletion(sms);
		}
		else{
			BinView.getInstance().addSMSDuringDeletion(sms);
		}
		AddSmsThread thread = new AddSmsThread(sms);
		addThread(thread);
		thread.start();
	}
	
	public static void calculateSMSes(ArrayList<SMS> smses){
		HaikuGenerator.smses.addAll(smses);
		for(int i = 0; i < smses.size(); i++){
			if(!MainView.getInstance().getBinView().isDeleting()){
				BinView.getInstance().addSMSBeforeDeletion(smses.get(i));
			}
			else{
				BinView.getInstance().addSMSDuringDeletion(smses.get(i));
			}
		}
		AddSmsesThread thread = new AddSmsesThread(smses);
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
	 * This method adds the words in the sms to the right ArrayLists. The sms itself should already be in the sms ArrayList
	 * @param sms
	 */
	public static void addSMS(SMS sms){
		try {
			smsSemaphore.acquire();
			allSmsLogWords.addAll(sms.getWords());
			if(themes.isEmpty()){
				addWordsWithTheme(sms.getWords());
//				smsLogWordsWithThemes.addAll(sms.getWords());
			}
			else{
				for(int i = 0; i < sms.getWords().size(); i++){
					if(themeWordIDs.contains(sms.getWords().get(i).getID())){
//						smsLogWordsWithThemes.add(sms.getWords().get(i));
						addWordWithTheme(sms.getWords().get(i));
					}
				}
			}
			for(int i = 0; i < sms.getWords().size(); i++){
				if(theAllThemeWordIDs.contains(sms.getWords().get(i).getID())){
					smsLogWordsWithTheAllTheme.add(sms.getWords().get(i));
				}
			}
			smsSemaphore.release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static void removeSMS(SMS sms){
		smses.remove(sms);
		MainView.getInstance().updateSMSView();
		removeThread((int)sms.getContactID()); //the contact is only saved if ALL smses of that contact is added. So if one is taken away, so is the contact.
		try {
			smsSemaphore.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		allSmsLogWords.removeAll(sms.getWords());
//		smsLogWordsWithThemes.removeAll(sms.getWords());
		removeWordsWithTheme(sms.getWords());
		smsLogWordsWithTheAllTheme.removeAll(sms.getWords());
		smsSemaphore.release();
	}
	
	private static void removeSMSWithoutUpdate(SMS sms){
		smses.remove(sms);
		removeThread((int)sms.getContactID()); //the contact is only saved if ALL smses of that contact is added. So if one is taken away, so is the contact.
		try {
			smsSemaphore.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		allSmsLogWords.removeAll(sms.getWords());
//		smsLogWordsWithThemes.removeAll(sms.getWords());
		removeWordsWithTheme(sms.getWords());
		smsLogWordsWithTheAllTheme.removeAll(sms.getWords());
		smsSemaphore.release();
	}
	
	public static ArrayList<SMS> getAllAddedSMS(){
		return smses;
	}
	
	public static ArrayList<SMS> addYear(int year){
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
		ArrayList<SMS> newSMSes = new ArrayList<SMS>();
		SMS tempSMS;
		if (cursor.moveToFirst()) {
			do{
				tempSMS = new SMS(cursor.getInt(cursor.getColumnIndexOrThrow("_id")), 
						cursor.getString(cursor.getColumnIndexOrThrow("body")), 
						cursor.getString(cursor.getColumnIndexOrThrow("date")), 
						cursor.getLong(cursor.getColumnIndexOrThrow("thread_id")),
						cursor.getString(cursor.getColumnIndexOrThrow("type")));
				if(!smses.contains(tempSMS) && tempSMS.getYear() == year){
					newSMSes.add(tempSMS);
				}
			}
			while(cursor.moveToNext());
		}
		calculateSMSes(newSMSes);
		updateThreadIDsADD(newSMSes); // must be done after smses list has been updated (done int calculateSMSes() method)
		MainView.getInstance().updateConversationsVisibility();
		return orderSMSByDate(newSMSes);
	}
	
	public static ArrayList<SMS> addYearFromSMSes(int year, int threadID){
		YearMonthConvo ym;
		for(int i = 0; i < DateView.MONTHS_NAME.length; i++){
			ym = new YearMonthConvo(new YearMonth(year, DateView.MONTHS_NAME[i]), threadID);
			if(!datesFromThreads.contains(ym)){
				datesFromThreads.add(ym);
				BinView.getInstance().addDate(ym.getYearMonth());
			}
		}
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
				if(!smses.contains(tempSMS) && tempSMS.getYear() == year){
					newSMSes.add(tempSMS);
				}
			}
			while(cursor.moveToNext());
		}
		calculateSMSes(newSMSes);
		updateThreadIDsADD(newSMSes); // must be done after smses list has been updated (done int calculateSMSes() method)
		MainView.getInstance().updateSMSView();
		return orderSMSByDate(newSMSes);
	}
	
	public static ArrayList<SMS> addDate(YearMonth date){
		dates.add(date);
		BinView.getInstance().addDate(date);
		Uri uri = Uri.parse(HaikuActivity.ALLBOXES);
		Cursor cursor = MainView.getInstance().getContext().getContentResolver().query(uri, null, null, null, null);
		ArrayList<SMS> newSMSes = new ArrayList<SMS>();
		SMS tempSMS;
		if (cursor.moveToFirst()) {
			do{
				tempSMS = new SMS(cursor.getInt(cursor.getColumnIndexOrThrow("_id")), 
						cursor.getString(cursor.getColumnIndexOrThrow("body")), 
						cursor.getString(cursor.getColumnIndexOrThrow("date")), 
						cursor.getLong(cursor.getColumnIndexOrThrow("thread_id")),
						cursor.getString(cursor.getColumnIndexOrThrow("type")));
				if(!smses.contains(tempSMS) && tempSMS.getYearMonth().equals(date)){
					newSMSes.add(tempSMS);
				}
			}
			while(cursor.moveToNext());
		}
		calculateSMSes(newSMSes);
		updateThreadIDsADD(newSMSes); // must be done after smses list has been updated (done int calculateSMSes() method)
		MainView.getInstance().updateConversationsVisibility();
		return orderSMSByDate(newSMSes);
	}
	
	public static ArrayList<SMS> addDateFromSMSes(YearMonth date, int threadID){
		datesFromThreads.add(new YearMonthConvo(date, threadID));
		BinView.getInstance().addDate(date);
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
		calculateSMSes(newSMSes);
		updateThreadIDsADD(newSMSes); // must be done after smses list has been updated (done int calculateSMSes() method)
		MainView.getInstance().updateSMSView();
		return orderSMSByDate(newSMSes);
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
		dates.remove(date);
		for(int i = datesFromThreads.size()-1; i >= 0; i--){
			if(datesFromThreads.get(i).getYearMonth().equals(date)){
				datesFromThreads.remove(i);
			}
		}
		for(int i = smses.size()-1; i >= 0; i--){
			if(smses.get(i).getYearMonth().equals(date)){
				removedSMS.add(smses.get(i));
//				smses.remove(i); // old
				removeSMSWithoutUpdate(smses.get(i)); // new
			}
		}
		MainView.getInstance().updateSMSView();
		return removedSMS;
	}
	
	public static void updateThreadIDsADD(ArrayList<SMS> smsesAdded){
		double startTime = System.currentTimeMillis();
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
			MainView.getInstance().updateConversationsVisibility();
		}
		Log.i("TAG", "Time to check if whole conversations were added: " + (System.currentTimeMillis() - startTime) + " ms");
	}
	
	public static ArrayList<YearMonth> getDates(){
		return dates;
	}
	
	public static ArrayList<YearMonthConvo> getDateConvos(){
		return datesFromThreads;
	}
	
	public static ArrayList<Theme> getAllThemes(){
		return allThemes;
	}
	
	private static void initThemes(){
		allThemes.addAll(HaikuActivity.databaseHandler.getAllThemes());
		theAllTheme = HaikuActivity.databaseHandler.getTheAllTheme();
		theAllThemeWordIDs.addAll(theAllTheme.getWordids()); // Will always be there
	}
	
	public static double testStartTime;
	private static int createdHaikusCounter;
	
	public static synchronized void createHaikus(){ //TODO
		nullHaikuGenerated = false;
		allGenerated = false;
		testStartTime = System.currentTimeMillis();
		haikus.clear();
		generationsCounter = 4;
		createdHaikusCounter = 0;
//		printAllUsableWords();
		haikus.add(new Haiku(!themes.isEmpty()));
		haikus.add(new Haiku(!themes.isEmpty()));
		haikus.add(new Haiku(!themes.isEmpty()));
		haikus.add(new Haiku(!themes.isEmpty()));
		
	}
	
	private static boolean nullHaikuGenerated = false;
	private static boolean allGenerated = false;
	
	public static synchronized void nullHaikuGenerated(){
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
			haikus.add(new Haiku(!themes.isEmpty()));
		}
		else if(createdHaikusCounter == NUMBER_OF_GENERATIONS){
			Log.i("TAG", "Time to generate all haikus: " + (System.currentTimeMillis()-testStartTime) + " ms");
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
	private static void updateUseWords(){
		for(PartOfSpeechList pl : allWordsUsedWithThemesOrderedByTypes){
			pl.getWords().clear();
		}
		if(themes.isEmpty()){
			// no theme(s) -> use all words
			addWordsWithTheme(allSmsLogWords);
			return;
		}
		for(int i = 0; i < allSmsLogWords.size(); i++){
			if(themeWordIDs.contains(allSmsLogWords.get(i).getID())){
				addWordWithTheme(allSmsLogWords.get(i));
			}
		}
//		smsLogWordsWithThemes.clear();
//		if(themes.isEmpty()){
//			smsLogWordsWithThemes.addAll(allSmsLogWords); // no theme(s) -> use all words
//			return;
//		}
//		for(int i = 0; i < allSmsLogWords.size(); i++){
//			if(themeWordIDs.contains(allSmsLogWords.get(i).getID())){
//				smsLogWordsWithThemes.add(allSmsLogWords.get(i));
//			}
//		}
	}
	
	public static ArrayList<PartOfSpeechList> getWordsUsed(){
		return allWordsUsedWithThemesOrderedByTypes;
	}
	
	public static ArrayList<Word> getWordsUsedWithTheAllTheme(){
		return smsLogWordsWithTheAllTheme;
	}
	
//	public static void printAllUsableWords(){
//		for(int i = 0; i < smsLogWordsWithThemes.size(); i++){
//			smsLogWordsWithThemes.get(i).print("TAG2");
//		}
//		Log.i("TAG2", "Number of usable words with selected themes: " + smsLogWordsWithThemes.size());
//		
//		for(int i = 0; i < smsLogWordsWithTheAllTheme.size(); i++){
//			smsLogWordsWithTheAllTheme.get(i).print("TAG2");
//		}
//		Log.i("TAG2", "Total number of usable words with the all theme: " + smsLogWordsWithTheAllTheme.size());
//		Log.i("TAG2", "Total number of usable words: " + (smsLogWordsWithThemes.size() + smsLogWordsWithTheAllTheme.size()));
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
//		Log.i("TAG2", "sentence: " + sentence);
//		for(int i = 0; i < notRealWords.size(); i++){
//			Log.i("TAG2", notRealWords.get(i));
//		}
		return notRealWords;
	}
}
