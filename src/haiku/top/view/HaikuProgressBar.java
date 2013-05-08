package haiku.top.view;

import haiku.top.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;
import android.view.View;

public class HaikuProgressBar extends View{
	private float progress = 0;
	private float maxProgress = 2;
	// min progress is always 0
	
	private Bitmap sliderImage;
	private Bitmap dotImage;
	
	private float sliderWidth;
	private float sliderHeight;
	
	private float drawOffset;
	
	public HaikuProgressBar(Context context, int dotDim, int sliderWidth, int sliderHeight) {
		super(context);
		sliderImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.slider_line);
		dotImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.slider_dot);
		
		dotImage = Bitmap.createScaledBitmap(dotImage, dotDim, dotDim, false);
		drawOffset = dotImage.getWidth()/2; // dotImage.getWidth()/2 so the dotImage is centered when its left is at 0
		
		sliderImage = Bitmap.createScaledBitmap(sliderImage, sliderWidth, sliderHeight, false);
		this.sliderWidth = sliderImage.getWidth();
		this.sliderHeight = sliderImage.getHeight();
	}
	
	@Override
    protected void onDraw(Canvas canvas) {
		canvas.drawBitmap(sliderImage, drawOffset, drawOffset, null); 
		canvas.drawBitmap(dotImage, (float) (sliderWidth - (sliderWidth-sliderWidth/4)*(progress/maxProgress)), (sliderHeight/2)*(progress/maxProgress), null);
		canvas.drawBitmap(dotImage, (float) (sliderWidth - (sliderWidth-sliderWidth/4)*(progress/maxProgress)), (sliderHeight - (sliderHeight/2)*(progress/maxProgress)), null);
	}

	public void changeProgress(int progress){
		this.progress += progress;
		invalidate();
	}
	
	public void setMaxProgress(int maxProgress){
		this.maxProgress = maxProgress;
	}
}
