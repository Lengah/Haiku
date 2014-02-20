package haiku.top.view.main.sms;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;
import haiku.top.model.smshandler.SMS;
import haiku.top.view.main.MainView;

public class SMSObjectCenter extends RelativeLayout{
	private TextView smsTextView;
	private TextView smsDateView;
	private SMS sms;
	private SMSObject parentObject;
	private int viewWidth;
	private int viewHeight;
	
	/**
	 * In % of the text height
	 */
	public static final double PADDING_BETWEEN_TEXT_AND_DATE = 0.0;
	
	public SMSObjectCenter(Context context, SMS sms, SMSObject parentObject){
		super(context);
		this.sms = sms;
		this.parentObject = parentObject;
		
		smsTextView = new TextView(context);
		smsDateView = new TextView(context);
		
		smsTextView.setTextColor(Color.BLACK);
		smsDateView.setTextColor(Color.BLACK);
		
//		smsTextView.setTypeface(parentObject.getTypeFace());
//		smsDateView.setTypeface(parentObject.getTypeFace());
		smsTextView.setText(sms.getMessage());
		smsDateView.setText(sms.getFullDate());
		smsTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
		smsDateView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
		double textLength = smsTextView.getPaint().measureText(smsTextView.getText().toString());
		double dateLength = smsDateView.getPaint().measureText(smsDateView.getText().toString());
		
		// whats left of the list width after padding, margin and min side width
		int temp = (int) (SMSObject.getMaxWidth() - 2*SMSObject.getHeightOfTextRow()*SMSObject.WIDTH_OF_SIDES_IN_ROWS);
		
		double maxWidth = temp - temp*2.0*SMSObject.WIDTH_OF_SIDES/100.0;
		viewWidth = (int) Math.min(maxWidth, Math.max(textLength, dateLength));
		
		int heightOfSMSText = (int) ((((int)textLength/(viewWidth+1)) + 1.8) * SMSObject.getHeightOfTextRow());
		int padding = (int) (SMSObject.getHeightOfTextRow() * PADDING_BETWEEN_TEXT_AND_DATE/100.0); // padding
		int heightOfDateText = SMSObject.getHeightOfTextRow();
		viewHeight = heightOfSMSText + padding + heightOfDateText;
		
		LayoutParams textParams = new RelativeLayout.LayoutParams((int) viewWidth, heightOfSMSText);
		addView(smsTextView, textParams);
		
		smsDateView.setGravity(Gravity.RIGHT);
		
		LayoutParams dateParams = new RelativeLayout.LayoutParams((int) viewWidth, heightOfDateText);
		dateParams.setMargins(0, heightOfSMSText + padding, 0, 0);
		addView(smsDateView, dateParams);
	}
	
	public int getViewWidth(){
		return viewWidth;
	}
	
	public int getViewHeight(){
		return viewHeight;
	}
	
}
