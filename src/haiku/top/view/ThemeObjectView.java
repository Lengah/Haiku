package haiku.top.view;

import haiku.top.model.Theme;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
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
	
	
	public ThemeObjectView(Context context, Theme theme) {
		super(context);
		this.context = context;
		this.theme = theme;
		themeText = new TextView(context);
		
		this.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 50));
		this.addView(themeText);
		themeText.setGravity(Gravity.CENTER);
		
		setBackgroundColor(Color.WHITE);
		
		themeText.setText(theme.toString());
		themeText.setTextColor(Color.BLACK);
		themeText.setTextSize(15);
		themeText.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		
		themeText.setTypeface(null, Typeface.BOLD);
	}
	
	public Theme getTheme(){
		return theme;
	}
}
