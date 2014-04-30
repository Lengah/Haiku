package haiku.top.model.generator;


import haiku.top.model.Word;
import haiku.top.model.WordAndNumber;
import haiku.top.view.binview.BinView;

import java.util.ArrayList;

import android.util.Log;

public class Haiku {
	private ArrayList<Word> row1;
	private ArrayList<Word> row2;
	private ArrayList<Word> row3;
	private boolean haikuFinished = false;
	private double startTime = System.currentTimeMillis();
	private boolean themes;
	private ArrayList<String> wordsUsed = new ArrayList<String>();
	private ArrayList<Long> cueWords = new ArrayList<Long>();
	private ArrayList<Word> usedWords = new ArrayList<Word>();
	
	public Haiku(boolean themes){
		this.themes = themes;
		generate(1);
	}
	
	public void addCueWords(ArrayList<Long> cueWords){
		this.cueWords.addAll(cueWords);
	}
	
	public void addCueWord(long cueWord){
		this.cueWords.add(cueWord);
	}
	
	public ArrayList<Word> getUsedWords(){
		return usedWords;
	}
	
	public ArrayList<Long> getCueWords(){
		return cueWords;
	}
	
	public void printUsedWords(){
		Log.i("TAG", "");
		String text = "";
		for(int i = 0; i < usedWords.size(); i++){
			text += "[" + usedWords.get(i).getText() + ", " + usedWords.get(i).getwordType() + "], ";
		}
		Log.i("TAG", text);
		Log.i("TAG", "");
	}
	
	public void removeCueWords(ArrayList<Long> ids){
//		Log.i("TAG", "Remove " + ids.size() + " cue words for word " + usedWords.get(usedWords.size()-1).getText() + ": " + cueWords.size());
		for(int i = 0; i < ids.size(); i++){
			for(int a = 0; a < cueWords.size(); a++){
				if(ids.get(i) == cueWords.get(a)){
					cueWords.remove(a);
					break;
				}
			}
		}
//		Log.i("TAG", "After: " + cueWords.size());
	}
	
	public void removeUsedWord(Word word){
		for(int i = usedWords.size()-1; i >= 0; i--){
			if(usedWords.get(i).equals(word)){
				usedWords.remove(i); // only want to remove one and remove it from the end of the list
				return;
			}
		}
	}
	
	public boolean containsThemes(){
		return themes;
	}
	
	public String getHaikuPoem(){
		return row1 + "\n" + row2 + "\n" + row3;
	}
	
	public ArrayList<Word> getRow(int row){
		if(row == 1){
			return row1;
		}
		if(row == 2){
			return row2;
		}
		if(row == 3){
			return row3;
		}
		return null;
	}
	
	public void generate(int row){
		if(row == 2){
			new FindSentenceThread(7, this, 2).start();
		}
		else{
			new FindSentenceThread(5, this, row).start();
		}
	}
	
//	public void generate(){
////		HaikuGenerator.printAllUsableWords();
//		new FindSentenceThread(5, this, 1).start();
//		new FindSentenceThread(7, this, 2).start();
//		new FindSentenceThread(5, this, 3).start();
//	}
	
	public void updateHaikuFinished(){
		haikuFinished = (row1 != null && row2 != null && row3 != null);
		if(haikuFinished){
			Log.i("TAG", "Time to finish one haiku: " + (System.currentTimeMillis() - startTime) + " ms");
			print();
			initWordsUsed();
			BinView.getInstance().haikuIsFinished();
			HaikuGenerator.nextHaiku();
		}
		else{
			HaikuGenerator.nullHaikuGenerated();
		}
		
//		haikuFinished = (row1 != null && row2 != null && row3 != null);
//		if(haikuFinished){
//			Log.i("TAG", "Time to finish one haiku: " + (System.currentTimeMillis() - startTime) + " ms");
//			print();
//			initWordsUsed();
//			if(!row1.equalsIgnoreCase("NULL") && !row2.equalsIgnoreCase("NULL") && !row3.equalsIgnoreCase("NULL")){
//				BinView.getInstance().haikuIsFinished();
//			}
//			else{
//				HaikuGenerator.nullHaikuGenerated();
//			}
//			HaikuGenerator.nextHaiku();
//		}
	}
	
	public ArrayList<String> getWordsUsed(){
		return wordsUsed;
	}
	
	public void initWordsUsed(){
		wordsUsed.addAll(getStringWords(row1));
		wordsUsed.addAll(getStringWords(row2));
		wordsUsed.addAll(getStringWords(row3));
		ArrayList<String> wordsRemoved = BinView.getInstance().getAllWordsRemoved();
		for(int i = 0; i < wordsRemoved.size(); i++){
			if(wordsUsed.contains(wordsRemoved.get(i))){// && !HaikuGenerator.getRulesWords().contains(wordsRemoved.get(i))){
				HaikuGenerator.removeHaiku(this);
				return;
			}
		}
	}
	
	public ArrayList<String> getStringWords(ArrayList<Word> words){
		ArrayList<String> r = new ArrayList<String>();
		for(Word w : words){
			r.add(w.getText());
		}
		return r;
	}
	
	public boolean isHaikuFinished(){
		return haikuFinished;
	}
	
	public synchronized void addRow(int row, ArrayList<Word> text){
		if(row == 1){
			row1 = text;
		}
		if(row == 2){
			row2 = text;
		}
		if(row == 3){
			row3 = text;
		}
		updateHaikuFinished();
	}
	
	public String getStringOfList(ArrayList<Word> words){
		String s = "";
		for(int i = 0; i < words.size(); i++){
			s += words.get(i).getText();
			if(i != words.size()-1){
				s += " ";
			}
		}
		return s;
	}
	
	public void print(){
		Log.i("TAG", "" + getStringOfList(row1));
		Log.i("TAG", "" + getStringOfList(row2));
		Log.i("TAG", "" + getStringOfList(row3));
		printUsedWords();
		Log.i("TAG", "Cue words: " + cueWords.size());
		Log.i("TAG", " ");
	}
}
