package haiku.top.view.main.sms;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;
import haiku.top.model.generator.HaikuGenerator;
import haiku.top.model.smshandler.SMS;
import haiku.top.view.main.MainView;

public class SMSObjectCenter extends RelativeLayout{
	private TextView smsTextView;
	private TextView smsDateView;
	private int viewWidth;
	private int viewHeight;
	private SMS sms;
	
	private static float SMS_TEXT_SIZE_SP = 15;
	private static float DATE_TEXT_SIZE_SP = 12;
	
	/**
	 * In % of the text height
	 */
	public static final double PADDING_BETWEEN_TEXT_AND_DATE = 0.0;
	
	/**
	 * All four sides, small margin so the text isnt right at the edge.
	 * % of list width
	 */
	public static final double SIDE_MARGIN = 1.0;
	
	public SMSObjectCenter(Context context, SMS sms){
		super(context);
		this.sms = sms;
		smsTextView = new TextView(context);
		smsDateView = new TextView(context);
		
		smsTextView.setTextColor(Color.BLACK);
		smsDateView.setTextColor(Color.BLACK);
		
		int sideMargin = (int) (MainView.getInstance().getListWidth()*SIDE_MARGIN/100.0);
		
//		smsTextView.setTypeface(parentObject.getTypeFace());
//		smsDateView.setTypeface(parentObject.getTypeFace());
		smsTextView.setText(sms.getMessage());
		smsDateView.setText(sms.getFullDate());
		smsTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, SMS_TEXT_SIZE_SP);
		smsDateView.setTextSize(TypedValue.COMPLEX_UNIT_SP, DATE_TEXT_SIZE_SP);
		
		double textLength = smsTextView.getPaint().measureText(smsTextView.getText().toString());
		double dateLength = smsDateView.getPaint().measureText(smsDateView.getText().toString());
		
		// what's remaining of the list's width after padding, margin and min side width
		int temp = (int) (SMSObject.getMaxWidth() - 2*sideMargin - 2*SMSObject.getHeightOfTextRow()*SMSObject.WIDTH_OF_SIDES_IN_ROWS);
		
		double maxWidth = temp - temp*2.0*SMSObject.WIDTH_OF_SIDES/100.0;
		viewWidth = (int) Math.min(maxWidth, Math.max(textLength, dateLength));
		
//		int viewRows = ((int)textLength/(viewWidth+1));
//		
//		int heightOfSMSText = (int) ((viewRows + 1.8) * SMSObject.getHeightOfTextRow());
		
		int viewRows = getRows(smsTextView.getPaint(), smsTextView.getText().toString(), viewWidth);
		Log.i("TAG3", "" + smsTextView.getText().toString());
		Log.i("TAG3", "rows: " + viewRows);
	
		int heightOfSMSText = getHeight(context, smsTextView.getText().toString(), smsTextView.getTypeface());
		int padding = (int) (SMSObject.getHeightOfTextRow() * PADDING_BETWEEN_TEXT_AND_DATE/100.0); // padding
		int heightOfDateText = SMSObject.getHeightOfTextRow();
		viewHeight = heightOfSMSText + padding + heightOfDateText + 2*sideMargin;
		
		LayoutParams textParams = new RelativeLayout.LayoutParams((int) viewWidth, heightOfSMSText);
		textParams.setMargins(sideMargin, sideMargin, 0, 0);
		addView(smsTextView, textParams);

		smsDateView.setGravity(Gravity.RIGHT);
		
		LayoutParams dateParams = new RelativeLayout.LayoutParams((int) viewWidth, heightOfDateText);
		dateParams.setMargins(sideMargin, sideMargin + heightOfSMSText + padding, 0, 0);
		addView(smsDateView, dateParams);
	}
	
	private int getHeight(Context context, CharSequence text, Typeface typeface) {
        TextView textView = new TextView(context);
        textView.setTypeface(typeface);
        textView.setText(text, TextView.BufferType.SPANNABLE);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, SMS_TEXT_SIZE_SP);
        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(viewWidth, View.MeasureSpec.AT_MOST);
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        textView.measure(widthMeasureSpec, heightMeasureSpec);
        return textView.getMeasuredHeight();
    }
	
	private int getRows(Paint paint, String text, int width){
		int maxWidth = 99*width/100;
		if(text.contains("NULL")){ //TODO
			return 4;
		}
		double textLength = paint.measureText(text);
		if(textLength >= maxWidth){
			String tempString = "";
			String temptempString = "";
			String[] words = text.split(" ");
			Log.i("TAG4", "words.length: " + words.length);
			if(words.length == 0){
				words = new String[]{text};
			}
			Log.i("TAG4", "words:");
			for(String w : words){
				Log.i("TAG4", "    '" + w + "'");
			}
			for(int i = 0; i < words.length && paint.measureText(tempString) <= maxWidth; i++){
				if(words[i].equals(" ") || words[i].equals("")){
					continue;
				}
				if(paint.measureText(words[i]) >= maxWidth && temptempString.length() == 0){
					temptempString = words[i];
					break;
				}
				temptempString = tempString;
				if(!tempString.equals("")){
					tempString += " ";
				}
				tempString += words[i];
			}
			Log.i("TAG4", "temptempString: " + temptempString);
			return 1 + getRows(paint, text.substring(temptempString.length()), width);
		}
		return 1;
	}
	
	public void setColor(int color){
		setBackgroundColor(color);
	}
	
	public int getViewWidth(){
		return viewWidth;
	}
	
	public int getViewHeight(){
		return viewHeight;
	}
	
}
