package haiku.top.view.binview;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Semaphore;

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
import haiku.top.view.date.QuarterCircle;
import haiku.top.view.date.YearMonthView;
import haiku.top.view.main.ConversationObjectView;
import haiku.top.view.main.MainView;
import haiku.top.view.main.sms.SMSObject;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Space;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

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
	
	private static final String SAVE_TEXT = "Save";
	private static final String SHARE_TEXT = "Share";
	
	private BinButton saveButton;
	private BinButton shareButton;
	
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
	
	public static final int DELETE_DISTANCE = 8; //10 
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
	private static final int TEXT_WIDTH = 470;
	private static final int TEXT_HEIGHT = 750;
	
	// the haiku
	private static final Position HAIKU_UPPER_LEFT = new Position(200, 570);
	private static final int HAIKU_WIDTH = 530;
	private static final int HAIKU_HEIGHT = 250;
	private int haikuWidth; // used to calculate the text size of the rows
	
	// the save button
	private static final Position SAVE_UPPER_LEFT = new Position(448, 960);
	private static final int SAVE_WIDTH = 250;
	private static final int SAVE_HEIGHT = (int) (250.0 * (446.0/655.0)); // the actual image file is 655x446 px
	
	// the share button is just as big as the save button
	private static final Position SHARE_UPPER_LEFT = new Position(168, 960);
	private static final int SHARE_WIDTH = 250;
	private static final int SHARE_HEIGHT = (int) (250.0 * (454.0/641.0)); // the actual image file is 641x454 px
	
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
//	private ArrayList<BinSMSView> lastChanged = new ArrayList<BinSMSView>(); // used by undo
	private ArrayList<BinSMSRowWord> lastChanged = new ArrayList<BinSMSRowWord>();
	
	private Haiku safeHaiku;
	private boolean showHaiku = false;
	private Haiku endHaiku;
	private boolean stateChanged = false; // If the user closes the bin and then opens it without adding new SMS, there is no need to generate new SMS
	
	private ProgressDialog threadProgressBar;
	private ProgressDialog saveProgressBar;
	private static Semaphore endProgress = new Semaphore(1);
	
	/**
	 * If the user has initiated deletion (if the SMSes has been combined)
	 */
	private boolean deletionInProgress = false;
	
	/**
	 * The width of a row in PX
	 */
	private int rowWidth;
	
	private BinCombinedSMS binCombinedSMSView;
	private static final float BIN_START_OPACITY = 75; //in %
	private float numberOfWordsLeft;
	private float stopAt;
	
	private View addingObjectDuringDeletion = null;
	
	private int textWidth;
	private int textHeight;
	
	private int textMarginLeft;
	private int textMarginTop;
	
	private LinearLayout pointerView;
	
	private int contactNameHeight;
	private static final double SCROLL_HEIGHT = 0.1; // 10% of the text area's height
	
	private static final float ALPHA_PROGRESS_DEFAULT = (float)1.0;
	private static final float ALPHA_PROGRESS_NOTREADY = (float)0.3;
	
	public static final float BIN_SMS_TEXT_SIZE_SP = 17;
	
	public BinView(Context context) {
		super(context);
		this.context = context;
		binView = this;
		setBackgroundResource(R.drawable.haikubin_extended);
		setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		
		threadProgressBar = new ProgressDialog(context);
		threadProgressBar.setMessage("Loading...");
		threadProgressBar.setCancelable(false);
		threadProgressBar.setIndeterminate(true);
		
		saveProgressBar = new ProgressDialog(context);
		saveProgressBar.setMessage("Saving...");
		saveProgressBar.setCancelable(false);
		saveProgressBar.setIndeterminate(true);
		
		pointerView = new LinearLayout(context);
		pointerView.setBackgroundColor(Color.BLACK);
		
		screenWidth = HaikuActivity.getInstance().getWindowWidth();
		screenHeight = HaikuActivity.getInstance().getWindowHeight();
		
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
		textWidth = (int)(((double)TEXT_WIDTH)/BIN_IMAGE_WIDTH*screenWidth);
		textHeight = (int)(((double)TEXT_HEIGHT)/BIN_IMAGE_HEIGHT*screenHeight);
		
		rowWidth = textWidth;
		
		textMarginLeft = (int)(((double)TEXT_UPPER_LEFT.getXPos())/BIN_IMAGE_WIDTH*screenWidth);
		textMarginTop = (int)(((double)TEXT_UPPER_LEFT.getYPos())/BIN_IMAGE_HEIGHT*screenHeight);
		
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
		
		contactNameHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics());
		contactName.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, contactNameHeight));
		
		// HAIKU
		haikuWidth = (int)(((double)HAIKU_WIDTH)/BIN_IMAGE_WIDTH*screenWidth);
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
//		row1.setTextSize(13);
//		row2.setTextSize(13);
//		row3.setTextSize(13);
		row1.setTypeface(MainView.getInstance().getHaikuTypeface());
		row2.setTypeface(MainView.getInstance().getHaikuTypeface());
		row3.setTypeface(MainView.getInstance().getHaikuTypeface());
		
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
		
		saveButton = new BinButton(context, SAVE_TEXT, saveHeight, saveWidth);
		saveButton.setTypeface(MainView.getInstance().getSaveTypeface());
		int saveButtonMaxSize = saveButton.calculateMaxSize();
		LayoutParams saveParams = new RelativeLayout.LayoutParams(saveWidth, saveHeight);
		saveParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		saveParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		saveParams.setMargins(saveMarginLeft, saveMarginTop, 0, 0);
		saveButton.setLayoutParams(saveParams);
		addView(saveButton);
		saveButton.setBackgroundResource(R.drawable.save_button);
		saveButton.setVisibility(GONE);
		
		
		// SHARE BUTTON
		int shareWidth = (int)(((double)SHARE_WIDTH)/BIN_IMAGE_WIDTH*screenWidth);
		int shareHeight = (int)(((double)SHARE_HEIGHT)/BIN_IMAGE_HEIGHT*screenHeight);
		
		int shareMarginLeft = (int)(((double)SHARE_UPPER_LEFT.getXPos())/BIN_IMAGE_WIDTH*screenWidth);
		
		shareButton = new BinButton(context, SHARE_TEXT, shareHeight, shareWidth);
		shareButton.setTypeface(MainView.getInstance().getShareTypeface());
		int shareButtonMaxSize = shareButton.calculateMaxSize();
		LayoutParams shareParams = new RelativeLayout.LayoutParams(shareWidth, shareHeight);
		shareParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		shareParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		shareParams.setMargins(shareMarginLeft, saveMarginTop, 0, 0); // same margin top as save
		shareButton.setLayoutParams(shareParams);
		addView(shareButton);
		shareButton.setBackgroundResource(R.drawable.share_button);
		shareButton.setVisibility(GONE);
		
		int sharedMaxSize = Math.min(saveButtonMaxSize, shareButtonMaxSize);
		saveButton.setTextSize(sharedMaxSize);
		shareButton.setTextSize(sharedMaxSize);
		
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
		
		ArrayList<SMS> smsRefresh = HaikuGenerator.getAllSMS();
		ArrayList<YearMonth> datesRefresh = HaikuGenerator.getDates();
		ArrayList<Theme> themesRefresh = HaikuGenerator.getThemes();
		
		for(int i = 0; i < smsRefresh.size(); i++){
			addSMSBeforeDeletion(smsRefresh.get(i));
		}
		
		for(int i = 0; i < datesRefresh.size(); i++){
			addDate(datesRefresh.get(i));
		}
		
		for(int i = 0; i < themesRefresh.size(); i++){
			addTheme(themesRefresh.get(i));
		}
		updateBinOpacity();
	}
	
	public static BinView getInstance(){
		return binView;
	}
	
	public void setAddingObjectDuringDeletion(View addingObjectDuringDeletion){
		this.addingObjectDuringDeletion = addingObjectDuringDeletion;
		this.addingObjectDuringDeletion.startDrag(null, new DragShadowBuilder(this.addingObjectDuringDeletion), null, 0);
	}
	
	public void resetAddingObjectDuringDeletion(){
		this.addingObjectDuringDeletion = null;
		MainView.getInstance().getHaikuDragListener().resetDeletionAddingFlag();
	}
	
	/**
	 * Returns the width of a (any) row in the SMS view in PX.
	 * @return
	 */
	public int getWidthOfRow(){
		return rowWidth;
	}
	
	/**
	 * If the user has initiated deletion (if the SMSes has been combined)
	 * @return
	 */
	public boolean isDeleting(){
		return deletionInProgress;
	}
	
	public void startDeleting(){
		deletionInProgress = true;
		textList.removeAllViews();
//		textScroll.removeAllViews();
		binCombinedSMSView = new BinCombinedSMS(context);
		textList.addView(binCombinedSMSView);
		for(int i = 0; i < smsView.size(); i++){
			binCombinedSMSView.addSMS(smsView.get(i).getSMS());
		}
		stopAt = 25; //TODO
		updateNumberOfWordsLeft();
		binCombinedSMSView.init();
	}
	
	public void updateNumberOfWordsLeft(){
		numberOfWordsLeft = 0;
		for(int i = 0; i < binCombinedSMSView.getRows().size(); i++){
			numberOfWordsLeft += binCombinedSMSView.getRows().get(i).getWords().size();
		}
		progressBar.setProgress((int)(progressBar.getMaxProgress()*stopAt/numberOfWordsLeft));
	}
	
	public BinCombinedSMS getBinCombinedSMSView(){
		return binCombinedSMSView;
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
		ymv.setOnTouchListener(this);
	}
	
	public void removeDate(YearMonthView ymv){
		dateList.removeView(ymv);
		datesView.remove(ymv);
		return;
	}
	
	private boolean showingContactName = false;
	private long contactID;
	
	public void addSMSBeforeDeletion(SMS sms){
		//Before date change
//		BinSMSView tv = new BinSMSView(context, sms);
//		tv.setPadding(0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics()), 0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics()));
//		textList.addView(tv);
//		tv.setOnTouchListener(this);
//		smsView.add(tv);
//		if(smsView.size() == 1){
//			// Only sms! -> show contact
//			updateContactName();
//		}
//		else if(showingContactName && contactID != sms.getContactID()){
//			showingContactName = false;
//			contactName.setVisibility(GONE);
//		}
//		stateChanged = true;
		
		BinSMSView tv = new BinSMSView(context, sms);
		tv.setPadding(0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics()), 0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics()));
		boolean added = false;
		for(int i = 0; i < smsView.size(); i++){
			if(Long.parseLong(smsView.get(i).getSMS().getDate()) < Long.parseLong(sms.getDate())){
				smsView.add(i, tv);
				textList.addView(tv, i);
				added = true;
				break;
			}
		}
		if(!added){
			smsView.add(tv);
			textList.addView(tv);
		}
		tv.setOnTouchListener(this);
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
	
	/**
	 * Just checks the contact name
	 * @param sms
	 */
	public void addSMSDuringDeletion(SMS sms){
		if(showingContactName && contactID != sms.getContactID()){
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
			addSMSBeforeDeletion(smses.get(i));
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
		stopAt = 0;
		numberOfWordsLeft = 0;
		progressBar.resetProgress();
		haikuView.setVisibility(GONE);
		textScroll.setVisibility(VISIBLE);
		saveButton.setVisibility(GONE);
		shareButton.setVisibility(GONE);
		haikuFinished = false;
		showHaiku = false;
		deletionInProgress = false;
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
//		textScroll.removeAllViews();ssf
//		textScroll.addView(textList);
		HaikuGenerator.reset();
		updateContactName();
		MainView.getInstance().updateConversationsVisibility();
		MainView.getInstance().updateThemeView();
		MainView.getInstance().updateSMSView();
		updateThemeView();
	}
	
	public void allThreadsReady(){
		try {
			endProgress.acquire();
			if(threadProgressBar.isShowing()){
				threadProgressBar.dismiss();
				createHaikus();
			}
			endProgress.release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void onOpen(){
		progressBar.setAlpha(ALPHA_PROGRESS_NOTREADY);
		try {
			endProgress.acquire();
			if(HaikuGenerator.threadsAreRunning()){
				threadProgressBar.show();
			}
			else{
				createHaikus();
			}
			endProgress.release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void createHaikus(){
		if(!stateChanged){
			// the bin was closed and then opened, but the contents did not change -> nothing needs to be done
			progressBar.setAlpha(ALPHA_PROGRESS_DEFAULT);
			return;
		}
		if(haikuFinished){ // stateChanged is true here
			// new SMS has been added to the bin. If new haikus are not generated, they will not be used
			resetHaikuFinished();
			HaikuGenerator.updateWordsUsed();
		}
		if(progressBar.getProgress() == 0){
			// update the progressBar if it hasn't been moved
//			progressBar.setMaxProgress(50 + (int)(((double)smsView.size())*1.0)); //TODO
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
	
	public boolean isShowingContactName(){
		return showingContactName;
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
				if(deletionInProgress && binCombinedSMSView != null){
					binCombinedSMSView.updateStopWhenNRows();
				}
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
		ArrayList<String> addresses = HaikuActivity.getConversationNumbers(context, firstContactID);
		if(addresses.size() != 1){
			// shouldn't be shown since it is of a conversation with many participants.
			return;
		}
		// is not shown, but should be
		showingContactName = true;
		contactID = firstContactID;
		contactName.setText(HaikuActivity.getContactName(context, addresses.get(0)));
		contactName.setVisibility(VISIBLE);
		
		// OLD
//		showingContactName = true;
//		contactID = firstContactID;
//		Uri uri = Uri.parse(HaikuActivity.ALLBOXES);
//		Cursor cursor = context.getContentResolver().query(uri, null, "thread_id='" + firstContactID + "'", null, null);
//		if(cursor.moveToFirst()){
//			String address = cursor.getString(cursor.getColumnIndexOrThrow("address"));
//			contactName.setText(HaikuActivity.getContactName(context, address).get(0)); //TODO should the others also be shown? doesn't matter?
//		}
//		contactName.setVisibility(VISIBLE);
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
	
	public int getContactNameHeight(){
		return contactNameHeight;
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
		endHaiku = HaikuGenerator.getRandomReadyHaiku();
//		setHaikuFonts();
		row1.setText(endHaiku.getRow(1));
		row2.setText(endHaiku.getRow(2));
		row3.setText(endHaiku.getRow(3));
		int size = getMaxTextSizeForHaiku();
//		row1.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
//		row2.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
//		row3.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
		row1.setTextSize(size);
		row2.setTextSize(size);
		row3.setTextSize(size);
		haikuView.setVisibility(VISIBLE);
		textScroll.setVisibility(GONE);
		saveButton.setVisibility(VISIBLE);
		shareButton.setVisibility(VISIBLE);
		contactName.setVisibility(GONE);
		showHaiku = true;
		deletionInProgress = false;//TODO
	}
	
	private int getMaxTextSizeForHaiku(){
		int size1 = 0;
		int size2 = 0;
		int size3 = 0;
		String text = (String) row1.getText();
		Paint textPaint = row1.getPaint(); 
		Rect textRect;
	    do {
	    	size1++;
//	    	textPaint.setTextSize(size1);
	    	row1.setTextSize(size1);
	    	textPaint = row1.getPaint(); 
	        textRect = new Rect();
	        textPaint.getTextBounds(text, 0, text.length(), textRect);
	    } while(textPaint.measureText(text) < 9*haikuWidth/10);
//	    Log.i("TAG", "haikuWidth: " + haikuWidth);
//	    Log.i("TAG", "first width: " + textPaint.measureText(text));
//	    Log.i("TAG", "second width: " + textRect.width());
	    
	    text = (String) row2.getText();
		textPaint = row2.getPaint(); 
	    do {
	    	size2++;
//	    	textPaint.setTextSize(size2);
	    	row2.setTextSize(size2);
	    	textPaint = row2.getPaint(); 
	        textRect = new Rect();
	        textPaint.getTextBounds(text, 0, text.length(), textRect);
	    } while(textPaint.measureText(text) < 9*haikuWidth/10);
	    
	    text = (String) row3.getText();
		textPaint = row3.getPaint(); 
	    do {
	    	size3++;
//	    	textPaint.setTextSize(size3);
	    	row3.setTextSize(size3);
	    	textPaint = row3.getPaint(); 
	        textRect = new Rect();
	        textPaint.getTextBounds(text, 0, text.length(), textRect);
	    } while(textPaint.measureText(text) < 9*haikuWidth/10);
	    
		return Math.min(size1, Math.min(size2, size3));
	}
	
	public boolean isShowingHaiku(){
		return showHaiku;
	}
	
	public void undoLastChange(){
		numberOfWordsLeft += lastChanged.size();
		for(int i = 0; i < lastChanged.size(); i++){
//			lastChanged.get(i).undoLast();
			lastChanged.get(i).undo();
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
		lastChanged.clear();
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
			HaikuActivity.getInstance().runOnUiThread(new Runnable(){           
		        @Override
		        public void run(){
		        	progressBar.setAlpha(ALPHA_PROGRESS_DEFAULT);
		        }
		    });
		}
	}
	
	public void allHaikusAreGenerated(){
		if(!haikuFinished){
			Log.i("TAG", "No haiku found!");
			HaikuActivity.getInstance().runOnUiThread(new Runnable(){           
		        @Override
		        public void run(){
		        	Toast.makeText(getContext(), "A Haiku cannot be created with the current sms input (add more!)",Toast.LENGTH_LONG).show();
		        }
		    });
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
	
	public void updateBinOpacity(){
		setAlpha((BIN_START_OPACITY+(100-BIN_START_OPACITY)*progressBar.getProgress()/progressBar.getMaxProgress())/100);
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
	private int eventsNeededForDelete = 10;
	private int eventsNeededForUndo = 5;
	private int eventCounter = 0;
	
	private int noWordsRemovedCounter = 0;
	private static final int TRIES_UNTIL_AUTO_COMPLETION = 3;

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
							//TODO - just a deletion marker
							if(!deletionInProgress){
								startDeleting();
								eventCounter = 0;
								return true;
							}
							numberOfWordsLeft -= lastChanged.size();
//							Log.i("TAG", "(delete) lastChanged.size(): " + lastChanged.size());
							binCombinedSMSView.delete(lastChanged);
//							ArrayList<BinSMSRowWord> lastChangedTemp = new ArrayList<BinSMSRowWord>();
//							for(int i = 0; i < lastChanged.size(); i++){
//								lastChangedTemp.add(lastChanged.get(i));
//							}
							resetUndo();
							safeHaiku = HaikuGenerator.getRandomReadyHaiku();
//							String temp;
//							ArrayList<String> tempWords;
//							for(int i = 0; i < smsView.size(); i++){
//								if(randomGenerator.nextInt(100) < PERCENTAGE_TO_CHANGE){
//									tempWords = smsView.get(i).setUsedWordsAtRandom();
//									for(int t = tempWords.size() - 1; t >= 0; t--){
//										temp = tempWords.get(t);
//										if(safeHaiku.getWordsUsed().contains(temp)){
//											for(int a = 0; a < wordsUsed.size(); a++){ // MUST be somewhere here, otherwise there is something wrong somewhere else in the code
//												if(wordsUsed.get(a).getWord().equals(temp)){
//													if(wordsUsed.get(a).getNumberOf() == 1){
//														// can not remove this word
//														smsView.get(i).undoIndex(t);
//													}
//													else{
//														// can remove it
//														wordsUsed.get(a).decrease();
//														removeWord(temp);
//														lastChanged.add(smsView.get(i));
//													}
//													break;
//												}
//											}
//										}
//										else{
//											for(int a = wordsUsed.size() - 1; a >= 0; a--){
//												if(wordsUsed.get(a).getWord().equals(temp)){
//													removeWord(temp);
//													lastChanged.add(smsView.get(i));
//													if(wordsUsed.get(a).decrease()){
//														allWordsRemoved.add(wordsUsed.get(a).getWord());
//														wordsUsed.remove(a);
//													}
//													break;
//												}
//											}
//										}
//									}
//								}
//							}
							ArrayList<String> temps;
							ArrayList<BinSMSRowWord> tempWords;
							tempWords = binCombinedSMSView.setToBeDeleted();
							boolean removed;
							for(int t = tempWords.size() - 1; t >= 0; t--){
								temps = tempWords.get(t).getRealWordStrings();
								String temp;
								removed = false;
								if(temps.isEmpty()){
									// no checks has to be made
									// can remove it
//									Log.i("TAG", "remove " + tempWords.get(t).getWord());
									tempWords.get(t).setRemovedNext();
									if(!lastChanged.contains(tempWords.get(t))){
										lastChanged.add(tempWords.get(t));
									}
								}
								for(int i = 0; i < temps.size(); i++){
									if(removed){
										break;
									}
									temp = temps.get(i);
									if(safeHaiku.getWordsUsed().contains(temp)){
										for(int a = 0; a < wordsUsed.size(); a++){ // MUST be somewhere here, otherwise there is something wrong somewhere else in the code
											if(wordsUsed.get(a).getWord().equals(temp)){
												if(wordsUsed.get(a).getNumberOf() == 1){
													// can not remove this word
//													Log.i("TAG", "don't remove " + tempWords.get(t).getWord());
													tempWords.get(t).undo(); // The object might have been set to be removed earlier
													tempWords.remove(t);
													removed = true;
												}
												else{
													// can remove it
													wordsUsed.get(a).decrease();
													removeWord(temp);
													if(!lastChanged.contains(tempWords.get(t))){
														lastChanged.add(tempWords.get(t));
													}
													tempWords.get(t).setRemovedNext();
//													Log.i("TAG", "remove " + tempWords.get(t).getWord());
												}
												break;
											}
										}
									}
									else{
										// can remove it
//										Log.i("TAG", "remove " + tempWords.get(t).getWord());
										tempWords.get(t).setRemovedNext();
										if(!lastChanged.contains(tempWords.get(t))){
											lastChanged.add(tempWords.get(t));
										}
										for(int a = wordsUsed.size() - 1; a >= 0; a--){
											if(wordsUsed.get(a).getWord().equals(temp)){
												removeWord(temp);
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
//							binCombinedSMSView.delete(lastChangedTemp); // Causes bugs, needs to be deleted before
							HaikuGenerator.checkIfHaikusAreValid(allWordsRemoved);
							canUndo = true;
							eventCounter = 0;
							if(lastChanged.isEmpty()){
								noWordsRemovedCounter++;
								if(noWordsRemovedCounter > TRIES_UNTIL_AUTO_COMPLETION){
									progressBar.setProgress((int) progressBar.getMaxProgress()+1);
									return true;
								}
							}
							else{
								noWordsRemovedCounter = 0;
							}
						}
					}
					else if(canUndo){
						// undo
						eventCounter--;
						progressBar.decProgress();
						if(eventCounter == -eventsNeededForUndo){
							canUndo = false;
							undoLastChange();
							eventCounter = 0;
						}
					}
					oldDistance = distance;
//					Log.i("TAG", "numberOfWordsLeft: " + numberOfWordsLeft);
//					Log.i("TAG", "stopAt: " + stopAt);
//					Log.i("TAG", "progressBar.getMaxProgress(): " + progressBar.getMaxProgress());
//					Log.i("TAG", "progress: " + ((int)(progressBar.getMaxProgress()*stopAt/numberOfWordsLeft)));
					progressBar.setProgress((int)(progressBar.getMaxProgress()*stopAt/numberOfWordsLeft));
				}
        		return true;
			}
			// not deleting
			if(pressedDownOn == null){
				// If no particular view has been pressed down on, no view can be dragged -> just return
				return false;
			}
			double distance = Math.sqrt((eventX-startX)*(eventX-startX)+(eventY-startY)*(eventY-startY));
			if(!deletionInProgress && (pressedDownOn instanceof BinSMSView || pressedDownOn instanceof YearMonthView)){ // Because of scrolling
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
			else if(distance > MainView.MOVE_TO_DRAG_RANGE && pressedDownOn instanceof ThemeObjectView){ // Themes can still be removed even if deletion is in progress 
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
	
	public void save(){
		if(!saveProgressBar.isShowing()){
			saveProgressBar.show();
		}
		new Thread(){
			public void run(){
				if(!HaikuActivity.getInstance().isSafeMode()){
					// safe mode is off!
					// DELETE
					ArrayList<SMS> smsToDelete = new ArrayList<SMS>();
					for(int i = 0; i < smsView.size(); i++){
						smsToDelete.add(smsView.get(i).getSMS());
					}
					HaikuActivity.getInstance().deleteSMS(smsToDelete);
					
				}
				HaikuActivity.getInstance().addHaikuSMS(endHaiku);
				doneSaving();
			}
		}.start();
	}
	
	public synchronized void doneSaving(){
		HaikuActivity.getInstance().runOnUiThread(new Runnable() {
	        public void run() {
	        	MainView.getInstance().updateConversations();
	    		reset();
	    		if(saveProgressBar.isShowing()){
	    			saveProgressBar.dismiss();
	    		}
	        }
	    });
	}
	
	public void share(){
		HaikuActivity.getInstance().shareMessage(endHaiku.getHaikuPoem());
	}

	@Override
	public void onClick(View v) {
//		if(v.equals(saveButton)){
//			save();
//		}
//		else if(v.equals(shareButton)){
//			share();
//		}
//		else{
			// close bin view
			stateChanged = false;
			MainView.getInstance().closeBinView();
//		}
	}
	
	public void clickedButton(View v){
		if(v.equals(saveButton)){
			save();
		}
		else if(v.equals(shareButton)){
			share();
		}
	}
	
	private int rowIndex = 0;
	private int xIndex = 0;
	
	private ScrollThread scrollThread;
	public static final int SCROLL_TIME = 400; // in ms
	
	public void scrollUp(){
		if(textScroll.getScrollY() > 0){
			textScroll.smoothScrollBy(0, -binCombinedSMSView.getHeightOfRow());
			if(textScroll.getScrollY() < 0){
				textScroll.scrollTo(0, 0);
			}
			updateTextArea(lastX, lastY);
		}
	}
	
	public void scrollDown(){
		int maxScroll = binCombinedSMSView.getRows().size()*binCombinedSMSView.getHeightOfRow()-textHeight;
		if(textScroll.getScrollY() < maxScroll){
//			textScroll.scrollBy(0, binCombinedSMSView.getHeightOfRow());
			textScroll.smoothScrollBy(0, binCombinedSMSView.getHeightOfRow());
			if(textScroll.getScrollY() > maxScroll){
				textScroll.scrollTo(0, maxScroll);
			}
			updateTextArea(lastX, lastY);
		}
	}
	
	public void scrollDown(int rows){
		if(rows == 0){
			return;
		}
    	int maxScroll = binCombinedSMSView.getRows().size()*binCombinedSMSView.getHeightOfRow()-textHeight;
		if(textScroll.getScrollY() < maxScroll){
			textScroll.smoothScrollBy(0, binCombinedSMSView.getHeightOfRow()*rows);
			if(textScroll.getScrollY() > maxScroll){
				textScroll.scrollTo(0, maxScroll);
			}
		}
	}
	
	public void scrollDownD(int distance){
		if(distance == 0){
			return;
		}
    	int maxScroll = binCombinedSMSView.getRows().size()*binCombinedSMSView.getHeightOfRow()-textHeight;
		if(textScroll.getScrollY() < maxScroll){
			textScroll.smoothScrollBy(0, distance);
			if(textScroll.getScrollY() > maxScroll){
				textScroll.scrollTo(0, maxScroll);
			}
		}
	}
	
	public int getRowIndexAtTop(){
		return textScroll.getScrollY()/binCombinedSMSView.getHeightOfRow();
	}
	
	public int getCurrentScrollPos(){
		return textScroll.getScrollY();
	}
	
	public void instantScrollTo(int scrollY){
		textScroll.scrollTo(0, scrollY);
	}
	
	public int getHeightOfText(){
		return textHeight;
	}
	
	public void smoothScrollToTop(){
		int offset = textScroll.getScrollY();
		scrollDownD(offset);
	}
	
	public void smoothScrollTo(int scrollY){
		textScroll.smoothScrollTo(0, scrollY);
	}
	
	public void instantScrollDown(int rows){
		if(rows == 0){
			return;
		}
		int maxScroll = binCombinedSMSView.getRows().size()*binCombinedSMSView.getHeightOfRow()-textHeight;
		if(textScroll.getScrollY() < maxScroll){
			textScroll.scrollBy(0, binCombinedSMSView.getHeightOfRow()*rows);
			if(textScroll.getScrollY() > maxScroll){
				textScroll.scrollTo(0, maxScroll);
			}
		}
	}
	
	private float lastX;
	private float lastY;
	
	public void updateTextArea(float x, float y){
		if(ended){
			return;
		}
		lastX = x;
		lastY = y;
		// pointer is in the SMS view
		boolean below0 = false;
		if(showingContactName){
			rowIndex = (int) ((textScroll.getScrollY() + y - textMarginTop - contactNameHeight)/binCombinedSMSView.getHeightOfRow());
			if(rowIndex < 0){
				below0 = true;
				rowIndex = 0;
			}
		}
		else{
			rowIndex = (int) ((textScroll.getScrollY() + y - textMarginTop)/binCombinedSMSView.getHeightOfRow());
		}
		if(rowIndex > binCombinedSMSView.getRows().size()-1){
			rowIndex = binCombinedSMSView.getRows().size()-1;
			xIndex = (int) binCombinedSMSView.getRows().get(rowIndex).getCurrentOffset();
		}
		else{
			if(below0){
				xIndex = 0;
			}
			else{
				xIndex = binCombinedSMSView.availablePosition(rowIndex, (int) (x - textMarginLeft));
			}
		}
		removeView(pointerView);
		LayoutParams params = new RelativeLayout.LayoutParams((int) binCombinedSMSView.getLengthOfSpace(), binCombinedSMSView.getHeightOfRow());
		params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		if(showingContactName){
			params.setMargins(xIndex + textMarginLeft, (rowIndex*binCombinedSMSView.getHeightOfRow() + textMarginTop - textScroll.getScrollY() + contactNameHeight), 0, 0);
		}
		else{
			params.setMargins(xIndex + textMarginLeft, (rowIndex*binCombinedSMSView.getHeightOfRow() + textMarginTop - textScroll.getScrollY()), 0, 0);
		}
		addView(pointerView, params);
	}

	private boolean ended = false;
	
	@Override
	public boolean onDrag(View v, DragEvent event) {
		int action = event.getAction();
	    switch (action) {
	    	case DragEvent.ACTION_DRAG_STARTED:
	    		ended = false;
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
	    		if(addingObjectDuringDeletion != null){
	    			if(x > textMarginLeft && x < textMarginLeft + textWidth && event.getY() > textMarginTop && event.getY() < textMarginTop + textHeight){
	    				updateTextArea(x, event.getY());
	    				//TODO
	    				//scroll
	    				if((!showingContactName && event.getY() < textMarginTop + textHeight*SCROLL_HEIGHT)
	    						|| (showingContactName && event.getY() < (textMarginTop+contactNameHeight) + (textHeight-contactNameHeight)*SCROLL_HEIGHT)){
	    					if(scrollThread == null){
	    						// Start scroll up
		    					scrollThread = new ScrollThread(true);
		    					scrollThread.start();
	    					}
	    				}
	    				else if(event.getY() > textMarginTop + textHeight - textHeight*SCROLL_HEIGHT){
	    					if(scrollThread == null){
	    						// Start scroll down
	    						scrollThread = new ScrollThread(false);
		    					scrollThread.start();
	    					}
	    				}
	    				else{
	    					// stop any current scroll
	    					if(scrollThread != null){
	    						scrollThread.stopScrolling();
	    						scrollThread = null;
	    					}
	    				}
	    			}
	    			else{
	    				// stop any current scroll
	    				if(scrollThread != null){
    						scrollThread.stopScrolling();
    						scrollThread = null;
    					}
	    			}
	    		}
	    		break;
	    	case DragEvent.ACTION_DROP:
	    		if(addingObjectDuringDeletion != null){
	    			if(!inDropRange){ // drop range is outside of the bin view. To add an object it must be within it.
	    				ArrayList<SMS> smses = new ArrayList<SMS>();
	    				if(addingObjectDuringDeletion instanceof ConversationObjectView){
	    					smses = HaikuGenerator.addThread(((ConversationObjectView)addingObjectDuringDeletion).getThreadID());
	    				}
	    				if(addingObjectDuringDeletion instanceof SMSObject){
	    					smses.add(((SMSObject)addingObjectDuringDeletion).getSMS());
	    					HaikuGenerator.calculateSMS(((SMSObject)addingObjectDuringDeletion).getSMS());
	    				}
	    				if(addingObjectDuringDeletion instanceof QuarterCircle){
	    					if(((QuarterCircle) addingObjectDuringDeletion).isYearView()){
	    	    				if(MainView.getInstance().isShowingSMS()){
	    	    					smses = HaikuGenerator.addYearFromSMSes(MainView.getInstance().getSelectedYear(), MainView.getInstance().getSelectedConvoThreadID());
	    	    				}
	    	    				else{
	    	    					smses = HaikuGenerator.addYear(MainView.getInstance().getSelectedYear());
	    	    				}
	    	    			}
	    	    			else{
	    	    				if(MainView.getInstance().isShowingSMS()){
	    	    					smses = HaikuGenerator.addDateFromSMSes(new YearMonth(MainView.getInstance().getSelectedYear(), ((QuarterCircle)addingObjectDuringDeletion).getMonth()), MainView.getInstance().getSelectedConvoThreadID());
	    	    				}
	    	    				else{
	    	    					smses = HaikuGenerator.addDate(new YearMonth(MainView.getInstance().getSelectedYear(), ((QuarterCircle)addingObjectDuringDeletion).getMonth()));
	    	    				}
	    		    		}
	    				}
	    				binCombinedSMSView.addSMSesAtPosition(smses, rowIndex, xIndex);
	    				onOpen();
	    			}
	    			else{
	    				addingObjectDuringDeletion.setAlpha(MainView.OPACITY_DEFAULT);
	    			}
	    			break;
	    		}
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
	    		ended = true;
	    		if(addingObjectDuringDeletion != null){
	    			// stop any current scroll
    				if(scrollThread != null){
						scrollThread.stopScrolling();
						scrollThread = null;
					}
	    			resetAddingObjectDuringDeletion();
		    		removeView(pointerView);
	    		}
	    		else if(!inDropRange){
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

//	private void setHaikuFonts() {
//        Typeface charle = Typeface.createFromAsset(context.getAssets(), "fonts/CharlemagneStd-Bold.otf");
//        Typeface basker = Typeface.createFromAsset(context.getAssets(), "fonts/LibreBaskerville-Regular.ttf");
//        Typeface djgross = Typeface.createFromAsset(context.getAssets(), "fonts/DJGROSS.ttf");
//        
//        double random = Math.random();     
//        if (random < 0.16) { //random set fonts
//            row1.setTypeface(djgross);
//            row2.setTypeface(basker);
//            row3.setTypeface(charle);
//        }   	
//        else if (random > 0.16 && random < 0.32) {
//            row1.setTypeface(charle);
//            row2.setTypeface(basker);
//            row3.setTypeface(djgross);
//        }
//        else if (random > 0.32 && random < 0.48) {
//            row1.setTypeface(basker);
//            row2.setTypeface(djgross);
//            row3.setTypeface(charle);
//        }
//        else if (random > 0.48 && random < 0.64) {
//            row1.setTypeface(charle);
//            row2.setTypeface(djgross);
//            row3.setTypeface(basker);
//        }
//        else if (random > 0.64 && random < 0.82) {
//            row1.setTypeface(djgross);
//            row2.setTypeface(charle);
//            row3.setTypeface(basker);
//        }
//        else {
//            row1.setTypeface(basker);
//            row2.setTypeface(charle);
//            row3.setTypeface(djgross);
//        }
//	}
	
}
