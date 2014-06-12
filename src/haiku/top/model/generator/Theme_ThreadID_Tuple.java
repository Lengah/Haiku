package haiku.top.model.generator;

public class Theme_ThreadID_Tuple {
	private long themeID;
	private long threadID;
	
	public Theme_ThreadID_Tuple(long themeID, long threadID){
		this.themeID = themeID;
		this.threadID = threadID;
	}
	
	public long getThreadID(){
		return threadID;
	}
	
	public long getThemeID(){
		return themeID;
	}
}
