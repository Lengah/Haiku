package haiku.top.model;

import java.util.ArrayList;

public class Word {
	private long id;
	private String text;
	private String syllables;
	private ArrayList<String> wordTypes; //parthofspeech
	private int numberOfSyllables;

	/**
	 * FORMAT: slips|slips|verb (usu participle).intransitive verb.transitive verb.noun.plural noun.singular verb
	 * @param dicLine
	 */
	/*public Word(String dicLine) {
		text = dicLine.substring(0, dicLine.indexOf('|'));
		dicLine = dicLine.substring(dicLine.indexOf('|')+1);
		syllables = dicLine.substring(0, dicLine.indexOf('|'));
		dicLine = dicLine.substring(dicLine.indexOf('|')+1);
		wordTypes = new ArrayList<String>();
		while(dicLine.contains(".")){
			wordTypes.add(dicLine.substring(0, dicLine.indexOf('.')));
			dicLine = dicLine.substring(dicLine.indexOf('.')+1);
		}
		// last word type left (what's left of the string)
		wordTypes.add(dicLine);
		initNumberOfSyllables();
	}*/
	
	public Word(long id, String text, String syllables, ArrayList<String> wordTypes) {
		this.id = id;
		this.text = text;
		this.syllables = syllables;
		this.wordTypes = new ArrayList<String>(wordTypes);
		initNumberOfSyllables();
	}
	
	public long getID(){
		return id;
	}
	
	public String getText() {
		return text;
	}
	public String getSyllables() {
		return syllables;
	}
	
	public void initNumberOfSyllables() {
		int pos;
		String temp = syllables;
		int counter = 1;
		while((pos = temp.indexOf('·')) != -1) {
			counter++;
			temp = temp.substring(pos+1);
		}
		numberOfSyllables = counter;
	}
	
	public int getNumberOfSyllables(){
		return numberOfSyllables;
	}
	
	public ArrayList<String> getwordTypes() {
		return wordTypes;
	}
	
	@Override
	public boolean equals(Object word){
		if(word instanceof Word && this.id == ((Word)word).getID()){
			return true;
		}
		return false;
	}
}