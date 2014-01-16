package haiku.top.view.binview;

import haiku.top.HaikuActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.widget.TextView;

public class FallingWordAnimation {
	private BinSMSRowWord word;
	private TranslateAnimation animation;
	private int rows;
	private TextView movingView;
	public static final int TIME_TO_FALL_ONE_ROW = 200;
	private boolean started = false;
	private boolean finished = false;
	
	public FallingWordAnimation(TextView movingView, BinSMSRowWord word, int rows){
		this.movingView = movingView;
		this.word = word;
		this.rows = rows;
		updateAnimation();
	}
	
	public void addRows(int rows, BinSMSRowWord word, TextView movingView){
		this.rows += rows;
		this.word = word;
		this.movingView = movingView;
		updateAnimation();
	}
	
	private void updateAnimation(){
		word.setVisibility(View.GONE);
		animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF,0,Animation.RELATIVE_TO_SELF,0,
				Animation.RELATIVE_TO_SELF, (float)-rows, Animation.RELATIVE_TO_SELF, 0);
		animation.setDuration(TIME_TO_FALL_ONE_ROW*rows);
	}
	
	public void start(){
		if(started){
			return;
		}
		started = true;
		movingView.startAnimation(animation);
		animation.setAnimationListener(new AnimationListener() {
			public void onAnimationStart(Animation animation) {
			}
			public void onAnimationRepeat(Animation animation) {
			}
			public void onAnimationEnd(Animation animation) {
				word.setVisibility(View.VISIBLE);
				movingView.setVisibility(View.GONE);
				finished = true;
			}
		});
	}
	
	public boolean isFinished(){
		return finished;
	}
	
	public BinSMSRowWord getWord(){
		return word;
	}
	
	public TextView getMovingView(){
		return movingView;
	}
}
