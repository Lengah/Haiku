package haiku.top.model.generator;

import haiku.top.model.date.YearMonth;
import haiku.top.model.smshandler.SMS;

import java.util.ArrayList;

public class DateSMS {
	private YearMonth yearMonth;
	private ArrayList<Long> smsIDs = new ArrayList<Long>();
	private long conversationID;
	public static final long ALL_CONVERSATIONS_ID = -1;
	
	public DateSMS(YearMonth yearMonth, long conversationID){
		this.yearMonth = yearMonth;
		this.conversationID = conversationID;
	}
	
	public DateSMS(YearMonth yearMonth, ArrayList<SMS> sms, long conversationID){
		this.yearMonth = yearMonth;
		this.conversationID = conversationID;
		addSMS(sms);
	}
	
	public YearMonth getYearMonth(){
		return yearMonth;
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
