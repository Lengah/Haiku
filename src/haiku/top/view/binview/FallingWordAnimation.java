package haiku.top.view.binview;

import java.util.ArrayList;

import android.util.Log;
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
	
	private FallingWordAnimation previousAnimation = null;
	private FallingWordAnimation nextAnimation = null;
	
	public FallingWordAnimation(TextView movingView, BinSMSRowWord word, int rows){
		this.movingView = movingView;
		this.word = word;
		this.rows = rows;
		updateAnimation();
	}
	
	private FallingWordAnimation(TextView movingView, BinSMSRowWord word, int rows, FallingWordAnimation previousAnimation){
		this.movingView = movingView;
		this.word = word;
		this.rows = rows;
		this.previousAnimation = previousAnimation;
		setMovingViewGone();
		updateAnimation();
	}
	
	public void addRows(int rows, BinSMSRowWord word, TextView movingView){
		if(started){
			if(nextAnimation != null){
				nextAnimation.addRows(rows, word, movingView);
			}
			else{
				// create a new animation
				nextAnimation = new FallingWordAnimation(movingView, word, rows, this);
			}
			return;
		}
		this.rows += rows;
		this.word = word;
		this.movingView = movingView;
		updateAnimation();
	}
	
	private void updateAnimation(){
		setWordViewGone();
		animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF,0,Animation.RELATIVE_TO_SELF,0,
				Animation.RELATIVE_TO_SELF, (float)-rows, Animation.RELATIVE_TO_SELF, 0);
		animation.setDuration((long) (TIME_TO_FALL_ONE_ROW*rows));
	}
	
	
	private void finish(){
		finished = true;
		if(previousAnimation != null){
			previousAnimation.finish();
		}
	}
	
	private static int sizeY = BinView.getInstance().getHeightOfText();
	private static int height = BinView.getInstance().getBinCombinedSMSView().getHeightOfRow();
	
	public void start(){
		if(started){
			return;
		}
		started = true;
		int startRow = word.getRow().getRowIndex()-rows+1;
		int endRow = getLastAnimation().getWord().getRow().getRowIndex() + 1;
		int scrollY = BinView.getInstance().getTextScroll().getScrollY();
		
		
		if(endRow*height < scrollY || startRow*height > scrollY+sizeY){
			// is not in view now and will not be animated to fall into view -> don't animate it
			setWordViewVisible();
			finish();
			return;
		}
		setMovingViewVisible();
		
		animation.setAnimationListener(new AnimationListener() {
			public void onAnimationStart(Animation animation) {
			}
			public void onAnimationRepeat(Animation animation) {
			}
			public void onAnimationEnd(Animation animation) {
				if(nextAnimation != null && !finished){
					nextAnimation.start();
					setMovingViewGone();
				}
				else{
					setWordViewVisible();
					finish();
				}
			}
		});
		movingView.startAnimation(animation);
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
	
	public ArrayList<TextView> getMovingViews(){
		ArrayList<TextView> list = new ArrayList<TextView>();
		list.add(movingView);
		if(nextAnimation != null){
			list.addAll(nextAnimation.getMovingViews());
		}
		return list;
	}
	
	public TextView getMovingView(){
		return movingView;
	}
	
	public int getAllRows(){
		if(nextAnimation != null){
			return rows + nextAnimation.getAllRows();
		}
		return rows;
	}
	
	public FallingWordAnimation getLastAnimation(){
		if(nextAnimation != null){
			return nextAnimation.getLastAnimation();
		}
		return this;
	}
	
	public int getRows(){
		return (int) rows;
	}
	
	public void wordDeleted(){
		setMovingViewGone();
		finished = true;
		if(nextAnimation != null){
			nextAnimation.wordDeleted();
		}
	}
	
	private void setMovingViewGone(){
		movingView.setText(""); // changing the visibility to GONE does not work!
	}
	
	private void setMovingViewVisible(){
		movingView.setText(word.getWord()); // changing the visibility to VISIBLE does not work!
	}
	
	private void setWordViewGone(){
		word.setText("");
	}
	
	private void setWordViewVisible(){
		word.setText(word.getWord());
	}
	
	public void updateTextColor(int color){
		movingView.setTextColor(color);
		if(nextAnimation != null){
			nextAnimation.updateTextColor(color);
		}
	}
}
