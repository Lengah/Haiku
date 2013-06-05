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
		DatabaseHandler databaseHandler = new DatabaseHandler(MainView.getInstance().getContext());
        try {
        	databaseHandler.openReadOnlyDataBase();
        } catch(SQLException sqle){
        	throw sqle; 
        }
		databaseHandler.initSMSES(smses);
		for(int i = 0; i < smses.size(); i++){
			HaikuGenerator.addSMS(smses.get(i));
		}
		Log.i("TAG", "smses Worker executed in: " + (System.currentTimeMillis() - startTime) + " ms");
	}
}
