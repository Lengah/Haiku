package haiku.top.model.generator;

import haiku.top.model.smshandler.SMS;
import java.util.ArrayList;

public class ThemeSMS {
	private long themeID;
	private ArrayList<Long> smsIDs = new ArrayList<Long>();
	private long conversationID;
	
	public ThemeSMS(long themeID, long conversationID){
		this.themeID = themeID;
		this.conversationID = conversationID;
	}
	
	public ThemeSMS(long themeID, ArrayList<SMS> sms, long conversationID){
		this.themeID = themeID;
		this.conversationID = conversationID;
		addSMS(sms);
	}
	
	public long getThemeID(){
		return themeID;
	}
	
	public long getConversationID(){
		return conversationID;
	}
	
	public ArrayList<Long> getSMSIDs(){
		return smsIDs;
	}
	
	public void addSMSIDs(ArrayList<Long> ids){
		smsIDs.addAll(ids);
	}
	
	public void addSMS(ArrayList<SMS> sms){
		for(SMS s : sms){
			smsIDs.add(s.getID());
		}
	}
	
	public void addSMSID(long id){
		smsIDs.add(id);
	}
}
