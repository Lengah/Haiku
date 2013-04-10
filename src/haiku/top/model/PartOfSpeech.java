package haiku.top.model;

public class PartOfSpeech {
	private String type;
	
	public PartOfSpeech(){}
	
	public PartOfSpeech(String type){
		this.type = type;
	}
	
	public void setType(String type){
		this.type = type;
	}
	
	public String getType(){
		return type;
	}
}
