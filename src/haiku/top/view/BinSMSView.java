package haiku.top.view;

import haiku.top.model.SMS;
import haiku.top.model.SMSBinWord;
import haiku.top.model.Word;

import java.util.ArrayList;
import java.util.Random;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BinSMSView extends LinearLayout{
	private ArrayList<SMSBinWord> words = new ArrayList<SMSBinWord>(); // doesn't have to be "real" words, so it isn't of the type Word
	private ArrayList<Integer> usedWordsIndexes = new ArrayList<Integer>();
	private static final int COLOR_DEFAULT = Color.BLACK;
	private static final int COLOR_REMOVED = Color.GRAY;
	private SMS sms;
	private TextView textView;
	private String message;
	
	public BinSMSView(Context context, SMS sms) {
		super(context);
		this.sms = sms;
		
		// not just "real" words -> can't use the database because the database only contain "real" words
		String textMessage = sms.getMessage().toLowerCase();
		String word;
		int skippedLetters = 0;
		int pos1;
		int pos2;
		while(textMessage.length() > 0){
			// Remove symbols from the start
			pos1 = 0;
			while(textMessage.charAt(pos1) != 'a' && textMessage.charAt(pos1) != 'b' && textMessage.charAt(pos1) != 'c' && textMessage.charAt(pos1) != 'd'
				 && textMessage.charAt(pos1) != 'e' && textMessage.charAt(pos1) != 'f' && textMessage.charAt(pos1) != 'g' && textMessage.charAt(pos1) != 'h'
				 && textMessage.charAt(pos1) != 'i' && textMessage.charAt(pos1) != 'j' && textMessage.charAt(pos1) != 'k' && textMessage.charAt(pos1) != 'l'
				 && textMessage.charAt(pos1) != 'm' && textMessage.charAt(pos1) != 'n' && textMessage.charAt(pos1) != 'o' && textMessage.charAt(pos1) != 'p'
				 && textMessage.charAt(pos1) != 'q' && textMessage.charAt(pos1) != 'r' && textMessage.charAt(pos1) != 's' && textMessage.charAt(pos1) != 't'
				 && textMessage.charAt(pos1) != 'u' && textMessage.charAt(pos1) != 'v' && textMessage.charAt(pos1) != 'w' && textMessage.charAt(pos1) != 'x'
			     && textMessage.charAt(pos1) != 'y' && textMessage.charAt(pos1) != 'z' && textMessage.charAt(pos1) != 'é' && textMessage.charAt(pos1) != 'è'
			     && textMessage.charAt(pos1) != 'å' && textMessage.charAt(pos1) != 'ä' && textMessage.charAt(pos1) != 'ö' && textMessage.charAt(pos1) != '\''){
				pos1++;
				if(pos1 >= textMessage.length()){
					break;
				}
			}
			if(pos1 >= textMessage.length()){
				break; // just a bunch of symbols left of the message
			}
			// find the end of the word
			pos2 = pos1;
			while(textMessage.charAt(pos2) == 'a' || textMessage.charAt(pos2) == 'b' || textMessage.charAt(pos2) == 'c' || textMessage.charAt(pos2) == 'd'
				 || textMessage.charAt(pos2) == 'e' || textMessage.charAt(pos2) == 'f' || textMessage.charAt(pos2) == 'g' || textMessage.charAt(pos2) == 'h'
				 || textMessage.charAt(pos2) == 'i' || textMessage.charAt(pos2) == 'j' || textMessage.charAt(pos2) == 'k' || textMessage.charAt(pos2) == 'l'
				 || textMessage.charAt(pos2) == 'm' || textMessage.charAt(pos2) == 'n' || textMessage.charAt(pos2) == 'o' || textMessage.charAt(pos2) == 'p'
				 || textMessage.charAt(pos2) == 'q' || textMessage.charAt(pos2) == 'r' || textMessage.charAt(pos2) == 's' || textMessage.charAt(pos2) == 't'
				 || textMessage.charAt(pos2) == 'u' || textMessage.charAt(pos2) == 'v' || textMessage.charAt(pos2) == 'w' || textMessage.charAt(pos2) == 'x'
			     || textMessage.charAt(pos2) == 'y' || textMessage.charAt(pos2) == 'z' || textMessage.charAt(pos1) == 'é' || textMessage.charAt(pos1) == 'è'
			     || textMessage.charAt(pos1) == 'å' || textMessage.charAt(pos1) == 'ä' || textMessage.charAt(pos1) == 'ö' || textMessage.charAt(pos1) == '\''){
				pos2++;
				if(pos1+pos2 >= textMessage.length()){
					break;
				}
			}
			// a word is found between indexes pos1 and pos2
			word = textMessage.substring(pos1, pos2);
			if(word.length() == 0){
				break;
			}
			words.add(new SMSBinWord(word, pos1 + skippedLetters, pos2 + skippedLetters));
			if(pos2+1 <= textMessage.length()){
				textMessage = textMessage.substring(pos2+1);
				skippedLetters += pos2+1;
			}
			else{
				break;
			}
		}
		textView = new TextView(context);
		addView(textView);
		textView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
//		String tempString;
//		for(int i = 0; i < words.size(); i++){
//			tempString = "<font color='black'>" + words.get(i) + "</font>";
//			wordsWithColors.add(tempString);
//		}
//		updateMessage();
		message = "<font color='black'>" + sms.getMessage() + "</font>";
		textView.setText(Html.fromHtml(message), TextView.BufferType.SPANNABLE);
		//TODO test!!
//		if(words.size() > 5){
//			setUsedWordAtPos(5);
//		}
//		
//		if(words.size() > 7){
//			setUsedWordAtPos(7);
//		}
		// /test
	}
	
	public void updateMessage(){
		message = "";

		int lastPos = 0;
		int pos1;
		int pos2;
		for(int i = 0; i < usedWordsIndexes.size(); i++){
			pos1 = words.get(usedWordsIndexes.get(i)).getStartPos();
			pos2 = words.get(usedWordsIndexes.get(i)).getEndPos();
//			Log.i("TAG", "pos1: " + pos1 + ", pos2: " + pos2 + ", message length: " + sms.getMessage().length() + ", word: " + words.get(usedWordsIndexes.get(i)).getWord());
			message += "<font color='black'>" + sms.getMessage().substring(lastPos, pos1) + "</font>" + "<font color='grey'>" + sms.getMessage().substring(pos1, pos2) + "</font>";
			lastPos = pos2;
		}
		message += "<font color='black'>" + sms.getMessage().substring(lastPos) + "</font>"; // The rest of the message
		textView.setText(Html.fromHtml(message), TextView.BufferType.SPANNABLE);
	}
	
	public void setUsedWordAtPos(int index){
		// They are added in order from smallest to biggest index
		for(int i = 0; i <= usedWordsIndexes.size(); i++){
			if(i == usedWordsIndexes.size()){
				usedWordsIndexes.add(index);
				break;
			}
			else if(index < usedWordsIndexes.get(i)){
				usedWordsIndexes.add(i, index);
				break;
			}
		}
		lastIndexSetToUsed = index;
		updateMessage();
	}
	
	public void unsetUsedWordAtPos(int index){
		Integer ind = index;
		usedWordsIndexes.remove(ind);
		updateMessage();
	}
	
	public SMS getSMS(){
		return sms;
	}
	
	private static Random randomGenerator = new Random();
	
	/**
	 * 
	 * @return the index of the text set to used
	 */
	public void setUsedWordAtRandom(){
		ArrayList<Integer> notUsed = new ArrayList<Integer>();
		for(int i = 0; i < words.size(); i++){
			notUsed.add(i);
		}
		notUsed.removeAll(usedWordsIndexes);
		int randomIndex = randomGenerator.nextInt(notUsed.size());
		setUsedWordAtPos(usedWordsIndexes.get(randomIndex));
	}
	
	private int lastIndexSetToUsed = -1;
	
	public void undoLast(){
		if(lastIndexSetToUsed != -1){ // if -1, then nothing has been done -> nothing to undo
			unsetUsedWordAtPos(lastIndexSetToUsed);
		}
	}

}
