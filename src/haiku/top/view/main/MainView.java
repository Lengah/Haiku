package haiku.top.view.main;

import java.util.ArrayList;

import org.w3c.dom.NameList;

import android.R.xml;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;

import android.util.DisplayMetrics;
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
import android.widget.OverScroller;
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
import haiku.top.view.binview.BinView;
import haiku.top.view.binview.HaikuProgressBar;
import haiku.top.view.date.DateView;
import haiku.top.view.main.sms.SMSObject;

public class MainView extends RelativeLayout implements OnClickListener, OnLongClickListener, OnTouchListener{
	private static MainView mv;
	private Context context;
	
	public static final int COLOR_THEME_BACKGROUND = Color.rgb(251, 206, 13); // "#FBCE0D"
	/**
	 * in %. The small bin view fills out the rest
	 */
	public static final double THEME_HEIGHT = 35.0;
	/**
	 * in dp
	 */
	public static final double RIGHT_VIEWS_WIDTH = 100;
	private double rightViewsWidth;
	
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
	public static final int BACKGROUND_COLOR_BIN_REMOVE = Color.WHITE; // no change atm
	
	private ScrollView themeScroll;
	private LinearLayout themeList;
	
	private ScrollView contactScroll;
	private LinearLayout contactList;
	
	private LinearLayout smslayout;
	private LinearLayout pickedContactLayout;
	private LinearLayout pickedContactNamesLayout;
	private ImageView contactPic;
	private ConversationObjectView chosenContact;
	private ScrollView namesScroll;
	private LinearLayout namesScrollLayout;
	
	private ScrollView smsScroll;
	private RelativeLayout smsList;
	
	private SmallBinView haikuBinViewSmall;
	private HaikuBinDragListener haikuBinDragListener;
	private BinView haikuBinViewExtended;
	
	private View viewBeingDragged = null;
	
	private ArrayList<ConversationObjectView> conversations = new ArrayList<ConversationObjectView>();
//	private ArrayList<SMSObjectView> smsObjects = new ArrayList<SMSObjectView>(); //OLD sms view
	private ArrayList<SMSObject> smsObjects = new ArrayList<SMSObject>();
	private ArrayList<ThemeObjectView> themeObjects = new ArrayList<ThemeObjectView>();
//	private int threadIDInUse; // The conversation the user is currently looking at
	
	/**
	 * 0 = no view (default main view), 1 = sms view, 2 = bin view, 3 = date open
	 */
	private ArrayList<Integer> viewsOpenInOrder = new ArrayList<Integer>();

	private boolean dateViewClosed = true;
	private boolean binViewClosed = true;
	
	private DateView dateView;
	
	private boolean lookingAtHaikus = false;
	private int listWidth;
	
	private Typeface haikuTypeface;
	private Typeface smsBinTypeface;
	private Typeface smsBinCombinedTypeface;
	private Typeface smsListTypeface;
	private Typeface contactsTypeface;
	private Typeface themeTypeface;
	private Typeface dateTypeface;
	private Typeface saveTypeface;
	private Typeface shareTypeface;
	
	private LayoutParams fillerParams;
	private float fillerHeight;
	
	public MainView(Context context) {
		super(context);
		this.context = context;
		mv = this;
		
		fillerHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) DateView.TIME_SMALL_SIZE, getResources().getDisplayMetrics());
		fillerParams = new LayoutParams(LayoutParams.MATCH_PARENT, (int) fillerHeight);
		
		haikuTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/AGARAMONDPRO-REGULAR.OTF");
		smsBinTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/AGARAMONDPRO-REGULAR.OTF");
		smsBinCombinedTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/AGARAMONDPRO-REGULAR.OTF");
		smsListTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/AGARAMONDPRO-REGULAR.OTF");
		contactsTypeface = null;
		themeTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/AGARAMONDPRO-REGULAR.OTF");
		dateTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/AGARAMONDPRO-REGULAR.OTF");
		saveTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/AGARAMONDPRO-REGULAR.OTF");
		shareTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/AGARAMONDPRO-REGULAR.OTF");
		
		setBackgroundColor(BACKGROUND_COLOR_DEFAULT);
		
		rightViewsWidth = HaikuActivity.convertDpToPixel((float) RIGHT_VIEWS_WIDTH);
		int themeViewHeight = (int) (HaikuActivity.getInstance().getWindowHeight()*THEME_HEIGHT/100.0);
		int smallBinViewHeight = HaikuActivity.getInstance().getWindowHeight()-themeViewHeight;
		
		themeScroll = new ScrollView(context);
		themeScroll.setVerticalScrollBarEnabled(false);
		themeScroll.setBackgroundColor(COLOR_THEME_BACKGROUND);
		themeScroll.setRotation(THEME_ROTATION);
		LayoutParams themeParams = new RelativeLayout.LayoutParams((int) rightViewsWidth, themeViewHeight);
		themeParams.addRule(ALIGN_PARENT_RIGHT);
		themeParams.addRule(ALIGN_PARENT_TOP);
		
		haikuBinViewSmall = new SmallBinView(context, (int) rightViewsWidth, smallBinViewHeight);
		LayoutParams haikuBinViewSmallParams = new RelativeLayout.LayoutParams((int) rightViewsWidth, smallBinViewHeight);
		haikuBinViewSmallParams.addRule(ALIGN_PARENT_RIGHT);
		haikuBinViewSmallParams.addRule(ALIGN_PARENT_BOTTOM);
		
		addView(themeScroll, themeParams);
		addView(haikuBinViewSmall, haikuBinViewSmallParams);
		
		ImageView yellowBackground = new ImageView(context);
		yellowBackground.setBackgroundColor(Color.rgb(251, 206, 13));
		
		int width = (int) (2*rightViewsWidth);
        
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
	    listWidth = HaikuActivity.getInstance().getWindowWidth() - Math.round(110 * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));   
        
        SMSObject.setMarginSides((int) (listWidth*(SMSObject.MARGIN_SIDES/100.0)));
        SMSObject.setPaddingSides((int) (listWidth*(SMSObject.PADDING_SIDES/100.0)));
        SMSObject.calc();
        
        double radiansAngle = ((double)Math.abs(THEME_ROTATION))/180.0*Math.PI; // The rotation in radians
        
        // the distance to the top of the screen
        int yOffset = (int) (Math.sin(radiansAngle)*((double)width)/2); // the width of the actual themeview is width/2
        
        // the distance from the themeview's xpos to the xpos at the top of the screen if you follow the angle
        int xOffset = (int) Math.sqrt(yOffset*yOffset*(1.0/(Math.cos(radiansAngle)*Math.cos(radiansAngle)) - 1));
        
		LayoutParams fillParams = new RelativeLayout.LayoutParams(width, themeViewHeight);
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
		contactScroll.setOverScrollMode(OVER_SCROLL_ALWAYS);
		contactList.setOverScrollMode(OVER_SCROLL_ALWAYS);
		
		smslayout = (LinearLayout)findViewById(R.id.smslayout);
		pickedContactLayout = (LinearLayout)findViewById(R.id.pickedcontact_layout);
		pickedContactNamesLayout = (LinearLayout)findViewById(R.id.pickedcontactnames_layout);
		contactPic = (ImageView)findViewById(R.id.pickedcontactpic);
		namesScroll = (ScrollView)findViewById(R.id.scrollofnames);
		
		smsScroll = (ScrollView)findViewById(R.id.scrollofsms);
		smsList = (RelativeLayout)findViewById(R.id.listofsms);
		
		haikuBinDragListener = new HaikuBinDragListener();
		haikuBinViewSmall.setOnDragListener(haikuBinDragListener);
		haikuBinViewSmall.bringToFront();
		
		haikuBinViewExtended = new BinView(context);
		
		addView(haikuBinViewExtended);
		haikuBinViewExtended.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		
		pickedContactLayout.setOnClickListener(this);
		namesScroll.setOnClickListener(this);
		haikuBinViewSmall.setOnClickListener(this);
//		haikuBinViewExtended.setOnClickListener(this);
		haikuBinViewExtended.setVisibility(View.GONE);
		
//		contactList.setAdapter(new ContactListAdapter(context, HaikuActivity.getThreads(context), true));
		
		updateConversations();
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
//		smsScroll.setVisibility(GONE);
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
		
		
		// Needed for when the user closes the program and then later opens it
		updateConversationsVisibility();
		updateSMSView();
		updateThemeView();
		
//		contactScroll.setOnTouchListener(new OnTouchListener() {
//			   @Override
//			   public boolean onTouch(View v, MotionEvent event) {
//				   if(event.getAction() == MotionEvent.ACTION_DOWN){
//						isScrollingPastTop = false;
//						lastY = event.getY();
//					}
//				   if(event.getAction() == MotionEvent.ACTION_UP){
//						if(shouldSnapBack){
//							//Log.i("TAG", "snapback!");
//							contactScroll.post(new Runnable() { 
//						        public void run() { 
//						        	contactScroll.smoothScrollTo(0, fillerHeight);
//						        }
//							});
//							return true;
//						}
//					}
//				   if(event.getAction() == MotionEvent.ACTION_MOVE){
//						// Scrolling
//						//Log.i("TAG", "scrolling");
//						if(!isScrollingPastTop && lastY < event.getY() && contactScroll.getScrollY() <= fillerHeight){
//							isScrollingPastTop = true;
//							shouldSnapBack = true;
//						}
//						if(isScrollingPastTop && contactScroll.getScrollY() > fillerHeight){
//							isScrollingPastTop = false;
//							shouldSnapBack = false;
//						}
//						if(isScrollingPastTop){
//							//Log.i("TAG", "custom scroll down");
//							contactScroll.scrollBy(0, (int) (lastY - event.getY()));
//							lastY = event.getY();
//							return true;
//						}
//					}
//					if(!isScrollingPastTop && contactScroll.getScrollY() < fillerHeight){
//						//Log.i("TAG", "too far!");
//						contactScroll.post(new Runnable() { 
//					        public void run() {
//					        	contactScroll.smoothScrollTo(0, fillerHeight);
//					        } 
//						});
//						return true;
//					}
//					return false;
//			   }
//		});
	}
	
	public static synchronized MainView getInstance(){
		return mv;
	}
	
	public Typeface getHaikuTypeface(){
		return haikuTypeface;
	}
	
	public Typeface getSmsBinTypeface(){
		return smsBinTypeface;
	}
	
	public Typeface getSmsBinCombinedTypeface(){
		return smsBinCombinedTypeface;
	}
	
	public Typeface getSmsListTypeface(){
		return smsListTypeface;
	}
	
	public Typeface getContactsTypeface(){
		return contactsTypeface;
	}
	
	public Typeface getThemeTypeface(){
		return themeTypeface;
	}
	
	public Typeface getDateTypeface(){
		return dateTypeface;
	}
	
	public Typeface getSaveTypeface(){
		return saveTypeface;
	}
	
	public Typeface getShareTypeface(){
		return shareTypeface;
	}
	
	public int getListWidth(){
		return listWidth;
	}
	
	/**
	 * if this method returns an empty list, then the program should exit when the user
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
	
	public HaikuBinDragListener getHaikuDragListener(){
		return haikuBinDragListener;
	}
	
	public ConversationObjectView getConversationObject(long threadID){
		for(ConversationObjectView cov : conversations){
			if(cov.getThreadID() == threadID){
				return cov;
			}
		}
		return null;
	}
	
	private int biggestSMSCount = 0;
	private int latestCounter = 5;
	
	/**
	 * A complete update of the conversations. Does a new database query and recreates all the conversation objects
	 */
	public void updateConversations(){
		conversations.clear();
		contactList.removeAllViews();
		boolean haikuContactAdded = false;
		boolean recent;
		Cursor cursor = HaikuActivity.getThreads(context);
		if (cursor.moveToFirst()) {
			do{
				recent = false;
				if(latestCounter > 0){
					latestCounter--;
					recent = true;
				}
//				conversations.add(new ConversationObjectView(context, cursor.getInt(cursor.getColumnIndexOrThrow("thread_id")), cursor.getString(cursor.getColumnIndexOrThrow("address"))));
				ConversationObjectView temp = new ConversationObjectView(context, cursor.getInt(cursor.getColumnIndexOrThrow("thread_id")), recent);
				if(temp.isHaikuConversation()){
					latestCounter++;
					conversations.add(0, temp);
					haikuContactAdded = true;
				}
				else if(!temp.getNames().isEmpty()){
					for(int i = 0; i <= conversations.size(); i++){
						if(!(i==0 && haikuContactAdded) && (i == conversations.size() || HaikuActivity.compareIgnoreCase(temp.getNames().get(0), conversations.get(i).getNames().get(0)) >= 0)){
							conversations.add(i, temp);
							break;
						}
					}
				}
				temp.setOnClickListener(this);
				temp.setOnTouchListener(this);
			}
			while(cursor.moveToNext());
		}
//		contactList.addView(filler);
		for(int i = 0; i < conversations.size(); i++){
			if(conversations.get(i).getSMSCount() > biggestSMSCount){
				biggestSMSCount = conversations.get(i).getSMSCount();
			}
		}
		for(int i = 0; i < conversations.size(); i++){
			conversations.get(i).calculateAndSetSpacing(biggestSMSCount);
			contactList.addView(conversations.get(i));
//			conversations.get(i).setAlpha(OPACITY_DEFAULT); // Lags
		}
		contactList.addView(new View(getContext()), fillerParams);
//		contactScroll.post(new Runnable() { 
//	        public void run() { 
//	        	contactScroll.scrollBy(0, fillerHeight);
//	        } 
//		});
	}
	
	/**
	 * Just sets the conversations' alpha
	 */
	public void updateConversationsVisibility(){
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
////		smsScroll.setVisibility(VISIBLE);
//		smslayout.setVisibility(VISIBLE);
//		Cursor cursor = HaikuActivity.getThread(context, threadID);
//		if(chosenContact.getPicture() != null){
//			contactPic.setImageBitmap(chosenContact.getPicture());
//		}
//		else{
////			contactPic.setBackgroundDrawable(R.drawable.delete_by_haiku_logo);
//		}
//		contactName.setText(chosenContact.getName());
//		//Log.i("TAG", "Count: " + cursor.getCount());
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
		HaikuActivity.getInstance().runOnUiThread(new Runnable(){           
	        @Override
	        public void run(){
	        	RelativeLayout.LayoutParams fillerParams2 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int) fillerHeight);
	        	fillerParams2.setMargins(0, smsListTopOffset, 0, 0);
	        	smsList.addView(new View(getContext()), fillerParams2);
	        }
		});
	}
	
	public synchronized void stopWorkerThreadIfActive(){
		if(workerThread != null){
			workerThread.stopWorking();
		}
	}
	
	public boolean isShowingSMS(){
		// if 1 has a higher index than 0 it means that smses are being shown
		return viewsOpenInOrder.indexOf(1) > viewsOpenInOrder.indexOf(0);
	}
	
	public int getSelectedConvoThreadID(){
		return chosenContact.getThreadID();
	}
	
	public boolean isLookingAtHaikus(){
		return lookingAtHaikus;
	}
	
	private boolean canEnlargeNamesList = true;
	
	public void updateContactNames(int threadID){
		namesListIsEnlarged = false;
		pickedContactNamesLayout.removeAllViews();
		namesScroll.removeAllViews();
		pickedContactNamesLayout.setVisibility(View.VISIBLE);
		namesScroll.setVisibility(View.GONE);
		if(chosenContact.getNames().size() == 1){
			canEnlargeNamesList = false; // can not enlarge it because there is only one
			TextView temp = new TextView(context);
			temp.setTextColor(Color.BLACK);
			int layoutHeight = 50;
			int textSize = 15;
			temp.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) HaikuActivity.convertDpToPixel(layoutHeight)));
			temp.setTextSize(textSize);
			temp.setGravity(Gravity.CENTER_VERTICAL);
			temp.setText(chosenContact.getNames().get(0));
			pickedContactNamesLayout.addView(temp);
		}
		else{
			canEnlargeNamesList = true;
			TextView temp;
			TextView temp2;
			int layoutHeight = 25;
			int textSize = 15;
			
			int maxScrollListHeight = (int) HaikuActivity.convertDpToPixel(150);
			namesScrollLayout = new LinearLayout(context);
			int listHeight = Math.min(maxScrollListHeight, (int) HaikuActivity.convertDpToPixel(layoutHeight) * chosenContact.getNames().size());
			namesScrollLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
			namesScrollLayout.setOrientation(LinearLayout.VERTICAL);
			namesScrollLayout.setOnClickListener(this);
			namesScroll.addView(namesScrollLayout);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,listHeight);
			params.setMargins((int) HaikuActivity.convertDpToPixel(3), 0, 0, 0);
			namesScroll.setLayoutParams(params);
			
			
			for(String s : chosenContact.getNames()){
				temp = new TextView(context);
				temp.setTextColor(Color.BLACK);
				temp.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) HaikuActivity.convertDpToPixel(layoutHeight)));
				temp.setTextSize(textSize); // sp as default
				temp.setGravity(Gravity.CENTER_VERTICAL);
				temp.setText(s);
				pickedContactNamesLayout.addView(temp);
				
				
				temp2 = new TextView(context);
				temp2.setTextColor(Color.BLACK);
				temp2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) HaikuActivity.convertDpToPixel(layoutHeight)));
				temp2.setTextSize(textSize); // sp as default
				temp2.setGravity(Gravity.CENTER_VERTICAL);
				temp2.setText(s);
				namesScrollLayout.addView(temp2);
			}
		}
	}
	
	public void setSMSView(int threadID){
		smsListTopOffset = 0;
		viewsOpenInOrder.add(VIEW_SHOWN_SMS);
		contactScroll.setVisibility(GONE);
		smslayout.setVisibility(VISIBLE);
		if(chosenContact.getPicture() != null){
			contactPic.setImageBitmap(chosenContact.getPicture());
		}
		else{
//			contactPic.setBackgroundDrawable(R.drawable.delete_by_haiku_logo);
		}
//		contactName.setText(chosenContact.getName());
		updateContactNames(threadID);
		if(chosenContact.getNames().get(0).equals("Haiku")){
			lookingAtHaikus = true;
		}
		else{
			lookingAtHaikus = false;
		}
		workerThread = new ShowSMSESThread(threadID, lookingAtHaikus);
		workerThread.start();
	}
	
	private int smsListTopOffset;
	
	public SMSObject getLastObjectInSMSList(){
		if(smsObjects.isEmpty()){
			return null;
		}
		return smsObjects.get(smsObjects.size()-1);
	}
	
	public synchronized void addSMSToView(SMS sms){
//		SMSObjectTopBottom lastObjectInList = null;
//		if(!smsObjects.isEmpty()){
//			lastObjectInList = (SMSObjectTopBottom)smsObjects.get(smsObjects.size()-1);
//		}
//		else{
//			// else -> first object -> no constraints on where to draw
//			lastObjectInList = new SMSObjectTopBottom(context); // is generated when the SMSObject is created
//			smsObjects.add(lastObjectInList);
//			smsObjects.get(smsObjects.size()-1).setOnLongClickListener(this);
//			smsObjects.get(smsObjects.size()-1).setOnTouchListener(this);
//		}
		SMS prevSMS = null;
		if(!smsObjects.isEmpty()){
			prevSMS = smsObjects.get(smsObjects.size()-1).getSMS();
		}
		final SMSObject smsObject = new SMSObject(context, sms, prevSMS);
		
		smsObjects.add(smsObject);
		smsObjects.get(smsObjects.size()-1).setOnLongClickListener(this);
		smsObjects.get(smsObjects.size()-1).setOnTouchListener(this);
		
//		smsObjects.add(smsObject.getBottomBox());
//		smsObjects.get(smsObjects.size()-1).setOnLongClickListener(this);
//		smsObjects.get(smsObjects.size()-1).setOnTouchListener(this);
		
		HaikuActivity.getInstance().runOnUiThread(new Runnable(){           
	        @Override
	        public void run(){
//	        	if(smsList.getChildCount() == 0){
//	        		//first
//	        		RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(listWidth, (int) (SMSObject.getHeightOfTextRow()*HEIGHT_OF_TOPBOTTOM_SMSOBJECTS_IN_ROWS));
//	        		smsList.addView(smsObject.getTopBox(), params1);
//	        	}
	        	RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(smsObject.getTotalWidth(), smsObject.getTotalHeight());
	        	if(smsObject.getSMS().isSent()){
	        		params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
	        		params.setMargins(0, smsListTopOffset, smsObject.getPadding(), 0);
	        	}
	        	else{
	        		params.setMargins(smsObject.getPadding(), smsListTopOffset, 0, 0);
	        	}
	        	smsList.addView(smsObject, params);
	        	smsListTopOffset += smsObject.getOffsetCreatedByThisView();
//	        	LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(listWidth, (int) (SMSObject.getHeightOfTextRow()*HEIGHT_OF_TOPBOTTOM_SMSOBJECTS_IN_ROWS));
//	        	smsList.addView(smsObject.getBottomBox(), params3);
	        }
	    });
	}
	
	// Old - With the old SMS view which was just an oval (works fine)
//	public synchronized void addSMSToView(final SMSObjectView smsObject){
//		smsObjects.add(smsObject);
////		if(lookingAtHaikus){ // somehow cancels the touch?
//			smsObjects.get(smsObjects.size()-1).setOnLongClickListener(this);
////		}
//		smsObjects.get(smsObjects.size()-1).setOnTouchListener(this);
//		HaikuActivity.getInstance().runOnUiThread(new Runnable(){           
//	        @Override
//	        public void run(){
//	        	smsList.addView(smsObject);
//	    		if(HaikuGenerator.getAllAddedSMS().contains(smsObject.getSMS())){
//	    			smsObject.setAlpha(OPACITY_USED);
//	    		}
//	    		else{
//	    			smsObject.setAlpha(OPACITY_DEFAULT);
//	    		}        
//	        }
//	    });
//	}
	
	/**
	 * Just changes the visibility of the smsObjects
	 */
	public void updateSMSView(){
		boolean empty = true;
		for(int i = 0; i < smsObjects.size(); i++){
			if(!(smsObjects.get(i) instanceof SMSObject)){
				continue;
			}
			if(HaikuGenerator.getAllAddedSMS().contains(((SMSObject)smsObjects.get(i)).getSMS())){
//				smsObjects.get(i).setVisibility(GONE);
				smsObjects.get(i).setAlpha(OPACITY_USED);
//				smsObjects.get(i).setSelectedColor();
			}
			else{
//				smsObjects.get(i).setVisibility(VISIBLE);
				smsObjects.get(i).setAlpha(OPACITY_DEFAULT);
//				smsObjects.get(i).resetColor();
				empty = false;
			}
		}
		if(empty && !smsObjects.isEmpty()){
			HaikuGenerator.addThread((int)((SMSObject)smsObjects.get(0)).getSMS().getContactID());
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
		updateConversationsVisibility();
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
	
	public SmallBinView getSmallBinView(){
		return haikuBinViewSmall;
	}
	
	private boolean namesListIsEnlarged = false;
	
	@Override
	public void onClick(View v) {
		if(v instanceof ConversationObjectView){
			chosenContact = (ConversationObjectView) v;
			setSMSView(((ConversationObjectView)v).getThreadID());
		}
		else if(v.equals(haikuBinViewSmall)){
			openBinView();
		}
		else if((v.equals(pickedContactLayout) || v.equals(namesScroll) || v.equals(namesScrollLayout)) && canEnlargeNamesList){
//			closeSMSView();
			if(namesListIsEnlarged){
				namesScroll.setVisibility(View.GONE);
				pickedContactNamesLayout.setVisibility(View.VISIBLE);
				namesListIsEnlarged = false;
			}
			else{
				pickedContactNamesLayout.setVisibility(View.GONE);
				namesScroll.setVisibility(View.VISIBLE);
				namesListIsEnlarged = true;
			}
		}
	}

	@Override
	public boolean onLongClick(View v) { //TODO
		// Code for drag
//		if(v.getAlpha() == OPACITY_USED){
//			// The view is already in the bin
//			return false;
//		}
//		v.setAlpha(OPACITY_USED);
//		viewBeingDragged = v;
//		v.startDrag(null, new DragShadowBuilder(v), null, 0);
//		return false;
		
		// Code for sharing
		if(lookingAtHaikus && v instanceof SMSObject){
			// The user wants to share one of his haikus
		    HaikuActivity.getInstance().shareMessage(((SMSObject)v).getSMS().getMessage());
		}
		return false;
	}

	private int startX;
	private int startY;
	
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
	
	public void openBinViewToAdd(){
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
	
	public BinView getBinView(){
		return haikuBinViewExtended;
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
