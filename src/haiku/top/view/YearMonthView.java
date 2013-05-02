package haiku.top.view;

import haiku.top.model.YearMonth;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.View;

public class YearMonthView extends View{
	private YearMonth yearMonth;
	private int width;
	private int height;
	private Rect rect;
	private Paint backgroundPaint;
	private Paint framePaint;
	private Paint monthPaint;
	private Paint yearPaint;
	
	private static final double PROPORTIONAL_HEIGHT_OF_MONTH_TEXT = 0.6; // 0.6 = 60%. The rest is the padding and the year text. This is so they don't overlap
	private static final double SIZE_OF_TEXT_PADDING_TOP = 0.1;
	private static final double SIZE_OF_TEXT_PADDING_BETWEEN = 0.05;
	private static final double SIZE_OF_MONTH_TEXT_PADDING_LEFT = 0.1;
	private static final double SIZE_OF_MONTH_TEXT_PADDING_RIGHT = 0.1;
	private static final double SIZE_OF_YEAR_TEXT_PADDING_LEFT = 0.5;
	private static final double SIZE_OF_YEAR_TEXT_PADDING_RIGHT = 0.05;
	
	public YearMonthView(Context context, YearMonth yearMonth, int width, int height) {
		super(context);
		this.yearMonth = yearMonth;
		
		// See the input width and height as dp values and convert them to px values
		this.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, width, getResources().getDisplayMetrics());
		this.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, height, getResources().getDisplayMetrics());
//		this.width = width;
//		this.height = height;
		rect = new Rect(0, 0, width, height);
		
		backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		backgroundPaint.setStyle(Paint.Style.FILL);
		backgroundPaint.setColor(Color.rgb(234, 52, 147));
		
		framePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		framePaint.setStyle(Paint.Style.STROKE);
		framePaint.setStrokeWidth(2);
		framePaint.setColor(Color.WHITE);
		
		monthPaint = new Paint();
		monthPaint.setStyle(Paint.Style.FILL);
		monthPaint.setColor(Color.WHITE);
		monthPaint.setTypeface(Typeface.DEFAULT); //TODO Adobe Garamond Pro
		
		yearPaint = new Paint();
		yearPaint.setStyle(Paint.Style.FILL);
		yearPaint.setColor(Color.WHITE);
		yearPaint.setTypeface(Typeface.DEFAULT); //TODO Adobe Garamond Pro
		
		updateMonthTextSize();
		updateYearTextSize();
	}
	
	@Override
    protected void onDraw(Canvas canvas) {
		canvas.drawRect(rect, backgroundPaint);
		canvas.drawRect(rect, framePaint);
		canvas.drawText(yearMonth.getMonth().toString(), (float)(width*SIZE_OF_MONTH_TEXT_PADDING_LEFT), (float)(height*(SIZE_OF_TEXT_PADDING_TOP + PROPORTIONAL_HEIGHT_OF_MONTH_TEXT)), monthPaint);
		canvas.drawText("" + yearMonth.getYear(), (float)(width*SIZE_OF_YEAR_TEXT_PADDING_LEFT), (float)(height*(SIZE_OF_TEXT_PADDING_TOP + PROPORTIONAL_HEIGHT_OF_MONTH_TEXT + SIZE_OF_TEXT_PADDING_BETWEEN)), yearPaint);
	}
	
	public void setYearMonth(YearMonth yearMonth){
		this.yearMonth = yearMonth;
	}
	
	public YearMonth getYearMonth(){
		return yearMonth;
	}
	
	private void updateMonthTextSize(){
		int size = 0;
		Rect monthRect;
		String text = yearMonth.getMonth().toString();
	    do {
	    	size++;
	        monthPaint.setTextSize(size);
	        monthRect = new Rect();
	        monthPaint.getTextBounds(text, 0, text.length(), monthRect);
	    } while(monthPaint.measureText(text) < width*(1-SIZE_OF_MONTH_TEXT_PADDING_LEFT - SIZE_OF_MONTH_TEXT_PADDING_RIGHT)
	    		&& monthRect.height() <= height*PROPORTIONAL_HEIGHT_OF_MONTH_TEXT);
	}
	
	private void updateYearTextSize(){
		int size = 0;
		Rect yearRect;
		String text = "" + yearMonth.getYear();
	    do {
	    	size++;
	        yearPaint.setTextSize(size);
	        yearRect = new Rect();
	        yearPaint.getTextBounds(text, 0, text.length(), yearRect);
	    } while(yearPaint.measureText(text) < width*(1-SIZE_OF_YEAR_TEXT_PADDING_LEFT - SIZE_OF_YEAR_TEXT_PADDING_RIGHT)
	    		&& yearRect.height() <= height*(1-PROPORTIONAL_HEIGHT_OF_MONTH_TEXT - SIZE_OF_TEXT_PADDING_TOP - SIZE_OF_TEXT_PADDING_BETWEEN));
	}

}
