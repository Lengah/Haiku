package haiku.top.model;

import java.util.ArrayList;

public class Contact {
	private long id;
	private String name;
	private ArrayList<SMS> sms = new ArrayList<SMS>();
	
	public Contact(long id, String name){
		this.id = id;
		this.name = name;
	}
	
	public void addSMS(SMS sms){
		this.sms.add(sms);
	}
	
	public void removeSMS(int index){
		this.sms.remove(index);
	}
	
	public void removeSMS(SMS sms){
		this.sms.remove(sms);
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public void setID(long id){
		this.id = id;
	}
	
	public String getName(){
		return this.name;
	}
	
	public long getID(){
		return id;
	}
	
	public ArrayList<SMS> getSMS(){
		return sms;
	}
}
