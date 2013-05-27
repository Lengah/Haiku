package haiku.top.model;

public class Theme {
	private long id;
	private String name;
	
	public Theme(long id, String name){
		this.id = id;
		this.name = name;
	}
	
	public long getID(){
		return id;
	}
	
	public String getName(){
		return name;
	}
	
}
