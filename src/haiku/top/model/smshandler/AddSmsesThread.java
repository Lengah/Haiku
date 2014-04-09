package haiku.top.model.smshandler;

import haiku.top.HaikuActivity;
import haiku.top.model.generator.HaikuGenerator;
import haiku.top.model.sql.DatabaseHandler;
import haiku.top.view.main.MainView;

import java.io.IOException;
import java.util.ArrayList;

import android.database.SQLException;
import android.util.Log;

public class AddSmsesThread extends Thread{
	private ArrayList<SMS> smses;
	
	public AddSmsesThread(ArrayList<SMS> smses){
		this.smses = smses;
	}
	
	public void run(){
		double startTime = System.currentTimeMillis();
		// Since this thread will probably use the database under a long period of time it need its own version of the database
//		DatabaseHandler databaseHandler = new DatabaseHandler(MainView.getInstance().getContext());
//        try {
//        	databaseHandler.openReadOnlyDataBase();
//        } catch(SQLException sqle){
//        	throw sqle; 
//        }
		DatabaseHandler.getInstance().initSMSES(smses);
//		boolean shouldSkip;
		for(int i = 0; i < smses.size(); i++){
//			shouldSkip = false;
//			for(int a = i-1; a >= 0; a--){
//				Log.i("TAG4", "smses.get(" + a + ").getContactID(): " + smses.get(a).getContactID());
//				Log.i("TAG4", "smses.get(" + i + ").getContactID(): " + smses.get(i).getContactID());
//				Log.i("TAG4", "smses.get(" + a + ").getMessage().equals(smses.get(i).getMessage()) : " + smses.get(a).getMessage().equals(smses.get(i).getMessage()));
//				Log.i("TAG4", "MainView.getInstance().getConversationObject(smses.get(" + i + ").getContactID()).getNames().size(): " + (MainView.getInstance().getConversationObject(smses.get(i).getContactID()).getNames().size()));
//				Log.i("TAG4", "" + i + "-" + a + ": " + (i-a));
//				if(smses.get(a).getContactID() == smses.get(i).getContactID() 
//						&& smses.get(a).getMessage().equals(smses.get(i).getMessage()) 
//						&& MainView.getInstance().getConversationObject(smses.get(i).getContactID()) != null
//						&& MainView.getInstance().getConversationObject(smses.get(i).getContactID()).getNames().size() > i-a){
//							// probably the same...
//							shouldSkip = true;
//							Log.i("TAG4", "shouldSkip = true");
//							break;
//						}
//			}
//			if(!shouldSkip){
//				Log.i("TAG4", "add");
				HaikuGenerator.addSMS(smses.get(i));
//			}
		}
//		Log.i("TAG", "smses Worker executed in: " + (System.currentTimeMillis() - startTime) + " ms");
		HaikuGenerator.removeThread(this);
	}
}
