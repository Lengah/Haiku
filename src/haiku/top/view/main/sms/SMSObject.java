package haiku.top.view.main.sms;

import java.util.ArrayList;

import haiku.top.HaikuActivity;
import haiku.top.model.Word;
import haiku.top.model.generator.HaikuGenerator;
import haiku.top.model.smshandler.SMS;
import haiku.top.view.main.MainView;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;

public class SMSObject extends RelativeLayout{
//	public static final int COLOR_OF_SENT_SMS = Color.rgb(204, 247, 201);
	public static final int COLOR_OF_SENT_SMS = Color.rgb(207, 224, 231);
	
//	public static final int COLOR_OF_RECEIVED_SMS = Color.rgb(252, 252, 166);
	public static final int COLOR_OF_RECEIVED_SMS = Color.rgb(235, 237, 190);
	
	/**
	 * The padding to the right and left (depending whether it is a sent or a received message) in % of the list width.
	 * Used so there will be a small space to the edge of the screen.
	 */
	public static final double PADDING_SIDES = 2.0;
	
	/**
	 * The margin to the right and left (depending whether it is a sent or a received message) in % of the list width.
	 * Used to indicate if it is a sent or a received SMS.
	 */
	public static final double MARGIN_SIDES = 7.0;
	
	/**
	 * The side boxes' max width defined by the size of the center box in %.
	 * The bigger the box the bigger max size of the side boxes
	 */
	public static final double WIDTH_OF_SIDES = 5.0;
	
	/**
	 * 1 = the height a row takes up in the sms, 2 = 2 rows etc
	 */
	public static final double WIDTH_OF_SIDES_IN_ROWS = 1.0;
	
	private static int paddingSides;
	private static int marginSides;
	
	/**
	 * In % relative to the regular text
	 */
	public static final double SIZE_OF_DATE_TEXT = 60.0;
	private static int heightOfTextRow;
	private static int maxWidth;
	private static Typeface smsTypeface;
	
	private SMSObjectCenter centerBox;
	private SMSObjectBackground background;
	
	/**
	 * 1 = the height a row takes up in the sms, 2 = 2 rows etc
	 */
	public static final double HEIGHT_OF_TOPBOTTOM_SMSOBJECTS_IN_ROWS = 1.0;
	/**
	 * In % of the center's height
	 */
	public static final double HEIGHT_OF_TOPBOTTOM_EXTRA_MODIFIER = 5.0;
	
	/**
	 * The sms to draw
	 */
	private SMS sms;
	
	private long seed;

	public SMSObject(Context context, SMS sms) {
		super(context);
		this.sms = sms;
		calcSeed();
//		if(sms.isSent()){
//			margin = marginSides;
//		}
//		else{
//			margin = paddingSides;
//		}
		// generate middle box
		centerBox = new SMSObjectCenter(context, sms); // generates in constructor
		
		background = new SMSObjectBackground(context, this);
		
//		RelativeLayout.LayoutParams paramsLeft = new RelativeLayout.LayoutParams(sideWidth, height);
//		paramsLeft.setMargins(leftMargin, 0, 0, 0);
//		addView(leftBox, paramsLeft);
		
		addView(background, new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		
		RelativeLayout.LayoutParams paramsCenter = new RelativeLayout.LayoutParams(centerBox.getViewWidth(), centerBox.getViewHeight());
		paramsCenter.setMargins(getSideBoxWidth(), getHeightOfTopOrBottomBox(), 0, 0);
		addView(centerBox, paramsCenter);
		
//		RelativeLayout.LayoutParams paramsRight = new RelativeLayout.LayoutParams(sideWidth, height);
//		paramsRight.setMargins(leftMargin+sideWidth+centerWidth, 0, 0, 0);
//		addView(rightBox, paramsRight);
		setColor();
		if(HaikuGenerator.getAllAddedSMS().contains(sms)){
			setAlpha(MainView.OPACITY_USED);
		}
	}
	
	private void calcSeed(){
		String temp = "" +  (sms.getID()/sms.getYear()) + sms.getMessage().length();
		seed = Long.parseLong(temp);
		ArrayList<String> words = sms.getNotRealWords();
		long comb = 0;
		for(int i = 0; i < words.size(); i++){
			comb += Long.parseLong("" + i + words.get(i).length());
		}
		if(!words.isEmpty()){
			comb = comb * words.get(words.size()/2).length();
		}
		String date = "";
		if(sms.getDate().length() > 2){
			date = sms.getDate().substring(sms.getDate().length()-2, sms.getDate().length());
		}
		seed = Long.parseLong("" + seed + comb + date);
		Log.i("TAG2", "seed: " + seed);
	}
	
	public long getSeed(){
		return seed;
	}
	
	public void setColor(){
		if(sms.isSent()){
			background.setColor(SMSObject.COLOR_OF_SENT_SMS);
			centerBox.setColor(SMSObject.COLOR_OF_SENT_SMS);
		}
		else{
			background.setColor(SMSObject.COLOR_OF_RECEIVED_SMS);
			centerBox.setColor(SMSObject.COLOR_OF_RECEIVED_SMS);
		}
	}

	public SMSObjectCenter getCenterBox(){
		return centerBox;
	}
	
	public SMS getSMS(){
		return sms;
	}
	
	public static void setMarginSides(int margin){
		marginSides = margin;
	}
	
	public static void setPaddingSides(int padding){
		paddingSides = padding;
	}
	
	public static void setHeightOfTextRow(int h){
		heightOfTextRow = h;
	}
	
	public static int getHeightOfTextRow(){
		return heightOfTextRow;
	}
	
	public static Typeface getTypeFace(){
		return smsTypeface;
	}
	
	public static void calc(){
		maxWidth = MainView.getInstance().getListWidth() - marginSides - paddingSides; //margin to one side, padding to the other
		calcRowHeight();
	}
	
	private static void calcRowHeight(){
		TextView smsTextView = new TextView(HaikuActivity.getInstance().getApplicationContext());
////	smsTypeface = Typeface.createFromAsset(HaikuActivity.getInstance().getApplicationContext().getAssets(), "fonts/AGARAMONDPRO-REGULAR.OTF");
////	smsTextView.setTypeface(smsTypeface);
		Rect textRect = new Rect();
		String text = "aAbBcCdDeEfFgGhHiIjJkKLlmMoOpPqQrRtTsSuUvVxXyYzZ";
		smsTextView.getPaint().getTextBounds(text, 0, text.length(), textRect);
		setHeightOfTextRow(textRect.height());
	}
	
	public static int getMaxWidth(){
		return maxWidth;
	}
	
	public int getOffsetCreatedByThisView(){
		return centerBox.getViewHeight()+getHeightOfTopOrBottomBox();
	}
	
	public int getTotalHeight(){
		return centerBox.getViewHeight()+2*getHeightOfTopOrBottomBox();
	}
	
	public int getHeightOfTopOrBottomBox(){
		return (int) (heightOfTextRow*HEIGHT_OF_TOPBOTTOM_SMSOBJECTS_IN_ROWS + centerBox.getViewHeight()*HEIGHT_OF_TOPBOTTOM_EXTRA_MODIFIER/100.0);
	}
	
	public int getTotalWidth(){
		return centerBox.getViewWidth()+2*getSideBoxWidth();
	}
	
	public int getSideBoxWidth(){
		return (int) (heightOfTextRow*WIDTH_OF_SIDES_IN_ROWS + centerBox.getViewWidth()*WIDTH_OF_SIDES/100.0);
	}
	
	public int getMargin(){
		return marginSides;
	}
	
	public int getPadding(){
		return paddingSides;
	}
	
}
