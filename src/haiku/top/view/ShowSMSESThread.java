package haiku.top.view;

import haiku.top.HaikuActivity;
import haiku.top.model.SMS;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

public class ShowSMSESThread extends Thread{
	private int threadID;
	private boolean stop = false;
	
	public ShowSMSESThread(int threadID){
		this.threadID = threadID;
	}
	
	public void run(){
		Context context = MainView.getInstance().getContext();
		Cursor cursor = HaikuActivity.getThread(context, threadID);
		Log.i("TAG", "Count: " + cursor.getCount());
		if (cursor.moveToFirst()) {
			SMSObjectView smsObject;
			do{
				smsObject = new SMSObjectView(context, cursor.getString(cursor.getColumnIndexOrThrow("type")),new SMS(cursor.getInt(cursor.getColumnIndexOrThrow("_id")), cursor.getString(cursor.getColumnIndexOrThrow("body")), cursor.getString(cursor.getColumnIndexOrThrow("date")), threadID));
				MainView.getInstance().addSMSToView(smsObject);
			}
			while(cursor.moveToNext() && !stop);
		}
		cursor.close();
		MainView.getInstance().removeWorkerThread();
	}
	
	public void stopWorking(){
		stop = true;
	}
	
}
