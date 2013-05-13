package haiku.top.view;

import java.util.ArrayList;

import haiku.top.HaikuActivity;
import haiku.top.R;
import haiku.top.model.HaikuGenerator;
import haiku.top.model.SMS;
import haiku.top.model.Theme;
import haiku.top.model.YearMonth;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.net.Uri;
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
	
	private HaikuProgressBar progressBar;
	
	private ScrollView dateScroll;
	private LinearLayout dateList;
	
	private LinearLayout themeListTotal;
	private LinearLayout themeListUpper;
	private LinearLayout themeListLower;
	
	private TextView contactName;
	private ScrollView textScroll;
	private LinearLayout textList;
	
	private ImageButton saveButton;
	
	private ArrayList<YearMonth> dates;
	private ArrayList<SMS> sms;
	private ArrayList<Theme> themes;
	
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
	
	public static final int DELETE_DISTANCE = 30;
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
	
	// the save button
	private static final Position SAVE_UPPER_LEFT = new Position(518, 1010);
	private static final int SAVE_WIDTH = 200;
	private static final int SAVE_HEIGHT = 200;
	
	// the date list
	private static final Position DATE_UPPER_LEFT = new Position(70, 870);
	private static final int DATE_WIDTH = 130;
	private static final int DATE_HEIGHT = 280;
	private static final int DATE_OBJECT_HEIGHT = 70; // The width is the same
	private static final int DATE_ROTATION = -5;
	private int dateWidth;
	private int dateObjectHeight;
	
	// The themes
//	private static final int THEME_WIDTH = 900; // wrap content istället
	private static final int THEME_HEIGHT = 100;
	private static final int THEME_ROTATION = -45;
//	private static final int THEME_OBJECT_WIDTH = 200; // wrap content istället
	private static final int THEME_OBJECT_HEIGHT = 40;
	private static final Position THEME_UPPER_LEFT = new Position(300, 270);
	private static final int THEME_LEFT_PADDING = 30;
	private static final int THEME_BOTTOM_LIST_OFFSET = 50;
//	private int themeObjectWidth;
	private int themeObjectHeight;
	private int themePadding;
	
	private View viewBeingDragged = null;
	
	private int smsCounter = 0; // used to know if the sms bin is empty (when an sms is removed it is just removed visualy)
	
	public BinView(Context context) {
		super(context);
		this.context = context;
		setBackgroundResource(R.drawable.haikubin_extended);
		setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		screenWidth = size.x;
		screenHeight = size.y;
		
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
//		saveButton.setImageResource(R.drawable.save_button);
		
		
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
//		int themeWidth = (int)(((double)THEME_WIDTH)/BIN_IMAGE_WIDTH*screenWidth);
		int themeHeight = (int)(((double)THEME_HEIGHT)/BIN_IMAGE_HEIGHT*screenHeight);
		
		int themeMarginLeft = (int)(((double)THEME_UPPER_LEFT.getXPos())/BIN_IMAGE_WIDTH*screenWidth);
		int themeMarginTop = (int)(((double)THEME_UPPER_LEFT.getYPos())/BIN_IMAGE_HEIGHT*screenHeight);
		
//		themeObjectWidth = (int)(((double)THEME_OBJECT_WIDTH)/BIN_IMAGE_WIDTH*screenWidth);
		themeObjectHeight = (int)(((double)THEME_OBJECT_HEIGHT)/BIN_IMAGE_WIDTH*screenHeight);
		themePadding = (int)(((double)THEME_LEFT_PADDING)/BIN_IMAGE_WIDTH*screenHeight);
		
		int lowerOffsetLeft = (int)(((double)THEME_BOTTOM_LIST_OFFSET)/BIN_IMAGE_WIDTH*screenWidth);
		int lowerOffsetTop = themeHeight - themeObjectHeight*2;
		
		themeListTotal= new LinearLayout(context);
		themeListTotal.setOrientation(LinearLayout.VERTICAL);
		LayoutParams themeListParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, themeHeight);
		themeListParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		themeListParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		themeListParams.setMargins(themeMarginLeft, themeMarginTop, 0, 0);
		themeListTotal.setLayoutParams(themeListParams);
		themeListTotal.setRotation(THEME_ROTATION);
		
		themeListUpper = new LinearLayout(context);
		themeListUpper.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, themeObjectHeight));
		themeListUpper.setOrientation(LinearLayout.HORIZONTAL);
		
		themeListLower = new LinearLayout(context);
		themeListLower.setPadding(lowerOffsetLeft, lowerOffsetTop, 0, 0);
		themeListLower.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, themeObjectHeight));
		themeListLower.setOrientation(LinearLayout.HORIZONTAL);
		
		themeListTotal.addView(themeListUpper);
		themeListTotal.addView(themeListLower);
		addView(themeListTotal);
		
		saveButton.setVisibility(GONE);
		setOnClickListener(this);
		setOnTouchListener(this);
		setOnDragListener(this);
	}
	
	public void update(){
		dates = HaikuGenerator.getDates();
		sms = HaikuGenerator.getAllAddedSMS();
		
		boolean onlyOneContact = true; // if there only is one contact in the bin its name will be shown
		
		dateList.removeAllViews();
		textList.removeAllViews();
		
		datesView.clear();
		smsView.clear();
		
		YearMonthView ymv;
		for(int i = 0; i < dates.size(); i++){
			ymv = new YearMonthView(context, dates.get(i), dateWidth, dateObjectHeight);
			datesView.add(ymv);
			dateList.addView(ymv);
			ymv.setOnTouchListener(this);
		}

		updateTheme();
		
		long threadID = -1;
		if(sms.isEmpty()){
			onlyOneContact = false;
		}
		else{
			threadID = sms.get(0).getContactID();
		}
		
		LayoutParams textParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		textParams.setMargins(0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics()), 0, 0);
		smsCounter = 0;
		BinSMSView tv;
		for(int i = 0; i < sms.size(); i++){
			tv = new BinSMSView(context, sms.get(i));
			
			tv.setPadding(0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics()), 0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics()));
//			tv.setLayoutParams(textParams);
			textList.addView(tv);
			if(sms.get(i).getContactID() != threadID){
				onlyOneContact = false;
			}
			tv.setOnTouchListener(this);
			smsCounter++;
		}
		if(onlyOneContact){
			Uri uri = Uri.parse(HaikuActivity.ALLBOXES);
			Cursor cursor = context.getContentResolver().query(uri, null, "thread_id='" + threadID + "'", null, null);
			if(cursor.moveToFirst()){
				String address = cursor.getString(cursor.getColumnIndexOrThrow("address"));
				contactName.setText(HaikuActivity.getContactName(context, address));
			}
			else{
				contactName.setText("Name not found");
			}
			contactName.setVisibility(VISIBLE);
		}
		else{
			contactName.setVisibility(GONE);
		}
	}
	
	/**
	 * This method doesn't change what smses in the view
	 */
	public void updateTheme(){
		themes = HaikuGenerator.getThemes();
		themeListUpper.removeAllViews();
		themeListLower.removeAllViews();
		themesView.clear();
		ThemeObjectView tob;
		for(int i = 0; i < themes.size(); i++){
			tob = new ThemeObjectView(context, themes.get(i), true);
			if(i < 3){
				// upper list
				themeListUpper.addView(tob);
			}
			else{
				// lower list
				themeListLower.addView(tob);
			}
			themesView.add(tob);
			tob.setOnTouchListener(this);
			if(i == 1 || i == 2 || i == 4 || i == 5){
				tob.setPadding(themePadding, 0, 0, 0);
			}
		}
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
		//TODO
		saveButton.setVisibility(VISIBLE);
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

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		int eventX = (int) event.getX();
		int eventY = (int) event.getY();
		if(event.getPointerCount() >= 2){
    		isDeleting = true;
    		twoFingers = true;
    	}
		else{
			isDeleting = false;
		}
		
		if(event.getAction() == MotionEvent.ACTION_DOWN){
			oldDistance = -1;
			startX = eventX;
			startY = eventY;
			startTime = System.currentTimeMillis();
			if(pressedDownOn == null && v != null && !(v instanceof BinView)){
				pressedDownOn = v;
			}
			return true;
		}
		else if(event.getAction() == MotionEvent.ACTION_UP){
			if(!twoFingers && MainView.CLICK_TIME > System.currentTimeMillis()-startTime){
				// Click!
				MainView.getInstance().closeBinView();
				twoFingers = false;
			}
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
				else if(Math.abs(distance-oldDistance) > deleteDistance){
					if(distance < oldDistance){
						// delete
						Log.i("TAG", "DELETE");
					}
					else{
						// undo
						Log.i("TAG", "UNDO");
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
			if(pressedDownOn instanceof BinSMSView){
				if(startX != -1 && Math.abs(startX - ((int) event.getX())) > MainView.MOVE_TO_DRAG_RANGE
						&& 45 > Math.acos(Math.abs(((int) event.getX()) - startX)
								/Math.sqrt((((int) event.getX()) - startX) * (((int) event.getX()) - startX)
								+ (((int) event.getY()) - startY) * (((int) event.getY()) - startY)))*180/Math.PI){
					pressedDownOn.setAlpha(MainView.OPACITY_USED);
					viewBeingDragged = pressedDownOn;//s
					pressedDownOn.startDrag(null, new DragShadowBuilder(pressedDownOn), null, 0);
					return true;
				}
			}
			else if(distance > MainView.MOVE_TO_DRAG_RANGE){
				// start drag
				Log.i("TAG", "BinSMSView drag");
				pressedDownOn.setAlpha(MainView.OPACITY_USED);
				viewBeingDragged = pressedDownOn;
				pressedDownOn.startDrag(null, new DragShadowBuilder(pressedDownOn), null, 0);
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean onLongClick(View arg0) {
		return false;
	}

	@Override
	public void onClick(View v) {
		MainView.getInstance().closeBinView();
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
	    				viewBeingDragged.setVisibility(GONE); // much faster than checking the whole view
	    				smsCounter--;
	    				if(smsCounter == 0){
	    					contactName.setVisibility(GONE);
	    				}
	    				//TODO check the generated haikus!!
	    			}
	    			if(viewBeingDragged instanceof ThemeObjectView){
	    				HaikuGenerator.removeTheme(((ThemeObjectView) viewBeingDragged).getTheme());
	    				updateTheme();
	    			}
	    			if(viewBeingDragged instanceof YearMonthView){
	    				HaikuGenerator.removeDate(((YearMonthView) viewBeingDragged).getYearMonth());
	    				update();
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
