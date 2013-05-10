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
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
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

public class BinView extends RelativeLayout implements OnClickListener, OnLongClickListener, OnTouchListener{
	private Context context;
	
	private HaikuProgressBar progressBar;
	
	private ScrollView dateScroll;
	private LinearLayout dateList;
	
	private HorizontalScrollView themeScroll;
	private LinearLayout themeList;
	
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
	
	private static int screenWidth;
	private static int screenHeight;
	
	// These positions are compared to the image width and height
	
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
	
	// the theme list
	private static final Position THEME_UPPER_LEFT = new Position(350, 180);
	private static final int THEME_WIDTH = 200;
//	private static final int THEME_HEIGHT = 100;
	private static final int THEME_ROTATION = 0;
	
	private View viewBeingDragged = null;
	
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
		
//		LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//		layoutInflater.inflate(R.layout.bin_view,this);
		
//		dateScroll = (ScrollView)findViewById(R.id.dateScroll);
//		dateList = (LinearLayout)findViewById(R.id.listofdates);
//		
//		themeScroll = (ScrollView)findViewById(R.id.themeScroll);
//		themeList = (LinearLayout)findViewById(R.id.listofthemes);
//		
//		contactName = (TextView)findViewById(R.id.bincontactname);
//		textScroll = (ScrollView)findViewById(R.id.textScroll);
//		textList = (LinearLayout)findViewById(R.id.listoftext);
//		
//		saveButton = (ImageButton)findViewById(R.id.saveButton);

//      int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics());
//      int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, getResources().getDisplayMetrics());
//		
//		addView(progressBar);
//		LayoutParams params1 = new RelativeLayout.LayoutParams(width, height);
//		params1.addRule(RelativeLayout.ALIGN_PARENT_TOP);
//		params1.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
//		
//		int marginLeft= (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, getResources().getDisplayMetrics());
//      int marginTop = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, getResources().getDisplayMetrics());
//		
//		params1.setMargins(marginLeft, marginTop, 0, 0);
//		progressBar.setLayoutParams(params1);
		
		
		
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
		
//		Log.i("TAG", "Slider: (" + sliderMarginLeft + ", " + sliderMarginTop + "), W: " + sliderWidth + ", H: " + sliderHeight);
		
		
		
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
		int themeWidth = (int)(((double)THEME_WIDTH)/BIN_IMAGE_WIDTH*screenWidth);
//		int themeHeight = (int)(((double)THEME_HEIGHT)/BIN_IMAGE_HEIGHT*screenHeight);
		
		int themeMarginLeft = (int)(((double)THEME_UPPER_LEFT.getXPos())/BIN_IMAGE_WIDTH*screenWidth);
		int themeMarginTop = (int)(((double)THEME_UPPER_LEFT.getYPos())/BIN_IMAGE_HEIGHT*screenHeight);
		
		themeScroll = new HorizontalScrollView(context);
		themeList = new LinearLayout(context);
		themeList.setOrientation(LinearLayout.HORIZONTAL);
		themeList.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		LayoutParams themeScrollParams = new RelativeLayout.LayoutParams(themeWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
		themeScrollParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		themeScrollParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		themeScrollParams.setMargins(themeMarginLeft, themeMarginTop, 0, 0);
		themeScroll.setLayoutParams(themeScrollParams);
		themeScroll.setRotation(THEME_ROTATION);
		themeScroll.addView(themeList);
		addView(themeScroll);
		
		
		saveButton.setVisibility(GONE);
		setOnClickListener(this);
		setOnTouchListener(this);
	}
	
	public void update(){
		dates = HaikuGenerator.getDates();
		sms = HaikuGenerator.getAllAddedSMS();
		themes = HaikuGenerator.getThemes();
		
		boolean onlyOneContact = true; // if there only is one contact in the bin its name will be shown
		
		dateList.removeAllViews();
		textList.removeAllViews();
		themeList.removeAllViews();
		
		datesView.clear();
		themesView.clear();
		smsView.clear();
		
		YearMonthView ymv;
		for(int i = 0; i < dates.size(); i++){
			ymv = new YearMonthView(context, dates.get(i), dateWidth, dateObjectHeight);
			datesView.add(ymv);
			dateList.addView(ymv);
		}
		
		ThemeObjectView tob;
		for(int i = 0; i < themes.size(); i++){
			tob = new ThemeObjectView(context, themes.get(i), true);
			themesView.add(tob);
			themeList.addView(tob);
		}
		
		long threadID = -1;
		if(sms.isEmpty()){
			onlyOneContact = false;
		}
		else{
			threadID = sms.get(0).getContactID();
		}
		
		LayoutParams textParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		textParams.setMargins(0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics()), 0, 0);
		
		BinSMSView tv;
		for(int i = 0; i < sms.size(); i++){
			tv = new BinSMSView(context, sms.get(i));
			
			tv.setPadding(0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics()), 0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics()));
//			tv.setLayoutParams(textParams);
			textList.addView(tv);
			if(sms.get(i).getContactID() != threadID){
				onlyOneContact = false;
			}
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
	
	int startX;
	int startY;

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if(event.getAction() == MotionEvent.ACTION_DOWN){
			startX = (int) event.getX();
			startY = (int) event.getY();
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

}
