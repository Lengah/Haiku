package haiku.top.model;

public class SMS {
	private long id;
	private String message;
	private String date;
	private long contactID;
	
	public SMS(long id, String message, String date, long contactID){
		this.id = id;
		this.message = message;
		this.date = date;
		this.contactID = contactID;
	}
	
	public SMS(){
		
	}
	
	public String getDate(){
		return date;
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
}
