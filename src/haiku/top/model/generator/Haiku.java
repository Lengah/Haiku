package haiku.top.model.generator;


import haiku.top.model.WordAndNumber;
import haiku.top.view.bin.BinView;

import java.util.ArrayList;

import android.util.Log;

public class Haiku {
	private String row1;
	private String row2;
	private String row3;
	private boolean haikuFinished = false;
	private double startTime = System.currentTimeMillis();
	private boolean themes;
	private ArrayList<String> wordsUsed = new ArrayList<String>();
	
	public Haiku(boolean themes){
		this.themes = themes;
	}
	
	public boolean containsThemes(){
		return themes;
	}
	
	public String getHaikuPoem(){
		return row1 + "\n" + row2 + "\n" + row3;
	}
	
	public String getRow(int row){
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
	
	public void generate(){
//		HaikuGenerator.printAllUsableWords();
		new FindSentenceThread(5, this, 1).start();
		new FindSentenceThread(7, this, 2).start();
		new FindSentenceThread(5, this, 3).start();
	}
	
	public void updateHaikuFinished(){
		haikuFinished = (row1 != null && row2 != null && row3 != null);
		if(haikuFinished){
			Log.i("TAG", "Time to finish one haiku: " + (System.currentTimeMillis() - startTime) + " ms");
			print();
			initWordsUsed();
			BinView.getInstance().haikuIsFinished();
		}
	}
	
	public ArrayList<String> getWordsUsed(){
		return wordsUsed;
	}
	
	public void initWordsUsed(){
		wordsUsed.addAll(HaikuGenerator.getWords(row1));
		wordsUsed.addAll(HaikuGenerator.getWords(row2));
		wordsUsed.addAll(HaikuGenerator.getWords(row3));
		ArrayList<String> wordsRemoved = BinView.getInstance().getAllWordsRemoved();
		for(int i = 0; i < wordsRemoved.size(); i++){
			if(wordsUsed.contains(wordsRemoved.get(i)) && !HaikuGenerator.getRulesWords().contains(wordsRemoved.get(i))){
				HaikuGenerator.removeHaiku(this);
				return;
			}
		}
	}
	
	public boolean isHaikuFinished(){
		return haikuFinished;
	}
	
	public synchronized void addRow(int row, String text){
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
	
	public void print(){
		Log.i("TAG", "" + row1);
		Log.i("TAG", "" + row2);
		Log.i("TAG", "" + row3);
		Log.i("TAG", " ");
	}
}
