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
import haiku.top.model.Text;
import haiku.top.model.Word;
import haiku.top.view.binview.BinView;
import haiku.top.view.main.MainView;


public class HaikuRowWord extends TextView{
	private Text word;
	private float length; // in PX
	private float startPos; // in PX
	private Row row;
	
	public HaikuRowWord(Context context, Text word, float startPos, float length, Row row){
		super(context);
		this.word = word;
		this.startPos = startPos;
		this.length = length;
		this.row = row;
		String startBlack = "<font color='black'>";
		String startGrey = "<font color='grey'>";
		String endColor = "</font>";
		String message = "";
		if(word instanceof Word){
			boolean onBlack = true;
			String[] parts = ((Word)word).getSyllables().split("·");
			for(String s : parts){
				if(onBlack){
					message += startBlack + s + endColor;
				}
				else{
					message += startGrey + s + endColor;
				}
				onBlack = !onBlack;
			}
		}
		else{
			message = startBlack + word.getText() + endColor;
		}
		setText(Html.fromHtml(message), TextView.BufferType.SPANNABLE);
		setTypeface(MainView.getInstance().getHaikuTypeface());
		setTextSize(TypedValue.COMPLEX_UNIT_SP, BinView.getInstance().getHaikuView().getTextSize());
	}
	
	public void setRow(Row row){
		this.row = row;
	}
	
	public Row getRow(){
		return row;
	}
	
	public Text getWord(){
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
		row.initDrag(this);
	}
}
