package haiku.top.model.generator;

import java.util.ArrayList;

import haiku.top.model.PartOfSpeech;
import haiku.top.model.Word;

public class PartOfSpeechList {
	private PartOfSpeech partOfSpeech;
	private ArrayList<Word> words = new ArrayList<Word>();
	
	public PartOfSpeechList(PartOfSpeech partOfSpeech){
		this.partOfSpeech = partOfSpeech;
	}
	
	public PartOfSpeech getPartOfSpeech(){
		return partOfSpeech;
	}
	
	public ArrayList<Word> getWords(){
		return words;
	}
	
	/**
	 * Doesn't have to exist in the list. Removes ALL occurrences of the word.
	 * @param word
	 */
	public void removeWord(Word word){
		for(int i = words.size()-1; i >= 0; i--){
			if(words.get(i).equals(word)){
				words.remove(i);
			}
		}
	}
}
