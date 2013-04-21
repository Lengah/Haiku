package haiku.top.view;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

public class DateView extends RelativeLayout implements OnTouchListener{
	private QuarterCircle yearView;
	
	private ArrayList<QuarterCircle> months = new ArrayList<QuarterCircle>();
	private String[] monthsName = new String[] 
			{"January", "Februari", "Mars", "April", "May", "June", "Juli", "August", "September", "October", "November", "December"};
	
	public static final int MONTHS_SHOWN = 4;
	boolean dateViewClosed = true;
	
	public DateView(Context context) {
		super(context);
		yearView = new QuarterCircle(context, 100);
		yearView.setText("Time");
		LayoutParams params1 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		params1.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		int sizeOfMonth = 90/MONTHS_SHOWN;
		for(int i = 0; i < 5; i++){
			Log.i("TAG", "Start: " + (-90 + sizeOfMonth*i + sizeOfMonth) + ", End: " + (-90 + sizeOfMonth*i));
			months.add(new QuarterCircle(context, monthsName[i], 180, -90 + sizeOfMonth*i + sizeOfMonth, -90 + sizeOfMonth*i, yearView.getRadius()));
//			months.get(i).setVisibility(GONE);
			months.get(i).setLayoutParams(params1);
			addView(months.get(i));
		}
		params1 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		params1.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		yearView.setLayoutParams(params1);
		addView(yearView);
		yearView.bringToFront();
//		yearView.setVisibility(GONE);
	}
	
	public void openDateView(){
		dateViewClosed = !dateViewClosed;
		MainView.getInstance().addViewElement(MainView.VIEW_SHOWN_DATE);
		yearView.setText("2013");
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

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		Log.i("TAG", "Click!");
		if(dateViewClosed){
			openDateView();
		}
		else{
			closeDateView();
		}
		return false;
	}
}
