package haiku.top.view.binview;

import haiku.top.model.Word;
import haiku.top.model.generator.HaikuGenerator;
import haiku.top.model.smshandler.SMS;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;

public class BinCombinedSMS extends LinearLayout{
	private Context context;
	private ArrayList<BinSMSRow> rows = new ArrayList<BinSMSRow>();
	
	private float lengthOfSpace;
	
	private Character[] seperationChars = {' ', '\n', '\t'};
	
	private Paint paint;
	private int height;
	
	public BinCombinedSMS(Context context) {
		super(context);
		this.context = context;
		setOrientation(VERTICAL);
		addRow();
		paint = (new TextView(context)).getPaint();// All rows have the same paint properties
		lengthOfSpace = paint.measureText(" ");
		
//		Rect textRect = new Rect();
//		String text = "abcdefghijABCDEFQT";
//		paint.getTextBounds(text, 0, text.length(), textRect);
//		height = textRect.height();
	}
	
	private void addRow(){
		BinSMSRow row = new BinSMSRow(context);
		rows.add(row);
		addView(row);
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
		for(int i = 0; i < wordsToBeDeleted.size(); i++){
			wordsToBeDeleted.get(i).delete();
		}
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
