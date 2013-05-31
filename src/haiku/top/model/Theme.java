package haiku.top.model;

import haiku.top.HaikuActivity;

import java.util.ArrayList;

public class Theme {
	private long id;
	private String name;
	private ArrayList<Long> wordids;
	
	public Theme(long id, String name){
		this.id = id;
		this.name = name;
	}
	
	public ArrayList<Long> getWordids() {
		if (wordids == null)
			wordids = HaikuActivity.databaseHandler.initTheme(this);
		return wordids;
	}
	
	public long getID(){
		return id;
	}
	
	public String getName(){
		return name;
	}
	
}
