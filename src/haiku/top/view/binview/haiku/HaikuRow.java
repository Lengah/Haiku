package haiku.top.view.binview.haiku;

import haiku.top.model.NonWordText;
import haiku.top.model.Text;
import haiku.top.model.Word;
import haiku.top.view.binview.BinView;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Paint;
import android.util.Log;
import android.widget.RelativeLayout;

public class HaikuRow extends Row{
	private int rowIndex;
	private HaikuView parent;
	private int currentIndex;
	
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
	
	public void setWords(ArrayList<Text> words){
		Paint paint = parent.getPaint();
		int position = 0;
		int length;
		HaikuRowWord temp;
		LayoutParams params;
		for(Text w : words){
			length = (int) paint.measureText(w.getText());
			if(w instanceof NonWordText){
				position = (int) Math.max(0, position-parent.getLengthOfSpace());
			}
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
	
	public void readdWord(HaikuRowWord word){
		words.add(indexOfDraggedWord, word);
		redraw(true);
//		if(word.getRow().equals(this)){
//			addWord(word);
//			return;
//		}
//		boolean added = false;
//		for(int i = 0; i < words.size(); i++){
//			if(added){
//				words.get(i).setStartPos(words.get(i).getStartPos() + word.getLength() + parent.getLengthOfSpace());
//			}
//			else if(words.get(i).getStartPos() > word.getStartPos()){
//				words.add(i, word);
//				added = true;
//			}
//		}
//		if(!added){
//			// add it at the end
//			words.add(word);
//		}
//		LayoutParams params = new RelativeLayout.LayoutParams((int) word.getLength(), LayoutParams.MATCH_PARENT);
//		params.setMargins((int)word.getStartPos(), 0, 0, 0);
//		addView(word, params);
	}
	
	public void addWord(HaikuRowWord word){
		words.add(currentIndex, word);
		word.setRow(this);
		currentIndex = -1;
		redraw(true);
//		if(currentIndex == 0){
//			word.setStartPos(0);
//		}
//		else{
//			word.setStartPos(words.get(currentIndex-1).getStartPos() + words.get(currentIndex-1).getLength() + parent.getLengthOfSpace());
//		}
//		words.add(currentIndex, word);
//		LayoutParams params = new RelativeLayout.LayoutParams((int) word.getLength(), LayoutParams.MATCH_PARENT);
//		params.setMargins((int)word.getStartPos(), 0, 0, 0);
//		addView(word, params);
	}
	
	public void removeWord(HaikuRowWord word){
		words.remove(word);
		redraw(false);
//		removeView(word);
//		for(int i = 0; i < words.size(); i++){
//			if(words.get(i).getStartPos() > word.getStartPos()){
//				words.get(i).setStartPos(words.get(i).getStartPos() - word.getLength() - parent.getLengthOfSpace());
//			}
//		}
		
	}
	
	public HaikuView getHaikuView(){
		return parent;
	}
	
//	/**
//	 * Offsets all words to the right of currentIndex depending on the word's length
//	 * @param word
//	 */
//	private void offset(HaikuRowWord word){
//		Log.i("TAG4", "offset");
//		for(int i = currentIndex; i < words.size(); i++){
//			float newStartPos = words.get(i).getStartPos() + word.getLength() + parent.getLengthOfSpace();
//			words.get(i).setStartPos(newStartPos);
//			LayoutParams params = new RelativeLayout.LayoutParams((int) words.get(i).getLength(), LayoutParams.MATCH_PARENT);
//			params.setMargins((int)words.get(i).getStartPos(), 0, 0, 0);
//			words.get(i).setLayoutParams(params);
//		}
//		invalidate();
//	}
//	
//	private void reset(HaikuRowWord word){
//		Log.i("TAG4", "reset");
//		for(int i = currentIndex; i < words.size(); i++){
//			float newStartPos = words.get(i).getStartPos() - word.getLength() - parent.getLengthOfSpace();
//			words.get(i).setStartPos(newStartPos);
//			LayoutParams params = new RelativeLayout.LayoutParams((int) words.get(i).getLength(), LayoutParams.MATCH_PARENT);
//			params.setMargins((int)words.get(i).getStartPos(), 0, 0, 0);
//			words.get(i).setLayoutParams(params);
//		}
//		invalidate();
//	}
	
	private void redraw(boolean added){
		removeAllViews();
		for(int i = 0; i < words.size(); i++){
			if(i == 0){
				words.get(i).setStartPos(0);
			}
			else{
				if(words.get(i).getWord() instanceof NonWordText){
					words.get(i).setStartPos(words.get(i-1).getStartPos() + words.get(i-1).getLength());
				}
				else{
					words.get(i).setStartPos(words.get(i-1).getStartPos() + words.get(i-1).getLength() + parent.getLengthOfSpace());
				}
			}
			if(i == currentIndex){
				if(words.get(i).getWord() instanceof NonWordText){
					words.get(i).setStartPos(words.get(i).getStartPos() + parent.getDraggedView().getLength());
				}
				else{
					words.get(i).setStartPos(words.get(i).getStartPos() + parent.getDraggedView().getLength() + parent.getLengthOfSpace());
				}
			}
			if(added && words.get(i).getStartPos() + words.get(i).getLength() > BinView.getInstance().getHaikuWidth()){
				// does not fit
				for(int a = words.size()-1; a >= i; a--){
					parent.addToExtraRow(words.get(a));
					words.remove(a);
				}
				return;
			}
			LayoutParams params = new RelativeLayout.LayoutParams((int) words.get(i).getLength(), LayoutParams.MATCH_PARENT);
			params.setMargins((int)words.get(i).getStartPos(), 0, 0, 0);
			addView(words.get(i), params);
//			words.get(i).setLayoutParams(params);
		}
	}
	
//	private boolean canBeAdded(HaikuRowWord word){
//		canBeAdded = (words.get(words.size()-1).getStartPos() + word.length() + parent.getLengthOfSpace() <= BinView.getInstance().getHaikuWidth());
//		return canBeAdded;
//	}
	
//	private void decCurrentIndex(){
//		currentIndex--;
//		redraw(false);
////		float newStartPos = words.get(currentIndex).getStartPos() + parent.getDraggedView().getLength() + parent.getLengthOfSpace();
////		words.get(currentIndex).setStartPos(newStartPos);
////		LayoutParams params = new RelativeLayout.LayoutParams((int) words.get(currentIndex).getLength(), LayoutParams.MATCH_PARENT);
////		params.setMargins((int)words.get(currentIndex).getStartPos(), 0, 0, 0);
////		words.get(currentIndex).setLayoutParams(params);
////		invalidate();
//	}
//	
//	private void incCurrentIndex(){
//		currentIndex++;
//		redraw(false);
////		if(currentIndex > 0){
////			// decrease the current
////			float newStartPos = words.get(currentIndex).getStartPos() - parent.getDraggedView().getLength() - parent.getLengthOfSpace();
////			words.get(currentIndex).setStartPos(newStartPos);
////			LayoutParams params = new RelativeLayout.LayoutParams((int) words.get(currentIndex).getLength(), LayoutParams.MATCH_PARENT);
////			params.setMargins((int)words.get(currentIndex).getStartPos(), 0, 0, 0);
////			words.get(currentIndex).setLayoutParams(params);
////		}
////		currentIndex++;
////		float newStartPos = words.get(currentIndex).getStartPos() + parent.getDraggedView().getLength() + parent.getLengthOfSpace();
////		words.get(currentIndex).setStartPos(newStartPos);
////		LayoutParams params = new RelativeLayout.LayoutParams((int) words.get(currentIndex).getLength(), LayoutParams.MATCH_PARENT);
////		params.setMargins((int)words.get(currentIndex).getStartPos(), 0, 0, 0);
////		words.get(currentIndex).setLayoutParams(params);
////		invalidate();
//	}
	
	private int indexOfDraggedWord = -1;
	
	public void initDrag(HaikuRowWord word){
		indexOfDraggedWord = words.indexOf(word);
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
		calcCurrentIndex();
		redraw(false);
//		if(canBeAdded(parent.getDraggedView())){
//			offset(parent.getDraggedView());
//		}
	}
	
	public void dragExitedRow(){
		currentIndex = -1;
		redraw(false);
//		if(canBeAdded){
//			reset(parent.getDraggedView());
//		}
	}
	
	public void dragEndedOnRow(){
		addWord(parent.getDraggedView());
		parent.setDraggedView(null);
		
//		if(canBeAdded){
//			addWord(parent.getDraggedView());
//			parent.setDraggedView(null);
//		}
//		else{
//			(parent.getDraggedView()).getRow().readdWord(parent.getDraggedView());
//		}
	}
	
	private void calcCurrentIndex(){
		currentIndex = words.size();
		for(int i = 0; i < words.size(); i++){
			if(words.get(i).getStartPos() + words.get(i).getLength()/2 > parent.getDragPosition().getXPos()){
				currentIndex = i;
				return;
			}
		}
	}
	
	public void dragEvent(){
//		Log.i("TAG4", "currentIndex: " + currentIndex);
//		if(currentIndex > 0 && currentIndex < words.size()){
//			Log.i("TAG4", "parent.getDragPosition().getXPos(): " + parent.getDragPosition().getXPos());
//			Log.i("TAG4", "words.get(currentIndex-1).getStartPos(): " + words.get(currentIndex-1).getStartPos());
//			Log.i("TAG4", "words.get(currentIndex-1).getLength()/2: " + words.get(currentIndex-1).getLength()/2);
//			if(parent.getDragPosition().getXPos() < words.get(currentIndex-1).getStartPos() + words.get(currentIndex-1).getLength()/2){
//				decCurrentIndex();
//			}
//		}
//		if(currentIndex < words.size() && currentIndex > 0){
//			Log.i("TAG4", "parent.getDragPosition().getXPos(): " + parent.getDragPosition().getXPos());
//			Log.i("TAG4", "words.get(currentIndex).getStartPos(): " + words.get(currentIndex).getStartPos());
//			Log.i("TAG4", "words.get(currentIndex).getLength()/2: " + words.get(currentIndex).getLength()/2);
//			if(parent.getDragPosition().getXPos() > words.get(currentIndex).getStartPos() + words.get(currentIndex).getLength()/2){
//				incCurrentIndex();
//			}
//		}
		int prevCI = currentIndex;
		calcCurrentIndex();
		if(prevCI != currentIndex){
			redraw(false);
		}
	}
}
