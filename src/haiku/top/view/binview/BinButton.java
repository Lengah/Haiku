//package haiku.top.view.binview;
//
//import android.content.Context;
//import android.graphics.Color;
//import android.graphics.Paint;
//import android.graphics.Rect;
//import android.graphics.Typeface;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//import android.view.Gravity;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.View.OnTouchListener;
//
//public class BinButton extends LinearLayout implements OnTouchListener{
//	private TextView textView;
//	private boolean hasPressedOpacity = false;
//	private int height;
//	private int width;
//	
//	/**
//	 * In %
//	 */
//	private static final double OPACITY_DEFAULT = 60.0;
//	
//	/**
//	 * In %
//	 */
//	private static final double OPACITY_PRESSED = 100.0;
//	
//	/**
//	 * In ms
//	 */
//	private static final double PRESS_TIME = 250.0;
//	
//
//	public BinButton(Context context, String text, int height, int width) {
//		super(context);
//		this.height = height;
//		this.width = width;
//		textView = new TextView(context);
//		textView.setText(text);
//		textView.setTextColor(Color.BLACK);
//		setDefaultOpacity();
//		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
//		addView(textView, params);
//		textView.setGravity(Gravity.CENTER);
//		
//		setOnTouchListener(this);
//	}
//	
//	public void setTypeface(Typeface typeface){
//		textView.setTypeface(typeface);
//	}
//	
//	public int calculateMaxSize(){
//		int size = 0;
//		String text = textView.getText().toString();
//		Paint textPaint = textView.getPaint();
//		Rect textRect;
//	    do {
//	    	size++;
//	    	textView.setTextSize(size);
//	    	textPaint = textView.getPaint();
//	        textRect = new Rect();
//	        textPaint.getTextBounds(text, 0, text.length(), textRect);
//	    } while(textPaint.measureText(text) < 4*width/6 && textRect.height() <= 4*height/6);
//		
//		return size;
//	}
//	
//	public void setTextSize(int sp){
//		textView.setTextSize(sp);
//	}
//	
//	public void setPressedOpacity(){
//		textView.setAlpha((float) (OPACITY_PRESSED/100.0));
//		hasPressedOpacity = true;
//	}
//
//	public void setDefaultOpacity(){
//		textView.setAlpha((float) (OPACITY_DEFAULT/100.0));
//		hasPressedOpacity = false;
//	}
//	
//	public boolean hasPressedOpacity(){
//		return hasPressedOpacity;
//	}
//	
//	private double startTime;
//	
//
//	@Override
//	public boolean onTouch(View v, MotionEvent event) {
//		if(event.getAction() == MotionEvent.ACTION_DOWN){
//			setPressedOpacity();
//			startTime = System.currentTimeMillis();
//		}
//		else if(event.getAction() == MotionEvent.ACTION_UP){
//			if(PRESS_TIME > System.currentTimeMillis()-startTime){
//				BinView.getInstance().clickedButton(this);
//			}
//			setDefaultOpacity();
//		}
//		return true;
//	}
//}
