package haiku.top.view;

import java.util.DuplicateFormatFlagsException;

import haiku.top.model.Theme;
import haiku.top.model.Word;
import haiku.top.view.bin.BinView;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class ThemeObjectView extends LinearLayout{
	private TextView themeText;
	private Theme theme;
	private Context context;
	private int height = (int)(50 * this.getResources().getDisplayMetrics().density + 0.5f);
	
	
	public ThemeObjectView(Context context, Theme theme, boolean bin) {
		super(context);
		this.context = context;
		this.theme = theme;
		themeText = new TextView(context);
		
		if(bin){
			setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		}
		else{
			setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height));
		}
		
		addView(themeText);
		themeText.setGravity(Gravity.CENTER_VERTICAL);
		setBackgroundColor(Color.rgb(251, 206, 13));
		
//		Paint textPaint = themeText.getPaint(); // "Please use this only to consult the Paint's properties and not to change them."
//        textPaint.setColor(Color.WHITE);
//        textPaint.setTypeface(Typeface.DEFAULT); //TODO Adobe Garamond Pro
		
		themeText.setText(theme.getName());
		themeText.setTextColor(Color.WHITE);
		themeText.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		themeText.setTypeface(Typeface.DEFAULT);
		
//		themeText.setAlpha(MainView.OPACITY_FULL);
		int maxSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 20, getResources().getDisplayMetrics());
		if(bin){
//			themeText.setTextSize(2*maxSize/3);
			themeText.setPadding((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics()), 0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics()), 0);
			int width = BinView.getThemeObjectWidth();
			int height = BinView.getThemeObjectHeight();
			
			String text = theme.getName();
			Paint textPaint = themeText.getPaint();
			Rect textRect;
			int size = 0;  
		    do {
		    	size++;
		    	textPaint.setTextSize(size);
		        textRect = new Rect();
		        textPaint.getTextBounds(text, 0, text.length(), textRect);
		    } while(textPaint.measureText(text) < 4*width/5
		    		&& textRect.height() <= 4*height/5);
		}
		else{
			themeText.setPadding((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics()), 0, 0, 0);
			//TODO 100 siffran är tagen från mainview.xml under theme scrollviewen. Om den ändras ska även det här värdet ändras!
	        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics()) - (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
	        width = 9*width/10;
			
			int size = 0;
		    do {
		    	size++;
		        themeText.setTextSize(size);
		    } while(themeText.getPaint().measureText("" + themeText.getText()) < width
		    		&& size < maxSize);
		}
//		updateTextSize();
	}
	
//	private void updateTextSize(){
//		int size = 0;
//		//TODO 100 siffran är tagen från mainview.xml under theme scrollviewen. Om den ändras ska även det här värdet ändras!
//        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics()) - (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
//        width = 9*width/10;
//		String text = theme.toString();
//		Paint textPaint = new Paint();
//		Rect textRect;
//	    do {
//	    	size++;
//	    	textPaint.setTextSize(size);
//	        textRect = new Rect();
//	        textPaint.getTextBounds(text, 0, text.length(), textRect);
//	    } while(textPaint.measureText(text) < width
//	    		&& textRect.height() <= 4*height/5);
//	    themeText.setTextSize(textPaint.getTextSize());
//	}
	
	public int getHeightOfView(){
		return height;
	}
	
	public Theme getTheme(){
		return theme;
	}
	
	@Override
	public boolean equals(Object themeObject){
		if(themeObject instanceof ThemeObjectView && this.theme.equals(((ThemeObjectView)themeObject).getTheme())){
			return true;
		}
		return false;
	}
}
