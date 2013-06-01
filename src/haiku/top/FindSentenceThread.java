package haiku.top;

import haiku.top.model.Haiku;
import haiku.top.model.HaikuGenerator;
import haiku.top.model.Word;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

import android.util.Log;

public class FindSentenceThread extends Thread{
	private int syllables;
	private ArrayList<Word> wordsUsed;
	private InputStream rules;
	private BufferedReader reader;
	private Random randomGenerator = new Random();
	private Haiku haiku;
	private int row;
	
	public static final String START_OBJECT = "<sentence>";
	
	public FindSentenceThread(int numberOfSyllables, Haiku haiku, int row){
		this.syllables = numberOfSyllables;
		this.haiku = haiku;
		this.row = row;
		wordsUsed = new ArrayList<Word>(HaikuGenerator.getWordsUsed());
	}
	
	public void run(){
		double startTime = System.currentTimeMillis();
		String sentence = getStructureWithSyllables(START_OBJECT);
		if(sentence == null){
			sentence = "NULL";
		}
		while(sentence.contains("a/an")){
			int index = sentence.indexOf("a/an");
			index += 5;
			if(sentence.charAt(index) == 'a' || sentence.charAt(index) == 'e' || sentence.charAt(index) == 'u' || sentence.charAt(index) == 'i' || sentence.charAt(index) == 'o'){
				sentence = sentence.substring(0, index-5) + "an " + sentence.substring(index);
			}
			else{
				sentence = sentence.substring(0, index-5) + "a " + sentence.substring(index);
			}
		}
		while(sentence.contains(" , ")){
			int index = sentence.indexOf(" , ");
			sentence = sentence.substring(0, index) + ", " + sentence.substring(index+3);
		}
		sentence = sentence.substring(0, 1).toUpperCase() + sentence.substring(1);
		haiku.addRow(row, sentence);
		Log.i("TAG", "find sentence worker thread finished in :" + (System.currentTimeMillis() - startTime) + " ms");
	}
	
	private String getStructureWithSyllables(String structure){
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
			try {
				ArrayList<Integer> rowsLeft = new ArrayList<Integer>();
				boolean firstTime = true;
				do{
					// needs to read from text file
					rules = HaikuActivity.getInstance().getAssets().open("rules.txt");
					reader = new BufferedReader(new InputStreamReader(rules));
					String tempText;
					while ((tempText = reader.readLine()) != null) {// or until the part is found
						if(tempText.contains(firstPart + "=")){
							// The right line is found!
							if(firstTime){
								int rows = Integer.parseInt(tempText.substring(tempText.indexOf('=')+1));
								for(int i = 0; i < rows; i++){
									rowsLeft.add(i);
								}
								firstTime = false;
							}
							break;
						}
					}
					if(tempText == null){
						// did not find the structure
						Log.i("TAG", "did not find the structure: " + firstPart);
						return null;
					}
					randomIndex = randomGenerator.nextInt(rowsLeft.size());
					int row = rowsLeft.get(randomIndex);
					while(row > 0){ // if there are 4 rows, the rows will be 0, 1, 2 and 3. so to get to the forth row this loop will happen 3 times
						reader.readLine();
						row--;
					}
					tempText = reader.readLine();
					String returnStringOfTheRest = null;
					if(theRest == null){
						// the last object in the tempText structure will also be the last object in the sentence -> call this method again with the tempText structure
						returnString = getStructureWithSyllables(tempText);
					}
					else{
						// the last object in the tempText structure will NOT be the last object in the sentence -> call the inner method
						returnString = getStructureWithSyllablesInner(tempText);
						if(returnString != null){
							returnStringOfTheRest = getStructureWithSyllables(theRest);
						}
					}
					if(returnString != null && theRest != null && returnStringOfTheRest != null){
						return returnString + " " + returnStringOfTheRest; // found a sentence!
					}
					else if(returnString != null && theRest == null){
						return returnString; // found a sentence!
					}
					// if here, then the attempt failed -> try another row
					rowsLeft.remove(randomIndex);
				}while(!rowsLeft.isEmpty());
				return null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else if(structure.charAt(0) == '('){
			int endIndex = structure.indexOf(')');
			firstPart = structure.substring(0, endIndex+1);
			if(endIndex+1 != structure.length()){
				theRest = structure.substring(endIndex+1);
			}
			String tempString = structure.substring(1, endIndex);
			ArrayList<String> wordTypes = new ArrayList<String>();
			int tempIndex;
			while((tempIndex = tempString.indexOf('.')) != -1){
				wordTypes.add(tempString.substring(0, tempIndex));
				tempString = tempString.substring(tempIndex+1);
			}
			// one more
			wordTypes.add(tempString);
			ArrayList<Word> availableWords = getWords(wordTypes);
			if(theRest == null){
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
					return null;
				}
				randomIndex = randomGenerator.nextInt(rightAmountOfSyllablesWords.size());
				// A whole sentence has been found!
				return rightAmountOfSyllablesWords.get(randomIndex).getText();
			}
			// not the last object 
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
					continue;
				}
				returnString = getStructureWithSyllables(theRest);
				if(returnString == null){
					// the rest of the sentence can not be completed with this word
					tempSyllabels = availableWords.get(randomIndex).getNumberOfSyllables();
					syllables += tempSyllabels;
					// since we know that words with the same amount of syllables as the word we just tried won't work, we can remove them from the list
					availableWords.remove(randomIndex);
					for(int i = availableWords.size()-1; i >= 0; i--){
						if(availableWords.get(i).getNumberOfSyllables() == tempSyllabels){
							availableWords.remove(i);
						}
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
			int syllIndexS = structure.indexOf('(');
			int syllIndexE = structure.indexOf(')');
			int syll = Integer.parseInt(structure.substring(syllIndexS+1, syllIndexE));
			syllables -= syll;
			if(theRest == null && syllables != 0){
				// was the last object, but wrong amount of syllables used
				syllables += syll;
				return null;
			}
			if(theRest == null && syllables == 0){
				return structure.substring(1, syllIndexS);
			}
			if(theRest != null && syllables <= 0){
				// there are more objects, but all syllables are used
				syllables += syll;
				return null;
			}
			if(theRest != null && syllables > 0){
				// not all syllables are used and the sentence isn't finished
				returnString = getStructureWithSyllables(theRest);
				if(returnString == null){
					return null;
				}
				else{
					return structure.substring(1, syllIndexS) + returnString;
				}
			}
		}
		return null;
	}
	
	/**
	 * This method is like getStructureWithSyllables(), but it will not try to use up all syllables if it is the last object (since it actually isn't the last object).
	 * @param structure
	 * @return
	 */
	private String getStructureWithSyllablesInner(String structure){
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
			try {
				ArrayList<Integer> rowsLeft = new ArrayList<Integer>();
				boolean firstTime = true;
				do{
					// needs to read from text file
					rules = HaikuActivity.getInstance().getAssets().open("rules.txt");
					reader = new BufferedReader(new InputStreamReader(rules));
					String tempText;
					while ((tempText = reader.readLine()) != null) {// or until the part is found
						if(tempText.contains(firstPart + "=")){
							// The right line is found!
							if(firstTime){
								int rows = Integer.parseInt(tempText.substring(tempText.indexOf('=')+1));
								for(int i = 0; i < rows; i++){
									rowsLeft.add(i);
								}
								firstTime = false;
							}
							break;
						}
					}
					if(tempText == null){
						// did not find the structure
						Log.i("TAG", "did not find the structure: " + firstPart);
						return null;
					}
					randomIndex = randomGenerator.nextInt(rowsLeft.size());
					int row = rowsLeft.get(randomIndex);
					while(row > 0){ // if there are 4 rows, the rows will be 0, 1, 2 and 3. so to get to the forth row this loop will happen 3 times
						reader.readLine();
						row--;
					}
					tempText = reader.readLine();
					String returnStringOfTheRest = null;
					if(theRest == null){
						// the last object in the tempText structure will also be the last object in this structure (but not the sentence)
						returnString = getStructureWithSyllablesInner(tempText);
					}
					else{
						// the last object in the tempText structure will NOT be the last object in this structure
						returnString = getStructureWithSyllablesInner(tempText);
						if(returnString != null){
							returnStringOfTheRest = getStructureWithSyllablesInner(theRest);
						}
					}
					if(returnString != null && theRest != null && returnStringOfTheRest != null){
						return returnString + " " + returnStringOfTheRest; // found a sentence!
					}
					else if(returnString != null && theRest == null){
						return returnString; // found a sentence!
					}
					// if here, then the attempt failed -> try another row
					rowsLeft.remove(randomIndex);
				}while(!rowsLeft.isEmpty());
				return null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else if(structure.charAt(0) == '('){
			int endIndex = structure.indexOf(')');
			firstPart = structure.substring(0, endIndex+1);
			if(endIndex+1 != structure.length()){
				theRest = structure.substring(endIndex+1);
			}
			String tempString = structure.substring(1, endIndex);
			ArrayList<String> wordTypes = new ArrayList<String>();
			int tempIndex;
			while((tempIndex = tempString.indexOf('.')) != -1){
				wordTypes.add(tempString.substring(0, tempIndex));
				tempString = tempString.substring(tempIndex+1);
			}
			// one more
			wordTypes.add(tempString);
			ArrayList<Word> availableWords = getWords(wordTypes);
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
					continue;
				}
				if(theRest == null){
					// last object in this structure
					return availableWords.get(randomIndex).getText();
				}
				// not the last object
				returnString = getStructureWithSyllablesInner(theRest);
				if(returnString == null){
					// the rest of the sentence can not be completed with this word
					tempSyllabels = availableWords.get(randomIndex).getNumberOfSyllables();
					syllables += tempSyllabels;
					// since we know that words with the same amount of syllables as the word we just tried won't work, we can remove them from the list
					availableWords.remove(randomIndex);
					for(int i = availableWords.size()-1; i >= 0; i--){
						if(availableWords.get(i).getNumberOfSyllables() == tempSyllabels){
							availableWords.remove(i);
						}
					}
					continue;
				}
				// return string did return something!
				// we have found a complete sentence!
				return availableWords.get(randomIndex).getText() + " " + returnString;
			}
			return null; // no words with the right word types and syllables exist in the bin
		}
		else if(structure.charAt(0) == '['){
			int endIndex = structure.indexOf(']');
			firstPart = structure.substring(0, endIndex+1);
			if(endIndex+1 != structure.length()){
				theRest = structure.substring(endIndex+1);
			}
			int syllIndexS = structure.indexOf('(');
			int syllIndexE = structure.indexOf(')');
			int syll = Integer.parseInt(structure.substring(syllIndexS+1, syllIndexE));
			syllables -= syll;
			if(syllables <= 0){
				// there are more objects, but all syllables are used. Since this is the inner method there will be more objects!
				syllables += syll;
				return null;
			}
			if(theRest == null){
				return structure.substring(1, syllIndexS);
			}
			// theRest != null
			// not all syllables are used and the structure isn't finished
			returnString = getStructureWithSyllablesInner(theRest);
			if(returnString == null){
				return null;
			}
			else{
				return structure.substring(1, syllIndexS) + returnString;
			}
		}
		return null;
	}
	
	/**
	 * 
	 * @return All words in the bin with the right part-of-speech(es)
	 */
	private ArrayList<Word> getWords(ArrayList<String> wordTypes){
		ArrayList<Word> words = new ArrayList<Word>();
		boolean exists;
		for(int i = 0; i < wordsUsed.size(); i++){
			exists = true;
			for(int t = 0; t < wordTypes.size(); t++){
				if(wordsUsed.get(i).getwordTypes().contains(wordTypes.get(t))){
					exists = false;
					break;
				}
			}
			if(exists){
				words.add(wordsUsed.get(i));
			}
		}
		return words;
	}
}
