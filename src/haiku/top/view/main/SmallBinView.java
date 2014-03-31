package haiku.top.view.main;

import haiku.top.HaikuActivity;
import haiku.top.R;
import haiku.top.model.Position;
import haiku.top.model.Theme;
import haiku.top.model.date.YearMonth;
import haiku.top.model.generator.HaikuGenerator;
import haiku.top.view.ThemeObjectView;
import haiku.top.view.date.YearMonthView;

import java.util.ArrayList;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.RelativeLayout.LayoutParams;

public class SmallBinView extends RelativeLayout{
	private ArrayList<YearMonthView> datesView = new ArrayList<YearMonthView>();
	private ArrayList<ThemeObjectView> themesView = new ArrayList<ThemeObjectView>();
	
	private ScrollView dateScroll;
	private LinearLayout dateList;
	
	private ArrayList<LinearLayout> themeViews;
	
	private static final double WIDTH = 320.0; // These are the widht and height in pixels of the image haikubin_small (the background)
	private static final double HEIGHT = 1046.0;
	
	// the date list
	private static final Position DATE_UPPER_LEFT = new Position(70, 650);//new Position(70, 870);
	private static final int DATE_WIDTH = 160;
	private static final int DATE_HEIGHT = 340;
	private static final int DATE_OBJECT_HEIGHT = 90; // The width is the same
	private static final int DATE_ROTATION = -5;
	private int dateWidth;
	private int dateObjectHeight;
	
	// The themes
	private static final int THEME_OBJECT_WIDTH = 180;
	private static final int THEME_OBJECT_HEIGHT = 80;
	private static final Position THEME1_UPPER_LEFT = new Position(58, 130); // = new Position(280, 230);
	private static final int THEME_PADDING = 10;
	private static final int THEME_BOTTOM_MARGIN_LEFT = 70;
	private static final int THEME_BOTTOM_MARGIN_TOP = 20;
	private static final int THEME_ROTATION = -45;
	private static int themeObjectWidth;
	private static int themeObjectHeight;
	private Context context;
	
	public SmallBinView(Context context, int width, int height) {
		super(context);
		this.context = context;
        setBackgroundResource(R.drawable.haikubin_small);
        
	    // DATE
		dateWidth = (int)(((double)DATE_WIDTH)/WIDTH*width);
		int dateHeight = (int)(((double)DATE_HEIGHT)/HEIGHT*height);
		dateObjectHeight = (int)(((double)DATE_OBJECT_HEIGHT)/HEIGHT*height);
		
		int dateMarginLeft = (int)(((double)DATE_UPPER_LEFT.getXPos())/WIDTH*width);
		int dateMarginTop = (int)(((double)DATE_UPPER_LEFT.getYPos())/HEIGHT*height);
		
		dateScroll = new ScrollView(context);
		dateList = new LinearLayout(context);
		dateList.setOrientation(LinearLayout.VERTICAL);
		dateList.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		LayoutParams dateScrollParams = new RelativeLayout.LayoutParams(dateWidth, dateHeight);
		dateScrollParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		dateScrollParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		dateScrollParams.setMargins(dateMarginLeft, dateMarginTop, 0, 0);
		dateScroll.setLayoutParams(dateScrollParams);
		dateScroll.setRotation(DATE_ROTATION);
		dateScroll.addView(dateList);
		addView(dateScroll);
		
		// THEME
		themeViews = new ArrayList<LinearLayout>();
		for(int i = 0; i < 5; i++){
			themeViews.add(new LinearLayout(context));
			addView(themeViews.get(i));
//					themeViews.get(i).setVisibility(GONE);
		}
		int theme1MarginLeft = (int)(((double)THEME1_UPPER_LEFT.getXPos())/WIDTH*width);
		int theme1MarginTop = (int)(((double)THEME1_UPPER_LEFT.getYPos())/HEIGHT*height);
		
		themeObjectWidth = (int)(((double)THEME_OBJECT_WIDTH)/WIDTH*width);
		themeObjectHeight = (int)(((double)THEME_OBJECT_HEIGHT)/HEIGHT*height);
		int themePadding = (int)(((double)THEME_PADDING)/WIDTH*width);
		
		int lowerOffsetLeft = (int)(((double)THEME_BOTTOM_MARGIN_LEFT)/WIDTH*width);
		int lowerOffsetTop  = (int)(((double)THEME_BOTTOM_MARGIN_TOP)/HEIGHT*height);
		double themeRotationAbs = Math.abs(THEME_ROTATION);
		
		LayoutParams theme1Params = new RelativeLayout.LayoutParams(themeObjectWidth, themeObjectHeight);
		theme1Params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		theme1Params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		theme1Params.setMargins(theme1MarginLeft, theme1MarginTop, 0, 0);
		themeViews.get(0).setLayoutParams(theme1Params);
		themeViews.get(0).setRotation(THEME_ROTATION);
		
		double angleToNextRow = 90 - themeRotationAbs;
		
		int theme3MarginLeft = (int) (theme1MarginLeft + (themeObjectHeight+lowerOffsetTop)*Math.cos(angleToNextRow/180*Math.PI));
		int theme3MarginTop = (int) (theme1MarginTop + (themeObjectHeight+lowerOffsetTop)*Math.sin(angleToNextRow/180*Math.PI));
		LayoutParams theme3Params = new RelativeLayout.LayoutParams(themeObjectWidth, themeObjectHeight);
		theme3Params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		theme3Params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		theme3Params.setMargins(theme3MarginLeft, theme3MarginTop, 0, 0);
		themeViews.get(2).setLayoutParams(theme3Params);
		themeViews.get(2).setRotation(THEME_ROTATION);
		
		int theme2MarginLeft = (int) (theme3MarginLeft - (themeObjectWidth+themePadding)*Math.cos(themeRotationAbs/180*Math.PI));
		int theme2MarginTop = (int) (theme3MarginTop + (themeObjectWidth+themePadding)*Math.sin(themeRotationAbs/180*Math.PI));
		LayoutParams theme2Params = new RelativeLayout.LayoutParams(themeObjectWidth, themeObjectHeight);
		theme2Params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		theme2Params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		theme2Params.setMargins(theme2MarginLeft, theme2MarginTop, 0, 0);
		themeViews.get(1).setLayoutParams(theme2Params);
		themeViews.get(1).setRotation(THEME_ROTATION);
		
		
		int theme4MarginLeft = (int) (theme2MarginLeft);
		int theme4MarginTop = (int) (theme2MarginTop + (themeObjectHeight+lowerOffsetTop)*Math.sin(angleToNextRow/180*Math.PI)*2);
		LayoutParams theme4Params = new RelativeLayout.LayoutParams(themeObjectWidth, themeObjectHeight);
		theme4Params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		theme4Params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		theme4Params.setMargins(theme4MarginLeft, theme4MarginTop, 0, 0);
		themeViews.get(3).setLayoutParams(theme4Params);
		themeViews.get(3).setRotation(THEME_ROTATION);
		
		int theme5MarginLeft = (int) (theme4MarginLeft + (themeObjectWidth+themePadding)*Math.cos(themeRotationAbs/180*Math.PI));
		int theme5MarginTop = (int) (theme4MarginTop - (themeObjectWidth+themePadding)*Math.sin(themeRotationAbs/180*Math.PI));
		LayoutParams theme5Params = new RelativeLayout.LayoutParams(themeObjectWidth, themeObjectHeight);
		theme5Params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		theme5Params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		theme5Params.setMargins(theme5MarginLeft, theme5MarginTop, 0, 0);
		themeViews.get(4).setLayoutParams(theme5Params);
		themeViews.get(4).setRotation(THEME_ROTATION);
		
	}
	
	public void addTheme(Theme theme){
		ThemeObjectView tob = new ThemeObjectView(context, theme, themeObjectWidth, themeObjectHeight);
		themesView.add(tob);
//		tob.setOnTouchListener(this);
//		stateChanged = true;
		updateThemeView();
	}
	
	public void removeTheme(ThemeObjectView tob){
		themesView.remove(tob);
		updateThemeView();
//		resetHaikuFinished();
//		HaikuGenerator.updateWordsUsed();
//		HaikuGenerator.createHaikus();
	}
	
	public void updateThemeView(){ // if one of the middle themes are removed all after moves
		for(int i = 0; i < themeViews.size(); i++){
			themeViews.get(i).removeAllViews();
		}
		for(int i = 0; i < themesView.size(); i++){
			themeViews.get(i).addView(themesView.get(i));
		}
	}
	
	public void addDate(YearMonth ym){
		for(int i = 0; i < datesView.size(); i++){
			if(datesView.get(i).getYearMonth().equals(ym)){
				return;
			}
		}
		YearMonthView ymv = new YearMonthView(context, ym, dateWidth, dateObjectHeight);
		datesView.add(ymv);
		dateList.addView(ymv);
//		ymv.setOnTouchListener(this);
	}
	
	public void removeDate(YearMonth ym){
		for(YearMonthView ymv : datesView){
			if(ymv.getYearMonth().equals(ym)){
				dateList.removeView(ymv);
				datesView.remove(ymv);
				return;
			}
		}
	}
	
	public void clear(){
		datesView.clear();
		dateList.removeAllViews();
		for(int i = 0; i < themeViews.size(); i++){
			themeViews.get(i).removeAllViews();
		}
	}
	
}
