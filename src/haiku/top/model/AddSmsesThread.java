package haiku.top.model;

import haiku.top.HaikuActivity;

import java.util.ArrayList;

import android.util.Log;

public class AddSmsesThread extends Thread{
	private ArrayList<SMS> smses;
	
	public AddSmsesThread(ArrayList<SMS> smses){
		this.smses = smses;
	}
	
	public void run(){
		double startTime = System.currentTimeMillis();
		HaikuActivity.databaseHandler.initSMSES(smses);
		for(int i = 0; i < smses.size(); i++){
			HaikuGenerator.addSMS(smses.get(i));
		}
		Log.i("TAG", "smses Worker executed in: " + (System.currentTimeMillis() - startTime) + " ms");
	}
}
