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

public class HaikuExtraRow extends Row{
	
	public HaikuExtraRow(Context context) {
		super(context);
	}
	
	public void reset(){
		words.clear();
	}
	
	public HaikuRowWord getWordAtPos(Position pos){
		int xPos = (int) pos.getXPos();
		xPos += BinView.getInstance().getHaikuRestWidth() * ((int)(pos.getYPos()/BinView.getInstance().getHaikuView().getHeightOfOneRow()));
		for(int i = 0; i < words.size(); i++){
			if(words.get(i).getStartPos() + words.get(i).getLength() > xPos){
				return words.get(i);
			}
		}
		return null;
	}
	
	public void addWord(HaikuRowWord word){
		words.add(word);
		word.setRow(this);
		redraw();
	}
	
	public void removeWord(HaikuRowWord word){
		words.remove(word);
		redraw();
	}
	
	private void redraw(){
		removeAllViews();
		int row = 0;
		for(int i = 0; i < words.size(); i++){
			if(i == 0){
				words.get(i).setStartPos(0);
			}
			else{
				words.get(i).setStartPos(words.get(i-1).getStartPos() + words.get(i-1).getLength() + BinView.getInstance().getHaikuView().getLengthOfSpace());
			}
			if(words.get(i).getStartPos() + words.get(i).getLength() > BinView.getInstance().getHaikuRestWidth()*(row+1)){
				row++;
				words.get(i).setStartPos(row * BinView.getInstance().getHaikuRestWidth());
			}
			LayoutParams params = new RelativeLayout.LayoutParams((int) words.get(i).getLength(), LayoutParams.MATCH_PARENT);
			params.setMargins((int)words.get(i).getStartPos() - row * BinView.getInstance().getHaikuRestWidth(), row * BinView.getInstance().getHaikuView().getHeightOfOneRow(), 0, 0);
			addView(words.get(i), params);
		}
	}
	
	
	public void initDrag(HaikuRowWord word){
		BinView.getInstance().getHaikuView().setDraggedView(word);
		words.remove(word);
		redraw();
	}
	
	public void dragEndedOnRow(){
		addWord(BinView.getInstance().getHaikuView().getDraggedView());
		BinView.getInstance().getHaikuView().setDraggedView(null);
	}
}
