package haiku.top.view.binview;

import haiku.top.view.main.MainView;

import java.util.ArrayList;
import java.util.Random;

import android.content.Context;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;

public class BinSMSRow extends RelativeLayout{
	private ArrayList<BinSMSRowWord> words = new ArrayList<BinSMSRowWord>();
	private int rowIndex;
	private BinCombinedSMS parent;
	
	public BinSMSRow(Context context, int rowIndex, BinCombinedSMS parent) {
		super(context);
		this.rowIndex = rowIndex;
		this.parent = parent;
	}
	
	public int getRowIndex(){
		return rowIndex;
	}
	
	public void setRowIndex(int rowIndex){
		this.rowIndex = rowIndex;
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
//	public static final int MIN_CHANCE_TO_DELETE = 15; // in %
//	public static final double MAX_INC_OF_CHANCE_TO_DELETE = 10.0; // in %
//	public static final double ROWS_AT_MAX = 100.0;
	private static final int CHANCE_TO_DELETE = 10;
	
	public ArrayList<BinSMSRowWord> setToBeDeleted(){
		ArrayList<BinSMSRowWord> wordsToDelete = new ArrayList<BinSMSRowWord>();
		for(int i = 0; i < words.size(); i++){
//			if(randomGenerator.nextInt(100) < getChanceToDelete()){
			if(randomGenerator.nextInt(100) < CHANCE_TO_DELETE){
				wordsToDelete.add(words.get(i));
			}
		}
		return wordsToDelete;
	}
	
//	// This calculation has to be done by EVERY row every time.
//	private int getChanceToDelete(){
//		return (int) (MIN_CHANCE_TO_DELETE + ((double)parent.getRows().size())/ROWS_AT_MAX * MAX_INC_OF_CHANCE_TO_DELETE);
//	}
	
	public ArrayList<BinSMSRowWord> getWords(){
		return words;
	}
	
	/**
	 * 
	 * @param startPos - The last index of the word before the block
	 * @param endPos - The first index of the word after the block
	 * @return All the words in the block
	 */
	public ArrayList<BinSMSRowWord> getWordsOverBlock(float startPos, float endPos){
		ArrayList<BinSMSRowWord> returnWords = new ArrayList<BinSMSRowWord>();
		for(int i = 0; i < words.size(); i++){
			if(words.get(i).getStartPos() > startPos && words.get(i).getStartPos() + words.get(i).getLength() < endPos){
				returnWords.add(words.get(i));
			}
		}
		return returnWords;
	}
	
	public ArrayList<BinSMSRowWord> delete(BinSMSRowWord word){
		ArrayList<BinSMSRowWord> wordsAbove = null;
		if(rowIndex!=0){
			float startPos = -1;
			float endPos = 99999;// fill out the rest of the view
			for(int i = 0; i < words.size(); i++){
				if(words.get(i).equals(word)){
					if(i > 0){
						startPos = words.get(i-1).getStartPos() + words.get(i-1).getLength();
					}
					if(i < words.size()-1){
						endPos = words.get(i+1).getStartPos();
					}
					break;
				}
			}
			wordsAbove = parent.getRows().get(rowIndex-1).getWordsOverBlock(startPos, endPos);
		}
		removeWord(word);
		return wordsAbove;
//		if(wordsAbove != null){
//			for(int i = 0; i < wordsAbove.size(); i++){
//				wordFallingDown(wordsAbove.get(i));
//			}
//		}
//		if(words.isEmpty()){
//			BinView.getInstance().getBinCombinedSMSView().removeRow(this);
//		}
	}
	
	/**
	 * Removes it without any animations or checks
	 */
	public void removeWord(BinSMSRowWord word){
		words.remove(word);
		removeView(word);
	}
	
	/**
	 * Deletes the word from its current row.
	 * Checks if the word can fall down even further. If it can it calls this method on the row below with the same word,
	 * if not the word is added to this row
	 */
	public void wordFallingDown(BinSMSRowWord word){
		if(rowIndex != parent.getRows().size()-1 && parent.getRows().get(rowIndex+1).canAddWord(word)){
			parent.getRows().get(rowIndex+1).wordFallingDown(word);
		}
		else{
			BinSMSRowWord newWord = new BinSMSRowWord(getContext(), word.getWord(), word.getStartPos(), word.getLength(), word.getRealWords(), this);
			addWord(newWord);
			parent.addFallingDownWord(newWord, rowIndex - word.getRow().rowIndex);
//			word.delete();
		}
	}
	
	/**
	 * 
	 * @param word
	 * @return True if the word fits on this row, false otherwise
	 */
	public boolean canAddWord(BinSMSRowWord word){
		if(words.isEmpty()){
			return true;
		}
		for(int i = 0; i < words.size(); i++){
			if(words.get(i).getStartPos() > word.getStartPos()){
				if(i==0){
					// does it fit at the start?
					return words.get(i).getStartPos() > word.getStartPos() + word.getLength();
				}
				else{
					// does it fit in between?
					return (words.get(i).getStartPos() > word.getStartPos() + word.getLength()) && (word.getStartPos() > words.get(i-1).getStartPos() + words.get(i-1).getLength());
				}
			}
		}
		// does it fit at the end?
		return word.getStartPos() > words.get(words.size()-1).getStartPos() + words.get(words.size()-1).getLength();
	}
	
	/**
	 * Check all words and see if they can fall down
	 * This should be calculated when the view is created.
	 */
	public void init(){
		if(rowIndex != 0){
			float startPos = -1;
			float endPos = 99999;
			for(int i = -1; i < words.size(); i++){
				if(i == -1){
					//startpos = -1;
					if(!words.isEmpty()){
						endPos = words.get(0).getStartPos();
					}
					//else -> endpos = 99999;
				}
				else if(i == words.size()-1){
					startPos = words.get(i).getStartPos() + words.get(i).getLength();
					endPos = 99999;
				}
				else{
					startPos = words.get(i).getStartPos() + words.get(i).getLength();
					endPos = words.get(i+1).getStartPos();
				}
				ArrayList<BinSMSRowWord> wordsAbove = parent.getRows().get(rowIndex-1).getWordsOverBlock(startPos, endPos);
				for(int a = 0; a < wordsAbove.size(); a++){
					wordFallingDown(wordsAbove.get(a));
				}
			}
		}
	}
	
//	public void init(){
//		if(rowIndex!=0){
//			float startPos = 0;
//			float endPos = 99999;// fill out the rest of the view
//			if(words.size() > 0){
//				startPos = words.get(words.size()-1).getStartPos() + words.get(words.size()-1).getLength();
//			}
//			ArrayList<BinSMSRowWord> wordsAbove = parent.getRows().get(rowIndex-1).getWordsOverBlock(startPos, endPos);
//			for(int i = 0; i < wordsAbove.size(); i++){
//				wordFallingDown(wordsAbove.get(i));
//			}
//		}
//	}
	
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
	
	public void calculateOffsetOfWords(){
		int offset;
		for(int i = 0; i < words.size(); i++){
			offset = 0;
			if(i == 0){
				offset += words.get(i).getStartPos();
				if(rowIndex != 0){
					offset += MainView.getInstance().getBinView().getWidthOfRow() - parent.getRows().get(rowIndex-1).getCurrentOffset();
				}
			}
			else{
				offset += words.get(i).getStartPos() - (words.get(i-1).getStartPos() + words.get(i-1).getLength());
			}
			words.get(i).setOffset(offset);
		}
	}
	
	/**
	 * Calculates the offset of all words to the right of pos and returns the offset of the first word
	 * @param pos
	 * @return
	 */
	public int calculateOffsetOfWords(int pos){
		int firstOffset = 0;
		int offset;
		boolean first = true;
		for(int i = 0; i < words.size(); i++){
			if(words.get(i).getStartPos() < pos){
				continue;
			}
			offset = 0;
			if(first){
				first = false;
				offset = (int) (words.get(i).getStartPos() - pos);
				firstOffset = offset;
			}
			else{
				if(i == 0){
					offset += words.get(i).getStartPos();
					if(rowIndex != 0){
						offset += MainView.getInstance().getBinView().getWidthOfRow() - parent.getRows().get(rowIndex-1).getCurrentOffset();
					}
				}
				else{
					offset += words.get(i).getStartPos() - (words.get(i-1).getStartPos() + words.get(i-1).getLength());
				}
			}
			words.get(i).setOffset(offset);
		}
		if(first){
			// no calculation was made
			firstOffset = (int) (MainView.getInstance().getBinView().getWidthOfRow() - pos);
		}
		return firstOffset;
	}
	
	public boolean isTextAt(int pos){
		for(int i = 0; i < words.size(); i++){
			if(words.get(i).getStartPos() >= pos && words.get(i).getStartPos() + words.get(i).getLength() <= pos){
				return true;
			}
		}
		return false;
	}
	
	public BinCombinedSMS getBinCombinedSMSView(){
		return parent;
	}
	
	/**
	 * Returns how the row looks
	 * @return
	 */
	public String getText(){
		return getTextWithStartPos(0);
	}
	
	/**
	 * Returns how the row looks starting at the inputed parameter
	 * @param startPos
	 * @return
	 */
	public String getTextWithStartPos(int startPos){
		String message = "";
		float widthOfSpace = parent.getLengthOfSpace();
		int pos = startPos;
		for(int i = 0; i < words.size(); i++){
			if(words.get(i).getStartPos() < startPos){
				continue;
			}
			while(pos < words.get(i).getStartPos()){
				message += " ";
				pos += widthOfSpace;
			}
			message += words.get(i).getWord();
			pos += words.get(i).getLength();
		}
		float spaceLeft = MainView.getInstance().getBinView().getWidthOfRow() - getCurrentOffset();
		for(int i = 0; i < spaceLeft/widthOfSpace; i++){
			message += " ";
		}
		return message;
	}
	
	/**
	 * Removes all words that start after or at pos. Returns the words removed
	 * @param pos
	 */
	public ArrayList<BinSMSRowWord> removeAfterPos(int pos){
		ArrayList<BinSMSRowWord> wordsRemoved = new ArrayList<BinSMSRowWord>();
		for(int i = words.size()-1; i >= 0; i--){
			if(words.get(i).getStartPos() >= pos){
				wordsRemoved.add(words.get(i));
				removeView(words.get(i));
				words.remove(i);
			}
		}
		return wordsRemoved;
	}
	
//	public void printWords(){
//		String message = "";
//		for(int i = 0; i < words.size(); i++){
//			message += " " + words.get(i).getWord(); 
//		}
//		//Log.i("TAG", "row " + rowIndex + ":" + message);
//	}
}
