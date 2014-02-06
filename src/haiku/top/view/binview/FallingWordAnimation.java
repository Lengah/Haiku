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
	public static final int TIME_TO_FALL_ONE_ROW = 200; //200
	private boolean started = false;
	private boolean finished = false;
	
//	private double startTime;
//	private boolean updated = false;
	
//	private TranslateAnimation secondAnimation;
//	private BinSMSRowWord secondWord;
//	private int secondRows = 0;
//	private TextView secondMovingView;
	
	public FallingWordAnimation(TextView movingView, BinSMSRowWord word, int rows){
		this.movingView = movingView;
		this.word = word;
		this.rows = rows;
		updateAnimation();
	}
	
	public void addRows(int rows, BinSMSRowWord word, TextView movingView){
//		if(started){
//			// Continue the animation after this one has finished
//			secondRows += rows;
//			secondWord = word;
//			secondMovingView = movingView;
//			secondMovingView.setVisibility(View.GONE);
//			updateSecondAnimation();
//			return;
//		}
		this.rows += rows;
		this.word = word;
		this.movingView = movingView;
		updateAnimation();
	}
	
//	public void updateWhileRunning(int rows, BinSMSRowWord word, TextView movingView){
//		// rows is the extra rows it will fall
//		updated = true;
//		movingView.clearAnimation();
//		double timeFallen = System.currentTimeMillis()-startTime;
//		double rowsFallen = timeFallen/TIME_TO_FALL_ONE_ROW;
//		this.rows = rows + rowsFallen;
//		this.word = word;
//		this.movingView = movingView;
//		updateAnimation();
//		started = false;
//	}
	
	private void updateAnimation(){
		word.setVisibility(View.GONE);
		animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF,0,Animation.RELATIVE_TO_SELF,0,
				Animation.RELATIVE_TO_SELF, (float)-rows, Animation.RELATIVE_TO_SELF, 0);
		animation.setDuration((long) (TIME_TO_FALL_ONE_ROW*rows));
	}
	
//	private void updateSecondAnimation(){
//		secondWord.setVisibility(View.GONE);
//		secondAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF,0,Animation.RELATIVE_TO_SELF,0,
//				Animation.RELATIVE_TO_SELF, (float)-secondRows, Animation.RELATIVE_TO_SELF, 0);
//		secondAnimation.setDuration((long) (TIME_TO_FALL_ONE_ROW*secondRows));
//	}
//	
//	public void removed(){
//		finished = true;
//		movingView.clearAnimation();
////		movingView.setVisibility(View.GONE);
//	}
	
	public void start(){
		if(started){
			return;
		}
//		startTime = System.currentTimeMillis();
		started = true;
		movingView.setVisibility(View.VISIBLE);
		movingView.startAnimation(animation);
		animation.setAnimationListener(new AnimationListener() {
			public void onAnimationStart(Animation animation) {
			}
			public void onAnimationRepeat(Animation animation) {
			}
			public void onAnimationEnd(Animation animation) {
//				if(updated){
//					Log.i("TAG", "updated end");
//					updated = false;
//					movingView.setVisibility(View.GONE);
//				}
//				if(finished){ // canceled manually.
//					return;
//				}
//				if(secondAnimation != null){
//					BinView.getInstance().getBinCombinedSMSView().addAnimation(new FallingWordAnimation(secondMovingView, secondWord, secondRows));
//				}
//				else{
					word.setVisibility(View.VISIBLE);
//				}
				movingView.setVisibility(View.GONE);
				finished = true;
			}
		});
	}
	
	public boolean hasStarted(){
		return started;
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
	
	public int getRows(){
		return (int) rows;
	}
	
	public void updateTextColor(int color){
		movingView.setTextColor(color);
//		if(secondMovingView != null){ //TODO
//			secondMovingView.setTextColor(color);
//		}
	}
}
