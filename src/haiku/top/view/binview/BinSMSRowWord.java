package haiku.top.view.binview;

import haiku.top.model.Word;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.widget.TextView;

public class BinSMSRowWord extends TextView{
	private String word;
	private float length; // in PX
	private float startPos; // in PX
	private ArrayList<Word> realWords; // Since this class represents a "block", it can contain several "real" words, although it will almost always only be one (if any). For example hello/hi is a block, but two real words
	private boolean willRemoveNext = false;
	private BinSMSRow row;
	
	private static final int DEFAULT_COLOR = Color.BLACK;
	private static final int REMOVE_COLOR = Color.GRAY;
	
	public BinSMSRowWord(Context context, String word, float startPos, float length, ArrayList<Word> realWords, BinSMSRow row){
		super(context);
//		Log.i("TAG", "add word: " + word + ", startPos: " + startPos + ", length: " + length + ", realwords: " + realWords.size());
//		for(int i = 0; i < realWords.size(); i++){
//			Log.i("TAG", "realWords " + (i+1) + ": " + realWords.get(i).getText());
//		}
		Log.i("TAG", "");
		this.word = word;
		this.startPos = startPos;
		this.length = length;
		this.realWords = realWords;
		this.row = row;
		setText(word);
		setTextColor(DEFAULT_COLOR);
	}
	
	/**
	 * Updates the color and a variable
	 */
	public void undo(){
		setTextColor(DEFAULT_COLOR);
		willRemoveNext = false;
	}
	
	/**
	 * Updates the color and a variable
	 */
	public void setRemovedNext(){
		setTextColor(REMOVE_COLOR);
		willRemoveNext = true;
	}
	
	public boolean willRemoveNext(){
		return willRemoveNext;
	}
	
	public void delete(){
		row.delete(this);
	}
	
	/**
	 * The representation on the screen (does not have to be an actual word, it's just a combination of characters)
	 * @return
	 */
	public String getWord(){
		return word;
	}
	
	public ArrayList<Word> getRealWords(){
		return realWords;
	}
	
	public ArrayList<String> getRealWordStrings(){
		ArrayList<String> ret = new ArrayList<String>();
		for(int i = 0; i < realWords.size(); i++){
			ret.add(realWords.get(i).getText());
		}
		return ret;
	}
	
	public float getLength(){
		return length;
	}
	
	public float getStartPos(){
		return startPos;
	}
	
	@Override
	public boolean equals(Object obj){
		// This method will return true if compared with a BinSMSRowWord in a different row with the same word is in the exact same position
		return obj instanceof BinSMSRowWord && word.equals(((BinSMSRowWord)obj).getWord()) && length == ((BinSMSRowWord)obj).getLength() && startPos == ((BinSMSRowWord)obj).getStartPos(); 
	}

}
