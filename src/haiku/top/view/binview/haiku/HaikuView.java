package haiku.top.view.binview.haiku;

import haiku.top.model.Position;
import haiku.top.model.Word;
import haiku.top.view.binview.BinView;
import haiku.top.view.main.MainView;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.OnTouchListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class HaikuView  extends RelativeLayout{
	private float lengthOfSpace;
	private ArrayList<HaikuRow> rows = new ArrayList<HaikuRow>();
	
	private Paint paint;
	private int height;
	private int textSize;
	
	private HaikuRowWord draggedView;
	
	public HaikuView(Context context){
		super(context);
	}
	
	public void update(ArrayList<Word> row1, ArrayList<Word> row2, ArrayList<Word> row3){
		removeAllViews();
		rows.clear();
		
		String sRow1 = getStringOfList(row1);
		String sRow2 = getStringOfList(row2);
		String sRow3 = getStringOfList(row3);
		textSize = getMaxTextSizeForHaiku(sRow1, sRow2, sRow3);
		TextView testView = new TextView(getContext());
		testView.setTypeface(MainView.getInstance().getHaikuTypeface());
		testView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
		paint = testView.getPaint();// All rows have the same paint properties
		lengthOfSpace = paint.measureText(" ");
		
		Rect textRect = new Rect();
		String text = "abcdefghijABCDEFQT";
		paint.getTextBounds(text, 0, text.length(), textRect);
		height = (int) (textRect.height()*1.3);
		
		HaikuRow hRow1 = new HaikuRow(getContext(), rows.size(), this);
		rows.add(hRow1);
		HaikuRow hRow2 = new HaikuRow(getContext(), rows.size(), this);
		rows.add(hRow2);
		HaikuRow hRow3 = new HaikuRow(getContext(), rows.size(), this);
		rows.add(hRow3);
		
		LayoutParams params1 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, height);
		params1.setMargins(0, hRow1.getRowIndex()*height, 0, 0);
		addView(hRow1, params1);
		
		LayoutParams params2 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, height);
		params2.setMargins(0, hRow2.getRowIndex()*height, 0, 0);
		addView(hRow2, params2);
		
		LayoutParams params3 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, height);
		params3.setMargins(0, hRow3.getRowIndex()*height, 0, 0);
		addView(hRow3, params3);
		
		hRow1.setWords(row1);
		hRow2.setWords(row2);
		hRow3.setWords(row3);
	}
	
	public ArrayList<HaikuRow> getRows(){
		return rows;
	}
	
	public int getTextSize(){
		return textSize;
	}
	
	public String getStringOfList(ArrayList<Word> words){
		String s = "";
		for(int i = 0; i < words.size(); i++){
			s += words.get(i).getText();
			if(i != words.size()-1){
				s += " ";
			}
		}
		return s;
	}
	
	public Paint getPaint(){
		return paint;
	}
	
	public float getLengthOfSpace(){
		return lengthOfSpace;
	}
	
	private int getMaxTextSizeForHaiku(String s1, String s2, String s3){
		int size1 = 0;
		int size2 = 0;
		int size3 = 0;
		TextView row1 = new TextView(getContext());
		TextView row2 = new TextView(getContext());
		TextView row3 = new TextView(getContext());
		row1.setTypeface(MainView.getInstance().getHaikuTypeface());
		row2.setTypeface(MainView.getInstance().getHaikuTypeface());
		row3.setTypeface(MainView.getInstance().getHaikuTypeface());
		row1.setText(s1);
		row2.setText(s2);
		row3.setText(s3);
		String text = (String) row1.getText();
		Paint textPaint = row1.getPaint(); 
		Rect textRect;
	    do {
	    	size1++;
//	    	textPaint.setTextSize(size1);
	    	row1.setTextSize(size1);
	    	textPaint = row1.getPaint(); 
	        textRect = new Rect();
	        textPaint.getTextBounds(text, 0, text.length(), textRect);
	    } while(textPaint.measureText(text) < 9*BinView.getInstance().getHaikuWidth()/10
	    		&& textRect.height() < BinView.getInstance().getMaxHeightOfHaikuView()/4);
//	    //Log.i("TAG", "BinView.getInstance().getHaikuWidth(): " + BinView.getInstance().getHaikuWidth());
//	    //Log.i("TAG", "first width: " + textPaint.measureText(text));
//	    //Log.i("TAG", "second width: " + textRect.width());
	    
	    text = (String) row2.getText();
		textPaint = row2.getPaint(); 
	    do {
	    	size2++;
//	    	textPaint.setTextSize(size2);
	    	row2.setTextSize(size2);
	    	textPaint = row2.getPaint(); 
	        textRect = new Rect();
	        textPaint.getTextBounds(text, 0, text.length(), textRect);
	    } while(textPaint.measureText(text) < 9*BinView.getInstance().getHaikuWidth()/10
	    		&& textRect.height() < BinView.getInstance().getMaxHeightOfHaikuView()/4);
	    
	    text = (String) row3.getText();
		textPaint = row3.getPaint(); 
	    do {
	    	size3++;
//	    	textPaint.setTextSize(size3);
	    	row3.setTextSize(size3);
	    	textPaint = row3.getPaint(); 
	        textRect = new Rect();
	        textPaint.getTextBounds(text, 0, text.length(), textRect);
	    } while(textPaint.measureText(text) < 9*BinView.getInstance().getHaikuWidth()/10
	    		&& textRect.height() < BinView.getInstance().getMaxHeightOfHaikuView()/4);
	    
		return Math.min(size1, Math.min(size2, size3));
	}
	
	public void initDrag(HaikuRowWord word){
		dragPosition = new Position(word.getStartPos() + word.getLength()/2, 0);
		setDraggedView(word);
	}

	public HaikuRowWord getDraggedView() {
		return draggedView;
	}

	public void setDraggedView(HaikuRowWord draggedView) {
		this.draggedView = draggedView;
	}
	
	private Position dragPosition;
	
	public Position getDragPosition(){
		return dragPosition;
	}
	
	public boolean isDragging(){
		return draggedView != null;
	}
	
	public int getHeightOfOneRow(){
		return height;
	}
	
	private int currentRow;
	
	public void dragEvent(Position dragPosition){
		this.dragPosition = dragPosition;
		int newRow = (int) (dragPosition.getYPos()/height);
		newRow = Math.max(0, Math.min(newRow, 2));
		if(newRow != currentRow){
			if(currentRow != -1){
				rows.get(currentRow).dragExitedRow();
				rows.get(newRow).dragEnteredRow();
			}
			currentRow = newRow;
		}
		rows.get(currentRow).dragEvent();
	}
	
	public void dragLeftArea(){
		rows.get(currentRow).dragExitedRow();
	}
	
	public void dragStopped(Position dragPosition){
		this.dragPosition = dragPosition;
		rows.get(currentRow).dragEndedOnRow();
		currentRow = -1;
		draggedView = null;
	}
	
	public HaikuRowWord dragStarted(Position dragPosition){
		currentRow = (int) (dragPosition.getYPos()/height);
		currentRow = Math.min(currentRow, 2);
		return rows.get(currentRow).getWordAtXPos((int) dragPosition.getXPos());
	}
	
	public void addToExtraRow(HaikuRowWord word){
		BinView.getInstance().getHaikuRestView().addWord(word);
	}

	
}
