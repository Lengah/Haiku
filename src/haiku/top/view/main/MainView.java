package haiku.top.view.main;

import java.util.ArrayList;

import android.R.xml;
import android.content.Context;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;

import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.DragEvent;
import android.view.Gravity;
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
import haiku.top.model.Theme;
import haiku.top.model.generator.HaikuGenerator;
import haiku.top.model.smshandler.SMS;
import haiku.top.model.smshandler.ShowSMSESThread;
import haiku.top.model.sql.DatabaseHandler;
import haiku.top.view.ThemeObjectView;
import haiku.top.view.bin.BinView;
import haiku.top.view.date.DateView;

public class MainView extends RelativeLayout implements OnClickListener, OnLongClickListener, OnTouchListener{
	private static MainView mv;
	private Context context;
	public static final int ANIMATION_TIME_BIN = 300;
	public static final int ANIMATION_TIME_DATE = 300;
	public static final float OPACITY_USED = (float) 0.3;
	public static final float OPACITY_USED_DATE = (float) 0.1;
	public static final float OPACITY_DEFAULT = (float) 1; // 0.8 laggar
	public static final float OPACITY_FULL = 1;
	public static final int VIEW_SHOWN_SMS = 1;
	public static final int VIEW_SHOWN_BIN = 2;
	public static final int VIEW_SHOWN_DATE = 3;
	
	public static final double LONG_CLICK_TIME = 1000; // In ms
	public static final double CLICK_TIME = 100; // In ms
	public static final double MOVE_TO_DRAG_RANGE = 10;
	
	public static final int THEME_ROTATION = -5;
	
	public static final int BACKGROUND_COLOR_DEFAULT = Color.WHITE;
	public static final int BACKGROUND_COLOR_BIN_REMOVE = Color.RED;
	
	private ScrollView themeScroll;
	private LinearLayout themeList;
	
	private ScrollView contactScroll;
	private LinearLayout contactList;
	
	private LinearLayout smslayout;
	private ImageView contactPic;
	private TextView contactName;
	private ConversationObjectView chosenContact;
	private ScrollView smsScroll;
	private LinearLayout smsList;
	
	private ImageView haikuBinViewSmall;
	private BinView haikuBinViewExtended;
	
	private View viewBeingDragged = null;
	
	private ArrayList<ConversationObjectView> conversations = new ArrayList<ConversationObjectView>();
	private ArrayList<SMSObjectView> smsObjects = new ArrayList<SMSObjectView>();
	private ArrayList<ThemeObjectView> themeObjects = new ArrayList<ThemeObjectView>();
//	private int threadIDInUse; // The conversation the user is currently looking at
	
	/**
	 * 0 = no view (default main view), 1 = sms view, 2 = bin view, 3 = date open
	 */
	private ArrayList<Integer> viewsOpenInOrder = new ArrayList<Integer>();

	private boolean dateViewClosed = true;
	private boolean binViewClosed = true;
	
	private DateView dateView;
	
	public MainView(Context context) {
		super(context);
		this.context = context;
		mv = this;
		setBackgroundColor(BACKGROUND_COLOR_DEFAULT);
		
		ImageView yellowBackground = new ImageView(context);
		yellowBackground.setBackgroundColor(Color.rgb(251, 206, 13));
		
		int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2*100, getResources().getDisplayMetrics());
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, getResources().getDisplayMetrics());
        
        double radiansAngle = ((double)Math.abs(THEME_ROTATION))/180.0*Math.PI; // The rotation in radians
        
        // the distance to the top of the screen
        int yOffset = (int) (Math.sin(radiansAngle)*((double)width)/2); // the width of the actual themeview is width/2
        
        // the distance from the themeview's xpos to the xpos at the top of the screen if you follow the angle
        int xOffset = (int) Math.sqrt(yOffset*yOffset*(1.0/(Math.cos(radiansAngle)*Math.cos(radiansAngle)) - 1));
        
		LayoutParams fillParams = new RelativeLayout.LayoutParams(width, height);
		fillParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		fillParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		fillParams.setMargins(0, -yOffset, -width/2 + xOffset, 0);
		yellowBackground.setLayoutParams(fillParams);
		
		yellowBackground.setRotation(THEME_ROTATION);
		addView(yellowBackground);
		
		LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutInflater.inflate(R.layout.mainview,this);
		
		contactScroll = (ScrollView)findViewById(R.id.scrollofcontacts);
		contactList = (LinearLayout)findViewById(R.id.listofcontacts);
		
		smslayout = (LinearLayout)findViewById(R.id.smslayout);
		contactPic = (ImageView)findViewById(R.id.pickedcontactpic);
		contactName = (TextView)findViewById(R.id.pickedcontactname);
		smsScroll = (ScrollView)findViewById(R.id.scrollofsms);
		smsList = (LinearLayout)findViewById(R.id.listofsms);
		
		themeScroll = (ScrollView)findViewById(R.id.themeview);
		haikuBinViewSmall = (ImageView)findViewById(R.id.binview);
		themeScroll.setRotation(THEME_ROTATION);
		
		haikuBinViewSmall.setOnDragListener(new HaikuBinDragListener(haikuBinViewSmall));
		haikuBinViewSmall.bringToFront();
		
		haikuBinViewExtended = new BinView(context);
		
		addView(haikuBinViewExtended);
		haikuBinViewExtended.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		
		haikuBinViewSmall.setOnClickListener(this);
//		haikuBinViewExtended.setOnClickListener(this);
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
		themes.addAll(HaikuGenerator.getAllThemes());
		
		themeList = new LinearLayout(context);
		themeList.setOrientation(LinearLayout.VERTICAL);
		themeList.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		themeScroll.addView(themeList);
		ThemeObjectView themeObject;
//		int height = 0;
		for(int i = 0; i < themes.size(); i++){
			themeObject = new ThemeObjectView(context, themes.get(i), false);
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
//		smsScroll.setVisibility(GONE); //TODO
		smslayout.setVisibility(GONE);
//		themeButton.bringToFront();
		themeScroll.bringToFront();
		themeScroll.setAlpha(OPACITY_DEFAULT);
		updateThemeView();
//		themeView.setVisibility(View.GONE);
//		themeButton.setOnClickListener(this);

		dateView = new DateView(context);
		addView(dateView);
		LayoutParams params1 = new RelativeLayout.LayoutParams(dateView.getRadius(), dateView.getRadius());
		params1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		params1.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		dateView.setLayoutParams(params1);
		dateView.bringToFront();

		haikuBinViewExtended.bringToFront();
	}
	
	public static synchronized MainView getInstance(){
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
	
	//Old. Slow, but working
//	public void setSMSView(int threadID){ 
//		viewsOpenInOrder.add(VIEW_SHOWN_SMS);
//		contactScroll.setVisibility(GONE);
////		smsScroll.setVisibility(VISIBLE); // TODO
//		smslayout.setVisibility(VISIBLE);
//		Cursor cursor = HaikuActivity.getThread(context, threadID);
//		if(chosenContact.getPicture() != null){
//			contactPic.setImageBitmap(chosenContact.getPicture());
//		}
//		else{
////			contactPic.setBackgroundDrawable(R.drawable.delete_by_haiku_logo);
//		}
//		contactName.setText(chosenContact.getName());
//		Log.i("TAG", "Count: " + cursor.getCount());
//		if (cursor.moveToFirst()) {
//			do{
//				smsObjects.add(new SMSObjectView(context, cursor.getString(cursor.getColumnIndexOrThrow("type")),new SMS(cursor.getInt(cursor.getColumnIndexOrThrow("_id")), cursor.getString(cursor.getColumnIndexOrThrow("body")), cursor.getString(cursor.getColumnIndexOrThrow("date")), threadID)));
//				smsObjects.get(smsObjects.size()-1).setOnLongClickListener(this);
//				smsObjects.get(smsObjects.size()-1).setOnTouchListener(this);
//			}
//			while(cursor.moveToNext());
//		}
//		cursor.close();
//		for(int i = 0; i < smsObjects.size(); i++){
//			smsList.addView(smsObjects.get(i));
//		}
//		updateSMSView();
//	}
	
	private ShowSMSESThread workerThread;
	
	public synchronized void removeWorkerThread(){
		workerThread = null;
	}
	
	public synchronized void stopWorkerThreadIfActive(){
		if(workerThread != null){
			workerThread.stopWorking();
		}
	}
	
	public void setSMSView(int threadID){
		viewsOpenInOrder.add(VIEW_SHOWN_SMS);
		contactScroll.setVisibility(GONE);
		smslayout.setVisibility(VISIBLE);
		if(chosenContact.getPicture() != null){
			contactPic.setImageBitmap(chosenContact.getPicture());
		}
		else{
//			contactPic.setBackgroundDrawable(R.drawable.delete_by_haiku_logo);
		}
		contactName.setText(chosenContact.getName());
		workerThread = new ShowSMSESThread(threadID);
		workerThread.start();
	}
	
	public synchronized void addSMSToView(final SMSObjectView smsObject){
		smsObjects.add(smsObject);
		smsObjects.get(smsObjects.size()-1).setOnLongClickListener(this);
		smsObjects.get(smsObjects.size()-1).setOnTouchListener(this);
		HaikuActivity.getInstance().runOnUiThread(new Runnable(){           
	        @Override
	        public void run(){
	        	smsList.addView(smsObject);
	    		if(HaikuGenerator.getAllAddedSMS().contains(smsObject.getSMS())){
	    			smsObject.setAlpha(OPACITY_USED);
	    		}
	    		else{
	    			smsObject.setAlpha(OPACITY_DEFAULT);
	    		}        
	        }
	    });
		
		
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
//		smsScroll.setVisibility(GONE); //TODO
		smslayout.setVisibility(GONE);
		smsObjects.clear();
		smsList.removeAllViews();
		removeViewElement(VIEW_SHOWN_SMS);
		stopWorkerThreadIfActive();
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
	
	private boolean isBinColor = false;
	
	public void setBinColor(){
		setBackgroundColor(BACKGROUND_COLOR_BIN_REMOVE);
		isBinColor = true;
	}
	
	public void resetBinColor(){
		setBackgroundColor(BACKGROUND_COLOR_DEFAULT);
		isBinColor = false;
	}
	
	public boolean isBinColor(){
		return isBinColor;
	}
	
	@Override
	public void onClick(View v) {
		if(v instanceof ConversationObjectView){
			chosenContact = (ConversationObjectView) v;
			setSMSView(((ConversationObjectView)v).getThreadID());
		}
		else if(v.equals(haikuBinViewSmall)){
			openBinView();
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
							+ (((int) event.getY()) - startY) * (((int) event.getY()) - startY)))*180/Math.PI){
				if(v.getAlpha() == OPACITY_USED){
					// already in the bin! Can't drag it!
					return false;
				}
				v.setAlpha(OPACITY_USED);
				viewBeingDragged = v;
				v.startDrag(null, new DragShadowBuilder(v), null, 0);
				return true;
			}
		}
		return false;
	}
	
	public void openBinView(){
//		haikuBinViewExtended.update();
		haikuBinViewExtended.onOpen();
//		haikuBinViewExtended.reset();
		binViewClosed = false;
		viewsOpenInOrder.add(VIEW_SHOWN_BIN);
		haikuBinViewSmall.setVisibility(GONE);
		haikuBinViewExtended.setVisibility(VISIBLE);
		if(!dateViewClosed){
			closeDateView();
		}
	}
	
	public void closeBinView(){
		binViewClosed = true;
		removeViewElement(VIEW_SHOWN_BIN);
		haikuBinViewSmall.setVisibility(VISIBLE);
		haikuBinViewExtended.setVisibility(GONE);
	}
	
	public boolean isBinViewClosed(){
		return binViewClosed;
	}
	
	public void updateDateView(){
		dateView.update();
	}
	
	public int getSelectedYear(){
		return dateView.getSelectedYear();
	}
	
	public void dateViewOpened(){
		dateViewClosed = false;
	}
	
	public void closeDateView(){
		dateViewClosed = true;
		dateView.closeDateView();
	}
}
