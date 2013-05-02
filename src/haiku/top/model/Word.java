package haiku.top.model;

public class Word {
	private String text;
	private String syllables;
	private String wordTypes;
	
	public Word(String dicLine) { 
		//FORMAT: slips|slips|verb (usu participle).intransitive verb.transitive verb.noun.plural noun.singular verb
		text = dicLine.substring(0, dicLine.indexOf('|'));
		dicLine = dicLine.substring(text.length()+1);
		syllables = dicLine.substring(0, dicLine.indexOf('|'));
		wordTypes = dicLine.substring(syllables.length()+1);
	}
	
	public Word(String text, String syllables, String wordTypes) { 
		this.text = text;
		this.syllables = syllables;
		this.wordTypes = wordTypes;
	}
	
	public int getNumberOfSyllables() {
		int pos;
		String temp = syllables;
		int counter = 0;
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
	public String getwordTypes() {
		return wordTypes;
	}
}

