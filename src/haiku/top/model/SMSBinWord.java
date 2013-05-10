package haiku.top.model;

// This class is used to show the words in different colors in the bin view
// The string does NOT look like it does in the sms. It is in lower case
public class SMSBinWord {
	private String word;
	private int startPos;
	private int endPos;
	
	public SMSBinWord(String word, int startPos, int endPos){
		this.word = word;
		this.startPos = startPos;
		this.endPos = endPos;
	}
	
	public String getWord(){
		return word;
	}
	
	public int getStartPos(){
		return startPos;
	}
	
	public int getEndPos(){
		return endPos;
	}
}
