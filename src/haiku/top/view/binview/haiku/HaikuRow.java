package haiku.top.view.binview.haiku;

import haiku.top.model.Position;
import haiku.top.model.Word;
import haiku.top.model.date.YearMonth;
import haiku.top.model.generator.HaikuGenerator;
import haiku.top.view.ThemeObjectView;
import haiku.top.view.binview.BinCombinedSMS;
import haiku.top.view.binview.BinSMSRowWord;
import haiku.top.view.binview.BinView;
import haiku.top.view.date.QuarterCircle;
import haiku.top.view.main.ConversationObjectView;
import haiku.top.view.main.MainView;
import haiku.top.view.main.sms.SMSObject;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnDragListener;
import android.view.View.OnTouchListener;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

public class HaikuRow extends RelativeLayout{
	private ArrayList<HaikuRowWord> words = new ArrayList<HaikuRowWord>();
	private int rowIndex;
	private HaikuView parent;
	
	public HaikuRow(Context context, int rowIndex, HaikuView parent) {
		super(context);
		this.rowIndex = rowIndex;
		this.parent = parent;
		
//		setOnTouchListener(this);
//		setOnDragListener(this);
	}
	
	public HaikuRowWord getWordAtXPos(int xPos){
		for(int i = 0; i < words.size(); i++){
			if(words.get(i).getStartPos() + words.get(i).getLength() > xPos){
				return words.get(i);
			}
		}
		return null;
	}
	
	/**
	 * Assumes that the list is empty
	 * @param words
	 */
	public void setWords(ArrayList<Word> words){
		Paint paint = parent.getPaint();
		int position = 0;
		int length;
		HaikuRowWord temp;
		LayoutParams params;
		for(Word w : words){
//			paint.getTextBounds(w.getText(), 0, w.getText().length(), textRect);
			length = (int) paint.measureText(w.getText());
//			length = textRect.width();
			temp = new HaikuRowWord(getContext(), w, position, length, this);
			this.words.add(temp);
			params = new RelativeLayout.LayoutParams((int) temp.getLength(), LayoutParams.MATCH_PARENT);
			params.setMargins((int)temp.getStartPos(), 0, 0, 0);
			addView(temp, params);
			position += length + parent.getLengthOfSpace();
		}
	}
	
	public int getRowIndex(){
		return rowIndex;
	}
	
	/**
	 * Adds the word into the row. It adds the word into the right position and updates the position of all words on that row.
	 * @param word
	 */
	public void readdWord(HaikuRowWord word){
		if(word.getRow().equals(this)){
			addWord(word);
			return;
		}
		boolean added = false;
		for(int i = 0; i < words.size(); i++){
			if(added){
				words.get(i).setStartPos(words.get(i).getStartPos() + word.getLength() + parent.getLengthOfSpace());
			}
			else if(words.get(i).getStartPos() > word.getStartPos()){
				words.add(i, word);
				added = true;
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
	
	public void addWord(HaikuRowWord word){
		if(currentIndex == 0){
			word.setStartPos(0);
		}
		else{
			word.setStartPos(words.get(currentIndex-1).getStartPos() + words.get(currentIndex-1).getLength() + parent.getLengthOfSpace());
		}
		words.add(currentIndex, word);
		LayoutParams params = new RelativeLayout.LayoutParams((int) word.getLength(), LayoutParams.MATCH_PARENT);
		params.setMargins((int)word.getStartPos(), 0, 0, 0);
		addView(word, params);
	}
	
	public void removeWord(HaikuRowWord word){
		words.remove(word);
		removeView(word);
		for(int i = 0; i < words.size(); i++){
			if(words.get(i).getStartPos() > word.getStartPos()){
				words.get(i).setStartPos(words.get(i).getStartPos() - word.getLength() - parent.getLengthOfSpace());
			}
		}
		
	}
	
	public HaikuView getHaikuView(){
		return parent;
	}
	
	private int currentIndex;
	
	/**
	 * Offsets all words to the right of currentIndex depending on the word's length
	 * @param word
	 */
	private void offset(HaikuRowWord word){
		Log.i("TAG4", "offset");
		for(int i = currentIndex; i < words.size(); i++){
			float newStartPos = words.get(i).getStartPos() + word.getLength() + parent.getLengthOfSpace();
			words.get(i).setStartPos(newStartPos);
			LayoutParams params = new RelativeLayout.LayoutParams((int) words.get(i).getLength(), LayoutParams.MATCH_PARENT);
			params.setMargins((int)words.get(i).getStartPos(), 0, 0, 0);
			words.get(i).setLayoutParams(params);
		}
		invalidate();
	}
	
	private void reset(HaikuRowWord word){
		Log.i("TAG4", "reset");
		for(int i = currentIndex; i < words.size(); i++){
			float newStartPos = words.get(i).getStartPos() - word.getLength() - parent.getLengthOfSpace();
			words.get(i).setStartPos(newStartPos);
			LayoutParams params = new RelativeLayout.LayoutParams((int) words.get(i).getLength(), LayoutParams.MATCH_PARENT);
			params.setMargins((int)words.get(i).getStartPos(), 0, 0, 0);
			words.get(i).setLayoutParams(params);
		}
		invalidate();
	}
	
	private boolean canBeAdded;
	
	private boolean canBeAdded(HaikuRowWord word){
		canBeAdded = (words.get(words.size()-1).getStartPos() + word.length() + parent.getLengthOfSpace() <= BinView.getInstance().getHaikuWidth());
		return canBeAdded;
	}
	
	private void calcCurrentIndex(){
		currentIndex = 0;
		for(int i = 0; i < words.size(); i++){
			if(words.get(i).getStartPos() > parent.getDragPosition().getXPos()){
				currentIndex = i-1;
				return;
			}
		}
	}
	
	private void decCurrentIndex(){
		Log.i("TAG4", "decCurrentIndex");
		currentIndex--;
		float newStartPos = words.get(currentIndex).getStartPos() + parent.getDraggedView().getLength() + parent.getLengthOfSpace();
		words.get(currentIndex).setStartPos(newStartPos);
		LayoutParams params = new RelativeLayout.LayoutParams((int) words.get(currentIndex).getLength(), LayoutParams.MATCH_PARENT);
		params.setMargins((int)words.get(currentIndex).getStartPos(), 0, 0, 0);
		words.get(currentIndex).setLayoutParams(params);
		invalidate();
	}
	
	private void incCurrentIndex(){
		Log.i("TAG4", "incCurrentIndex");
		if(currentIndex > 0){
			// decrease the current
			float newStartPos = words.get(currentIndex).getStartPos() - parent.getDraggedView().getLength() - parent.getLengthOfSpace();
			words.get(currentIndex).setStartPos(newStartPos);
			LayoutParams params = new RelativeLayout.LayoutParams((int) words.get(currentIndex).getLength(), LayoutParams.MATCH_PARENT);
			params.setMargins((int)words.get(currentIndex).getStartPos(), 0, 0, 0);
			words.get(currentIndex).setLayoutParams(params);
		}
		currentIndex++;
		float newStartPos = words.get(currentIndex).getStartPos() + parent.getDraggedView().getLength() + parent.getLengthOfSpace();
		words.get(currentIndex).setStartPos(newStartPos);
		LayoutParams params = new RelativeLayout.LayoutParams((int) words.get(currentIndex).getLength(), LayoutParams.MATCH_PARENT);
		params.setMargins((int)words.get(currentIndex).getStartPos(), 0, 0, 0);
		words.get(currentIndex).setLayoutParams(params);
		invalidate();
	}
	
	public void initDrag(HaikuRowWord word){
		parent.initDrag(word);
//		dragPosition = new Position(word.getStartPos() + word.getLength()/2, 0);
		calcCurrentIndex();
//		Log.i("TAG4", "currentIndex: " + currentIndex);
		words.remove(word);
		removeView(word);
	}
	
//	@Override
//	public boolean onDrag(View v, DragEvent event) {
//		if(!(v instanceof HaikuRowWord)){
//			return false;
//		}
//		dragPosition = new Position(event.getX(), event.getY());
//		int action = event.getAction();
//	    switch (action) {
//	    	case DragEvent.ACTION_DRAG_STARTED:
//	    		break;
//	    	case DragEvent.ACTION_DRAG_ENTERED:
//	    		Log.i("TAG4", "Entered row " + (rowIndex+1));
//	    		calcCurrentIndex();
//	    		if(canBeAdded((HaikuRowWord)v)){
//	    			offset((HaikuRowWord)v);
//	    		}
//	    		break;
//	    	case DragEvent.ACTION_DRAG_EXITED:
//	    		Log.i("TAG4", "Exited row " + (rowIndex+1));
//	    		if(canBeAdded){
//	    			reset((HaikuRowWord)v);
//	    		}
//	    		break;
//	    	case DragEvent.ACTION_DROP:
//	    		Log.i("TAG4", "dropped on row " + (rowIndex+1));
//	    		if(canBeAdded){
//	    			addWord((HaikuRowWord)v);
//	    			parent.setDraggedView(null);
//	    		}
//	    		else{
//	    			((HaikuRowWord)v).getRow().readdWord((HaikuRowWord)v);
//	    		}
//	    		break;
//	    	case DragEvent.ACTION_DRAG_ENDED:
//	    		break;
//	    	default:
//	    		break;
//	    }
//	    return true;
//	}
	
//	@Override
//	public boolean onTouch(View v, MotionEvent event) {
//		Log.i("TAG4", "onTouch on row " + (rowIndex+1));
//		dragPosition = new Position(event.getX(), event.getY());
//		if (event.getAction() == MotionEvent.ACTION_MOVE) {
//			Log.i("TAG4", "move on row " + (rowIndex+1));
//			if(currentIndex > 0){
//				if(dragPosition.getXPos() < words.get(currentIndex-1).getStartPos() + words.get(currentIndex-1).getLength()/2){
//					decCurrentIndex();
//				}
//			}
//			if(currentIndex < words.size()-1){
//				if(dragPosition.getXPos() > words.get(currentIndex+1).getStartPos() + words.get(currentIndex+1).getLength()/2){
//					incCurrentIndex();
//				}
//			}
//		}
//		return true;
//	}
	
	public void dragEnteredRow(){
		Log.i("TAG4", "Entered row " + (rowIndex+1));
		calcCurrentIndex();
		Log.i("TAG4", "currentIndex: " + currentIndex);
		if(canBeAdded(parent.getDraggedView())){
			offset(parent.getDraggedView());
		}
	}
	
	public void dragExitedRow(){
		Log.i("TAG4", "Exited row " + (rowIndex+1));
		if(canBeAdded){
			reset(parent.getDraggedView());
		}
	}
	
	public void dragEndedOnRow(){
		Log.i("TAG4", "dropped on row " + (rowIndex+1));
		if(canBeAdded){
			addWord(parent.getDraggedView());
			parent.setDraggedView(null);
		}
		else{
			(parent.getDraggedView()).getRow().readdWord(parent.getDraggedView());
		}
	}
	
	public void dragEvent(){
		Log.i("TAG4", "move on row " + (rowIndex+1));
		Log.i("TAG4", "currentIndex: " + currentIndex);
		if(currentIndex > 0 && currentIndex < words.size()){
			Log.i("TAG4", "parent.getDragPosition().getXPos(): " + parent.getDragPosition().getXPos());
			Log.i("TAG4", "words.get(currentIndex-1).getStartPos(): " + words.get(currentIndex-1).getStartPos());
			Log.i("TAG4", "words.get(currentIndex-1).getLength()/2: " + words.get(currentIndex-1).getLength()/2);
			if(parent.getDragPosition().getXPos() < words.get(currentIndex-1).getStartPos() + words.get(currentIndex-1).getLength()/2){
				decCurrentIndex();
			}
		}
		if(currentIndex < words.size() && currentIndex > 0){
			Log.i("TAG4", "parent.getDragPosition().getXPos(): " + parent.getDragPosition().getXPos());
			Log.i("TAG4", "words.get(currentIndex).getStartPos(): " + words.get(currentIndex).getStartPos());
			Log.i("TAG4", "words.get(currentIndex).getLength()/2: " + words.get(currentIndex).getLength()/2);
			if(parent.getDragPosition().getXPos() > words.get(currentIndex).getStartPos() + words.get(currentIndex).getLength()/2){
				incCurrentIndex();
			}
		}
	}
}
