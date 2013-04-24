package haiku.top.view;

import haiku.top.HaikuActivity;
import haiku.top.model.HaikuGenerator;
import haiku.top.model.Month;
import haiku.top.model.YearMonth;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

public class DateView extends RelativeLayout implements OnTouchListener, OnLongClickListener{
	private Context context;
	private QuarterCircle yearView;
	
	private ArrayList<QuarterCircle> months = new ArrayList<QuarterCircle>();
	private Month[] monthsName = new Month[] 
			{Month.January, Month.February, Month.Mars, Month.April, Month.May, Month.June, Month.Juli, Month.August, Month.September, Month.October, Month.November, Month.December};
	
	public static final int MONTHS_SHOWN = 4;
	private boolean dateViewClosed = true;
	private int yearSelected = 2013;
	
	public DateView(Context context) {
		super(context);
		this.context = context;
		yearView = new QuarterCircle(context, 100);
		yearView.setText("Time");
		LayoutParams params1 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		params1.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
//		int sizeOfMonth = 90/MONTHS_SHOWN;
		int sizeOfMonth = 25; // degrees
		for(int i = 0; i < monthsName.length; i++){
			Log.i("TAG", "Start: " + (-90 + sizeOfMonth*i + sizeOfMonth) + ", End: " + (-90 + sizeOfMonth*i));
			months.add(new QuarterCircle(context, monthsName[i], 180, -90 + sizeOfMonth*i + sizeOfMonth, -90 + sizeOfMonth*i, yearView.getRadius()));
//			months.get(i).setVisibility(GONE);
			months.get(i).setLayoutParams(params1);
			addView(months.get(i));
//			months.get(i).setOnTouchListener(this);
//			if(i == 0 || i == 1 || i == 2 || i == 4){
//				months.get(i).setVisibility(GONE);
//			}
		}
		params1 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		params1.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		yearView.setLayoutParams(params1);
		addView(yearView);
		yearView.bringToFront();
//		yearView.setVisibility(GONE);
//		yearView.setOnTouchListener(this);
//		setOnTouchListener(this);
		months.get(0).setOnTouchListener(this);
//		setOnLongClickListener(this);
	}
	
	public int getRadius(){
		return months.get(0).getRadius();
	}
	
	public void update(){
		for(int a = 0; a < months.size(); a++){
			months.get(a).setAlpha(MainView.OPACITY_FULL);
		}
		ArrayList<YearMonth> dates = HaikuGenerator.getDates();
		for(int i = 0; i < dates.size(); i++){
			if(dates.get(i).getYear() == yearSelected){
				for(int a = 0; a < months.size(); a++){
					if(dates.get(i).getMonth() == months.get(a).getMonth()){
						months.get(a).setAlpha(MainView.OPACITY_USED_DATE);
					}
				}
			}
		}
	}
	
	public int getSelectedYear(){
		return yearSelected;
	}
	
	public void openDateView(){
		dateViewClosed = !dateViewClosed;
		MainView.getInstance().addViewElement(MainView.VIEW_SHOWN_DATE);
		yearView.setText("" + yearSelected);
		Animation a = yearView.changeSizeTo(2, MainView.ANIMATION_TIME_DATE);
	    yearView.startAnimation(a);
	    a.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {}
			
			@Override
			public void onAnimationRepeat(Animation animation) {}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				
			}
		});
	}

	public void closeDateView(){
		dateViewClosed = !dateViewClosed;
		MainView.getInstance().removeViewElement(MainView.VIEW_SHOWN_DATE);
		Animation a = yearView.changeSizeTo(0.5, MainView.ANIMATION_TIME_DATE);
	    yearView.startAnimation(a);
	    a.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {}
			
			@Override
			public void onAnimationRepeat(Animation animation) {}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				yearView.setText("Time");
			}
		});
	}
	
	public QuarterCircle getYearView(){
		return yearView;
	}
	
	public ArrayList<QuarterCircle> getMonths(){
		return months;
	}

	private int startX;
	private int startY;
	
	private int scrollXOld;
	private int scrollYOld;
	
	private int scrollXNew;
	private int scrollYNew;
	
	private double startTime;
	
	private QuarterCircle pressedDownOn;
	
	private boolean isScrolling = false;
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
//		int windowHeight = HaikuActivity.getInstance().getWindowHeight();
//		int dateViewHeight = getHeight();
//		int offset = windowHeight - dateViewHeight;
		
		int eventX = (int) event.getX();
//		int eventY = (int) event.getY() + offset;
		int eventY = (int) event.getY();
		if(event.getAction() == MotionEvent.ACTION_DOWN){
			Log.i("TAG", "ACTION_DOWN");
			isScrolling = false;
			startX = eventX;
			startY = eventY;
			startTime = System.currentTimeMillis();
			pressedDownOn = null;
			for(int i = 0; i < months.size(); i++){
				if(months.get(i).isPosInView(startX, startY)){
					Log.i("TAG", months.get(i).getText() + " pressed");
					pressedDownOn = months.get(i);
					return true;
				}
			}
			pressedDownOn = yearView;
			return true;
//			if(v.equals(yearView)){
//				Log.i("TAG", "yearView pressed");
//				pressedDownOn = yearView;
//				return false;
//			}
		}
		if(event.getAction() == MotionEvent.ACTION_UP){
			Log.i("TAG", "ACTION_UP");
			if(MainView.CLICK_TIME > System.currentTimeMillis()-startTime){
				Log.i("TAG", "dateView clicked");
			}
			return false;
		}
		if(event.getAction() == MotionEvent.ACTION_MOVE){
//			Log.i("TAG", "ACTION_MOVE");
			if(isScrolling){
				// check that it is still in bounds
				boolean inBounds = false;
				for(int i = 0; i < months.size(); i++){
					if(months.get(i).isPosInView(eventX, eventY)){
						inBounds = true;;
					}
				}
				if(!inBounds){
					return false;
				}
				
				scrollXOld = scrollXNew;
				scrollYOld = scrollYNew;
				
				scrollXNew = eventX;
				scrollYNew = ((QuarterCircle)pressedDownOn).getRadius() - eventY;
				
				int oldAngle = (int) Math.acos(scrollXOld/Math.sqrt(scrollXOld*scrollXOld + scrollYOld*scrollYOld));
				int newAngle = (int) Math.acos(scrollXNew/Math.sqrt(scrollXNew*scrollXNew + scrollYNew*scrollYNew));
				
				int angleChange = newAngle-oldAngle;
				
				Log.i("TAG", "eventOld: (" + scrollXOld +", " + scrollYOld +"), eventNew: (" + scrollXNew + ", " + scrollYNew + ")");
				Log.i("TAG", "Old: " + oldAngle + ", new: " + newAngle +", change: " + angleChange);
				
				for(int i = 0; i < months.size(); i++){
					months.get(i).changeAngle(angleChange);
				}
				
				requestLayout();
				return true;
				
			}
			// Scroll start or drag
			
			double distance = Math.sqrt((eventX-startX)*(eventX-startX)+(eventY-startY)*(eventY-startY));
			if(pressedDownOn == yearView){
				if(distance > MainView.MOVE_TO_DRAG_RANGE){
					// The angle doesn't matter here
					Log.i("TAG", "Start touch drag " + ((QuarterCircle)pressedDownOn).getText());
					pressedDownOn.setAlpha(MainView.OPACITY_USED_DATE);
					MainView.getInstance().setDraggedView(pressedDownOn);
					v.startDrag(null, new DragShadowBuilder(pressedDownOn), null, 0);
					return true;
				}
			}
			else if(pressedDownOn instanceof QuarterCircle){
				// a month
				int startYTurned = ((QuarterCircle)pressedDownOn).getRadius() - startY; // easier to think this way
				int eventYTurned = ((QuarterCircle)pressedDownOn).getRadius() - eventY;
				if(distance > MainView.MOVE_TO_DRAG_RANGE){
					// Drag if the angle is correct
					if(-((QuarterCircle)pressedDownOn).getStartAngle() < Math.acos(Math.abs(eventX - startX)
									/Math.sqrt((eventX - startX) * (eventX - startX)
									+ (eventYTurned - startYTurned) * (eventYTurned - startYTurned)))
									
							&& -((QuarterCircle)pressedDownOn).getEndAngle() > Math.acos(Math.abs(eventX - startX)
									/Math.sqrt((eventX - startX) * (eventX - startX)
									+ (eventYTurned - startYTurned) * (eventYTurned - startYTurned)))){
						Log.i("TAG", "Start touch drag " + ((QuarterCircle)pressedDownOn).getText());
						pressedDownOn.setAlpha(MainView.OPACITY_USED_DATE);
						MainView.getInstance().setDraggedView(pressedDownOn);
						v.startDrag(null, new DragShadowBuilder(pressedDownOn), null, 0);
						return true;
					}
				}
				// Scroll start
				Log.i("TAG", "Start scroll!");
				isScrolling = true;
				scrollXNew = eventX;
				scrollYNew = eventYTurned;
			}
		}
		return false;
	}

	@Override
	public boolean onLongClick(View v) {
		Log.i("TAG", "OnLongClick, " + (pressedDownOn==null));
		if(pressedDownOn == null || isScrolling){
			return false;
		}
		if(pressedDownOn.getAlpha() == MainView.OPACITY_USED_DATE){
			// The view is already in the bin
			return false;
		}
		Log.i("TAG", "Start drag " + ((QuarterCircle)pressedDownOn).getText());
		pressedDownOn.setAlpha(MainView.OPACITY_USED_DATE);
		MainView.getInstance().setDraggedView(pressedDownOn);
		DragShadowBuilder shadow = new DragShadowBuilder(pressedDownOn);// TODO funkar inte
		shadow.onProvideShadowMetrics(new Point(pressedDownOn.getRadius(), pressedDownOn.getRadius()), new Point((int)(pressedDownOn.getMiddleTop().getXPos()+pressedDownOn.getMiddleBottom().getXPos())/2, (int)(pressedDownOn.getMiddleTop().getYPos()+pressedDownOn.getMiddleBottom().getYPos())/2));
		pressedDownOn.startDrag(null, shadow, null, 0);
		return false;
	}
}
