package haiku.top.view;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
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

public class DateView extends RelativeLayout implements OnTouchListener, OnClickListener{
	private QuarterCircle yearView;
	
	private ArrayList<QuarterCircle> months = new ArrayList<QuarterCircle>();
	private String[] monthsName = new String[] 
			{"January", "February", "Mars", "April", "May", "June", "Juli", "August", "September", "October", "November", "December"};
	
	public static final int MONTHS_SHOWN = 4;
	private boolean dateViewClosed = true;
	private int yearSelected = 2013;
	
	public DateView(Context context) {
		super(context);
		yearView = new QuarterCircle(context, 100);
		yearView.setText("Time");
		LayoutParams params1 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		params1.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		int sizeOfMonth = 90/MONTHS_SHOWN;
		for(int i = 0; i < monthsName.length; i++){
			Log.i("TAG", "Start: " + (-90 + sizeOfMonth*i + sizeOfMonth) + ", End: " + (-90 + sizeOfMonth*i));
			months.add(new QuarterCircle(context, monthsName[i], 180, -90 + sizeOfMonth*i + sizeOfMonth, -90 + sizeOfMonth*i, yearView.getRadius()));
//			months.get(i).setVisibility(GONE);
			months.get(i).setLayoutParams(params1);
			addView(months.get(i));
			months.get(i).setOnTouchListener(this);
			months.get(i).setOnClickListener(this);
		}
		params1 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		params1.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		yearView.setLayoutParams(params1);
		addView(yearView);
		yearView.bringToFront();
//		yearView.setVisibility(GONE);
		yearView.setOnTouchListener(this);
		yearView.setOnClickListener(this);
		setOnTouchListener(this);
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

	int startX;
	int startY;
	
	
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if(event.getAction() == MotionEvent.ACTION_DOWN){
			startX = (int) event.getX();
			startY = (int) event.getY();
			if(v.equals(yearView)){
				Log.i("TAG", "yearView pressed");
				return false;
			}
			else{
				Log.i("TAG", "X: " + startX + ", Y: " + startY);
				Position pos = new Position(startX, startY);
				for(int i = 0; i < months.size(); i++){
					if(months.get(i).isPosInView(pos)){
						Log.i("TAG", months.get(i).getText() + " pressed");
						return false;
					}
				}
			}
		}
		if(event.getAction() == MotionEvent.ACTION_MOVE){
			// Scroll or drag
		}
//		if(dateViewClosed){
//			openDateView();
//		}
//		else{
//			closeDateView();
//		}
		return false;
	}

	@Override
	public void onClick(View v) {
		
	}
}
