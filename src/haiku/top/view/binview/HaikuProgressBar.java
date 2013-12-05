package haiku.top.view.binview;

import haiku.top.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;
import android.view.View;

public class HaikuProgressBar extends View{
	private float progress = 0;
	private float maxProgress = 100;
	// min progress is always 0
	
	private Bitmap sliderImage;
	private Bitmap dotImage;
	
	private float sliderWidth;
	private float sliderHeight;
	private float sliderRadius;
	private float maxAngle;
	
	private float drawOffset;
	private float xPos;
	private float xPosOffset;
	
	public HaikuProgressBar(Context context, int dotDim, int sliderWidth, int sliderHeight) {
		super(context);
		sliderImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.slider_line);
		dotImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.slider_dot);
		
		dotImage = Bitmap.createScaledBitmap(dotImage, dotDim, dotDim, false);
		drawOffset = dotImage.getWidth()/2; // dotImage.getWidth()/2 so the dotImage is centered when its left is at 0
		
		sliderImage = Bitmap.createScaledBitmap(sliderImage, sliderWidth, sliderHeight, false);
		this.sliderWidth = sliderImage.getWidth();
		this.sliderHeight = sliderImage.getHeight();
		
		float tempWidth = (float) ((1-4.0/17.0)*this.sliderWidth); // the circle width does not start at x=0, but more like 4/17 parts into the image
		xPosOffset = this.sliderWidth - tempWidth;
		// The radius and an angle are needed to place the dots in the right X position.
		
		// Calculate the radius of the arc
		// Radius = W/2 + H^2/8W
		sliderRadius = tempWidth/2 + this.sliderHeight*this.sliderHeight/(8*tempWidth);
		
		// Calculate the max angle
		// The angle is the angle from the middle to the bottom/top (same angle).
		// for example when the progress is at max (dots are in the middle), the angle is 0 and when the progress is at min (dots are at the top and bottom) the angle is at its max
		float h = (float) Math.sqrt((sliderRadius-tempWidth)*(sliderRadius-tempWidth) + (this.sliderHeight/2)*(this.sliderHeight/2)); // hypotenusan
		maxAngle = (float) Math.asin((this.sliderHeight/2)/h);
		updateXpos();
	}
	
	@Override
    protected void onDraw(Canvas canvas) {
		canvas.drawBitmap(sliderImage, drawOffset, drawOffset, null); 
		canvas.drawBitmap(dotImage, xPos, (sliderHeight/2)*(progress/maxProgress), null);
		canvas.drawBitmap(dotImage, xPos, (sliderHeight - (sliderHeight/2)*(progress/maxProgress)), null);
	}

	public void changeProgress(int progress){
		this.progress += progress;
		BinView.getInstance().updateBinOpacity();
		updateXpos();
		invalidate();
	}
	
	public void setProgress(int progress){
		this.progress = progress;
		if(progress > maxProgress){
			progress = (int) maxProgress;
			BinView.getInstance().haikuReady();
		}
		BinView.getInstance().updateBinOpacity();
		updateXpos();
		invalidate();
	}
	
	public void incProgress(){
		progress++;
		if(progress > maxProgress){
			progress = maxProgress;
			BinView.getInstance().haikuReady();
		}
		BinView.getInstance().updateBinOpacity();
		updateXpos();
		invalidate();
	}
	
	public void decProgress(){
		progress--;
		if(progress < 0){
			progress = 0;
		}
		BinView.getInstance().updateBinOpacity();
		updateXpos();
		invalidate();
	}
	
	public float getProgress(){
		return progress;
	}
	
	private void updateXpos(){
		xPos = (float) (sliderRadius - sliderRadius * Math.cos((1 - progress/maxProgress) * maxAngle)) + xPosOffset;
	}
	
	public void setMaxProgress(int maxProgress){
		this.maxProgress = maxProgress;
	}
	
	public float getMaxProgress(){
		return maxProgress;
	}
	
	public void resetProgress(){
		progress = 0;
		BinView.getInstance().updateBinOpacity();
		updateXpos();
		invalidate();
	}
}
