package haiku.top.model;

import java.util.ArrayList;

public class Haiku {
	private ArrayList<Theme> themes;
	private String poem;
	private ArrayList<Word> wordsUsed;
	
	public Haiku(){
		themes = HaikuGenerator.getThemes();
	}
	
	public String getHaikuPoem(){
		return poem;
	}
	
	public void generate(){
		//TEST
		poem = HaikuGenerator.getPartOfSentence("<sentence>");
		while(poem.contains("a/an")){
			int index = poem.indexOf("a/an");
			index += 5;
			if(poem.charAt(index) == 'a' || poem.charAt(index) == 'e' || poem.charAt(index) == 'u' || poem.charAt(index) == 'i' || poem.charAt(index) == 'o'){
				poem = poem.substring(0, index-5) + "an " + poem.substring(index);
			}
			else{
				poem = poem.substring(0, index-5) + "a " + poem.substring(index);
			}
		}
		while(poem.contains(" , ")){
			int index = poem.indexOf(" , ");
			poem = poem.substring(0, index) + ", " + poem.substring(index+3);
		}
		poem = poem.substring(0, 1).toUpperCase() + poem.substring(1);
		// /TEST
		// First row
		
		// Second row
		// Third row
	}
	
	public ArrayList<Theme> getThemes(){
		return themes;
	}
	
	public ArrayList<Word> getWordsUsed(){
		return wordsUsed;
	}
}
