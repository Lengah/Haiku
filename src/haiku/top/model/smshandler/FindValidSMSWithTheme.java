package haiku.top.model.smshandler;

import haiku.top.HaikuActivity;
import haiku.top.model.Theme;
import haiku.top.model.Word;
import haiku.top.model.generator.HaikuGenerator;
import haiku.top.model.sql.DatabaseHandler;
import haiku.top.view.binview.BinView;
import haiku.top.view.main.MainView;

import java.util.ArrayList;

import android.util.Log;


public class FindValidSMSWithTheme extends Thread{
	private ArrayList<SMS> smses;
	private Theme theme;
	private long threadID;
	
	public FindValidSMSWithTheme(ArrayList<SMS> smses, Theme theme, long threadID){
		this.smses = smses;
		this.theme = theme;
		this.threadID = threadID;
	}
	
	public void run(){
//		DatabaseHandler.getInstance().initSMSES(smses);
//		boolean shouldAdd;
//		ArrayList<SMS> addedSMS = new ArrayList<SMS>();
//		for(SMS sms : smses){
//			shouldAdd = false;
//			for(Word word : sms.getWords()){
//				for(Long wordID : theme.getWordids()){
//					if(word.getID() == wordID){
//						shouldAdd = true;
//						break;
//					}
//				}
//				if(shouldAdd){
//					break;
//				}
//			}
//			if(shouldAdd){
//				addedSMS.add(sms);
//				HaikuGenerator.addThemeSMS(sms, theme);
//			}
//		}
		DatabaseHandler.getInstance().initSMSES(smses);
		ArrayList<SMS> addedSMS = calculateSMS(smses, theme);
		for(SMS sms : addedSMS){
			HaikuGenerator.addThemeSMS(sms, theme, threadID);
		}
		HaikuGenerator.updateUseWords();
		HaikuGenerator.updateThreadIDsADD(addedSMS);
//		BinView.getInstance().addSMSesAtLastPosition(addedSMS);
		HaikuActivity.getInstance().runOnUiThread(new Runnable(){           
	        @Override
	        public void run(){
				MainView.getInstance().updateSMSView();
			}
		});
		HaikuGenerator.removeThread(this);
	}
	
	/**
	 * Filters the entered SMS list and returns those SMS which fit the entered theme
	 * @param smses
	 * @param theme
	 * @return
	 */
	public static ArrayList<SMS> calculateSMS(ArrayList<SMS> smses, Theme theme){
		boolean shouldAdd;
		ArrayList<SMS> smsToAdd = new ArrayList<SMS>();
		for(SMS sms : smses){
			shouldAdd = false;
			for(Word word : sms.getWords()){
				for(Long wordID : theme.getWordids()){
					if(word.getID() == wordID){
						shouldAdd = true;
						break;
					}
				}
				if(shouldAdd){
					break;
				}
			}
			if(shouldAdd){
				smsToAdd.add(sms);
			}
		}
		return smsToAdd;
	}
	
	/**
	 * Filters the entered SMS list and returns those SMS which fit the entered themes
	 * @param smses
	 * @param themes
	 * @return
	 */
	public static ArrayList<SMS> calculateSMS(ArrayList<SMS> smses, ArrayList<Theme> themes){
		boolean shouldAdd;
		ArrayList<Long> themeWordIDs = new ArrayList<Long>();
		for(Theme theme : themes){
			themeWordIDs.addAll(theme.getWordids());
		}
		ArrayList<SMS> smsToAdd = new ArrayList<SMS>();
		for(SMS sms : smses){
			shouldAdd = false;
			for(Word word : sms.getWords()){
				for(Long wordID : themeWordIDs){
					if(word.getID() == wordID){
						shouldAdd = true;
						break;
					}
				}
				if(shouldAdd){
					break;
				}
			}
			if(shouldAdd){
				smsToAdd.add(sms);
			}
		}
		return smsToAdd;
	}
	
	public long getThreadID(){
		return threadID;
	}
}
