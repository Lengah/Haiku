package haiku.top.model;

public abstract class Text {
	protected String text;
	
	public Text(String text){
		this.text = text;
	}
	
	public String getText(){
		return text;
	}
}
