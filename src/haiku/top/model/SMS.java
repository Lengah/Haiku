package haiku.top.model;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SMS {
	private long id;
	private String message;
	private String date;
	private long contactID;
	private static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
	
	public SMS(long id, String message, String date, long contactID){
		this.id = id;
		this.message = message;
		this.date = date;
		this.contactID = contactID;
	}
	
	public SMS(){
		
	}
	
	/**
	 * 
	 * @return A long integer
	 */
	public String getDate(){
		return date;
	}
	
	/**
	 * converts the date to a more readable format
	 * @return dd/MM/yyyy HH:mm
	 */
	public String getFullDate(){
		Date obj = new Date(Long.parseLong(date));
		return sdf.format(obj.getTime());
	}
	
	public void setDate(String date){
		this.date = date;
	}
	
	public long getID(){
		return id;
	}
	
	public void setID(long id){
		this.id = id;
	}
	
	public String getMessage(){
		return message;
	}
	
	public void setMessage(String message){
		this.message = message;
	}
	
	public long getContactID(){
		return contactID;
	}
	
	public void setContact(long contactID){
		this.contactID = contactID;
	}
	
	@Override
	public boolean equals(Object sms){
		if(sms instanceof SMS && this.id == ((SMS)sms).getID()){
			return true;
		}
		return false;
	}
}
