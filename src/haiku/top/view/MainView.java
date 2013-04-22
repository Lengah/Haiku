package haiku.top.view;

import java.util.ArrayList;

import android.R.xml;
import android.content.Context;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;

import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.View.OnDragListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import haiku.top.HaikuActivity;
import haiku.top.R;
import haiku.top.model.HaikuGenerator;
import haiku.top.model.SMS;
import haiku.top.model.Theme;
import haiku.top.model.sql.DatabaseHandler;
import haiku.top.view.adapters.ContactListAdapter;

public class MainView extends RelativeLayout implements OnClickListener, OnLongClickListener, OnTouchListener{
	private static MainView mv;
	private Context context;
	public static final int ANIMATION_TIME_BIN = 300;
	public static final int ANIMATION_TIME_DATE = 300;
	public static final float OPACITY_USED = (float) 0.3;
	public static final float OPACITY_DEFAULT = (float) 1; // 0.8 laggar
	public static final float OPACITY_FULL = 1;
	public static final int VIEW_SHOWN_SMS = 1;
	public static final int VIEW_SHOWN_BIN = 2;
	public static final int VIEW_SHOWN_DATE = 3;
	
	public static final double LONG_CLICK_TIME = 1000; //In ms
	public static final double MOVE_TO_DRAG_RANGE = 15;
	
//	private Button themeButton;
	private ScrollView themeScroll;
	private LinearLayout themeList;
	
	private ScrollView contactScroll;
	private LinearLayout contactList;
	
	private ScrollView smsScroll;
	private LinearLayout smsList;
	
	private ImageView haikuBinViewSmall;
	private ImageView haikuBinViewExtended;
	
	private View viewBeingDragged = null;
	
	private ArrayList<ConversationObjectView> conversations = new ArrayList<ConversationObjectView>();
	private ArrayList<SMSObjectView> smsObjects = new ArrayList<SMSObjectView>();
	private ArrayList<ThemeObjectView> themeObjects = new ArrayList<ThemeObjectView>();
//	private int threadIDInUse; // The conversation the user is currently looking at
	
	/**
	 * 0 = no view (default main view), 1 = sms view, 2 = bin view, 3 = date open
	 */
	private ArrayList<Integer> viewsOpenInOrder = new ArrayList<Integer>();
	
//	private QuarterCircle dateView; //TODO
	boolean dateViewClosed = true;
	
	private DateView dateView2;
	
	public MainView(Context context) {
		super(context);
		this.context = context;
		mv = this;
		
		LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutInflater.inflate(R.layout.mainview,this);
		
		contactScroll = (ScrollView)findViewById(R.id.scrollofcontacts);
		contactList = (LinearLayout)findViewById(R.id.listofcontacts);
		
		smsScroll = (ScrollView)findViewById(R.id.scrollofsms);
		smsList = (LinearLayout)findViewById(R.id.listofsms);
		
//		themeButton = (Button)findViewById(R.id.themebutton);
		themeScroll = (ScrollView)findViewById(R.id.themeview);
		haikuBinViewSmall = (ImageView)findViewById(R.id.binview);
		haikuBinViewExtended = (ImageView)findViewById(R.id.binviewextended);
		
		haikuBinViewSmall.setOnDragListener(new HaikuBinDragListener(haikuBinViewSmall));
		haikuBinViewSmall.bringToFront();
		
		haikuBinViewSmall.setOnClickListener(this);
		haikuBinViewExtended.setOnClickListener(this);
		haikuBinViewExtended.setVisibility(View.GONE);
		
//		contactList.setAdapter(new ContactListAdapter(context, HaikuActivity.getThreads(context), true));
		
		Cursor cursor = HaikuActivity.getThreads(context);
		if (cursor.moveToFirst()) {
			do{
				conversations.add(new ConversationObjectView(context, cursor.getInt(cursor.getColumnIndexOrThrow("thread_id")), cursor.getString(cursor.getColumnIndexOrThrow("address"))));
				conversations.get(conversations.size()-1).setOnLongClickListener(this);
				conversations.get(conversations.size()-1).setOnClickListener(this);
				conversations.get(conversations.size()-1).setOnTouchListener(this);
			}
			while(cursor.moveToNext());
		}
		for(int i = 0; i < conversations.size(); i++){
			contactList.addView(conversations.get(i));
//			conversations.get(i).setAlpha(OPACITY_DEFAULT); // Lags
		}
		ArrayList<Theme> themes;
//		themes = DatabaseHandler.getAllThemes();
		themes = new ArrayList<Theme>();
		themes.add(Theme.happy);
		themes.add(Theme.sad);
		themes.add(Theme.summer);
		themes.add(Theme.time);
		themes.add(Theme.time);
		
		themeList = new LinearLayout(context);
		themeList.setOrientation(LinearLayout.VERTICAL);
		themeList.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		themeScroll.addView(themeList);
		ThemeObjectView themeObject;
//		int height = 0;
		for(int i = 0; i < themes.size(); i++){
			themeObject = new ThemeObjectView(context, themes.get(i));
			themeObjects.add(themeObject);
			themeList.addView(themeObject);
			themeObject.setOnLongClickListener(this);
			if(HaikuGenerator.getThemes().contains(themes.get(i))){
				themeObject.setAlpha(OPACITY_USED);
			}
			else{
				themeObject.setAlpha(OPACITY_DEFAULT);
			}
//			themeObject.setOnClickListener(this);
			themeObject.setOnTouchListener(this); // overrides the scroll function!
//			height += themeObject.getHeightOfView();
		}
		smsScroll.setVisibility(GONE);
//		themeButton.bringToFront();
		themeScroll.bringToFront();
		themeScroll.setAlpha(OPACITY_DEFAULT);
		updateThemeView();
//		themeView.setVisibility(View.GONE);
//		themeButton.setOnClickListener(this);
		
//		dateView = (QuarterCircle)findViewById(R.id.dateview); //TODO
//		dateView.setOnClickListener(this);
//		dateView.bringToFront();
//		
		
		dateView2 = new DateView(context);
		addView(dateView2);
		LayoutParams params1 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		params1.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		dateView2.setLayoutParams(params1);
		dateView2.bringToFront();
		

		haikuBinViewExtended.bringToFront();
	}
	
	public int getThemeScrollViewWidth(){
		return themeScroll.getWidth();
	}
	
	public static MainView getInstance(){
		return mv;
	}
	
	/**
	 * if this method returns en empty list, then the program should exit when the user
	 * presses the back button. Otherwise it should close the last view in the arraylist (when the user presses
	 * the back button). The view Integers are public constants in the MainView class. VIEW_SHOWN_...
	 * @return
	 */
	public ArrayList<Integer> getViewsShown(){
		return viewsOpenInOrder;
	}
	
	public View getDraggedView(){
		return viewBeingDragged;
	}
	
	public void setDraggedView(View v){
		viewBeingDragged = v;
	}
	
	public void updateConversations(){
		boolean isInGenerator;
		for(int i = 0; i < conversations.size(); i++){
			isInGenerator = false;
			for(int a = 0; a < HaikuGenerator.getThreadIDs().size(); a++){
				if(conversations.get(i).getThreadID() == HaikuGenerator.getThreadIDs().get(a)){
//					conversations.get(i).setVisibility(GONE);
					conversations.get(i).setAlpha(OPACITY_USED);
					isInGenerator = true;
				}
			}
			if(!isInGenerator){
//				conversations.get(i).setVisibility(VISIBLE);
				conversations.get(i).setAlpha(OPACITY_DEFAULT); // Gör så att det laggar!
//				conversations.get(i).setAlpha(OPACITY_FULL);
			}
		}
	}
	
	public void setSMSView(int threadID){
		viewsOpenInOrder.add(VIEW_SHOWN_SMS);
		contactScroll.setVisibility(GONE);
		smsScroll.setVisibility(VISIBLE);
		Cursor cursor = HaikuActivity.getThread(context, threadID);
		Log.i("TAG", "Count: " + cursor.getCount());
		if (cursor.moveToFirst()) {
			do{
				smsObjects.add(new SMSObjectView(context, cursor.getString(cursor.getColumnIndexOrThrow("type")),new SMS(cursor.getInt(cursor.getColumnIndexOrThrow("_id")), cursor.getString(cursor.getColumnIndexOrThrow("body")), cursor.getString(cursor.getColumnIndexOrThrow("date")), threadID)));
				smsObjects.get(smsObjects.size()-1).setOnLongClickListener(this);
				smsObjects.get(smsObjects.size()-1).setOnTouchListener(this);
			}
			while(cursor.moveToNext());
		}
		for(int i = 0; i < smsObjects.size(); i++){
			smsList.addView(smsObjects.get(i));
		}
		updateSMSView();
	}
	
	/**
	 * Just changes the visibility of the smsObjects
	 */
	public void updateSMSView(){
		boolean empty = true;
		for(int i = 0; i < smsObjects.size(); i++){
			if(HaikuGenerator.getAllAddedSMS().contains(smsObjects.get(i).getSMS())){
//				smsObjects.get(i).setVisibility(GONE);
				smsObjects.get(i).setAlpha(OPACITY_USED);
			}
			else{
//				smsObjects.get(i).setVisibility(VISIBLE);
				smsObjects.get(i).setAlpha(OPACITY_DEFAULT);
				empty = false;
			}
		}
		if(empty && !smsObjects.isEmpty()){
			HaikuGenerator.addThread((int)smsObjects.get(0).getSMS().getContactID());
		}
	}
	
	public void removeViewElement(int viewType){
		for(int i = viewsOpenInOrder.size()-1; i >= 0; i--){
			if(viewsOpenInOrder.get(i) == viewType){
				viewsOpenInOrder.remove(i);
			}
		}
	}
	
	public void addViewElement(int viewType){
		viewsOpenInOrder.add(viewType);
	}
	
	public void closeSMSView(){
		updateConversations();
		contactScroll.setVisibility(VISIBLE);
		smsScroll.setVisibility(GONE);
		smsObjects.clear();
		smsList.removeAllViews();
		removeViewElement(VIEW_SHOWN_SMS);
	}
	
	public void updateThemeView(){
		for(int i = 0; i < themeObjects.size(); i++){
			if(HaikuGenerator.getThemes().contains(themeObjects.get(i).getTheme())){
				themeObjects.get(i).setAlpha(OPACITY_USED);
			}
			else{
				themeObjects.get(i).setAlpha(OPACITY_DEFAULT);
			}
		}
	}
	
	@Override
	public void onClick(View v) {
//		if(v.equals(themeButton)){
//			openThemeView();
//		}
		/*else*/ if(v instanceof ConversationObjectView){
			setSMSView(((ConversationObjectView)v).getThreadID());
		}
//		else{
//			// themeObject
//			closeThemeView();
//		}
//		if(v.equals(dateView)){ //TODO
//			if(dateViewClosed){
//				openDateView();
//			}
//			else{
//				closeDateView();
//			}
//		}
		if(v.equals(haikuBinViewSmall)){
			openBinView();
		}
		if(v.equals(haikuBinViewExtended)){
			closeBinView();
		}
	}

	@Override
	public boolean onLongClick(View v) {
		if(v.getAlpha() == OPACITY_USED){
			// The view is already in the bin
			return false;
		}
		v.setAlpha(OPACITY_USED);
		viewBeingDragged = v;
		v.startDrag(null, new DragShadowBuilder(v), null, 0);
		return false;
	}

	int startX;
	int startY;
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if(event.getAction() == MotionEvent.ACTION_DOWN){
			startX = (int) event.getX();
			startY = (int) event.getY();
		}
		// overrides the scroll function!
		if(event.getAction() == MotionEvent.ACTION_MOVE){
			if(startX != -1 && Math.abs(startX - ((int) event.getX())) > MOVE_TO_DRAG_RANGE
					&& 45 > Math.acos(Math.abs(((int) event.getX()) - startX)
							/Math.sqrt((((int) event.getX()) - startX) * (((int) event.getX()) - startX)
							+ (((int) event.getY()) - startY) * (((int) event.getY()) - startY)))){
				v.setAlpha(OPACITY_USED);
				viewBeingDragged = v;
				v.startDrag(null, new DragShadowBuilder(v), null, 0);
				return true;
			}
		}
		return false;
	}
	
	public void openBinView(){
		viewsOpenInOrder.add(VIEW_SHOWN_BIN);
		haikuBinViewSmall.setVisibility(GONE);
		haikuBinViewExtended.setVisibility(VISIBLE);
		if(!dateViewClosed){
//			closeDateView(); //TODO
		}
	}
	
	public void closeBinView(){
		removeViewElement(VIEW_SHOWN_BIN);
		haikuBinViewSmall.setVisibility(VISIBLE);
		haikuBinViewExtended.setVisibility(GONE);
	}
	
	//TODO
//	public void openDateView(){
//		dateViewClosed = !dateViewClosed;
//		viewsOpenInOrder.add(VIEW_SHOWN_DATE);
//		dateView.setText("2013");
//		Animation a = dateView.changeSizeTo(2, ANIMATION_TIME_DATE);
//        dateView.startAnimation(a);
//        a.setAnimationListener(new AnimationListener() {
//			
//			@Override
//			public void onAnimationStart(Animation animation) {}
//			
//			@Override
//			public void onAnimationRepeat(Animation animation) {}
//			
//			@Override
//			public void onAnimationEnd(Animation animation) {
//				
//			}
//		});
//	}
//	
////	TODO
//	public void closeDateView(){
//		dateViewClosed = !dateViewClosed;
//		removeViewElement(VIEW_SHOWN_DATE);
//		Animation a = dateView.changeSizeTo(0.5, ANIMATION_TIME_DATE);
//        dateView.startAnimation(a);
//        a.setAnimationListener(new AnimationListener() {
//			
//			@Override
//			public void onAnimationStart(Animation animation) {}
//			
//			@Override
//			public void onAnimationRepeat(Animation animation) {}
//			
//			@Override
//			public void onAnimationEnd(Animation animation) {
//				dateView.setText("Time");
//			}
//		});
//	}
}
