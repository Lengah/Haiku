package haiku.top.view.date;

// OnLongClick and onTouch doesn't seem to work at the same time

import haiku.top.model.date.Month;
import haiku.top.model.date.YearMonth;
import haiku.top.model.date.YearMonthConvo;
import haiku.top.model.generator.HaikuGenerator;
import haiku.top.view.main.MainView;

import java.util.ArrayList;
import java.util.Calendar;

import android.content.Context;
import android.graphics.Point;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.view.animation.Animation.AnimationListener;
import android.widget.RelativeLayout;

public class DateView extends RelativeLayout implements OnTouchListener, OnClickListener{//, OnLongClickListener{
	private QuarterCircle yearView;
	
	private ArrayList<QuarterCircle> months = new ArrayList<QuarterCircle>();
	public static final Month[] MONTHS_NAME = new Month[] 
			{Month.January, Month.February, Month.March, Month.April, Month.May, Month.June, Month.July, Month.August, Month.September, Month.October, Month.November, Month.December};
	
	public static final Integer[] MONTHS_LENGTH = new Integer[] {
		31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31
	};
	
	public static final int SIZE_OF_MONTH = 25;
	public static final int ANIMATION_NEW_YEAR_DURATION = 500;
	public static final double YEAR_VIEW_FULL_SIZE = 130.0; // 100 Originally
	public static final double TIME_SMALL_SIZE = 90.0; // 50 Originally
	public static final int SIZE_FACTOR = 2;
//	public static final int TOTAL_SIZE = 210; // 180 Originally. The Months' size will be TOTAL_SIZE - YEAR_VIEW_FULL_SIZE
	public static final int DAY_SIZE = 7;
	//The size of the months will be TOTAL_SIZE - YEAR_VIEW_FULL_SIZE
	public static final int DEGREES_TO_NEW_YEAR = 30;
	private boolean dateViewClosed = true;
	private int yearSelected = getCurrentYear();
	
	public DateView(Context context) {
		super(context);
//		yearView = new QuarterCircle(context, YEAR_VIEW_FULL_SIZE/SIZE_FACTOR);
		yearView = new QuarterCircle(context, (int)TIME_SMALL_SIZE);
		yearView.setText("Time");
		LayoutParams params1;

		addView(yearView);
		for(int i = 0; i < MONTHS_NAME.length; i++){
//			//Log.i("TAG", "Start: " + (-90 + SIZE_OF_MONTH*i + SIZE_OF_MONTH) + ", End: " + (-90 + SIZE_OF_MONTH*i));
//			months.add(new QuarterCircle(context, MONTHS_NAME[i], 180, -90 + SIZE_OF_MONTH*i + SIZE_OF_MONTH, -90 + SIZE_OF_MONTH*i, yearView.getRadius()*SIZE_FACTOR));
//			months.add(new QuarterCircle(context, MONTHS_NAME[i], 210, -90 + SIZE_OF_MONTH*i + SIZE_OF_MONTH, -90 + SIZE_OF_MONTH*i, (int)YEAR_VIEW_FULL_SIZE*SIZE_FACTOR));
			months.add(new QuarterCircle(context, MONTHS_NAME[i], getDays(i) * DAY_SIZE, -90 + SIZE_OF_MONTH*i + SIZE_OF_MONTH, -90 + SIZE_OF_MONTH*i, (int)YEAR_VIEW_FULL_SIZE*SIZE_FACTOR));
			months.get(i).setVisibility(GONE);
			params1 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			params1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			params1.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
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
	
	private int getCurrentYear(){
		Calendar now = Calendar.getInstance();
		return now.get(Calendar.YEAR);
	}
	
	private int getDays(int monthIndex){
		return MONTHS_LENGTH[monthIndex];
	}
	
	public static int getBiggestMonthRadiusDP(){
		int radius = 0;
		for(Integer i : MONTHS_LENGTH){
			if(i > radius){
				radius = i;
			}
		}
		return radius*DAY_SIZE;
	}
	
	public int getRadius(){
		int radius = 0;
		for(QuarterCircle qc : months){
			if(qc.getRadius() > radius){
				radius = qc.getRadius();
			}
		}
		return radius;
	}
	
	public void updateMonthWithIndex(int index){
		if(HaikuGenerator.getDates().contains(new YearMonth(yearSelected, months.get(index).getMonth()))
				|| (MainView.getInstance().isShowingSMS() 
						&& HaikuGenerator.getDateConvos().contains(new YearMonthConvo(new YearMonth(yearSelected, months.get(index).getMonth()), MainView.getInstance().getSelectedConvoThreadID())))){
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
			if(dates.contains(ym)
					|| (MainView.getInstance().isShowingSMS() 
							&& HaikuGenerator.getDateConvos().contains(new YearMonthConvo(ym, MainView.getInstance().getSelectedConvoThreadID())))){
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
		update();
		opened = true;
		animating = true;
		dateViewClosed = !dateViewClosed;
		MainView.getInstance().addViewElement(MainView.VIEW_SHOWN_DATE);
		yearView.setText("" + yearSelected);
//		a = yearView.changeSizeTo(SIZE_FACTOR, MainView.ANIMATION_TIME_DATE); //TODO
		a = yearView.changeSizeTo(YEAR_VIEW_FULL_SIZE/TIME_SMALL_SIZE, MainView.ANIMATION_TIME_DATE);
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
//		a = yearView.changeSizeTo(1.0/SIZE_FACTOR, MainView.ANIMATION_TIME_DATE); // TODO
		a = yearView.changeSizeTo(TIME_SMALL_SIZE/YEAR_VIEW_FULL_SIZE, MainView.ANIMATION_TIME_DATE);
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
	
	// Fling
//	private static final int FLING_MIN_DEGREE = 30; // since this view is round it checks the distance in degrees, not in px
	private static final double FLING_MIN_SPEED = 50; // degrees/s
	private double oldTime;
	private double flingTime; // ms
	private double flingDistance; // degrees
	private double flingSpeed; // degrees/ms
	
	
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
			oldTime = System.currentTimeMillis();
			flingTime = 0;
			flingDistance = 0;
//			if(flinging){ // if the user presses down, the flinging will stop TODO fling code
//				a.cancel();
//			}
//			//Log.i("TAG", "ACTION_DOWN");
			isScrolling = false;
			startX = eventX;
			startY = eventY;
			startTime = System.currentTimeMillis();
			pressedDownOn = null;
			for(int i = 0; i < months.size(); i++){
				if(months.get(i).isPosInView(startX, startY)){
//					//Log.i("TAG", months.get(i).getText() + " pressed");
					pressedDownOn = months.get(i);
					return true;
				}
			}
			pressedDownOn = yearView;
			return true;
//			if(v.equals(yearView)){
//				//Log.i("TAG", "yearView pressed");
//				pressedDownOn = yearView;
//				return false;
//			}
		}
		if(event.getAction() == MotionEvent.ACTION_UP){
//			//Log.i("TAG", "ACTION_UP");
			if(!isScrolling && MainView.CLICK_TIME > System.currentTimeMillis()-startTime){
//				//Log.i("TAG", "dateView clicked");
				if(dateViewClosed){
					openDateView();
				}
				else{
					closeDateView();
				}
			}
//			else if(flingTime > 0){ // fling TODO fling code
//				animateFling();
//			}
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
//			//Log.i("TAG", "ACTION_MOVE");
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
				
//				//Log.i("TAG", "eventOld: (" + scrollXOld +", " + scrollYOld +"), eventNew: (" + scrollXNew + ", " + scrollYNew + ")");
//				//Log.i("TAG", "Old: " + oldAngle + ", new: " + newAngle +", change: " + angleChange);
				
				// Fling  TODO fling code
//				double timeDiff = System.currentTimeMillis() - oldTime;
//				// as soon as the speed is less than the minimum, then the fling is reset
////				//Log.i("TAG", "speed: " + Math.abs(((double)angleChange)/timeDiff)*1000);
//				if(Math.abs(((double)angleChange)/timeDiff*1000) < FLING_MIN_SPEED){
//					flingTime = 0;
//					flingDistance = 0;
//					oldTime = System.currentTimeMillis();
//				}
//				else{
//					flingTime += timeDiff;
//					double tempDistance = flingDistance;
//					flingDistance += angleChange;
//					if((tempDistance < 0 && tempDistance < flingDistance)
//							|| tempDistance > 0 && tempDistance > flingDistance){
//						// the user changed direction
//						flingTime = 0;
//						flingDistance = 0;
//						oldTime = System.currentTimeMillis();
//					}
//					// if the total speed is less than the minimum, the fling is reset
//					else if(Math.abs(flingDistance/flingTime*1000) < FLING_MIN_SPEED){
//						flingTime = 0;
//						flingDistance = 0;
//						oldTime = System.currentTimeMillis();
//					}
//				}
//				//Log.i("TAG", "fling time: " + flingTime);
				
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
//					//Log.i("TAG", "Start touch drag " + ((QuarterCircle)pressedDownOn).getText());
					MainView.getInstance().setDraggedView(pressedDownOn);
					v.startDrag(null, new DragShadowBuilder(pressedDownOn), null, 0);
					return true;
				}
			}
			else if(pressedDownOn instanceof QuarterCircle){
				// a month
				if(distance > MainView.MOVE_TO_DRAG_RANGE){
//					//Log.i("TAG", "Drag range!");
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
					
//					//Log.i("TAG", "1: Angle: " + angle + ", Min: " + minAngle + ", Max: " + maxAngle);
					
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
					
//					//Log.i("TAG", "2: Angle: " + angle + ", Min: " + minAngle + ", Max: " + maxAngle);
					if(!(pressedDownOn.getLeftTop().getYPos() < startYTurned // annoying to calculate and probably doesn't matter
							|| pressedDownOn.getRightTop().getXPos() < startX
							|| (eventX < startX && eventYTurned < startYTurned))){
//						//Log.i("TAG", "In range?");
						if(minAngle < angle	&& maxAngle > angle){
//							//Log.i("TAG", "Start touch drag " + ((QuarterCircle)pressedDownOn).getText());
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
							if(pressedDownOn.getAlpha() == MainView.OPACITY_USED_DATE){
								// Already in the bin! Can't drag!
								return false;
							}
							pressedDownOn.setAlpha(MainView.OPACITY_USED_DATE);
							dragMonth(pressedDownOn);
//							v.startDrag(null, new DragShadowBuilder(pressedDownOn), null, 0);
							return true;
						}
					}
					
					// Scroll start
//					//Log.i("TAG", "Start scroll!");
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
//		//Log.i("TAG", "ANIMATE SCROLL!! " + angleChange);
		scrolledSoFar = 0;
		scrolling = true;
		hasUpdated = false;
		a = new Animation(){
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
            	int changeAngle = (int) (angleChange*interpolatedTime) - scrolledSoFar;
//            	//Log.i("TAG", "(int) (angleChange*interpolatedTime): " + (int) (angleChange*interpolatedTime));
//            	//Log.i("TAG", "Angle: " + changeAngle + ", intertime: " + interpolatedTime + ", angleChange: " + angleChange + ", SoFar: "+ scrolledSoFar);
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
				scrolling = true;
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				scrolling = false;
			}
		});
        a.setDuration(ANIMATION_NEW_YEAR_DURATION);
        startAnimation(a);
	}
	
	//TODO fling code
//	private static final int MAX_FLING_DISTANCE = (int) (SIZE_OF_MONTH * (12.0 - 90.0/SIZE_OF_MONTH)); // from showing January to showing December
//	
//	// if the total angle is MAX_FLING_ANGLE or more and the total time is MAX_FLING_TIME ms or less, then it will fling as much as it can
//	private static final int MAX_FLING_ANGLE = 60;
//	private static final double MAX_FLING_TIME = 50;
//	
//	private static final double FLING_SPEED = 180; // degrees/s
//	
//	private boolean flinging = false;
//	
//	public void animateFling(){
//		int totalAngleChange = 0;
//		if(flingTime < MAX_FLING_TIME && flingDistance > MAX_FLING_ANGLE){
//			totalAngleChange = MAX_FLING_DISTANCE;
//		}
//		else{
//			totalAngleChange = (int) Math.abs(flingDistance*1000/flingTime);
//		}
//		int angleOffset;
//		if(flingDistance > 0){
//			// flinging clockwise
//			if(months.get(0).getEndAngle() > 0){
//				angleOffset = - 360 + months.get(0).getEndAngle() + 90;
//			}
//			else{
//				angleOffset = months.get(0).getEndAngle() + 90;
//			}
//			totalAngleChange = Math.min(totalAngleChange, MAX_FLING_DISTANCE+angleOffset);
//		}
//		else{
//			// flinging counter-clockwise
//			if(months.get(months.size()-1).getStartAngle() > 0){
//				angleOffset = months.get(months.size()-1).getStartAngle();
//			}
//			else{
//				angleOffset = 360 + months.get(months.size()-1).getStartAngle();
//			}	
//			totalAngleChange = Math.min(totalAngleChange, MAX_FLING_DISTANCE-angleOffset);
//			totalAngleChange = -totalAngleChange;
//		}
//		
//		final int angleChange = totalAngleChange;
//		long animationTime = Math.abs((long) (1/(FLING_SPEED/angleChange)*1000));
//		scrolledSoFar = 0;
//		flinging = true;
//		//Log.i("TAG", "AngleOffset: " + angleOffset);
//		//Log.i("TAG", "total time: " + flingTime + ", total distance: " + flingDistance);
//		//Log.i("TAG", "Fling! " + "distance: " + angleChange + ", time: " + animationTime);
//		//Log.i("TAG", " ");
//		a = new Animation(){
//            @Override
//            protected void applyTransformation(float interpolatedTime, Transformation t) {
//            	int changeAngle = (int) (angleChange*interpolatedTime) - scrolledSoFar;
//            	scrolledSoFar += changeAngle;
//    			for(int i = 0; i < months.size(); i++){
//					months.get(i).changeAngle(changeAngle);
//				}
//            }
//
//            @Override
//            public boolean willChangeBounds() {
//                return false;
//            }
//        };
//        a.setAnimationListener(new AnimationListener() {
//			
//			@Override
//			public void onAnimationStart(Animation animation) {
//				flinging = true;
//			}
//			
//			@Override
//			public void onAnimationRepeat(Animation animation) {}
//			
//			@Override
//			public void onAnimationEnd(Animation animation) {
//				flinging = false;
//			}
//		});
//        a.setDuration(animationTime);
//        startAnimation(a);
//	}

//	@Override
//	public boolean onLongClick(View v) {
//		//Log.i("TAG", "OnLongClick, " + (pressedDownOn==null));
//		if(pressedDownOn == null || isScrolling){
//			return false;
//		}
//		if(pressedDownOn.getAlpha() == MainView.OPACITY_USED_DATE){
//			// The view is already in the bin
//			return false;
//		}
//		//Log.i("TAG", "Start drag " + ((QuarterCircle)pressedDownOn).getText());
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
