package haiku.top.model;

import java.util.ArrayList;

public class Word {
	private String text;
	private String syllables;
	private ArrayList<String> wordTypes; //parthofspeech
	private ArrayList<String> themes;
	
	public Word(String text, String syllables, ArrayList<String> wordTypes, ArrayList<String> themes) { 
		this.text = text;
		this.syllables = syllables;
		this.wordTypes = new ArrayList<String>(wordTypes);
		this.themes = new ArrayList<String>(themes);
	}
	
	public String getText() {
		return text;
	}
	public String getSyllables() {
		return syllables;
	}
	
	public int getNumberOfSyllables() {
		int pos;
		String temp = syllables;
		int counter = 1;
		while((pos = temp.indexOf('·')) != -1) {
			counter++;
			temp = temp.substring(pos+1);
		}
		return counter;
	}
	
	public ArrayList<String> getwordTypes() {
		return new ArrayList<String>(wordTypes);
	}
	
	public ArrayList<String> getThemes() {
		return new ArrayList<String>(themes);
	}
}

