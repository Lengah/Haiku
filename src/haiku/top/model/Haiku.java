package haiku.top.model;


import java.util.ArrayList;

import android.util.Log;

public class Haiku {
	private String row1;
	private String row2;
	private String row3;
	private ArrayList<String> LinesWithSyllables5 = new ArrayList<String>();
	private ArrayList<String> LinesWithSyllables7 = new ArrayList<String>();
	private boolean haikuFinished = false;
	private double startTime = System.currentTimeMillis();
	
	public String getHaikuPoem(){
		return row1 + "\n" + row2 + "\n" + row3;
	}
	
	public void generate(){
		new FindSentenceThread(5, this, 1).start();
		new FindSentenceThread(7, this, 2).start();
		new FindSentenceThread(5, this, 3).start();
	}
	
	public void updateHaikuFinished(){
		haikuFinished = (row1 != null && row2 != null && row3 != null);
		if(haikuFinished){
			Log.i("TAG", "Time to finish one haiku: " + (System.currentTimeMillis() - startTime) + " ms");
			print();
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
