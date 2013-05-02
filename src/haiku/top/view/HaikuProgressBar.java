package haiku.top.view;

import haiku.top.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.View;

public class HaikuProgressBar extends View{
	private int progress = 0;
	private int maxProgress = 1;
	// min progress is always 0
	
	private Bitmap sliderImage;
	private Bitmap dotImage;
	
	public HaikuProgressBar(Context context) {
		super(context);
		sliderImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.slider_line);
		dotImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.slider_dot);
	}
	
	@Override
    protected void onDraw(Canvas canvas) {
		canvas.drawBitmap(sliderImage, 0, 0, null);
		canvas.drawBitmap(dotImage, 0, (getHeight()/2)*(progress/maxProgress), null);
		canvas.drawBitmap(dotImage, 0, getHeight() - (getHeight()/2)*(progress/maxProgress), null);
	}

	public void changeProgress(int progress){
		this.progress += progress;
		invalidate();
	}
	
	public void setMaxProgress(int maxProgress){
		this.maxProgress = maxProgress;
	}
}
