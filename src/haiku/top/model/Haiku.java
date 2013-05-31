package haiku.top.model;

import android.util.Log;

public class Haiku {
	private String poem;
	
	public String getHaikuPoem(){
		return poem;
	}
	
	public void generate(){
		//TEST
//		poem = HaikuGenerator.getPartOfSentence("<sentence>");
//		while(poem.contains("a/an")){
//			int index = poem.indexOf("a/an");
//			index += 5;
//			if(poem.charAt(index) == 'a' || poem.charAt(index) == 'e' || poem.charAt(index) == 'u' || poem.charAt(index) == 'i' || poem.charAt(index) == 'o'){
//				poem = poem.substring(0, index-5) + "an " + poem.substring(index);
//			}
//			else{
//				poem = poem.substring(0, index-5) + "a " + poem.substring(index);
//			}
//		}
//		while(poem.contains(" , ")){
//			int index = poem.indexOf(" , ");
//			poem = poem.substring(0, index) + ", " + poem.substring(index+3);
//		}
//		poem = poem.substring(0, 1).toUpperCase() + poem.substring(1);
		// /TEST
		// First row
		String row1 = HaikuGenerator.findSentenceWithSyllables(5);
		// Second row
		String row2 = HaikuGenerator.findSentenceWithSyllables(7);
		// Third row
		String row3 = HaikuGenerator.findSentenceWithSyllables(5);
		poem = row1 + "\n" + row2 + "\n" + row3;
		Log.i("TAG", poem);
	}
}
