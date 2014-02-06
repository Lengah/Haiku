package haiku.top.view.binview;

import haiku.top.HaikuActivity;
import haiku.top.model.Word;
import haiku.top.model.WordAndNumber;
import haiku.top.model.generator.HaikuGenerator;
import haiku.top.model.smshandler.SMS;
import haiku.top.view.main.MainView;

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
	private int stopWhenNRows;
	private static final double STOP_WHEN_SPACE_BOTTOM = 0.7; // 30% of text area's height
	
	private ArrayList<FallingWordAnimation> animations = new ArrayList<FallingWordAnimation>();
	
	public BinCombinedSMS(Context context) {
		super(context);
		this.context = context;
//		setOrientation(VERTICAL);
		paint = (new TextView(context)).getPaint();// All rows have the same paint properties
		lengthOfSpace = paint.measureText(" ");
		
		Rect textRect = new Rect();
		String text = "abcdefghijABCDEFQT";
		paint.getTextBounds(text, 0, text.length(), textRect);
		height = (int) (textRect.height()*1.3);
		addRow();
		
		updateStopWhenNRows();
	}
	
	public void updateStopWhenNRows(){
		if(BinView.getInstance().isShowingContactName()){
			stopWhenNRows = (int)(STOP_WHEN_SPACE_BOTTOM*(BinView.getInstance().getHeightOfText()-BinView.getInstance().getContactNameHeight()))/height;
		}
		else{
			stopWhenNRows = (int)(STOP_WHEN_SPACE_BOTTOM*BinView.getInstance().getHeightOfText())/height;
		}
	}
	
	public int getHeightOfRow(){
		return height;
	}
	
	public float getLengthOfSpace(){
		return lengthOfSpace;
	}
	
	private Thread scrollThread;
	
	public void animationsStarting(){
		if(scrollThread != null || rows.size() < stopWhenNRows){
			return;
		}
		scrollThread = new Thread(){
			public void run(){
				boolean empty;
				while(true){
					empty = true;
					for(int i = 0; i < animations.size(); i++){
						if(!animations.get(i).isFinished()){
							empty = false;
							break;
						}
					}
					if(empty){
						break;
					}
//					ArrayList<FallingWordAnimation> anims = new ArrayList<FallingWordAnimation>(animations);
//					for(int i = anims.size()-1; i >= 0; i--){
//						if(anims.get(i).isFinished()){
//							anims.remove(i);
//						}
//					}
//					if(anims.isEmpty()){
//						break;
//					}
					try {
						Thread.sleep(FallingWordAnimation.TIME_TO_FALL_ONE_ROW/2);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				int rowsToFall = 0;
				while(rows.get(rowsToFall).getWords().isEmpty()){
					rowsToFall++;
				}
				if(BinView.getInstance().getRowIndexAtTop() < rowsToFall){
					final int scrollD = rowsToFall*height-BinView.getInstance().getCurrentScrollPos();
					HaikuActivity.getInstance().runOnUiThread(new Runnable() {
				        public void run() {
				        	BinView.getInstance().scrollDownD(scrollD);
				        }
					});
					int lastY = -1;
					int newY;
					while(true){
						newY = BinView.getInstance().getCurrentScrollPos();
						if(lastY != -1 && lastY == newY){
							break;
						}
						lastY = newY;
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				final int rowsToF = rowsToFall;
				HaikuActivity.getInstance().runOnUiThread(new Runnable() {
			        public void run() {
		        		removeRowsFromTheTop(rowsToF);
			        }
				});
				
				scrollThread = null;
			}
		};
		scrollThread.start();
	}
	
	public void removeRowsFromTheTop(int numberOfRows){
		int currentScroll = BinView.getInstance().getCurrentScrollPos();
		
		for(int i = 0; i < numberOfRows; i++){
			removeView(rows.get(0));
			rows.remove(0);
		}
		LayoutParams params;
		for(int i = 0; i < rows.size(); i++){
			params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, height);
			params.setMargins(0, i*height, 0, 0);
			rows.get(i).setLayoutParams(params);
			rows.get(i).setRowIndex(i);
		}
		BinView.getInstance().instantScrollTo(currentScroll-numberOfRows*height);
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
		}
		TextView movingView = new TextView(context);
		movingView.setText(word.getWord());
		movingView.setTextColor(word.getCurrentTextColor());
		LayoutParams params = new RelativeLayout.LayoutParams((int) word.getLength(), height);
		params.setMargins((int) word.getStartPos(), word.getRow().getRowIndex()*height, 0, 0);
		addView(movingView, params);
		
		for(int i = 0; i < animations.size(); i++){ //TODO
			if(word.equals(animations.get(i).getWord())//){
					&& (animations.get(i).getWord().getRow().getRowIndex() == word.getRow().getRowIndex()-rows)){
//					|| animations.get(i).getWord().getRow().getRowIndex() == word.getRow().getRowIndex()-rows-animations.get(i).getRows())){
//				if(!animations.get(i).hasStarted()){
					removeView(animations.get(i).getMovingView());
//				}
//				if(animations.get(i).hasStarted()){
//					animations.get(i).updateWhileRunning(rows, word, movingView);
//				}
//				else{
					animations.get(i).addRows(rows, word, movingView);
//				}
				return;
			}
//			if(word.equals(animations.get(i).getWord()) && animations.get(i).getWord().getRow().getRowIndex() == word.getRow().getRowIndex()-rows-animations.get(i).getRows()){
//				if(animations.get(i).hasStarted()){
//					animations.get(i).updateWhileRunning(rows, word, movingView);
//					return;
//				}
//			}
		}
		
		animations.add(new FallingWordAnimation(movingView, word, rows));
	}
	
	/**
	 * Adds an animation to the list and starts it
	 * 
	 */
	public void addAnimation(FallingWordAnimation a){
		animations.add(a);
		a.start();
	}
	
	public void colorOfAWordUpdated(BinSMSRowWord word){
		for(int i = 0; i < animations.size(); i++){
			if(animations.get(i).getWord().equals(word)){
//				animations.get(i).getMovingView().setTextColor(word.getCurrentTextColor());
				animations.get(i).updateTextColor(word.getCurrentTextColor());
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
		animationsStarting();
//		animations.clear();
	}
	
	/**
	 * Check all words and see if they can fall down
	 * This should be calculated when the view is created.
	 */
	public void init(){
		for(int i = rows.size()-1; i > 0; i--){ // start at the bottom. Don't have to do it on the top row
			rows.get(i).init();
		}
		animateWords();
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
//			for(int a = 0; a < animations.size(); a++){ //TODO
//				if(wordsToBeDeleted.get(i).equals(animations.get(a).getWord()) && wordsToBeDeleted.get(i).getRow() == animations.get(a).getWord().getRow()){
//					animations.get(a).removed();
//					removeView(animations.get(a).getMovingView());
//					animations.remove(a);
//					break;
//				}
//			}
			wordsToBeDeleted.get(i).delete();
		}
		animateWords();
	}
	
	public ArrayList<BinSMSRow> getRows(){
		return rows;
	}
	
	public void addSMSesAtPosition(ArrayList<SMS> smses, int rowIndex, int xPos){
		ArrayList<BinSMSRowWord> wordsReplaced = new ArrayList<BinSMSRowWord>();
		int firstOffset = rows.get(rowIndex).calculateOffsetOfWords(xPos);
		String message = "";
		if(rows.get(rowIndex).isTextAt(xPos-1)){
			// If there is no space before one needs to be added
			message += " ";
		}
		for(int i = 0; i < smses.size(); i++){
			if(i > 0){
				message += " ";
			}
			message += smses.get(i).getMessage(); 
		}
		if(firstOffset > paint.measureText(message) + lengthOfSpace){ // There needs to be a space between words
			// the added words fit in the available space. There is no need to calculate new positions, just add those words.
			int pLength = 0;
			for(int i = 0; i < smses.size(); i++){
				pLength += addMessageAtRowStartingAtPos(smses.get(i), rowIndex, xPos + pLength);
			}
			animateWords();
			MainView.getInstance().getBinView().updateNumberOfWordsLeft();
			return;
		}
		
		// The added words do not fit -> all words after needs to be recalculated.
		wordsReplaced.addAll(rows.get(rowIndex).removeAfterPos(xPos));
		for(int i = rowIndex+1; i < rows.size(); i++){
			rows.get(i).calculateOffsetOfWords();
			wordsReplaced.addAll(rows.get(i).getWords());
		}
		while(rows.size() > rowIndex+1){
			removeView(rows.get(rowIndex+1));
			rows.remove(rowIndex+1);
		}
		for(int i = 0; i < smses.size(); i++){
			addSMS(smses.get(i));
		}
		for(int i = 0; i < wordsReplaced.size(); i++){
			addWord(wordsReplaced.get(i));
		}
//		for(int i = 0; i < rows.size(); i++){
//			rows.get(i).setRowIndex(i);
//		}
		MainView.getInstance().getBinView().updateNumberOfWordsLeft();
		init();
	}
	
	public int availablePosition(int row, int xPos){
//		Log.i("TAG", "check row: " + row);
		for(int i = 0; i < rows.get(row).getWords().size(); i++){
			if(rows.get(row).getWords().get(i).getStartPos() <= xPos
					&& rows.get(row).getWords().get(i).getStartPos() + rows.get(row).getWords().get(i).getLength() > xPos){
				// on a word -> change the xPos so it points on the end of the word
//				Log.i("TAG", "holding over word: " + rows.get(row).getWords().get(i).getWord());
				return (int) (rows.get(row).getWords().get(i).getStartPos() + rows.get(row).getWords().get(i).getLength());
			}
		}
		// not on a word -> the inputed xPos works
		return xPos;
	}
	
	private int addMessageAtRowStartingAtPos(SMS sms, int rowIndex, int xPos){
		int returnLength = 0;
		String message = " " + sms.getMessage();
		float offset;
		String temp;
		ArrayList<String> tempArray;
		int pos;
		float length;
		while(message.length() > 0){
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
			tempArray = findWordParts(temp.toLowerCase());
			message = message.substring(pos);
			length = paint.measureText(temp);
			ArrayList<Word> realWordsInBlock = new ArrayList<Word>();
			String tempWordText;
			for(int i = 0; i < sms.getWords().size(); i++){
				if(realWordsInBlock.contains(sms.getWords().get(i))){
					continue;
				}
				tempWordText = sms.getWords().get(i).getText().toLowerCase();
				for(int a = 0; a < tempArray.size(); a++){
					if(tempArray.get(a).equals(tempWordText)){
						realWordsInBlock.add(sms.getWords().get(i));
					}
				}
				
			}
			BinSMSRowWord wordAdded = new BinSMSRowWord(context, temp, returnLength + xPos + offset, length, realWordsInBlock, rows.get(rowIndex));
			returnLength += length + offset;
			if(rowIndex < rows.size()-1){
				// there are rows below where this word might be able to fall down to
				int rowsToFall = 0;
				while(rowIndex + rowsToFall + 1 < rows.size() && rows.get(rowIndex + rowsToFall + 1).canAddWord(wordAdded)){
					rowsToFall++;
				}
				rows.get(rowIndex + rowsToFall).addWord(wordAdded);
				wordAdded.setRow(rows.get(rowIndex + rowsToFall));
				if(rowsToFall > 0){
					// It fell down
					addFallingDownWord(wordAdded, rowsToFall);
				}
			}
		}
		return returnLength;
	}
	
	public void addSMS(SMS sms){
		//TEST
//		Log.i("TAG4", "SMS words");
//		for(int i = 0; i < sms.getWords().size(); i++){
//			Log.i("TAG4", "'" + sms.getWords().get(i).getText() + "', " + sms.getWords().get(i).getwordType());
//		}
//		Log.i("TAG4", "--------------------------");
//		ArrayList<Word> testForPrint = new ArrayList<Word>();
		// /TEST
		float spaceLeft;
		String message = " " + sms.getMessage();
		float offset;
		String temp;
		ArrayList<String> tempArray;
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
			tempArray = findWordParts(temp.toLowerCase());
			message = message.substring(pos);
			length = paint.measureText(temp);
			if(offset + length > spaceLeft){
				addRow();
			}
			ArrayList<Word> realWordsInBlock = new ArrayList<Word>();
			String tempWordText;
			for(int i = 0; i < sms.getWords().size(); i++){
				if(realWordsInBlock.contains(sms.getWords().get(i))){
					continue;
				}
				tempWordText = sms.getWords().get(i).getText().toLowerCase();
				for(int a = 0; a < tempArray.size(); a++){
					if(tempArray.get(a).equals(tempWordText)){
						realWordsInBlock.add(sms.getWords().get(i));
					}
				}
			}
//			testForPrint.addAll(realWordsInBlock);
			rows.get(rows.size()-1).addWord(new BinSMSRowWord(context, temp, rows.get(rows.size()-1).getCurrentOffset() + offset, length, realWordsInBlock, rows.get(rows.size()-1)));
		}
//		Log.i("TAG4", "Words found");
//		for(int i = 0; i < testForPrint.size(); i++){
//			Log.i("TAG4", "'" + testForPrint.get(i).getText() + "', " + testForPrint.get(i).getwordType());
//		}
//		Log.i("TAG4", "--------------------------");
//		Log.i("TAG4", "Words not found");
//		boolean found;
//		for(int t = 0; t < sms.getWords().size(); t++){
//			found = false;
//			for(int i = 0; i < testForPrint.size(); i++){
//				if(sms.getWords().get(t).equals(testForPrint.get(i))){
//					found = true;
//					break;
//				}
//			}
//			if(!found){
//				Log.i("TAG4", "'" + sms.getWords().get(t).getText() + "', " + sms.getWords().get(t).getwordType());
//			}
//		}
//		Log.i("TAG4", "--------------------------");
//		Log.i("TAG4", "--------------------------");
//		Log.i("TAG4", "--------------------------");
//		Log.i("TAG4", "--------------------------");
//		Log.i("TAG4", "--------------------------");
//		Log.i("TAG4", "--------------------------");
//		Log.i("TAG4", "--------------------------");
//		Log.i("TAG4", "--------------------------");
//		Log.i("TAG4", "--------------------------");
//		Log.i("TAG4", "--------------------------");
	}
	
	public static ArrayList<String> findWordParts(String text){
		ArrayList<String> returnArray = new ArrayList<String>();
		int lastIndex = 0;
		int currentIndex = 0;
		while(currentIndex < text.length()){
			if(!HaikuGenerator.isAWordCharacter(text.charAt(currentIndex))){
				if(lastIndex < currentIndex){ // found a word
					returnArray.add(text.substring(lastIndex, currentIndex));
					lastIndex = currentIndex+1;
				}
				else{
					lastIndex++; // more than one non character word
				}
			}
			currentIndex++;
		}
		if(lastIndex < currentIndex){ // found a word
			returnArray.add(text.substring(lastIndex, currentIndex));
		}
		return returnArray;
	}
	
	private void addWord(BinSMSRowWord word){
		float spaceLeft = BinView.getInstance().getWidthOfRow() - rows.get(rows.size()-1).getCurrentOffset();
		if(spaceLeft < word.getLength() + word.getOffset()){
			addRow();
			word.setOffset((int) (word.getOffset()-spaceLeft));
		}
		float startPos = rows.get(rows.size()-1).getCurrentOffset() + word.getOffset();
		rows.get(rows.size()-1).addWord(new BinSMSRowWord(context, word.getWord(), startPos, word.getLength(), word.getRealWords(), rows.get(rows.size()-1)));
	}
}
