package haiku.top.view.binview;

import haiku.top.model.Word;
import haiku.top.model.generator.HaikuGenerator;
import haiku.top.model.smshandler.SMS;

import java.util.ArrayList;

import android.R.anim;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;

public class BinCombinedSMS extends RelativeLayout{
	private Context context;
	private ArrayList<BinSMSRow> rows = new ArrayList<BinSMSRow>();
	
	private float lengthOfSpace;
	
	private Character[] seperationChars = {' ', '\n', '\t'};
	
	private Paint paint;
	private int height;
	private boolean started = false;
	
	private ArrayList<FallingWordAnimation> animations = new ArrayList<FallingWordAnimation>();
	
	public BinCombinedSMS(Context context) {
		super(context);
		this.context = context;
//		setOrientation(VERTICAL);
		addRow();
		paint = (new TextView(context)).getPaint();// All rows have the same paint properties
		lengthOfSpace = paint.measureText(" ");
		
		Rect textRect = new Rect();
		String text = "abcdefghijABCDEFQT";
		paint.getTextBounds(text, 0, text.length(), textRect);
		height = (int) (textRect.height()*1.3);
	}
	
	public void removeTopRow(){
		removeAllViews();
		rows.remove(0);
		LayoutParams params;
		for(int i = 0; i < rows.size(); i++){
			params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, height);
			params.setMargins(0, rows.get(i).getRowIndex()*height, 0, 0);
			addView(rows.get(i), params);
		}
	}
	
	/**
	 * The word should be placed at its new row before this method is called
	 * @param word - The object that is falling
	 * @param rows - How many rows it should fall
	 */
	public void addFallingDownWord(BinSMSRowWord word, int rows){
		if(!started){
			started = true;
			for(int i = animations.size()-1; i >= 0; i--){
				if(animations.get(i).isFinished()){
					removeView(animations.get(i).getMovingView());
					animations.remove(i);
				}
			}
//			if(this.rows.get(0).getWords().isEmpty()){
//				removeTopRow();
//			}
		}
		TextView movingView = new TextView(context);
		movingView.setText(word.getWord());
		movingView.setTextColor(word.getCurrentTextColor());
		LayoutParams params = new RelativeLayout.LayoutParams((int) word.getLength(), height);
		params.setMargins((int) word.getStartPos(), word.getRow().getRowIndex()*height, 0, 0);
		addView(movingView, params);
		
		for(int i = 0; i < animations.size(); i++){
			if(word.equals(animations.get(i).getWord()) && animations.get(i).getWord().getRow().getRowIndex() == word.getRow().getRowIndex()-rows){
				removeView(animations.get(i).getMovingView());
				animations.get(i).addRows(rows, word, movingView);
				return;
			}
		}
		
		animations.add(new FallingWordAnimation(movingView, word, rows));
	}
	
	public void colorOfAWordUpdated(BinSMSRowWord word){
		for(int i = 0; i < animations.size(); i++){
			if(animations.get(i).getWord().equals(word)){
				animations.get(i).getMovingView().setTextColor(word.getCurrentTextColor());
				return;
			}
		}
	}
	
	/**
	 * Starts all animations
	 */
	public void animateWords(){
//		Log.i("TAG", "Animations: " + animations.size());
		for(int i = 0; i < animations.size(); i++){
			animations.get(i).start();
		}
		started = false;
//		animations.clear();
	}
	
	/**
	 * The words to the far right in a row might not fill out the whole view. If that is the case, words above it can fall down.
	 * This should be calculated when the view is created.
	 */
	public void init(){
		for(int i = rows.size()-1; i > 0; i--){ // start at the bottom. Don't have to do it on the top row
			rows.get(i).init();
		}
	}
	
	private void addRow(){
		BinSMSRow row = new BinSMSRow(context, rows.size(), this);
		rows.add(row);
		LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, height);
		params.setMargins(0, row.getRowIndex()*height, 0, 0);
		addView(row, params);
	}
	
	private boolean isWrongChar(char c){
		for(int i = 0; i < seperationChars.length; i++){
			if(seperationChars[i].equals(c)){
				return true;
			}
		}
		return false;
	}
	
	public ArrayList<BinSMSRowWord> setToBeDeleted(){
		ArrayList<BinSMSRowWord> wordsToDelete = new ArrayList<BinSMSRowWord>();
//		Log.i("TAG", "rows.size(): " + rows.size());
		for(int i = 0; i < rows.size(); i++){
			wordsToDelete.addAll(rows.get(i).setToBeDeleted());
		}
		return wordsToDelete;
	}
	
	public void delete(ArrayList<BinSMSRowWord> wordsToBeDeleted){
		for(int i = wordsToBeDeleted.size()-1; i >= 0; i--){ // Start at the bottom
			wordsToBeDeleted.get(i).delete();
		}
		animateWords();
	}
	
	public ArrayList<BinSMSRow> getRows(){
		return rows;
	}
	
	public void removeRow(BinSMSRow row){
		rows.remove(row);
		removeView(row);
	}
	
	public void addSMS(SMS sms){
		float spaceLeft;
		String message = " " + sms.getMessage();
		float offset;
		String temp;
		int pos;
		float length;
		while(message.length() > 0){
			spaceLeft = BinView.getInstance().getWidthOfRow() - rows.get(rows.size()-1).getCurrentOffset();
			offset = 0;
			while(message.length() > 0 && isWrongChar(message.charAt(0))){
				offset += lengthOfSpace;
				message = message.substring(1);
			}
			if(!(message.length() > 0)){
				break;
			}
			pos = 0;
			while(message.length() > pos && !isWrongChar(message.charAt(pos))){
				pos++;
			}
			temp = message.substring(0, pos);
			message = message.substring(pos);
			length = paint.measureText(temp);
			if(offset + length > spaceLeft){
				addRow();
			}
			ArrayList<Word> realWordsInBlock = new ArrayList<Word>();
			String tempWordText;
			int tempInt;
			boolean exists;
			for(int i = 0; i < sms.getWords().size(); i++){
				tempWordText = sms.getWords().get(i).getText().toLowerCase();
				exists = false;
				for(int a = 0; a < realWordsInBlock.size(); a++){ // It didn't like my contains check
					if(realWordsInBlock.get(a).getText().equalsIgnoreCase(tempWordText)){
						exists = true;
						break;
					}
				}
				if(exists){
					continue;
				}
				if(temp.toLowerCase().contains(tempWordText)
						&& ((tempInt = temp.toLowerCase().indexOf(tempWordText.charAt(0))) == 0 
										|| !HaikuGenerator.isAWordCharacter(temp.toLowerCase().charAt(tempInt-1))) // It must either be the start of the word or the character before it is not a charcter used in a word (can be / for example)
						&& ((tempInt = temp.toLowerCase().indexOf(tempWordText.charAt(tempWordText.length()-1))) == temp.length()-1 
										|| !HaikuGenerator.isAWordCharacter(temp.toLowerCase().charAt(tempInt+1)))){ // It must either be the end of the word or the character after it is not a charcter used in a word (can be ! for example)
					realWordsInBlock.add(sms.getWords().get(i));
				}
			}
			rows.get(rows.size()-1).addWord(new BinSMSRowWord(context, temp, rows.get(rows.size()-1).getCurrentOffset() + offset, length, realWordsInBlock, rows.get(rows.size()-1)));
		}
	}
}
