package haiku.top.model;

public class SMS {
	private long id;
	private String message;
	private Contact contact;
	// date!
	
	public SMS(long id, String message, Contact contact){
		this.id = id;
		this.message = message;
		this.contact = contact;
	}
	
	public long getID(){
		return id;
	}
	
	public String getMessage(){
		return message;
	}
	
	public Contact getContact(){
		return contact;
	}
}
