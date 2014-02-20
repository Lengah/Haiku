package haiku.top.model.smshandler;

import haiku.top.HaikuActivity;
import haiku.top.view.main.MainView;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

public class ShowSMSESThread extends Thread{
	private int threadID;
	private boolean stop = false;
	private boolean lookingAtHaikus;
	
	public ShowSMSESThread(int threadID, boolean lookingAtHaikus){
		this.threadID = threadID;
		this.lookingAtHaikus = lookingAtHaikus;
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
		Log.i("TAG", "Count: " + cursor.getCount());
		if (cursor.moveToFirst()) {
			SMS sms;
			do{
				sms = new SMS(cursor.getInt(cursor.getColumnIndexOrThrow("_id")), cursor.getString(cursor.getColumnIndexOrThrow("body")), cursor.getString(cursor.getColumnIndexOrThrow("date")), threadID, cursor.getString(cursor.getColumnIndexOrThrow("type")));
				MainView.getInstance().addSMSToView(sms);
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
