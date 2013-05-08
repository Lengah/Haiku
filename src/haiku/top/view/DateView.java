package haiku.top.view;

// OnLongClick and onTouch doesn't seem to work at the same time

import haiku.top.HaikuActivity;
import haiku.top.model.HaikuGenerator;
import haiku.top.model.Month;
import haiku.top.model.YearMonth;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Point;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.view.animation.Animation.AnimationListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

public class DateView extends RelativeLayout implements OnTouchListener, OnClickListener{//, OnLongClickListener{
	private Context context;
	private QuarterCircle yearView;
	
	private ArrayList<QuarterCircle> months = new ArrayList<QuarterCircle>();
	public static final Month[] MONTHS_NAME = new Month[] 
			{Month.January, Month.February, Month.Mars, Month.April, Month.May, Month.June, Month.Juli, Month.August, Month.September, Month.October, Month.November, Month.December};
	
	public static final int SIZE_OF_MONTH = 25;
	public static final int ANIMATION_NEW_YEAR_DURATION = 500;
	public static final int YEAR_VIEW_FULL_SIZE = 100;
	public static final int SIZE_FACTOR = 2;
	public static final int DEGREES_TO_NEW_YEAR = 30;
	private boolean dateViewClosed = true;
	private int yearSelected = 2013;
	
	public DateView(Context context) {
		super(context);
		this.context = context;
		yearView = new QuarterCircle(context, YEAR_VIEW_FULL_SIZE/SIZE_FACTOR);
		yearView.setText("Time");
		LayoutParams params1 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		params1.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

		addView(yearView);
		for(int i = 0; i < MONTHS_NAME.length; i++){
//			Log.i("TAG", "Start: " + (-90 + SIZE_OF_MONTH*i + SIZE_OF_MONTH) + ", End: " + (-90 + SIZE_OF_MONTH*i));
			months.add(new QuarterCircle(context, MONTHS_NAME[i], 180, -90 + SIZE_OF_MONTH*i + SIZE_OF_MONTH, -90 + SIZE_OF_MONTH*i, yearView.getRadius()*SIZE_FACTOR));
			months.get(i).setVisibility(GONE);
			months.get(i).setLayoutParams(params1);
			addView(months.get(i));
//			months.get(i).setOnTouchListener(this);
//			months.get(i).setOnLongClickListener(this);
		}
		params1 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		params1.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		yearView.setLayoutParams(params1);
		
//		yearView.bringToFront();
		yearView.setYearView(true);
//		yearView.setVisibility(GONE);
//		yearView.setOnTouchListener(this);
//		setOnTouchListener(this);
		yearView.setOnClickListener(this);
		months.get(0).setOnTouchListener(this);
//		months.get(0).setOnLongClickListener(this);
		update();
//		closeDateView();
	}
	
	public int getRadius(){
		return months.get(0).getRadius();
	}
	
	public void updateMonthWithIndex(int index){
		if(HaikuGenerator.getDates().contains(new YearMonth(yearSelected, months.get(index).getMonth()))){
			months.get(index).setAlpha(MainView.OPACITY_USED_DATE);
		}
		else{
			months.get(index).setAlpha(MainView.OPACITY_FULL);
		}
	}
	
	public void update(){
//		for(int a = 0; a < months.size(); a++){
//			months.get(a).setAlpha(MainView.OPACITY_FULL);
//		}
		ArrayList<YearMonth> dates = HaikuGenerator.getDates();
		YearMonth ym;
		for(int i = 0; i < months.size(); i++){
			ym = new YearMonth(yearSelected, months.get(i).getMonth());
			if(dates.contains(ym)){
				months.get(i).setAlpha(MainView.OPACITY_USED_DATE);
			}
			else{
				months.get(i).setAlpha(MainView.OPACITY_FULL);
			}
		}
//		int counter = 0;
//		for(int i = 0; i < dates.size(); i++){
//			if(dates.get(i).getYear() == yearSelected){
//				for(int a = 0; a < months.size(); a++){
//					if(dates.get(i).getMonth() == months.get(a).getMonth()){
//						months.get(a).setAlpha(MainView.OPACITY_USED_DATE);
//						counter++;
//					}
//				}
//			}
//		}
//		if(counter == months.size()){
//			// All used
////			yearView.setAlpha(MainView.OPACITY_USED_DATE);
//			yearView.setAlpha(MainView.OPACITY_FULL);
//		}
//		else{
//			yearView.setAlpha(MainView.OPACITY_FULL);
//		}
	}
	
	public int getSelectedYear(){
		return yearSelected;
	}
	
	private Animation a;
	private boolean animating = false;
	private boolean opened = false;
	
	public void openDateView(){
		if(!MainView.getInstance().isBinViewClosed()){
			MainView.getInstance().closeBinView();
			return;
		}
		MainView.getInstance().dateViewOpened();
		if(animating || opened){
			return;
		}
		opened = true;
		animating = true;
		dateViewClosed = !dateViewClosed;
		MainView.getInstance().addViewElement(MainView.VIEW_SHOWN_DATE);
		yearView.setText("" + yearSelected);
		a = yearView.changeSizeTo(SIZE_FACTOR, MainView.ANIMATION_TIME_DATE);
	    yearView.startAnimation(a);
	    a.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {}
			
			@Override
			public void onAnimationRepeat(Animation animation) {}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				for(int i = 0; i < months.size(); i++){
					months.get(i).setVisibility(VISIBLE);
				}
				animating = false;
			}
		});
	}

	public void closeDateView(){
		isScrolling = false;
		if(animating || !opened){
			return;
		}
		opened = false;
		animating = true;
		isScrolling = false;
		for(int i = 0; i < months.size(); i++){
			months.get(i).setVisibility(GONE);
		}
		dateViewClosed = !dateViewClosed;
		MainView.getInstance().removeViewElement(MainView.VIEW_SHOWN_DATE);
		a = yearView.changeSizeTo(1.0/SIZE_FACTOR, MainView.ANIMATION_TIME_DATE);
	    yearView.startAnimation(a);
	    a.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {}
			
			@Override
			public void onAnimationRepeat(Animation animation) {}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				yearView.setText("Time");
				animating = false;
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
		if(scrolling){
			return false;
		}
//		int windowHeight = HaikuActivity.getInstance().getWindowHeight();
//		int dateViewHeight = getHeight();
//		int offset = windowHeight - dateViewHeight;
		
		int eventX = (int) event.getX();
//		int eventY = (int) event.getY() + offset;
		int eventY = (int) event.getY();
		if(event.getAction() == MotionEvent.ACTION_DOWN){
//			Log.i("TAG", "ACTION_DOWN");
			isScrolling = false;
			startX = eventX;
			startY = eventY;
			startTime = System.currentTimeMillis();
			pressedDownOn = null;
			for(int i = 0; i < months.size(); i++){
				if(months.get(i).isPosInView(startX, startY)){
//					Log.i("TAG", months.get(i).getText() + " pressed");
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
//			Log.i("TAG", "ACTION_UP");
			if(!isScrolling && MainView.CLICK_TIME > System.currentTimeMillis()-startTime){
//				Log.i("TAG", "dateView clicked");
				if(dateViewClosed){
					openDateView();
				}
				else{
					closeDateView();
				}
			}
			else{
				int angleChange = 0;
				if(months.get(0).getEndAngle() < 0 && months.get(0).getEndAngle() > -90){
					angleChange = -90 - months.get(0).getEndAngle();
				}
				else if(months.get(months.size()-1).getStartAngle() > -90
						&& months.get(months.size()-1).getStartAngle() < 0){
					angleChange = 0 - months.get(months.size()-1).getStartAngle();
				}
				if(angleChange != 0){
					for(int i = 0; i < months.size(); i++){
						months.get(i).changeAngle(angleChange);
					}
				}
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
				
				int oldAngle = (int) (Math.acos(scrollXOld/Math.sqrt(scrollXOld*scrollXOld + scrollYOld*scrollYOld))*180/Math.PI);
				int newAngle = (int) (Math.acos(scrollXNew/Math.sqrt(scrollXNew*scrollXNew + scrollYNew*scrollYNew))*180/Math.PI);
				
				int angleChange = oldAngle-newAngle;
				
//				Log.i("TAG", "eventOld: (" + scrollXOld +", " + scrollYOld +"), eventNew: (" + scrollXNew + ", " + scrollYNew + ")");
//				Log.i("TAG", "Old: " + oldAngle + ", new: " + newAngle +", change: " + angleChange);
				
				if(angleChange == 0){
					return true;
				}

				for(int i = 0; i < months.size(); i++){
					months.get(i).changeAngle(angleChange);
				}
				
				if(months.get(0).getEndAngle() < 0 && months.get(0).getEndAngle() >= -90+DEGREES_TO_NEW_YEAR){
					// previous year
					yearSelected--;
					yearView.setText("" + yearSelected);
					isScrolling = false;
					animateScroll(-months.get(months.size()-1).getStartAngle());
				}
				else if(months.get(months.size()-1).getStartAngle() > -90
						&& months.get(months.size()-1).getStartAngle() < -DEGREES_TO_NEW_YEAR){
					// next year
					yearSelected++;
					yearView.setText("" + yearSelected);
					isScrolling = false;
					animateScroll(-(months.get(0).getEndAngle()+90));
				}
				return true;
			}
			// Scroll start or drag
			
			double distance = Math.sqrt((eventX-startX)*(eventX-startX)+(eventY-startY)*(eventY-startY));
			if(pressedDownOn == yearView){
				if(distance > MainView.MOVE_TO_DRAG_RANGE && pressedDownOn.getAlpha() != MainView.OPACITY_USED_DATE){
					// The angle doesn't matter here
//					Log.i("TAG", "Start touch drag " + ((QuarterCircle)pressedDownOn).getText());
					MainView.getInstance().setDraggedView(pressedDownOn);
					v.startDrag(null, new DragShadowBuilder(pressedDownOn), null, 0);
					return true;
				}
			}
			else if(pressedDownOn instanceof QuarterCircle){
				// a month
				if(distance > MainView.MOVE_TO_DRAG_RANGE){
//					Log.i("TAG", "Drag range!");
					int startYTurned = ((QuarterCircle)pressedDownOn).getRadius() - startY; // easier to think this way ( (0, 0) is now in the bottom left instead of the top left)
					int eventYTurned = ((QuarterCircle)pressedDownOn).getRadius() - eventY;
					
					// Drag if the angle is correct
					// The angle is correct if the drag is heading out of the view. It is not correct if it will pass through another month.
					// Examples:
					// If the targeted month is at the top, a correct drag is straight up.
					// If the targeted month is to the right, a correct drag is to the right.
					
					// max angle is calculated from the month's left top position and the action down position.
					int maxAngle = (int) (Math.acos(Math.abs(pressedDownOn.getLeftTop().getXPos()-startX)
							/Math.sqrt((pressedDownOn.getLeftTop().getXPos()-startX)*(pressedDownOn.getLeftTop().getXPos()-startX)
							+ (pressedDownOn.getLeftTop().getYPos()-startYTurned)*(pressedDownOn.getLeftTop().getYPos()-startYTurned)))*180/Math.PI);
					
					// min angle is calculated from the month's right top position and the action down position.
					int minAngle = (int) (Math.acos(Math.abs(pressedDownOn.getRightTop().getXPos()-startX)
							/Math.sqrt((pressedDownOn.getRightTop().getXPos()-startX)*(pressedDownOn.getRightTop().getXPos()-startX)
							+ (pressedDownOn.getRightTop().getYPos()-startYTurned)*(pressedDownOn.getRightTop().getYPos()-startYTurned)))*180/Math.PI);
					
					// the angle is calculated from the action down position and the current position
					int angle = (int) (Math.acos(Math.abs(eventX-startX)
							/Math.sqrt((eventX-startX)*(eventX-startX)
							+ (eventYTurned-startYTurned)*(eventYTurned-startYTurned)))*180/Math.PI);
					
//					Log.i("TAG", "1: Angle: " + angle + ", Min: " + minAngle + ", Max: " + maxAngle);
					
					if(pressedDownOn.getLeftTop().getXPos() < startX){
						// maxAngle is bigger than 90 degrees. Mirror it on the y-axis
						maxAngle = maxAngle + 2*(90-maxAngle); // (90-maxAngle) is what's left to 90 degrees.
					}
					
					if(pressedDownOn.getRightTop().getYPos() < startYTurned){
						// minAngle is smaller than 0 degrees. Mirror it on the x-axis
						minAngle = -minAngle;
					}
					
					if(eventX < startX){
						// |angle| is bigger than 90 degrees. Mirror it on the y-axis
						angle = angle + 2*(90-angle); // (90-angle) is what's left to 90 degrees.
					}
					if(eventYTurned < startYTurned){
						// angle is smaller than 0 degrees. Mirror it on the x-axis
						angle = -angle;
					}
					
//					Log.i("TAG", "2: Angle: " + angle + ", Min: " + minAngle + ", Max: " + maxAngle);
					if(!(pressedDownOn.getLeftTop().getYPos() < startYTurned // annoying to calculate and probably doesn't matter
							|| pressedDownOn.getRightTop().getXPos() < startX
							|| (eventX < startX && eventYTurned < startYTurned))){
//						Log.i("TAG", "In range?");
						if(minAngle < angle	&& maxAngle > angle){
//							Log.i("TAG", "Start touch drag " + ((QuarterCircle)pressedDownOn).getText());
							// if the whole month isn't shown, scroll so it is
							int angleChange = 0;
							if(pressedDownOn.getStartAngle() > 0){
								angleChange = -pressedDownOn.getStartAngle();
							}
							if(pressedDownOn.getEndAngle() < -90){
								angleChange = -90-pressedDownOn.getEndAngle();
							}
							if(angleChange != 0){
//								animateScroll(angleChange);
								for(int i = 0; i < months.size(); i++){
									months.get(i).changeAngle(angleChange);
								}
							}
							pressedDownOn.setAlpha(MainView.OPACITY_USED_DATE);
							dragMonth(pressedDownOn);
//							v.startDrag(null, new DragShadowBuilder(pressedDownOn), null, 0);
							return true;
						}
					}
					
					// Scroll start
//					Log.i("TAG", "Start scroll!");
					isScrolling = true;
					scrollXNew = eventX;
					scrollYNew = eventYTurned;
				}
			}
		}
		return false;
	}
	
	private static int scrolledSoFar = 0;
	private boolean scrolling = false;
	private boolean hasUpdated = false;
	
	public void animateScroll(final int angleChange){
//		Log.i("TAG", "ANIMATE SCROLL!! " + angleChange);
		scrolledSoFar = 0;
		scrolling = true;
		hasUpdated = false;
		a = new Animation(){
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
            	int changeAngle = (int) (angleChange*interpolatedTime) - scrolledSoFar;
//            	Log.i("TAG", "(int) (angleChange*interpolatedTime): " + (int) (angleChange*interpolatedTime));
//            	Log.i("TAG", "Angle: " + changeAngle + ", intertime: " + interpolatedTime + ", angleChange: " + angleChange + ", SoFar: "+ scrolledSoFar);
            	scrolledSoFar += changeAngle;
            	// If the months from the previous/next year are no longer shown, all months are updated.
            	if(!hasUpdated && angleChange < 0){
            		// previous year -> check december
            		if(months.get(months.size()-1).getStartAngle() < -90){
            			// Update all months
            			update();
            		}
            	}
            	if(!hasUpdated && angleChange > 0){
            		// next year -> check january
            		if(months.get(0).getEndAngle() > 0){
            			// Update all months
            			update();
            		}
            	}
    			for(int i = 0; i < months.size(); i++){
    				// If the month is getting into view it needs to be updated.
    				// All months can not be updated at the same time since months from different years are shown at the same time
    				if(!hasUpdated && angleChange < 0){
    					// previous year -> check endAngle
    					if(months.get(i).getEndAngle() > 0 && months.get(i).getEndAngle() + changeAngle <= 0){
    						// Just now coming into view
    						updateMonthWithIndex(i);
    					}
    				}
    				if(!hasUpdated && angleChange > 0){
    					// next year -> check startAngle
    					if(months.get(i).getStartAngle() < -90 && months.get(i).getStartAngle() + changeAngle >= -90){
    						// Just now coming into view
    						updateMonthWithIndex(i);
    					}
    				}
					months.get(i).changeAngle(changeAngle);
				}
            }

            @Override
            public boolean willChangeBounds() {
                return false;
            }
        };
        a.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
//				Log.i("TAG", "Animation start!"); //
//				for(int i = 0; i < months.size(); i++){
//					Log.i("TAG", months.get(i).getText() + "| Start: " + months.get(i).getStartAngle() + ", End: " + months.get(i).getEndAngle());
//				}
				scrolling = true;
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {}
			
			@Override
			public void onAnimationEnd(Animation animation) {
//				Log.i("TAG", "Animation end!");
//				for(int i = 0; i < months.size(); i++){
//					Log.i("TAG", months.get(i).getText() + "| Start: " + months.get(i).getStartAngle() + ", End: " + months.get(i).getEndAngle());
//				}
				scrolling = false;
			}
		});
        a.setDuration(ANIMATION_NEW_YEAR_DURATION);
        startAnimation(a);
	}

//	@Override
//	public boolean onLongClick(View v) {
//		Log.i("TAG", "OnLongClick, " + (pressedDownOn==null));
//		if(pressedDownOn == null || isScrolling){
//			return false;
//		}
//		if(pressedDownOn.getAlpha() == MainView.OPACITY_USED_DATE){
//			// The view is already in the bin
//			return false;
//		}
//		Log.i("TAG", "Start drag " + ((QuarterCircle)pressedDownOn).getText());
//		if(v.equals(yearView)){
//			MainView.getInstance().setDraggedView(pressedDownOn);
//			v.startDrag(null, new DragShadowBuilder(pressedDownOn), null, 0);
//		}
//		else{
//			pressedDownOn.setAlpha(MainView.OPACITY_USED_DATE);
//			dragMonth(pressedDownOn);
//		}
//		return false;
//	}
	
	public void dragMonth(final QuarterCircle v){
		MainView.getInstance().setDraggedView(v);
		DragShadowBuilder shadow = new DragShadowBuilder(v){
			
			@Override
			public void onProvideShadowMetrics(Point shadowSize, Point touchPoint){
				int xpos = (int)((v.getMiddleTop().getXPos() + v.getMiddleBottom().getXPos())/2);
				int ypos = (int)(((v.getRadius() - v.getMiddleTop().getYPos()) + (v.getRadius() - v.getMiddleBottom().getYPos()))/2);
				touchPoint.set(xpos, ypos);
				shadowSize.set(v.getRadius(), v.getRadius());
			}
		};
		v.startDrag(null, shadow, null, 0);
	}

	@Override
	public void onClick(View v) {
		if(isScrolling){
			return;
		}
		if(dateViewClosed){
			openDateView();
		}
		else{
			closeDateView();
		}
	}
}
