package haiku.top.view;

import java.util.ArrayList;

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
import android.widget.LinearLayout.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import haiku.top.HaikuActivity;
import haiku.top.R;
import haiku.top.model.HaikuGenerator;
import haiku.top.model.SMS;
import haiku.top.model.Theme;
import haiku.top.model.sql.DatabaseHandler;
import haiku.top.view.adapters.ContactListAdapter;

public class MainView extends LinearLayout implements OnClickListener, OnLongClickListener, OnTouchListener{
	private static MainView mv;
	private Context context;
	private static final int ANIMATION_TIME_THEME = 300;
	private static final int ANIMATION_TIME_DATE = 300;
	
	private Button themeButton;
	private ScrollView themeView;
	
	private ScrollView contactScroll;
	private LinearLayout contactList;
	
	private ScrollView smsScroll;
	private LinearLayout smsList;
	
	private LinearLayout haikuBinView;
	
	private View viewBeingDragged = null;
	
	private ArrayList<ConversationObjectView> conversations = new ArrayList<ConversationObjectView>();
	private ArrayList<SMSObjectView> smsObjects = new ArrayList<SMSObjectView>();
//	private int threadIDInUse; // The conversation the user is currently looking at
	
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
		
		themeButton = (Button)findViewById(R.id.themebutton);
		themeView = (ScrollView)findViewById(R.id.themeview);
		haikuBinView = (LinearLayout)findViewById(R.id.binview);
		
		haikuBinView.setOnDragListener(new HaikuBinDragListener(haikuBinView));
		haikuBinView.bringToFront();
		
//		contactList.setAdapter(new ContactListAdapter(context, HaikuActivity.getThreads(context), true));
		
		Cursor cursor = HaikuActivity.getThreads(context);
		if (cursor.moveToFirst()) {
			do{
				conversations.add(new ConversationObjectView(context, cursor.getInt(cursor.getColumnIndexOrThrow("thread_id")), cursor.getString(cursor.getColumnIndexOrThrow("address"))));
				conversations.get(conversations.size()-1).setOnLongClickListener(this);
				conversations.get(conversations.size()-1).setOnClickListener(this);
			}
			while(cursor.moveToNext());
		}
		for(int i = 0; i < conversations.size(); i++){
			contactList.addView(conversations.get(i));
		}
		smsScroll.setVisibility(GONE);
		themeButton.bringToFront();
		themeView.bringToFront();
		themeView.setVisibility(View.GONE);
		themeButton.setOnClickListener(this);
	}
	
	public static MainView getInstance(){
		return mv;
	}
	
	public View getDraggedView(){
		return viewBeingDragged;
	}
	
	public void updateConversations(){
		boolean isInGenerator;
		for(int i = 0; i < conversations.size(); i++){
			isInGenerator = false;
			for(int a = 0; a < HaikuGenerator.getThreadIDs().size(); a++){
				if(conversations.get(i).getThreadID() == HaikuGenerator.getThreadIDs().get(a)){
					conversations.get(i).setVisibility(GONE);
					isInGenerator = true;
				}
			}
			if(!isInGenerator){
				conversations.get(i).setVisibility(VISIBLE);
			}
		}
	}
	
	public void setSMSView(int threadID){
		contactScroll.setVisibility(GONE);
		smsScroll.setVisibility(VISIBLE);
		Cursor cursor = HaikuActivity.getThread(context, threadID);
		Log.i("TAG", "Count: " + cursor.getCount());
		if (cursor.moveToFirst()) {
			do{
				smsObjects.add(new SMSObjectView(context, cursor.getString(cursor.getColumnIndexOrThrow("type")),new SMS(cursor.getInt(cursor.getColumnIndexOrThrow("_id")), cursor.getString(cursor.getColumnIndexOrThrow("body")), cursor.getString(cursor.getColumnIndexOrThrow("date")), threadID)));
				smsObjects.get(smsObjects.size()-1).setOnLongClickListener(this);
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
		for(int i = 0; i < smsObjects.size(); i++){
			if(HaikuGenerator.getAllAddedSMS().contains(smsObjects.get(i).getSMS())){
				smsObjects.get(i).setVisibility(GONE);
			}
			else{
				smsObjects.get(i).setVisibility(VISIBLE);
			}
		}
	}
	
	public void closeSMSView(){
		contactScroll.setVisibility(VISIBLE);
		smsScroll.setVisibility(GONE);
		smsObjects.clear();
		smsList.removeAllViews();
	}
	
	private TranslateAnimation translateAnimation;
	
	public void openThemeView(){
		updateThemeView();
		themeButton.setEnabled(false);
		themeView.setVisibility(View.VISIBLE);
		translateAnimation = new TranslateAnimation(Animation.ABSOLUTE, (float)(100-25), Animation.RELATIVE_TO_SELF, (float)0, Animation.RELATIVE_TO_SELF, (float)0, Animation.RELATIVE_TO_SELF, 0);
		translateAnimation.setDuration(ANIMATION_TIME_THEME);
		themeView.startAnimation(translateAnimation);
		translateAnimation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationStart(Animation animation) {
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				
			}
		});
	}
	
	public void closeThemeView(){
		translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.ABSOLUTE, (float)(100-25), Animation.RELATIVE_TO_SELF, (float)0, Animation.RELATIVE_TO_SELF, 0);
		translateAnimation.setDuration(ANIMATION_TIME_THEME);
		themeView.startAnimation(translateAnimation);
		translateAnimation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationStart(Animation animation) {
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				themeView.setVisibility(View.GONE);
				themeButton.setEnabled(true);
			}
		});
	}
	
	public void updateThemeView(){
		themeView.removeAllViews();
		ArrayList<Theme> themes;
//		themes = DatabaseHandler.getAllThemes();
		themes = new ArrayList<Theme>();
		themes.add(Theme.happy);
		themes.add(Theme.sad);
		themes.add(Theme.summer);
		themes.add(Theme.time);
		themes.add(Theme.time);
		themes.add(Theme.time);
		themes.add(Theme.time);
		themes.add(Theme.time);
		
		themes.removeAll(HaikuGenerator.getThemes()); // remove the themes that are already in the bin
		
		LinearLayout list = new LinearLayout(context);
		list.setOrientation(VERTICAL);
		list.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		ThemeObjectView themeObject;
		int height = 0;
		for(int i = 0; i < themes.size(); i++){
			themeObject = new ThemeObjectView(context, themes.get(i));
			list.addView(themeObject);
			themeObject.setOnLongClickListener(this);
			themeObject.setOnClickListener(this);
//			themeObject.setOnTouchListener(this); // overrides the scroll function!
			height += themeObject.getHeightOfView();
		}
		themeView.addView(list);
		if(themeView.getHeight() > height){
			// empty space at the end
			TextView emptySpace = new TextView(context);
			emptySpace.setHeight(themeView.getHeight()-height);
			list.addView(emptySpace);
			emptySpace.setOnClickListener(this);
		}
	}

	@Override
	public void onClick(View v) {
		if(v.equals(themeButton)){
			openThemeView();
		}
		else if(v instanceof ConversationObjectView){
			setSMSView(((ConversationObjectView)v).getThreadID());
		}
		else{
			// themeObject
			closeThemeView();
		}
	}

	@Override
	public boolean onLongClick(View v) {
		v.setVisibility(GONE);
		viewBeingDragged = v;
		v.startDrag(null, new DragShadowBuilder(v), null, 0);
		return false;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// overrides the scroll function!
		if(event.getAction() == MotionEvent.ACTION_MOVE){
			v.setVisibility(GONE);
			viewBeingDragged = v;
			v.startDrag(null, new DragShadowBuilder(v), null, 0);
			return true;
		}
		return false;
	}
}
