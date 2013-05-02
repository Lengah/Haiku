package haiku.top.view;

import haiku.top.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;

public class HaikuProgressBar extends View{
	private int progress = 0;
	private int maxProgress = 1;
	// min progress is always 0
	
//	private static final int DOT_IMAGE = R.drawable.slider_dot;
//	private static final int SLIDER_IMAGE = R.drawable.slider_line;

	private Bitmap sliderImage;
	private Bitmap dotImage;
	
	public HaikuProgressBar(Context context) {
		super(context);
	}
	
	@Override
    protected void onDraw(Canvas canvas) {
		canvas.drawBitmap(sliderImage, 0, 0, null);
		
	}

	public void changeProgress(int progress){
		invalidate();
	}
}
