package haiku.top.model.smshandler;

import java.util.ArrayList;

import haiku.top.HaikuActivity;
import haiku.top.view.main.MainView;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

public class ShowSMSESThread extends Thread{
	private int threadID;
	private boolean stop = false;
	private boolean lookingAtHaikus;
//	private int recipients;
	
//	private String lastMessage = null;
//	private int recipientsCounter = 0;
	
//	private ArrayList<Integer> unallowedIDs = new ArrayList<Integer>();
	
	public ShowSMSESThread(int threadID, boolean lookingAtHaikus){
		this.threadID = threadID;
		this.lookingAtHaikus = lookingAtHaikus;
//		recipients = HaikuActivity.getConversationNumbers(HaikuActivity.getInstance(), threadID).size();
//		if(recipients > 1){
//			this.lookingAtHaikus = false;
//		}
	}
	
	public void run(){
		Context context = MainView.getInstance().getContext();
		Cursor cursor;
		if(lookingAtHaikus){
			cursor = HaikuActivity.getHaikuThread(context, threadID);
		}
		else{
			cursor = HaikuActivity.getThread(context, threadID);
		}
//		Log.i("TAG", "Count: " + cursor.getCount());
		int id;
		String message;
		String date;
		String type;
		if (cursor.moveToFirst()) {
			SMS sms;
			do{
				id = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
				message = cursor.getString(cursor.getColumnIndexOrThrow("body"));
				date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
				type = cursor.getString(cursor.getColumnIndexOrThrow("type"));
				
//				if(unallowedIDs.contains(id)){
//					continue;
//				}
//				// not in the list
//				if (!type.equals("1")) {
////			        // sent = true;
//					for(int i = 0 ; i < recipients; i++){
//						unallowedIDs.add(id+i);
//					}
//				}
				
				sms = new SMS(id, message, date, threadID, type);
				MainView.getInstance().addSMSToView(sms);
//				Log.i("TAG4", "id: " + id);
//				if (!type.equals("1")) {
//			        // sent = true;
//					// only sent SMS will be shown multiple times
//					if(recipientsCounter == 0){
//						// first message in the block
//						lastMessage = message;
//						recipientsCounter++;
//						//add
//						Log.i("TAG4", "add1: " + message);
//						sms = new SMS(id, message, date, threadID, type);
//						MainView.getInstance().addSMSToView(sms);
//						continue;
//					}
//					else{
//						recipientsCounter++;
//						if(message.equals(lastMessage) && recipientsCounter <= recipients){
//							// the same message was sent to multiple recipients, but we only want to show it once -> discard this one
//							Log.i("TAG4", "discarded: " + message);
//							continue;
//						}
//						// don't discard this one. Leave the if statement, reset and add the SMS
//					}
//			    }
//				//reset
//				recipientsCounter = 0;
//				lastMessage = null;
//				
//				//add
//				lastMessage = message;
//				recipientsCounter = 1;
//				Log.i("TAG4", "add2: " + message);
//				sms = new SMS(id, message, date, threadID, type);
//				MainView.getInstance().addSMSToView(sms);
			}
			while(cursor.moveToNext() && !stop);
		}
		cursor.close();
		MainView.getInstance().removeWorkerThread();
	}
	
	// Old - With the old SMS view which was just an oval (works fine)
//	public void run(){
//		Context context = MainView.getInstance().getContext();
//		Cursor cursor;
//		if(lookingAtHaikus){
//			cursor = HaikuActivity.getHaikuThread(context, threadID);
//		}
//		else{
//			cursor = HaikuActivity.getThread(context, threadID);
//		}
//		Log.i("TAG", "Count: " + cursor.getCount());
//		if (cursor.moveToFirst()) {
//			SMSObjectView smsObject;
//			do{
//				smsObject = new SMSObjectView(context, cursor.getString(cursor.getColumnIndexOrThrow("type")),new SMS(cursor.getInt(cursor.getColumnIndexOrThrow("_id")), cursor.getString(cursor.getColumnIndexOrThrow("body")), cursor.getString(cursor.getColumnIndexOrThrow("date")), threadID));
//				MainView.getInstance().addSMSToView(smsObject);
//			}
//			while(cursor.moveToNext() && !stop);
//		}
//		cursor.close();
//		MainView.getInstance().removeWorkerThread();
//	}
	
	public void stopWorking(){
		stop = true;
	}
	
}
