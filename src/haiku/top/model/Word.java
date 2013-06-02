package haiku.top.model;

import java.util.ArrayList;

public class Word {
	private long id;
	private String text;
	private String syllables;
	private String wordType; //parthofspeech
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
	
	public Word(long id, String text, String syllables, String wordType) {
		this.id = id;
		this.text = text;
		this.syllables = syllables;
		this.wordType = wordType;
		initNumberOfSyllables();
	}
	
//	/**
//	 * If a word is created by this constructor, then wordTypes should be added in the near future
//	 * @param id
//	 * @param text
//	 * @param syllables
//	 */
//	public Word(long id, String text, String syllables) {
//		this.id = id;
//		this.text = text;
//		this.syllables = syllables;
//		initNumberOfSyllables();
//	}
//	
//	public void addWordType(PartOfSpeech wordType){
//		if(wordTypes == null){
//			wordTypes = new ArrayList<String>();
//		}
//		wordTypes.add(wordType.getType());
//	}
	
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
	
	public String getwordType() {
		return wordType;
	}
	
	@Override
	public boolean equals(Object word){
		if(word instanceof Word && this.id == ((Word)word).getID()){
			return true;
		}
		return false;
	}
}