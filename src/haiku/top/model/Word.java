package haiku.top.model;

public class Word {
	private long id;
	private String text;
	private String syllables;
//	private int numberOfSyllables;
//	private String wordType;
	
//	public Word(String word, String syllables, String wordType){
//		this.word = word;
//		this.syllables = syllables;
//		this.wordType = wordType;
//		countSyllables();
//	}
	
//	/**
//	 * The String must be: word|syllables|wordtype(s)
//	 * @param text - String|String|String
//	 */
//	public Word(String text){
//		word = text.substring(0, text.indexOf('|'));
//		text = text.substring(text.indexOf('|')+1);
//		syllables = text.substring(0, text.indexOf('|'));
//		wordType = text.substring(text.lastIndexOf('|')+1);
//		countSyllables();
//	}
	
	public Word(){}
	
	public Word(String text, String syllables){
		this.text = text;
		this.syllables = syllables;
	}
	
	public int countSyllables(){
		int pos;
		String temp = syllables;
		int counter = 0;
		while((pos = temp.indexOf('·')) != -1){
			counter++;
			temp = temp.substring(pos+1);
		}
		return counter;
//		this.numberOfSyllables = counter;
	}
	
	public void setID(long id){
		this.id = id;
	}
	
	public long getID(){
		return id;
	}
	
	public void setText(String text){
		this.text = text;
	}
	
	public String getText(){
		return text;
	}
	
	public void setSyllables(String syllables){
		this.syllables = syllables;
	}
	
	public String getSyllables(){
		return syllables;
	}
	
//	public int getNumberOfSyllables(){
//		return numberOfSyllables;
//	}
	
//	public String getWordType(){
//		return wordType;
//	}

//	public String toString(){
//		return word + "|" + syllables + "|" + wordType;
//	}
//	
//	public boolean equals(Word word){
//		if(this.word == word.getWord() && this.numberOfSyllables == word.getNumberOfSyllables() && this.wordType == word.getWordType()){
//			return true;
//		}
//		return false;
//	}
}

