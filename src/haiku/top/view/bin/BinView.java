package haiku.top.view.bin;

import java.util.ArrayList;
import java.util.Random;

import haiku.top.HaikuActivity;
import haiku.top.R;
import haiku.top.model.Position;
import haiku.top.model.SMSBinWord;
import haiku.top.model.Theme;
import haiku.top.model.WordAndNumber;
import haiku.top.model.date.YearMonth;
import haiku.top.model.generator.Haiku;
import haiku.top.model.generator.HaikuGenerator;
import haiku.top.model.smshandler.SMS;
import haiku.top.view.ThemeObjectView;
import haiku.top.view.date.YearMonthView;
import haiku.top.view.main.MainView;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.net.Uri;
import android.provider.UserDictionary.Words;
import android.util.FloatMath;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.DragShadowBuilder;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.View.OnDragListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Space;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;

public class BinView extends RelativeLayout implements OnClickListener, OnLongClickListener, OnTouchListener, OnDragListener{
	private Context context;
	private static BinView binView;
	
	private HaikuProgressBar progressBar;
	
	private ScrollView dateScroll;
	private LinearLayout dateList;
	
	private ArrayList<LinearLayout> themeViews;
	
	private TextView contactName;
	private ScrollView textScroll;
	private LinearLayout textList;
	
	private ImageButton saveButton;
	
	private LinearLayout haikuView;
	private TextView row1;
	private TextView row2;
	private TextView row3;
	
	private ArrayList<YearMonthView> datesView = new ArrayList<YearMonthView>();
	private ArrayList<BinSMSView> smsView = new ArrayList<BinSMSView>();
	private ArrayList<ThemeObjectView> themesView = new ArrayList<ThemeObjectView>();
	
//	private static final int DATE_WIDTH = 75; //in dp
//	private static final int DATE_HEIGHT = 50; //in dp
	
	
	// Everything in px
	private static final int BIN_IMAGE_WIDTH = 718;
	private static final int BIN_IMAGE_HEIGHT = 1280;
	
	private int screenWidth;
	private int screenHeight;
	
	public static final int DELETE_DISTANCE = 5;
	private int deleteDistance;
	
	// These positions are compared to the image width and height
	
	// seperates the drag listener from the rest of the view
	private static final Position DRAG_LINE1_DOT1 = new Position(10, 560);
	private static final Position DRAG_LINE1_DOT2 = new Position(70, 400);
	private static final Position DRAG_LINE1_DOT3 = new Position(150, 360);
	private static final Position DRAG_LINE1_DOT4 = new Position(160, 290);
	private static final Position DRAG_LINE1_DOT5 = new Position(380, 200);
	private static final Position DRAG_LINE1_DOT6 = new Position(420, 200);
	private static final Position DRAG_LINE1_DOT7 = new Position(525, 0);
	private static final Position DRAG_LINE1_DOT8 = new Position(600, 20);
	private static final Position DRAG_LINE1_DOT9 = new Position(660, 170);
	private static final Position DRAG_LINE1_DOT10 = new Position(718, 155);
	
	private static final Position DRAG_LINE2_DOT1 = new Position(5, 750);
	private static final Position DRAG_LINE2_DOT2 = new Position(150, 1200);
	private static final Position DRAG_LINE2_DOT3 = new Position(240, 1215);
	private static final Position DRAG_LINE2_DOT4 = new Position(336, 1280);
	
	private Position line1Dot1;
	private Position line1Dot2;
	private Position line1Dot3;
	private Position line1Dot4;
	private Position line1Dot5;
	private Position line1Dot6;
	private Position line1Dot7;
	private Position line1Dot8;
	private Position line1Dot9;
	private Position line1Dot10;
	
	private Position line2Dot1;
	private Position line2Dot2;
	private Position line2Dot3;
	private Position line2Dot4;
	
	private boolean inDropRange = false;
	// y = kx + m
	private double dragLine1K1;
	private double dragLine1K2;
	private double dragLine1K3;
	private double dragLine1K4;
	private double dragLine1K5;
	private double dragLine1K6;
	private double dragLine1K7;
	private double dragLine1K8;
	private double dragLine1K9;
	private double dragLine1M1;
	private double dragLine1M2;
	private double dragLine1M3;
	private double dragLine1M4;
	private double dragLine1M5;
	private double dragLine1M6;
	private double dragLine1M7;
	private double dragLine1M8;
	private double dragLine1M9;
	
	private double dragLine2K1;
	private double dragLine2K2;
	private double dragLine2K3;
	private double dragLine2M1;
	private double dragLine2M2;
	private double dragLine2M3;
	
	// the slider
	private static final Position SLIDER_UPPER_LEFT = new Position(120, 410);
	private static final int SLIDER_WIDTH = 22; // The width of the actual slider, not the whole view
	private static final int SLIDER_HEIGHT = 400; // The height of the actual slider, not the whole view (The view will only take up as much space as it needs)
	private static final int SLIDER_DOT_DIM = 35;
	
	// the smses
	private static final Position TEXT_UPPER_LEFT = new Position(240, 330);
	private static final int TEXT_WIDTH = 478;
	private static final int TEXT_HEIGHT = 750;
	
	// the haiku
	private static final Position HAIKU_UPPER_LEFT = new Position(240, 600);
	private static final int HAIKU_WIDTH = 478;
	private static final int HAIKU_HEIGHT = 250;
	
	// the save button
	private static final Position SAVE_UPPER_LEFT = new Position(468, 960);
	private static final int SAVE_WIDTH = 250;
	private static final int SAVE_HEIGHT = 250;
	
	// the date list
	private static final Position DATE_UPPER_LEFT = new Position(70, 870);
	private static final int DATE_WIDTH = 130;
	private static final int DATE_HEIGHT = 280;
	private static final int DATE_OBJECT_HEIGHT = 70; // The width is the same
	private static final int DATE_ROTATION = -5;
	private int dateWidth;
	private int dateObjectHeight;
	
	// The themes
	private static final int THEME_OBJECT_WIDTH = 120;
	private static final int THEME_OBJECT_HEIGHT = 60;
	private static final Position THEME1_UPPER_LEFT = new Position(280, 230);
	private static final int THEME_PADDING = 10;
	private static final int THEME_BOTTOM_MARGIN_LEFT = 70;
	private static final int THEME_BOTTOM_MARGIN_TOP = 20;
	private static final int THEME_ROTATION = -45;
	private static int themeObjectWidth;
	private static int themeObjectHeight;
	
	private View viewBeingDragged = null;
	
	private boolean haikuFinished = false;
	
	private static Random randomGenerator = new Random();
	public static final int PERCENTAGE_TO_CHANGE = 50;
	private ArrayList<WordAndNumber> wordsUsed = new ArrayList<WordAndNumber>(); // to know when a word is completely removed
	private ArrayList<String> allWordsRemoved = new ArrayList<String>(); // so that the haikus know which words they can't use
	private ArrayList<WordAndNumber> wordsRemovedLast = new ArrayList<WordAndNumber>(); // to easily undo last change and to check if some haikus are no longer valid
	private ArrayList<BinSMSView> lastChanged = new ArrayList<BinSMSView>(); // used by undo
	
	private Haiku safeHaiku;
	private boolean showHaiku = false;
	private Haiku endHaiku;
	private boolean stateChanged = false; // If the user closes the bin and then opens it without adding new SMS, there is no need to generate new SMS
	
	public BinView(Context context) {
		super(context);
		this.context = context;
		binView = this;
		setBackgroundResource(R.drawable.haikubin_extended);
		setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		screenWidth = size.x;
		screenHeight = size.y;
		screenHeight = screenHeight - HaikuActivity.getInstance().getStatusBarHeight();
		
		// See the DELETE_DISTANCE value as a dp value and convert it to a px value
        deleteDistance = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DELETE_DISTANCE, getResources().getDisplayMetrics());
       
		
		// calculate the line that seperates the listener from the rest of the view
		calculateOutOfBinView();
		
		// SLIDER
		int sliderWidth = (int)(((double)SLIDER_WIDTH)/BIN_IMAGE_WIDTH*screenWidth);
		int sliderHeight = (int)(((double)SLIDER_HEIGHT)/BIN_IMAGE_HEIGHT*screenHeight);
		
		int sliderMarginLeft = (int)(((double)SLIDER_UPPER_LEFT.getXPos())/BIN_IMAGE_WIDTH*screenWidth);
		int sliderMarginTop = (int)(((double)SLIDER_UPPER_LEFT.getYPos())/BIN_IMAGE_HEIGHT*screenHeight);
		
		int sliderDotSize = (int)(((double)SLIDER_DOT_DIM)/BIN_IMAGE_HEIGHT*screenHeight);
		
		progressBar = new HaikuProgressBar(context, sliderDotSize, sliderWidth, sliderHeight);
		LayoutParams progressBarParams = new RelativeLayout.LayoutParams(sliderWidth+sliderDotSize, sliderHeight+sliderDotSize); // the dot will be drawn in the middle of the line -> an extra sliderDotSize/2 is required at the left, right, bottom and top
		progressBarParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		progressBarParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		progressBarParams.setMargins(sliderMarginLeft, sliderMarginTop, 0, 0);
		progressBar.setLayoutParams(progressBarParams);
		addView(progressBar);
		
		// SMSES
		int textWidth = (int)(((double)TEXT_WIDTH)/BIN_IMAGE_WIDTH*screenWidth);
		int textHeight = (int)(((double)TEXT_HEIGHT)/BIN_IMAGE_HEIGHT*screenHeight);
		
		int textMarginLeft = (int)(((double)TEXT_UPPER_LEFT.getXPos())/BIN_IMAGE_WIDTH*screenWidth);
		int textMarginTop = (int)(((double)TEXT_UPPER_LEFT.getYPos())/BIN_IMAGE_HEIGHT*screenHeight);
		
		LinearLayout nameAndTextLayout = new LinearLayout(context);
		textScroll = new ScrollView(context);
		textList = new LinearLayout(context);
		contactName = new TextView(context);
		
		nameAndTextLayout.setOrientation(LinearLayout.VERTICAL);
		contactName.setTextColor(Color.BLACK);
		contactName.setGravity(Gravity.CENTER);
		contactName.setTypeface(null, Typeface.BOLD);
		contactName.setVisibility(GONE);
		textList.setOrientation(LinearLayout.VERTICAL);
		LayoutParams textParams = new RelativeLayout.LayoutParams(textWidth, textHeight);
		textParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		textParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		textParams.setMargins(textMarginLeft, textMarginTop, 0, 0);
		nameAndTextLayout.setLayoutParams(textParams);
		nameAndTextLayout.addView(contactName);
		nameAndTextLayout.addView(textScroll);
		textScroll.addView(textList);
		addView(nameAndTextLayout);
		
		int contactNameHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics());
		contactName.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, contactNameHeight));
		
		// HAIKU
		int	haikuWidth = (int)(((double)HAIKU_WIDTH)/BIN_IMAGE_WIDTH*screenWidth);
		int haikuHeight = (int)(((double)HAIKU_HEIGHT)/BIN_IMAGE_HEIGHT*screenHeight);
		
		int haikuMarginLeft = (int)(((double)HAIKU_UPPER_LEFT.getXPos())/BIN_IMAGE_WIDTH*screenWidth);
		int haikuMarginTop = (int)(((double)HAIKU_UPPER_LEFT.getYPos())/BIN_IMAGE_HEIGHT*screenHeight);
		
		haikuView = new LinearLayout(context);
		row1 = new TextView(context);
		row2 = new TextView(context);
		row3 = new TextView(context);
		
		haikuView.setOrientation(LinearLayout.VERTICAL);
		
		row1.setGravity(CENTER_VERTICAL);
		row2.setGravity(CENTER_VERTICAL);
		row3.setGravity(CENTER_VERTICAL);
		row1.setTextColor(Color.BLACK);
		row2.setTextColor(Color.BLACK);
		row3.setTextColor(Color.BLACK);
		row1.setTextSize(17);
		row2.setTextSize(17);
		row3.setTextSize(17);
		
		LayoutParams haikuParams = new RelativeLayout.LayoutParams(haikuWidth, haikuHeight);
		haikuParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		haikuParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		haikuParams.setMargins(haikuMarginLeft, haikuMarginTop, 0, 0);
		haikuView.setLayoutParams(haikuParams);
		haikuView.addView(row1);
		haikuView.addView(row2);
		haikuView.addView(row3);
		addView(haikuView);
		haikuView.setVisibility(GONE);
	
		
		// SAVE BUTTON
		int saveWidth = (int)(((double)SAVE_WIDTH)/BIN_IMAGE_WIDTH*screenWidth);
		int saveHeight = (int)(((double)SAVE_HEIGHT)/BIN_IMAGE_HEIGHT*screenHeight);
		
		int saveMarginLeft = (int)(((double)SAVE_UPPER_LEFT.getXPos())/BIN_IMAGE_WIDTH*screenWidth);
		int saveMarginTop = (int)(((double)SAVE_UPPER_LEFT.getYPos())/BIN_IMAGE_HEIGHT*screenHeight);
		
		saveButton = new ImageButton(context);
		LayoutParams saveParams = new RelativeLayout.LayoutParams(saveWidth, saveHeight);
		saveParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		saveParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		saveParams.setMargins(saveMarginLeft, saveMarginTop, 0, 0);
		saveButton.setLayoutParams(saveParams);
		addView(saveButton);
//		saveButton.setImageResource(R.drawable.save_button_default);
		saveButton.setBackgroundResource(R.drawable.save_button_default);
		saveButton.setOnClickListener(this);
		saveButton.setVisibility(GONE);
		
		
		// DATE
		dateWidth = (int)(((double)DATE_WIDTH)/BIN_IMAGE_WIDTH*screenWidth);
		int dateHeight = (int)(((double)DATE_HEIGHT)/BIN_IMAGE_HEIGHT*screenHeight);
		dateObjectHeight = (int)(((double)DATE_OBJECT_HEIGHT)/BIN_IMAGE_HEIGHT*screenHeight);
		
		int dateMarginLeft = (int)(((double)DATE_UPPER_LEFT.getXPos())/BIN_IMAGE_WIDTH*screenWidth);
		int dateMarginTop = (int)(((double)DATE_UPPER_LEFT.getYPos())/BIN_IMAGE_HEIGHT*screenHeight);
		
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
		for(int i = 0; i < 8; i++){
			themeViews.add(new LinearLayout(context));
			addView(themeViews.get(i));
//			themeViews.get(i).setVisibility(GONE);
		}
		int theme1MarginLeft = (int)(((double)THEME1_UPPER_LEFT.getXPos())/BIN_IMAGE_WIDTH*screenWidth);
		int theme1MarginTop = (int)(((double)THEME1_UPPER_LEFT.getYPos())/BIN_IMAGE_HEIGHT*screenHeight);
		
		themeObjectWidth = (int)(((double)THEME_OBJECT_WIDTH)/BIN_IMAGE_WIDTH*screenWidth);
		themeObjectHeight = (int)(((double)THEME_OBJECT_HEIGHT)/BIN_IMAGE_HEIGHT*screenHeight);
		int themePadding = (int)(((double)THEME_PADDING)/BIN_IMAGE_WIDTH*screenWidth);
		
		int lowerOffsetLeft = (int)(((double)THEME_BOTTOM_MARGIN_LEFT)/BIN_IMAGE_WIDTH*screenWidth);
		int lowerOffsetTop  = (int)(((double)THEME_BOTTOM_MARGIN_TOP)/BIN_IMAGE_HEIGHT*screenHeight);
		double themeRotationAbs = Math.abs(THEME_ROTATION);
		
		LayoutParams theme1Params = new RelativeLayout.LayoutParams(themeObjectWidth, themeObjectHeight);
		theme1Params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		theme1Params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		theme1Params.setMargins(theme1MarginLeft, theme1MarginTop, 0, 0);
		themeViews.get(0).setLayoutParams(theme1Params);
		themeViews.get(0).setRotation(THEME_ROTATION);
		
		int theme2MarginLeft = (int) (theme1MarginLeft + (themeObjectWidth+themePadding)*Math.cos(themeRotationAbs/180*Math.PI));
		int theme2MarginTop = (int) (theme1MarginTop - (themeObjectWidth+themePadding)*Math.sin(themeRotationAbs/180*Math.PI));
		LayoutParams theme2Params = new RelativeLayout.LayoutParams(themeObjectWidth, themeObjectHeight);
		theme2Params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		theme2Params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		theme2Params.setMargins(theme2MarginLeft, theme2MarginTop, 0, 0);
		themeViews.get(1).setLayoutParams(theme2Params);
		themeViews.get(1).setRotation(THEME_ROTATION);
		
		int theme3MarginLeft = (int) (theme2MarginLeft + (themeObjectWidth+themePadding)*Math.cos(themeRotationAbs/180*Math.PI));
		int theme3MarginTop = (int) (theme2MarginTop - (themeObjectWidth+themePadding)*Math.sin(themeRotationAbs/180*Math.PI));
		LayoutParams theme3Params = new RelativeLayout.LayoutParams(themeObjectWidth, themeObjectHeight);
		theme3Params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		theme3Params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		theme3Params.setMargins(theme3MarginLeft, theme3MarginTop, 0, 0);
		themeViews.get(2).setLayoutParams(theme3Params);
		themeViews.get(2).setRotation(THEME_ROTATION);
		
		double angleToNextRow = 90 - themeRotationAbs;
		
		int theme4MarginLeft = (int) (theme1MarginLeft + (themeObjectHeight+lowerOffsetTop)*Math.cos(angleToNextRow/180*Math.PI));
		int theme4MarginTop = (int) (theme1MarginTop + (themeObjectHeight+lowerOffsetTop)*Math.sin(angleToNextRow/180*Math.PI));
		// Also needs to offset it to the right so it doesn't cover the smses
		theme4MarginLeft = (int) (theme4MarginLeft + (lowerOffsetLeft)*Math.cos(themeRotationAbs/180*Math.PI));
		theme4MarginTop = (int) (theme4MarginTop - (lowerOffsetLeft)*Math.sin(themeRotationAbs/180*Math.PI));
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
		
		int theme6MarginLeft = (int) (theme4MarginLeft + (themeObjectHeight+lowerOffsetTop)*Math.cos(angleToNextRow/180*Math.PI));
		int theme6MarginTop = (int) (theme4MarginTop + (themeObjectHeight+lowerOffsetTop)*Math.sin(angleToNextRow/180*Math.PI));
		// Also needs to offset it to the right so it doesn't cover the smses
		theme6MarginLeft = (int) (theme6MarginLeft + (lowerOffsetLeft)*Math.cos(themeRotationAbs/180*Math.PI));
		theme6MarginTop = (int) (theme6MarginTop - (lowerOffsetLeft)*Math.sin(themeRotationAbs/180*Math.PI));
		LayoutParams theme6Params = new RelativeLayout.LayoutParams(themeObjectWidth, themeObjectHeight);
		theme6Params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		theme6Params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		theme6Params.setMargins(theme6MarginLeft, theme6MarginTop, 0, 0);
		themeViews.get(5).setLayoutParams(theme6Params);
		themeViews.get(5).setRotation(THEME_ROTATION);
		
		int theme7MarginLeft = (int) (theme6MarginLeft + (themeObjectWidth+themePadding)*Math.cos(themeRotationAbs/180*Math.PI));
		int theme7MarginTop = (int) (theme6MarginTop - (themeObjectWidth+themePadding)*Math.sin(themeRotationAbs/180*Math.PI));
		LayoutParams theme7Params = new RelativeLayout.LayoutParams(themeObjectWidth, themeObjectHeight);
		theme7Params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		theme7Params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		theme7Params.setMargins(theme7MarginLeft, theme7MarginTop, 0, 0);
		themeViews.get(6).setLayoutParams(theme7Params);
		themeViews.get(6).setRotation(THEME_ROTATION);
		
		int theme8MarginLeft = (int) (theme6MarginLeft + (themeObjectHeight+lowerOffsetTop)*Math.cos(angleToNextRow/180*Math.PI));
		int theme8MarginTop = (int) (theme6MarginTop + (themeObjectHeight+lowerOffsetTop)*Math.sin(angleToNextRow/180*Math.PI));
		// Also needs to offset it to the right so it doesn't cover the smses
		theme8MarginLeft = (int) (theme8MarginLeft + (lowerOffsetLeft)*Math.cos(themeRotationAbs/180*Math.PI));
		theme8MarginTop = (int) (theme8MarginTop - (lowerOffsetLeft)*Math.sin(themeRotationAbs/180*Math.PI));
		LayoutParams theme8Params = new RelativeLayout.LayoutParams(themeObjectWidth, themeObjectHeight);
		theme8Params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		theme8Params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		theme8Params.setMargins(theme8MarginLeft, theme8MarginTop, 0, 0);
		themeViews.get(7).setLayoutParams(theme8Params);
		themeViews.get(7).setRotation(THEME_ROTATION);
		
		
		setOnClickListener(this);
		setOnTouchListener(this);
		setOnDragListener(this);
		textScroll.setOnTouchListener(new OnTouchListener() {
			   @Override
			   public boolean onTouch(View v, MotionEvent event) {
				   if(event.getPointerCount() == 2){
			    		isDeleting = true;
			    		twoFingers = true;
			    		BinView.getInstance().onTouch(BinView.getInstance(), event);
			    	}
					if(event.getAction() == MotionEvent.ACTION_UP){
						twoFingers = false;
						pressedDownOn = null;
					}
					return isDeleting; // if false, the scrollview's own onTouch will handle the event
			   }
		});
	}
	
	public static BinView getInstance(){
		return binView;
	}
	
	public void addDate(YearMonth ym){
		YearMonthView ymv = new YearMonthView(context, ym, dateWidth, dateObjectHeight);
		datesView.add(ymv);
		dateList.addView(ymv);
		ymv.setOnTouchListener(this);
	}
	
	public void removeDate(YearMonthView ymv){
		dateList.removeView(ymv);
		datesView.remove(ymv);
		return;
	}
	
	private boolean showingContactName = false;
	private long contactID;
	
	public void addSMS(SMS sms){
		BinSMSView tv = new BinSMSView(context, sms);
		tv.setPadding(0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics()), 0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics()));
		textList.addView(tv);
		tv.setOnTouchListener(this);
		smsView.add(tv);
		if(smsView.size() == 1){
			// Only sms! -> show contact
			updateContactName();
		}
		else if(showingContactName && contactID != sms.getContactID()){
			showingContactName = false;
			contactName.setVisibility(GONE);
		}
		stateChanged = true;
	}
	
	public void removeSMS(BinSMSView sms){
		removeWords(sms.getWordsAsStrings());
		textList.removeView(sms);
		smsView.remove(sms);
		updateContactName();
		HaikuGenerator.checkIfHaikusAreValid(allWordsRemoved);
		if(HaikuGenerator.getRandomReadyHaiku() == null){
			HaikuGenerator.updateWordsUsed();
			resetHaikuFinished();
			HaikuGenerator.createHaikus();
		}
	}
	
	public void addSMSES(ArrayList<SMS> smses){
		for(int i = 0; i < smses.size(); i++){
			addSMS(smses.get(i));
		}
	}
	
	public void removeSMSES(ArrayList<SMS> smses){
		for(int i = smsView.size() -1 ; i >= 0; i--){
			if(smses.contains(smsView.get(i).getSMS())){
				removeWords(smsView.get(i).getWordsAsStrings());
				textList.removeView(smsView.get(i));
				smsView.remove(i);
			}	
		}
		updateContactName();
		HaikuGenerator.checkIfHaikusAreValid(allWordsRemoved);
		if(HaikuGenerator.getRandomReadyHaiku() == null){
			HaikuGenerator.updateWordsUsed();
			resetHaikuFinished();
			HaikuGenerator.createHaikus();
		}
	}
	
	public void reset(){
		progressBar.resetProgress();
		haikuView.setVisibility(GONE);
		textScroll.setVisibility(VISIBLE);
		saveButton.setVisibility(GONE);
		haikuFinished = false;
		showHaiku = false;
		wordsUsed.clear();
		allWordsRemoved.clear();
		wordsRemovedLast.clear();
		lastChanged.clear();
		datesView.clear();
		smsView.clear();
		themesView.clear();
		textList.removeAllViews();
		dateList.removeAllViews();
		safeHaiku = null;
		endHaiku = null;
		for(int i = 0; i < themeViews.size(); i++){
			themeViews.get(i).removeAllViews();
			themeViews.get(i).setVisibility(GONE);
		}
		HaikuGenerator.reset();
		updateContactName();
		MainView.getInstance().updateConversations();
		MainView.getInstance().updateThemeView();
//		progressBar.setMaxProgress(smsView.size()*10); //TODO not here
	}
	
	public void onOpen(){
		if(!stateChanged){
			// the bin was closed and then opened, but the contents did not change -> nothing needs to be done
			return;
		}
		if(stateChanged && haikuFinished){
			// new SMS has been added to the bin. If new haikus are not generated, they will not be used
			resetHaikuFinished();
			HaikuGenerator.updateWordsUsed();
		}
		// this is the first time the bin is opened ever or since the last change
		HaikuGenerator.createHaikus();
	}
	
	public void addWords(ArrayList<String> words){
		boolean found;
		for(int i = 0; i < words.size(); i++){
			found = false;
			for(int a = 0; a < wordsUsed.size(); a++){
				if(wordsUsed.get(a).getWord().equals(words.get(i))){
					found = true;
					wordsUsed.get(a).increase();
					break;
				}
			}
			if(!found){
				wordsUsed.add(new WordAndNumber(words.get(i)));
			}
		}
	}
	
	public void removeWords(ArrayList<String> words){
		for(int i = 0; i < words.size(); i++){
			for(int a = wordsUsed.size() - 1; a >= 0; a--){
				if(wordsUsed.get(a).getWord().equals(words.get(i))){
					if(wordsUsed.get(a).decrease()){
						wordsUsed.remove(a);
						allWordsRemoved.add(words.get(i));
					}
					break;
				}
			}
		}
	}
	
	/**
	 * Checks if the contact name should be shown and shows it if so.
	 * Called when an sms is removed from the view
	 */
	private void updateContactName(){
		if(showingContactName){ // Is already shown
			if(smsView.isEmpty()){ // but shouldn't be
				showingContactName = false;
				contactName.setVisibility(GONE);
			}
			return;
		}
		if(smsView.isEmpty()){ // Is not shown and shouldn't be shown
			return;
		}
		
		long firstContactID = smsView.get(0).getSMS().getContactID(); // can't be empty here
		for(int i = 1; i < smsView.size(); i++){
			if(smsView.get(i).getSMS().getContactID() != firstContactID){
				// shouldn't be shown
				return;
			}
		}
		// is not shown, but should be
		showingContactName = true;
		contactID = firstContactID;
		Uri uri = Uri.parse(HaikuActivity.ALLBOXES);
		Cursor cursor = context.getContentResolver().query(uri, null, "thread_id='" + firstContactID + "'", null, null);
		if(cursor.moveToFirst()){
			String address = cursor.getString(cursor.getColumnIndexOrThrow("address"));
			contactName.setText(HaikuActivity.getContactName(context, address));
		}
		else{
			contactName.setText("Name not found");
		}
		contactName.setVisibility(VISIBLE);
	}
	
	public void addTheme(Theme theme){
		ThemeObjectView tob = new ThemeObjectView(context, theme, true);
		themesView.add(tob);
		tob.setOnTouchListener(this);
		stateChanged = true;
		updateThemeView();
	}
	
	public void removeTheme(ThemeObjectView tob){
		themesView.remove(tob);
		updateThemeView();
		resetHaikuFinished();
		HaikuGenerator.updateWordsUsed();
		HaikuGenerator.createHaikus();
	}
	
	public void updateThemeView(){
		for(int i = 0; i < themeViews.size(); i++){
			themeViews.get(i).removeAllViews();
		}
		for(int i = 0; i < themesView.size(); i++){
			themeViews.get(i).addView(themesView.get(i));
		}
	}
	
	/**
	 * in px
	 * @return
	 */
	public static int getThemeObjectWidth(){
		return themeObjectWidth;
	}
	
	/**
	 * in px
	 * @return
	 */
	public static int getThemeObjectHeight(){
		return themeObjectHeight;
	}
	
	public void calculateOutOfBinView(){
		// calculate the line that seperates the listener from the rest of the view
		line1Dot1 = new Position((int)(((double)DRAG_LINE1_DOT1.getXPos())/BIN_IMAGE_WIDTH*screenWidth), screenHeight - (int)(((double)DRAG_LINE1_DOT1.getYPos())/BIN_IMAGE_HEIGHT*screenHeight));
		line1Dot2 = new Position((int)(((double)DRAG_LINE1_DOT2.getXPos())/BIN_IMAGE_WIDTH*screenWidth), screenHeight - (int)(((double)DRAG_LINE1_DOT2.getYPos())/BIN_IMAGE_HEIGHT*screenHeight));
		line1Dot3 = new Position((int)(((double)DRAG_LINE1_DOT3.getXPos())/BIN_IMAGE_WIDTH*screenWidth), screenHeight - (int)(((double)DRAG_LINE1_DOT3.getYPos())/BIN_IMAGE_HEIGHT*screenHeight));
		line1Dot4 = new Position((int)(((double)DRAG_LINE1_DOT4.getXPos())/BIN_IMAGE_WIDTH*screenWidth), screenHeight - (int)(((double)DRAG_LINE1_DOT4.getYPos())/BIN_IMAGE_HEIGHT*screenHeight));
		line1Dot5 = new Position((int)(((double)DRAG_LINE1_DOT5.getXPos())/BIN_IMAGE_WIDTH*screenWidth), screenHeight - (int)(((double)DRAG_LINE1_DOT5.getYPos())/BIN_IMAGE_HEIGHT*screenHeight));
		line1Dot6 = new Position((int)(((double)DRAG_LINE1_DOT6.getXPos())/BIN_IMAGE_WIDTH*screenWidth), screenHeight - (int)(((double)DRAG_LINE1_DOT6.getYPos())/BIN_IMAGE_HEIGHT*screenHeight));
		line1Dot7 = new Position((int)(((double)DRAG_LINE1_DOT7.getXPos())/BIN_IMAGE_WIDTH*screenWidth), screenHeight - (int)(((double)DRAG_LINE1_DOT7.getYPos())/BIN_IMAGE_HEIGHT*screenHeight));
		line1Dot8 = new Position((int)(((double)DRAG_LINE1_DOT8.getXPos())/BIN_IMAGE_WIDTH*screenWidth), screenHeight - (int)(((double)DRAG_LINE1_DOT8.getYPos())/BIN_IMAGE_HEIGHT*screenHeight));
		line1Dot9 = new Position((int)(((double)DRAG_LINE1_DOT9.getXPos())/BIN_IMAGE_WIDTH*screenWidth), screenHeight - (int)(((double)DRAG_LINE1_DOT9.getYPos())/BIN_IMAGE_HEIGHT*screenHeight));
		line1Dot10 = new Position((int)(((double)DRAG_LINE1_DOT10.getXPos())/BIN_IMAGE_WIDTH*screenWidth), screenHeight - (int)(((double)DRAG_LINE1_DOT10.getYPos())/BIN_IMAGE_HEIGHT*screenHeight));
		
		line2Dot1 = new Position((int)(((double)DRAG_LINE2_DOT1.getXPos())/BIN_IMAGE_WIDTH*screenWidth), screenHeight - (int)(((double)DRAG_LINE2_DOT1.getYPos())/BIN_IMAGE_HEIGHT*screenHeight));
		line2Dot2 = new Position((int)(((double)DRAG_LINE2_DOT2.getXPos())/BIN_IMAGE_WIDTH*screenWidth), screenHeight - (int)(((double)DRAG_LINE2_DOT2.getYPos())/BIN_IMAGE_HEIGHT*screenHeight));
		line2Dot3 = new Position((int)(((double)DRAG_LINE2_DOT3.getXPos())/BIN_IMAGE_WIDTH*screenWidth), screenHeight - (int)(((double)DRAG_LINE2_DOT3.getYPos())/BIN_IMAGE_HEIGHT*screenHeight));
		line2Dot4 = new Position((int)(((double)DRAG_LINE2_DOT4.getXPos())/BIN_IMAGE_WIDTH*screenWidth), screenHeight - (int)(((double)DRAG_LINE2_DOT4.getYPos())/BIN_IMAGE_HEIGHT*screenHeight));
		// (screenHeight - ) because (0, 0) is in the top and I want it in the bottom
		
		
		dragLine1K1 = ((line1Dot2.getYPos()-line1Dot1.getYPos())/(line1Dot2.getXPos()-line1Dot1.getXPos()));
		dragLine1K2 = ((line1Dot3.getYPos()-line1Dot2.getYPos())/(line1Dot3.getXPos()-line1Dot2.getXPos()));
		dragLine1K3 = ((line1Dot4.getYPos()-line1Dot3.getYPos())/(line1Dot4.getXPos()-line1Dot3.getXPos()));
		dragLine1K4 = ((line1Dot5.getYPos()-line1Dot4.getYPos())/(line1Dot5.getXPos()-line1Dot4.getXPos()));
		dragLine1K5 = ((line1Dot6.getYPos()-line1Dot5.getYPos())/(line1Dot6.getXPos()-line1Dot5.getXPos()));
		dragLine1K6 = ((line1Dot7.getYPos()-line1Dot6.getYPos())/(line1Dot7.getXPos()-line1Dot6.getXPos()));
		dragLine1K7 = ((line1Dot8.getYPos()-line1Dot7.getYPos())/(line1Dot8.getXPos()-line1Dot7.getXPos()));
		dragLine1K8 = ((line1Dot9.getYPos()-line1Dot8.getYPos())/(line1Dot9.getXPos()-line1Dot8.getXPos()));
		dragLine1K9 = ((line1Dot10.getYPos()-line1Dot9.getYPos())/(line1Dot10.getXPos()-line1Dot9.getXPos()));
		dragLine1M1 = line1Dot1.getYPos() - line1Dot1.getXPos()*dragLine1K1;
		dragLine1M2 = line1Dot2.getYPos() - line1Dot2.getXPos()*dragLine1K2;
		dragLine1M3 = line1Dot3.getYPos() - line1Dot3.getXPos()*dragLine1K3;
		dragLine1M4 = line1Dot4.getYPos() - line1Dot4.getXPos()*dragLine1K4;
		dragLine1M5 = line1Dot5.getYPos() - line1Dot5.getXPos()*dragLine1K5;
		dragLine1M6 = line1Dot6.getYPos() - line1Dot6.getXPos()*dragLine1K6;
		dragLine1M7 = line1Dot7.getYPos() - line1Dot7.getXPos()*dragLine1K7;
		dragLine1M8 = line1Dot8.getYPos() - line1Dot8.getXPos()*dragLine1K8;
		dragLine1M9 = line1Dot9.getYPos() - line1Dot9.getXPos()*dragLine1K9;
		
		dragLine2K1 = ((line2Dot2.getYPos()-line2Dot1.getYPos())/(line2Dot2.getXPos()-line2Dot1.getXPos()));
		dragLine2K2 = ((line2Dot3.getYPos()-line2Dot2.getYPos())/(line2Dot3.getXPos()-line2Dot2.getXPos()));
		dragLine2K3 = ((line2Dot4.getYPos()-line2Dot3.getYPos())/(line2Dot4.getXPos()-line2Dot3.getXPos()));
		dragLine2M1 = line2Dot1.getYPos() - line2Dot1.getXPos()*dragLine2K1;
		dragLine2M2 = line2Dot2.getYPos() - line2Dot2.getXPos()*dragLine2K2;
		dragLine2M3 = line2Dot3.getYPos() - line2Dot3.getXPos()*dragLine2K3;
	}
		
	public boolean isOutOfBinView(Position pos){
		// Line 1
		if(pos.getXPos() < line1Dot2.getXPos()){
			if(pos.getYPos() > dragLine1K1 * pos.getXPos() + dragLine1M1){
    			return true;
    		}
		}
		else if(pos.getXPos() < line1Dot3.getXPos()){
			if(pos.getYPos() > dragLine1K2 * pos.getXPos() + dragLine1M2){
    			return true;
    		}
		}
		else if(pos.getXPos() < line1Dot4.getXPos()){
			if(pos.getYPos() > dragLine1K3 * pos.getXPos() + dragLine1M3){
    			return true;
    		}
		}
		else if(pos.getXPos() < line1Dot5.getXPos()){
			if(pos.getYPos() > dragLine1K4 * pos.getXPos() + dragLine1M4){
    			return true;
    		}
		}
		else if(pos.getXPos() < line1Dot6.getXPos()){
			if(pos.getYPos() > dragLine1K5 * pos.getXPos() + dragLine1M5){
    			return true;
    		}
		}
		else if(pos.getXPos() < line1Dot7.getXPos()){
			if(pos.getYPos() > dragLine1K6 * pos.getXPos() + dragLine1M6){
    			return true;
    		}
		}
		else if(pos.getXPos() < line1Dot8.getXPos()){
			if(pos.getYPos() > dragLine1K7 * pos.getXPos() + dragLine1M7){
    			return true;
    		}
		}
		else if(pos.getXPos() < line1Dot9.getXPos()){
			if(pos.getYPos() > dragLine1K8 * pos.getXPos() + dragLine1M8){
    			return true;
    		}
		}
		else{ // >= line1Dot9.getXPos()
			if(pos.getYPos() > dragLine1K9 * pos.getXPos() + dragLine1M9){
    			return true;
    		}
		}
		
		// Line 2
		if(pos.getXPos() < line2Dot2.getXPos()){
			if(pos.getYPos() < dragLine2K1 * pos.getXPos() + dragLine2M1){
    			return true;
    		}
		}
		else if(pos.getXPos() < line2Dot3.getXPos()){
			if(pos.getYPos() < dragLine2K2 * pos.getXPos() + dragLine2M2){
    			return true;
    		}
		}
		else{ // >= line2Dot3.getXPos()
			if(pos.getYPos() < dragLine2K3 * pos.getXPos() + dragLine2M3){
    			return true;
    		}
		}
		return false;
	}
	
	public void haikuReady(){
		endHaiku = HaikuGenerator.getRandomReadyHaiku(); //TODO
		row1.setText(endHaiku.getRow(1));
		row2.setText(endHaiku.getRow(2));
		row3.setText(endHaiku.getRow(3));
		haikuView.setVisibility(VISIBLE);
		textScroll.setVisibility(GONE);
		saveButton.setVisibility(VISIBLE);
		contactName.setVisibility(GONE);
		showHaiku = true;
	}
	
	public void undoLastChange(){
		for(int i = 0; i < lastChanged.size(); i++){
			lastChanged.get(i).undoLast();
		}
		for(int i = allWordsRemoved.size() - 1; i >= 0; i--){
			for(int a = 0; a < wordsRemovedLast.size(); a++){
				if(allWordsRemoved.get(i).equals(wordsRemovedLast.get(a).getWord())){
					allWordsRemoved.remove(i);
					break;
				}
			}
		}
		boolean found;
		for(int a = 0; a < wordsRemovedLast.size(); a++){
			found = false;
			for(int i = 0; i < wordsUsed.size(); i++){
				if(wordsUsed.get(i).getWord().equals(wordsRemovedLast.get(a).getWord())){
					found = true;
					wordsUsed.get(i).increase(wordsRemovedLast.get(a).getNumberOf());
					break;
				}
			}
			if(!found){
				wordsUsed.add(new WordAndNumber(wordsRemovedLast.get(a).getWord(), wordsRemovedLast.get(a).getNumberOf()));
			}
		}
		wordsRemovedLast.clear();
		HaikuGenerator.undo();
	}
	
	public void resetUndo(){
		lastChanged.clear();
		wordsRemovedLast.clear();
		HaikuGenerator.resetHaikusRemoved();
	}
	
	public void removeWord(String word){
		boolean found = false;
		for(int i = 0; i < wordsRemovedLast.size(); i++){
			if(wordsRemovedLast.get(i).getWord().equals(word)){
				found = true;
				wordsRemovedLast.get(i).increase();
				break;
			}
		}
		if(!found){
			wordsRemovedLast.add(new WordAndNumber(word));
		}
		found = false;
		for(int i = 0; i < allWordsRemoved.size(); i++){
			if(allWordsRemoved.get(i).equals(word)){
				found = true;
				return;
			}
		}
		if(!found){
			for(int i = 0; i < wordsUsed.size(); i++){
				if(wordsUsed.get(i).getWord().equals(word)){
					found = true;
					break;
				}
			}
			if(!found){
				allWordsRemoved.add(word);
			}
		}
	}
	
	public ArrayList<String> getAllWordsRemoved(){
		return allWordsRemoved;
	}
	
	public void haikuIsFinished(){
		if(!haikuFinished){
			haikuFinished = true;
		}
	}
	
	public void resetHaikuFinished(){
		haikuFinished = false;
	}
	
	public View getDraggedView(){
		return viewBeingDragged;
	}
	
	public void setDraggedView(View v){
		viewBeingDragged = v;
	}
	
	private int startX;
	private int startY;
	private double startTime;
	private View pressedDownOn;
	private boolean isDeleting = false;
	private boolean twoFingers = false; // if two fingers are used at anytime during the event, a click will not occur. Using this because I'm not sure if the isDeleting boolean is enough
	private float oldDistance = 0;
	
	private static final double WAIT_TIME = 100; //ms
	
	private boolean canUndo = false;
	private int eventsNeededForDelete = 5;
	private int eventCounter = 0;

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		int eventX = (int) event.getX();
		int eventY = (int) event.getY();
		if(event.getPointerCount() == 2){
    		isDeleting = true;
    		twoFingers = true;
    	}
		else{
			isDeleting = false;
			if(event.getAction() != MotionEvent.ACTION_UP && System.currentTimeMillis() - startTime < WAIT_TIME){
				return true;
			}
		}
		
//		Log.i("TAG", "" + event.getPointerCount());
		
		if(event.getAction() == MotionEvent.ACTION_DOWN){
			oldDistance = -1;
			startX = eventX;
			startY = eventY;
			startTime = System.currentTimeMillis();
			pressedDownOn = null;
			if(v != null && !(v instanceof BinView)){
				pressedDownOn = v;
			}
			return true;
		}
		else if(event.getAction() == MotionEvent.ACTION_UP){
			if(!twoFingers && MainView.CLICK_TIME > System.currentTimeMillis()-startTime){
				// Click!
				stateChanged = false;
				MainView.getInstance().closeBinView();
				twoFingers = false;
			}
			twoFingers = false;
			pressedDownOn = null;
		}
		else if (event.getAction() == MotionEvent.ACTION_MOVE) {
			if(isDeleting){
				Position finger1 = new Position(event.getX(0), event.getY(0));
				Position finger2 = new Position(event.getX(1), event.getY(1));
				float distance = (float) Math.sqrt((finger1.getXPos()-finger2.getXPos())*(finger1.getXPos()-finger2.getXPos())
						+ (finger1.getYPos()-finger2.getYPos())*(finger1.getYPos()-finger2.getYPos()));
				if(oldDistance < 0){
					// first distance check
					oldDistance = distance;
				}
				else if(Math.abs(distance-oldDistance) > deleteDistance && haikuFinished && !showHaiku){
					if(distance < oldDistance){
						// delete
						eventCounter++;
						progressBar.incProgress();
						if(eventCounter == eventsNeededForDelete){
							resetUndo();
							safeHaiku = HaikuGenerator.getRandomReadyHaiku();
							String temp;
							ArrayList<String> tempWords;
							for(int i = 0; i < smsView.size(); i++){
								if(randomGenerator.nextInt(100) < PERCENTAGE_TO_CHANGE){
									tempWords = smsView.get(i).setUsedWordsAtRandom();
									for(int t = tempWords.size() - 1; t >= 0; t--){
										temp = tempWords.get(t);
										if(safeHaiku.getWordsUsed().contains(temp)){
											for(int a = 0; a < wordsUsed.size(); a++){ // MUST be somewhere here, otherwise there is something wrong somewhere else in the code
												if(wordsUsed.get(a).getWord().equals(temp)){
													if(wordsUsed.get(a).getNumberOf() == 1){
														// can not remove this word
														smsView.get(i).undoIndex(t);
													}
													else{
														// can remove it
														wordsUsed.get(a).decrease();
														removeWord(temp);
														lastChanged.add(smsView.get(i));
													}
													break;
												}
											}
										}
										else{
											for(int a = wordsUsed.size() - 1; a >= 0; a--){
												if(wordsUsed.get(a).getWord().equals(temp)){
													removeWord(temp);
													lastChanged.add(smsView.get(i));
													if(wordsUsed.get(a).decrease()){
														allWordsRemoved.add(wordsUsed.get(a).getWord());
														wordsUsed.remove(a);
													}
													break;
												}
											}
										}
									}
								}
							}
							HaikuGenerator.checkIfHaikusAreValid(allWordsRemoved);
							canUndo = true;
							eventCounter = 0;
						}
					}
					else if(canUndo){
						// undo
						eventCounter--;
						progressBar.decProgress();
						if(eventCounter == -eventsNeededForDelete){
							canUndo = false;
							undoLastChange();
							eventCounter = 0;
						}
					}
					oldDistance = distance;
				}
        		return true;
			}
			// not deleting
			if(pressedDownOn == null){
				// If no particular view has been pressed down on, no view can be dragged -> just return
				return false;
			}
			double distance = Math.sqrt((eventX-startX)*(eventX-startX)+(eventY-startY)*(eventY-startY));
			if(pressedDownOn instanceof BinSMSView || pressedDownOn instanceof YearMonthView){ // Because of scrolling
				if(startX != -1 && Math.abs(startX - ((int) event.getX())) > MainView.MOVE_TO_DRAG_RANGE
						&& 45 > Math.acos(Math.abs(((int) event.getX()) - startX)
								/Math.sqrt((((int) event.getX()) - startX) * (((int) event.getX()) - startX)
								+ (((int) event.getY()) - startY) * (((int) event.getY()) - startY)))*180/Math.PI){
					pressedDownOn.setAlpha(MainView.OPACITY_USED);
					viewBeingDragged = pressedDownOn;
					pressedDownOn.startDrag(null, new DragShadowBuilder(pressedDownOn), null, 0);
					return true;
				}
			}
			else if(distance > MainView.MOVE_TO_DRAG_RANGE){
				// start drag
				pressedDownOn.setAlpha(MainView.OPACITY_USED);
				viewBeingDragged = pressedDownOn;
				pressedDownOn.startDrag(null, new DragShadowBuilder(pressedDownOn), null, 0);
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean onLongClick(View v) {
		return false;
	}

	@Override
	public void onClick(View v) {
		if(v.equals(saveButton)){
			//TODO
			Log.i("TAG", "save!");
			if(!HaikuActivity.getInstance().isSafeMode()){
				// safe mode is off!
				// DELETE
				ArrayList<SMS> smsToDelete = new ArrayList<SMS>();
				for(int i = 0; i < smsView.size(); i++){
					smsToDelete.add(smsView.get(i).getSMS());
				}
//				HaikuActivity.getInstance().deleteSMS(smsToDelete); //TODO
			}
//			HaikuActivity.getInstance().saveHaiku(endHaiku); //TODO
			reset();
		}
		else{
			// close bin view
			stateChanged = false;
			MainView.getInstance().closeBinView();
		}
	}

	@Override
	public boolean onDrag(View v, DragEvent event) {
		int action = event.getAction();
	    switch (action) {
	    	case DragEvent.ACTION_DRAG_STARTED:
	    		break;
	    	case DragEvent.ACTION_DRAG_ENTERED:
	    		break;
	    	case DragEvent.ACTION_DRAG_EXITED:
	    		break;
	    	case DragEvent.ACTION_DRAG_LOCATION:
	    		float x = event.getX();
	    		float y = screenHeight - event.getY();
	    		if(isOutOfBinView(new Position(x, y))){
	    			// in drop range
	    			inDropRange = true;
	    			if(!MainView.getInstance().isBinColor()){
	    				MainView.getInstance().setBinColor();
	    			}
	    		}
	    		else{
	    			// not in drop range
	    			inDropRange = false;
	    			if(MainView.getInstance().isBinColor()){
	    				MainView.getInstance().resetBinColor();
	    			}
	    		}
	    		break;
	    	case DragEvent.ACTION_DROP:
	    		if(inDropRange){
	    			if(viewBeingDragged instanceof BinSMSView){
	    				HaikuGenerator.removeSMS(((BinSMSView)viewBeingDragged).getSMS());
	    				removeSMS((BinSMSView)viewBeingDragged);
	    			}
	    			if(viewBeingDragged instanceof ThemeObjectView){
	    				HaikuGenerator.removeTheme(((ThemeObjectView) viewBeingDragged).getTheme());
	    				removeTheme((ThemeObjectView) viewBeingDragged);
	    			}
	    			if(viewBeingDragged instanceof YearMonthView){
	    				ArrayList<SMS> removedSMS = HaikuGenerator.removeDate(((YearMonthView) viewBeingDragged).getYearMonth());
	    				removeDate((YearMonthView) viewBeingDragged);
	    				removeSMSES(removedSMS);
	    			}
	    		}
	    		else{
	    			viewBeingDragged.setAlpha(MainView.OPACITY_FULL);
	    		}
	    		break;
	    	case DragEvent.ACTION_DRAG_ENDED:
	    		if(!inDropRange){
	    			viewBeingDragged.setAlpha(MainView.OPACITY_FULL);
	    		}
	    		inDropRange = false;
	    		viewBeingDragged = null;
	    		pressedDownOn = null;
	    		MainView.getInstance().resetBinColor();
	    		break;
	    	default:
	    		break;
	    }
	    return true;
	}

}