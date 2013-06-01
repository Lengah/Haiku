package haiku.top.model;

public class PartOfSpeech {
	private long id;
	private String type;
	
	public PartOfSpeech(long id, String type){
		this.id = id;
		this.type = type;
	}
	
	public long getID(){
		return id;
	}
	
	public String getType(){
		return type;
	}
}
