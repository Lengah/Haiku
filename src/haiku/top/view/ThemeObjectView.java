package haiku.top.view;

import java.util.DuplicateFormatFlagsException;

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
		
		setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 75));
		addView(themeText);
		themeText.setGravity(Gravity.CENTER);
		setBackgroundColor(Color.WHITE);
		
		themeText.setText(theme.toString());
		themeText.setTextColor(Color.BLACK);
		themeText.setTextSize(25);
		themeText.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		
		themeText.setTypeface(null, Typeface.BOLD);
	}
	
	public Theme getTheme(){
		return theme;
	}
}
