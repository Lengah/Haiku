package haiku.top.model.smshandler;

import haiku.top.model.generator.HaikuGenerator;
import android.util.Log;

public class AddSmsThread extends Thread{
	private SMS sms;
	
	public AddSmsThread(SMS sms){
		this.sms = sms;
	}
	
	public void run(){
		double startTime = System.currentTimeMillis();
		sms.getWords();
		HaikuGenerator.addSMS(sms);
		Log.i("TAG", "sms Worker executed in: " + (System.currentTimeMillis() - startTime) + " ms");
	}
}
