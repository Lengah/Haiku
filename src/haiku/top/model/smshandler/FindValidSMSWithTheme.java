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
	
	public FindValidSMSWithTheme(ArrayList<SMS> smses, Theme theme){
		this.smses = smses;
		this.theme = theme;
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
		ArrayList<SMS> addedSMS = calculateSMS(smses, theme);
		for(SMS sms : addedSMS){
			HaikuGenerator.addThemeSMS(sms, theme);
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
	
	public static ArrayList<SMS> calculateSMS(ArrayList<SMS> smses, Theme theme){
		DatabaseHandler.getInstance().initSMSES(smses);
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
}
