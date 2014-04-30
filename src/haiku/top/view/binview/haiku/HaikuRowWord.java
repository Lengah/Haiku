package haiku.top.view.binview.haiku;

import android.R.color;
import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.OnTouchListener;
import android.widget.TextView;
import haiku.top.model.Word;
import haiku.top.view.binview.BinView;
import haiku.top.view.main.MainView;


public class HaikuRowWord extends TextView{
	private Word word;
	private float length; // in PX
	private float startPos; // in PX
	private HaikuRow row;
	
	
	public HaikuRowWord(Context context, Word word, float startPos, float length, HaikuRow row){
		super(context);
		this.word = word;
		this.startPos = startPos;
		this.length = length;
		this.row = row;
		String startBlack = "<font color='black'>";
		String startGrey = "<font color='grey'>";
		String endColor = "</font>";
		String message = "";
		boolean onBlack = true;
		String[] parts = word.getSyllables().split("·");
		for(String s : parts){
			if(onBlack){
				message += startBlack + s + endColor;
			}
			else{
				message += startGrey + s + endColor;
			}
			onBlack = !onBlack;
		}
		setText(Html.fromHtml(message), TextView.BufferType.SPANNABLE);
		setTypeface(MainView.getInstance().getHaikuTypeface());
		setTextSize(TypedValue.COMPLEX_UNIT_SP, row.getHaikuView().getTextSize());
		
//		setOnTouchListener(this);
	}
	
	public void setRow(HaikuRow row){
		this.row = row;
	}
	
	public HaikuRow getRow(){
		return row;
	}
	
	public Word getWord(){
		return word;
	}

	public float getStartPos() {
		return startPos;
	}

	public void setStartPos(float startPos) {
		this.startPos = startPos;
	}

	public float getLength() {
		return length;
	}
	
	public void dragStarted(){
		Log.i("TAG4", "Start drag on word '" + word.getText() + "'");
		row.initDrag(this);
	}

//	@Override
//	public boolean onTouch(View v, MotionEvent event) {
//		if(event.getAction() == MotionEvent.ACTION_DOWN){
//			Log.i("TAG4", "Start drag on word '" + word.getText() + "'");
//			row.initDrag(this);
//			return true;
//		}
//		return false;
//	}
}
