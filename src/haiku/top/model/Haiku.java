package haiku.top.model;


import haiku.top.view.BinView;

import java.util.ArrayList;

import android.util.Log;

public class Haiku {
	private String row1;
	private String row2;
	private String row3;
	private boolean haikuFinished = false;
	private double startTime = System.currentTimeMillis();
	private boolean themes;
	
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
			BinView.getInstance().haikuIsFinished();
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
