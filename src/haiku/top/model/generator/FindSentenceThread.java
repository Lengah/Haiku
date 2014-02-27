package haiku.top.model.generator;

import haiku.top.model.Word;
import java.util.ArrayList;
import java.util.Random;

import android.util.Log;

public class FindSentenceThread extends Thread{
	private int syllables;
	private ArrayList<Word> wordsUsed;
	private ArrayList<Word> backupWords; // words that are in the all theme. will only use these if it can't find one in the wordsUsed list.
	private Random randomGenerator = new Random();
	private Haiku haiku;
	private int row;
	private boolean themes;
	
	public static final String START_OBJECT = "<sentence>";
	public static final double CHANCE_TO_START_WITH_THEME_LIST = 70; // in %
	
	private static final int CHANCE_INCREASE_OF_CUE_WORDS = 5; // all cue words will have a 5 times bigger chance to be selected than any other word
	
	public FindSentenceThread(int numberOfSyllables, Haiku haiku, int row){
		this.syllables = numberOfSyllables;
		this.haiku = haiku;
		this.row = row;
		wordsUsed = new ArrayList<Word>(HaikuGenerator.getWordsUsed());
		backupWords = new ArrayList<Word>(HaikuGenerator.getWordsUsedWithTheAllTheme());
		themes = haiku.containsThemes();
	}
	
	public void run(){
		String sentence = getSentence(START_OBJECT, false);
		if(sentence == null){
			sentence = "NULL";
		}
//		while(sentence.contains("a/an")){
//			int index = sentence.indexOf("a/an");
//			index += 5;
//			if(sentence.charAt(index) == 'a' || sentence.charAt(index) == 'e' || sentence.charAt(index) == 'u' || sentence.charAt(index) == 'i' || sentence.charAt(index) == 'o'){
//				sentence = sentence.substring(0, index-5) + "an " + sentence.substring(index);
//			}
//			else{
//				sentence = sentence.substring(0, index-5) + "a " + sentence.substring(index);
//			}
//		}
		for(int i = 0; i < HaikuGenerator.getNonWordsInRules().size(); i++){
			while(sentence.contains(" " + HaikuGenerator.getNonWordsInRules().get(i) + " ")){
				int index = sentence.indexOf(" " + HaikuGenerator.getNonWordsInRules().get(i) + " ");
				sentence = sentence.substring(0, index) + HaikuGenerator.getNonWordsInRules().get(i) + " " + sentence.substring(index+3);
			}
		}
		sentence = sentence.substring(0, 1).toUpperCase() + sentence.substring(1);
//		sentence = sentence.toUpperCase();
		haiku.addRow(row, sentence);
		if(row != 3){
			haiku.generate(row+1); // start generating the next row
		}
	}
	
	private void updateWordList(ArrayList<Word> words){
		// double all words (so that the avoid duplications has an effect
		for(int i = words.size()-1; i >= 0; i--){
			words.add(words.get(i));
		}
		//cue words
		for(int i = words.size()-1; i >= 0; i--){
			for(int a = 0; a < haiku.getCueWords().size(); a++){
				if(words.get(i).getID() == haiku.getCueWords().get(a)){
					for(int b = 0; b < CHANCE_INCREASE_OF_CUE_WORDS; b++){
						words.add(words.get(i));
					}
				}
			}
		}
		// avoid duplications
		// if the word is used, the chance of it being used again is decreased
		boolean found;
		for(int a = 0; a < haiku.getUsedWords().size(); a++){
			found = false;
			for(int i = words.size()-1; i >= 0; i--){
//				if(words.get(i).equals(haiku.getUsedWords().get(a))){
				if(words.get(i).getText().equalsIgnoreCase(haiku.getUsedWords().get(a).getText())){
					if(found){
						words.remove(i);
					}
					else{
						found = true;
					}
				}
			}
		}
	}
	
	private String getSentence(String structure, boolean inner){
		int randomIndex;
		String returnString;
		String firstPart = null;
		String theRest = null;
		if(structure.charAt(0) == '<'){
			int endIndex = structure.indexOf('>');
			firstPart = structure.substring(0, endIndex+1);
			if(endIndex+1 != structure.length()){
				theRest = structure.substring(endIndex+1);
			}
//			ArrayList<Integer> rowsLeft = new ArrayList<Integer>();
//			int ruleRowIndex;
//			String tempText;
//			for(ruleRowIndex = 0; ruleRowIndex < HaikuGenerator.getRules().length; ruleRowIndex++){
//				tempText = HaikuGenerator.getRules()[ruleRowIndex];
//				if(tempText.contains(firstPart + "=")){
//					int rows = Integer.parseInt(tempText.substring(tempText.indexOf('=')+1));
//					for(int i = 1; i <= rows; i++){
//						rowsLeft.add(i);
//					}
//					break;
//				}
//			}
			ArrayList<WeightedRow> rowsLeft = new ArrayList<WeightedRow>();
			int ruleRowIndex;
			String tempText;
			for(ruleRowIndex = 0; ruleRowIndex < HaikuGenerator.getRules().length; ruleRowIndex++){
				tempText = HaikuGenerator.getRules()[ruleRowIndex];
				if(tempText.contains(firstPart + "=")){
					int rows = Integer.parseInt(tempText.substring(tempText.indexOf('=')+1, tempText.indexOf('|')));
					String weights = tempText.substring(tempText.indexOf('|')+1);
					int weight;
					for(int i = 1; i <= rows; i++){
//						Log.i("TAG", "firstpart: " + firstPart + ", weights: " + weights);
						weight = Integer.parseInt(weights.substring(0, weights.indexOf('.')));
						weights = weights.substring(weights.indexOf('.')+1); //TODO out of bounds?
						rowsLeft.add(new WeightedRow(i, weight));
					}
					break;
				}
			}
			int weightSum;
			int counter;
			int row;
			WeightedRow chosenRow = null;
			while(!rowsLeft.isEmpty()){
//				randomIndex = randomGenerator.nextInt(rowsLeft.size());
//				int row = rowsLeft.get(randomIndex);
				weightSum = 0;
				for(int i = 0; i < rowsLeft.size(); i++){
					weightSum += rowsLeft.get(i).getWeight();
				}
				randomIndex = randomGenerator.nextInt(weightSum)+1; // [1, weightSum]
				counter = 0;
				row = -1; // so it will crash if it can't find it (so I will notice)
				for(int i = 0; i < rowsLeft.size(); i++){
					counter += rowsLeft.get(i).getWeight();
					if(counter >= randomIndex){
						row = rowsLeft.get(i).getRow();
						chosenRow = rowsLeft.get(i);
						break;
					}
				}
				
				tempText = HaikuGenerator.getRules()[ruleRowIndex+row];
				String returnStringOfTheRest = null;
				if(theRest == null){
					// the last object in the tempText structure will also be the last object in the sentence -> call this method again with the tempText structure
					returnString = getSentence(tempText, inner);
				}
				else{
					// the last object in the tempText structure will NOT be the last object in the sentence -> call the inner method
					returnString = getSentence(tempText, true);
					if(returnString != null){
						returnStringOfTheRest = getSentence(theRest, inner);
					}
				}
				if(returnString != null && theRest != null && returnStringOfTheRest != null){
					return returnString + " " + returnStringOfTheRest; // found a sentence!
				}
				else if(returnString != null && theRest == null){
					return returnString; // found a sentence!
				}
				// if here, then the attempt failed
				if(returnString != null){
					// "give back" the syllables
					int number = getWordCount(returnString);
					Word temp;
					for(int i = 0; i < number; i++){
						temp = haiku.getUsedWords().get(haiku.getUsedWords().size()-1);
						syllables += temp.getNumberOfSyllables();
						haiku.removeCueWords(temp.getCueWordIDs());
						haiku.removeUsedWord(temp);
					}
//					syllables += countSyllables(returnString);
				}
				// try another row
//				rowsLeft.remove(randomIndex);
				rowsLeft.remove(chosenRow);
			}
			return null;
		}
		else if(structure.charAt(0) == '('){
			boolean switched = false;
			boolean startWithThemeList = true;
			randomIndex = randomGenerator.nextInt(100+1);
			if(randomIndex > CHANCE_TO_START_WITH_THEME_LIST){
				startWithThemeList = false;
			}
			int endIndex = structure.indexOf(')');
			if(endIndex+1 != structure.length()){
				theRest = structure.substring(endIndex+1);
			}
			String wordType = structure.substring(1, endIndex);
			ArrayList<Word> availableWords;
			if(!themes){
				// if there are no selected themes, then the getWords() method will return all words available. There is no need for a switch
				switched = true;
				availableWords = getWords(wordType);
			}
			else{
				if(startWithThemeList){
					availableWords = getWords(wordType);
				}
				else{
					availableWords = getBackUpWords(wordType);
				}
			}
			if(availableWords.isEmpty() && !switched){
				switched = true;
				if(startWithThemeList){
					availableWords = getBackUpWords(wordType);
				}
				else{
					availableWords = getWords(wordType);
				}
			}
//			Log.i("TAG2", "before: " + availableWords.size());
			updateWordList(availableWords);
//			Log.i("TAG2", "after: " + availableWords.size());
			if(!inner && theRest == null){
				// the last object
				// find a word with the right amount of syllables
				ArrayList<Word> rightAmountOfSyllablesWords = new ArrayList<Word>();
				for(int i = 0; i < availableWords.size(); i++){
					if(availableWords.get(i).getNumberOfSyllables() == syllables){
						rightAmountOfSyllablesWords.add(availableWords.get(i));
					}
				}
				if(rightAmountOfSyllablesWords.isEmpty()){
					// no words found
					// check the other list
					if(startWithThemeList){
						availableWords = getBackUpWords(wordType);
					}
					else{
						availableWords = getWords(wordType);
					}
					updateWordList(availableWords);
					rightAmountOfSyllablesWords = new ArrayList<Word>();
					for(int i = 0; i < availableWords.size(); i++){
						if(availableWords.get(i).getNumberOfSyllables() == syllables){
							rightAmountOfSyllablesWords.add(availableWords.get(i));
						}
					}
					if(rightAmountOfSyllablesWords.isEmpty()){
						// no words found
						return null;
					}
					
				}
				randomIndex = randomGenerator.nextInt(rightAmountOfSyllablesWords.size());
				// A whole sentence has been found!
				haiku.getCueWords().addAll(rightAmountOfSyllablesWords.get(randomIndex).getCueWordIDs());//TODO
				haiku.getUsedWords().add(rightAmountOfSyllablesWords.get(randomIndex));
				return rightAmountOfSyllablesWords.get(randomIndex).getText();
			}
			// not the last object and/or inner is true
			// pick a random word that doesn't have too many syllables
			int tempSyllabels;
			while(!availableWords.isEmpty()){
				randomIndex = randomGenerator.nextInt(availableWords.size());
				syllables -= availableWords.get(randomIndex).getNumberOfSyllables();
				if(syllables <= 0){
					tempSyllabels = availableWords.get(randomIndex).getNumberOfSyllables();
					syllables += tempSyllabels;
					// since we know that words with the same amount of syllables or more as the word we just tried won't work, we can remove them from the list
					availableWords.remove(randomIndex);
					for(int i = availableWords.size()-1; i >= 0; i--){
						if(availableWords.get(i).getNumberOfSyllables() >= tempSyllabels){
							availableWords.remove(i);
						}
					}
					if(availableWords.isEmpty() && !switched){
						switched = true;
						if(startWithThemeList){
							availableWords = getBackUpWords(wordType);
						}
						else{
							availableWords = getWords(wordType);
						}
					}
					continue;
				}
				haiku.getCueWords().addAll(availableWords.get(randomIndex).getCueWordIDs());//TODO
				haiku.getUsedWords().add(availableWords.get(randomIndex));
				if(theRest == null){ // if this is true then so is the boolean inner
					// last object in this structure
					return availableWords.get(randomIndex).getText();
				}
				// not the last object or !inner
				returnString = getSentence(theRest, inner);
				if(returnString == null){
					// the rest of the sentence can not be completed with this word
					tempSyllabels = availableWords.get(randomIndex).getNumberOfSyllables();
					syllables += tempSyllabels;
					haiku.removeCueWords(availableWords.get(randomIndex).getCueWordIDs());
					haiku.removeUsedWord(availableWords.get(randomIndex));
					// since we know that words with the same amount of syllables as the word we just tried won't work, we can remove them from the list
					availableWords.remove(randomIndex);
					for(int i = availableWords.size()-1; i >= 0; i--){
						if(availableWords.get(i).getNumberOfSyllables() == tempSyllabels){
							availableWords.remove(i);
						}
					}
					if(availableWords.isEmpty() && !switched){
						switched = true;
						if(startWithThemeList){
							availableWords = getBackUpWords(wordType);
						}
						else{
							availableWords = getWords(wordType);
						}
						updateWordList(availableWords);
					}
					continue;
				}
				// return string did return something!
				// we have found a complete sentence!
				return availableWords.get(randomIndex).getText() + " " + returnString;
			}
			return null; // no words with the right word types exist in the bin
		}
		else if(structure.charAt(0) == '['){
			int endIndex = structure.indexOf(']');
			firstPart = structure.substring(0, endIndex+1);
			if(endIndex+1 != structure.length()){
				theRest = structure.substring(endIndex+1);
			}
			if(!inner){
				if(theRest == null){
					if(syllables != 0){
						// was the last object, but wrong amount of syllables used
						return null;
					}
					// syllables == 0
					return structure.substring(1, endIndex);
				}
			}
			else if(theRest == null){ // inner == true
				return structure.substring(1, endIndex);
			}
			//theRest != null && syllables > 0
			// The sentence isn't finished
			returnString = getSentence(theRest, inner);
			if(returnString == null){
				return null;
			}
			else{
				return structure.substring(1, endIndex) + " " + returnString;
			}
		}
		return null;
	}
		
	/**
	 * 
	 * @return All words in the bin with the right part-of-speech
	 */
	private ArrayList<Word> getWords(String wordType){
		ArrayList<Word> words = new ArrayList<Word>();
		for(int i = 0; i < wordsUsed.size(); i++){
			if(wordsUsed.get(i).getwordType().equals(wordType)){
				words.add(wordsUsed.get(i));
			}
		}
		return words;
	}
	
	/**
	 * 
	 * @return All backup words in the bin with the right part-of-speech
	 */
	private ArrayList<Word> getBackUpWords(String wordType){
		ArrayList<Word> words = new ArrayList<Word>();
		for(int i = 0; i < backupWords.size(); i++){
			if(backupWords.get(i).getwordType().equals(wordType)){
				words.add(backupWords.get(i));
			}
		}
		return words;
	}
	
	/**
	 * Calculates and returns the number of words in a text string
	 * @param sentence
	 * @return
	 */
	private int getWordCount(String sentence){
		return HaikuGenerator.getWords(sentence).size();
	}
	
	private int countSyllables(String sentence){
		int numberOfSyllables = 0;
		ArrayList<String> words = HaikuGenerator.getWords(sentence);
//		for(int i = words.size() - 1; i >= 0; i--){ //TODO 2/2/2014 den här ska nog inte vara här längre
//			if(i > 0 && words.get(i).equals("an") && words.get(i-1).equals("a")){
//				words.remove(i);
//			}
//			if(words.get(i).equals("a")){
//				words.set(i, "a/an");
//			}
//		}
		boolean found;
		for(int i = 0; i < words.size(); i++){
			found = false;
			for(int a = 0; a < wordsUsed.size(); a++){
				if(wordsUsed.get(a).getText().equals(words.get(i))){
					numberOfSyllables += wordsUsed.get(a).getNumberOfSyllables();
					found = true;
					break;
				}
			}
			if(found){
				continue;
			}
			for(int a = 0; a < backupWords.size(); a++){
				if(backupWords.get(a).getText().equals(words.get(i))){
					numberOfSyllables += backupWords.get(a).getNumberOfSyllables();
					found = true;
					break;
				}
			}
//			if(found){
//				continue;
//			}
//			for(int a = 0; a < HaikuGenerator.getRulesWords().size(); a++){
//				if(HaikuGenerator.getRulesWords().get(a).getText().equals(words.get(i))){
//					numberOfSyllables += HaikuGenerator.getRulesWords().get(a).getNumberOfSyllables();
//					found = true;
//					break;
//				}
//			}
			if(!found){
				Log.i("TAG", "ERROR: Word " + words.get(i) + " was not found!");
			}
		}
		return numberOfSyllables;
	}
}
