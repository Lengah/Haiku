package haiku.top.view.binview;

import java.util.ArrayList;
import java.util.Random;

import android.content.Context;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;

public class BinSMSRow extends RelativeLayout{
	private ArrayList<BinSMSRowWord> words = new ArrayList<BinSMSRowWord>();
	
	public BinSMSRow(Context context) {
		super(context);
	}
	
	/**
	 * Adds the word into the row. It adds the word into the right position.
	 * @param word
	 */
	public void addWord(BinSMSRowWord word){
		boolean added = false;
		for(int i = 0; i < words.size(); i++){
			if(words.get(i).getStartPos() > word.getStartPos()){
				words.add(i, word);
				added = true;
				break;
			}
		}
		if(!added){
			// add it at the end
			words.add(word);
		}
		LayoutParams params = new RelativeLayout.LayoutParams((int) word.getLength(), LayoutParams.MATCH_PARENT);
		params.setMargins((int)word.getStartPos(), 0, 0, 0);
		addView(word, params);
	}
	
	private static Random randomGenerator = new Random();
	public static final int CHANCE_TO_DELETE = 10; // in %
	
	public ArrayList<BinSMSRowWord> setToBeDeleted(){
		ArrayList<BinSMSRowWord> wordsToDelete = new ArrayList<BinSMSRowWord>();
		for(int i = 0; i < words.size(); i++){
			if(randomGenerator.nextInt(100) < CHANCE_TO_DELETE){
				wordsToDelete.add(words.get(i));
			}
		}
		return wordsToDelete;
	}
	
	public ArrayList<BinSMSRowWord> getWords(){
		return words;
	}
	
	public void delete(BinSMSRowWord word){
		words.remove(word);
		removeView(word);
		if(words.isEmpty()){
			BinView.getInstance().getBinCombinedSMSView().removeRow(this);
		}
	}
	
	/**
	 * Returns the length in PX of the row as it is.
	 * Used when adding words at the end.
	 * @return
	 */
	public float getCurrentOffset(){
		if(words.isEmpty()){
			return 0;
		}
		return words.get(words.size()-1).getStartPos() + words.get(words.size()-1).getLength();
	}
}
