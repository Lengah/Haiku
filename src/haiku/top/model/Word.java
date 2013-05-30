package haiku.top.model;

import java.util.ArrayList;

public class Word {
	private String text;
	private String syllables;
	private ArrayList<String> wordTypes;
	
/*	public Word(String dicLine) { 
		//FORMAT: slips|slips|verb (usu participle).intransitive verb.transitive verb.noun.plural noun.singular verb
		text = dicLine.substring(0, dicLine.indexOf('|'));
		dicLine = dicLine.substring(text.length()+1);
		syllables = dicLine.substring(0, dicLine.indexOf('|'));
		wordTypes = dicLine.substring(syllables.length()+1);
	}*/
	
	public Word(String text, String syllables, ArrayList<String> wordTypes) { 
		this.text = text;
		this.syllables = syllables;
		this.wordTypes = new ArrayList<String>(wordTypes);
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
	
	public String getText() {
		return text;
	}
	public String getSyllables() {
		return syllables;
	}
	
	public ArrayList<String> getwordTypes() {
		return new ArrayList<String>(wordTypes);
	}
}

