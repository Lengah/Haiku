package haiku.top.view;

import haiku.top.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.view.animation.Transformation;
import android.widget.LinearLayout.LayoutParams;

public class QuarterCircle extends View{
	private Paint circlePaint;
    private Paint circleStrokePaint;
    private Paint textPaint;
    private RectF circleArc;

    // Attrs
    private int circleRadius = 0;
    private int circleFillColor;
    private int circleStrokeColor;
    private int circleStartAngle;
    private int circleEndAngle;
    private String text = "";
    
	public QuarterCircle(Context context, AttributeSet attrs) {
		super(context, attrs);
        init(attrs); // Read all attributes

        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setStyle(Paint.Style.FILL);
        circlePaint.setColor(circleFillColor);
        circleStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circleStrokePaint.setStyle(Paint.Style.STROKE);
        circleStrokePaint.setStrokeWidth(2);
        circleStrokePaint.setColor(circleStrokeColor);
        
        textPaint = new Paint();
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(Color.WHITE);
        textPaint.setTypeface(Typeface.DEFAULT); //TODO Adobe Garamond Pro
        
        if(text.length() > 0){
        	updateTextSize();
        }
	}
	
	public QuarterCircle(Context context, String text) {
		super(context);
        init(); // Read all attributes

        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setStyle(Paint.Style.FILL);
        circlePaint.setColor(circleFillColor);
        circleStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circleStrokePaint.setStyle(Paint.Style.STROKE);
        circleStrokePaint.setStrokeWidth(2);
        circleStrokePaint.setColor(circleStrokeColor);
        
        textPaint = new Paint();
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(Color.WHITE);
        textPaint.setTypeface(Typeface.DEFAULT); //TODO Adobe Garamond Pro
        this.text = text;
        
        if(this.text.length() > 0){
        	updateTextSize();
        }
	}
	
	public QuarterCircle(Context context) {
		super(context);
        init(); // Read all attributes

        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setStyle(Paint.Style.FILL);
        circlePaint.setColor(circleFillColor);
        circleStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circleStrokePaint.setStyle(Paint.Style.STROKE);
        circleStrokePaint.setStrokeWidth(2);
        circleStrokePaint.setColor(circleStrokeColor);
        
        
        
        textPaint = new Paint();
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(Color.WHITE);
        textPaint.setTypeface(Typeface.DEFAULT); //TODO Adobe Garamond Pro
	}
	
	public QuarterCircle(Context context, int radius) {
		super(context);
		circleRadius = radius;
        init(); // Read all attributes

        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setStyle(Paint.Style.FILL);
        circlePaint.setColor(circleFillColor);
        circleStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circleStrokePaint.setStyle(Paint.Style.STROKE);
        circleStrokePaint.setStrokeWidth(2);
        circleStrokePaint.setColor(circleStrokeColor);
        
        textPaint = new Paint();
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(Color.WHITE);
        textPaint.setTypeface(Typeface.DEFAULT); //TODO Adobe Garamond Pro
	}
	
	public void init(AttributeSet attrs){
        // Go through all custom attrs.
        TypedArray attrsArray = getContext().obtainStyledAttributes(attrs, R.styleable.QuarterCircle);
        circleRadius = attrsArray.getInteger(R.styleable.QuarterCircle_cRadius, 0);
        circleFillColor = attrsArray.getColor(R.styleable.QuarterCircle_cFillColor, 16777215);
        circleStrokeColor = attrsArray.getColor(R.styleable.QuarterCircle_cStrokeColor, -1);
        circleStartAngle = attrsArray.getInteger(R.styleable.QuarterCircle_cAngleStart, 0);
        circleEndAngle = attrsArray.getInteger(R.styleable.QuarterCircle_cAngleEnd, 360);
        text = attrsArray.getString(R.styleable.QuarterCircle_text);
        
        // See the circleRadius value as a dp value and convert it to a px value //TODO funkar det??
        circleRadius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, circleRadius, getResources().getDisplayMetrics());
        circleArc = new RectF(-circleRadius, 0, circleRadius, circleRadius*2);
        // Google tells us to call recycle.
        attrsArray.recycle();
    }
	
	public void init(){
        circleFillColor = Color.rgb(234, 52, 147);
        circleStrokeColor = Color.WHITE;
        circleStartAngle = 0;
        circleEndAngle = -90;
        text = "";
        
        // See the circleRadius value as a dp value and convert it to a px value //TODO funkar det??
        circleRadius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, circleRadius, getResources().getDisplayMetrics());
        circleArc = new RectF(-circleRadius, 0, circleRadius, circleRadius*2);
	}

    @Override
    protected void onDraw(Canvas canvas) {   
//        if(newRadius != -1){
//        	animateSizeChange(canvas);
//        }
        
        canvas.translate(-1, 1);
        canvas.drawArc(circleArc, circleStartAngle, circleEndAngle, true, circlePaint);
        canvas.drawArc(circleArc, circleStartAngle, circleEndAngle, true, circleStrokePaint);

        canvas.drawText(text, circleRadius/10, 3*circleRadius/4, textPaint);
    }
    
    @Override 
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        int measuredWidth = measureWidth(widthMeasureSpec);
        circleArc.set(-circleRadius, 0, circleRadius, circleRadius*2);
        int measuredHeight = measureHeight(heightMeasureSpec);
        setMeasuredDimension(measuredWidth, measuredHeight);
//        Log.i("TAG", "measuredHeight =>" + String.valueOf(measuredHeight) + "px measuredWidth => " + String.valueOf(measuredWidth) + "px");
    }

    private int measureHeight(int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        int result = 0;
        if (specMode == MeasureSpec.AT_MOST) {
//            result = circleRadius * 2;
        	result = circleRadius;
        } else if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        }
        return result;
    }

    private int measureWidth(int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        int result = 0;
        if (specMode == MeasureSpec.AT_MOST) {
//            result = specSize;
            result = circleRadius;
        } 
        else if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        }
         return result;
    }
    
    /**
     * Animates the new size in % compared with the current size, with the given speed (100%/s).
     * 
     * For example if radius is set to 200, the view will be dubbled.
     * If radius is set to 50, the view will be halved.
     * If the speed is set to 100 in the first example and 50 is the second, both animations
     * will take one second.
     * @param radius - The size it will expand/decrease to (in % compared to the current size)
     * @param speed - The speed of the animation (100%/s)
     */
//    public void changeSizeTo(int radius, int speed){
//    	this.newRadius = radius*circleRadius/100;
//    	this.animationSpeed = speed;
//    	Log.i("TAG", "oldRadius: " + circleRadius + ", newRadius: " + newRadius);
//    	invalidate();
//    	measure(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
//    	
//    	measure(MeasureSpec.AT_MOST, MeasureSpec.AT_MOST);
//        final int targetHeight = getMeasuredHeight();
//    	Animation a = new Animation(){
//            @Override
//            protected void applyTransformation(float interpolatedTime, Transformation t) {
//                getLayoutParams().height = interpolatedTime == 1
//                        ? LayoutParams.WRAP_CONTENT
//                        : (int)(targetHeight * interpolatedTime);
//                requestLayout();
//            }
//
//            @Override
//            public boolean willChangeBounds() {
//                return true;
//            }
//        };
//
        // 1dp/ms
//        a.setDuration((int)(targtetHeight / getContext().getResources().getDisplayMetrics().density));
//        a.setDuration((radius/speed)*1000);
//    	
//    }
    
    /**
     * Creates an animation that will expand the view to a new size with the radius oldRadius*radiusFactor, with the duration in ms.
     * Returns the animation to the caller
     * @param radiusFactor - The new size compared to the current size
     * @param duration - In ms
     * 
     * @return - The animation that will expand the view
     */
    public Animation changeSizeTo(double radiusFactor, int duration) {
    	measure(MeasureSpec.AT_MOST, MeasureSpec.AT_MOST);
    	final int targetRadius = (int)(radiusFactor*circleRadius);
    	final int startRadius = circleRadius;
        Animation a = new Animation(){
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
            	if(startRadius > targetRadius){
            		getLayoutParams().height = interpolatedTime == 1
                            ? targetRadius
                            : Math.min(startRadius, startRadius-(int)(targetRadius * interpolatedTime));
            	}
            	else{
            		getLayoutParams().height = interpolatedTime == 1
                            ? targetRadius
                            : Math.min(targetRadius, startRadius+(int)((targetRadius-startRadius) * interpolatedTime));
            	}
            	getLayoutParams().width = getLayoutParams().height;
                circleRadius = getLayoutParams().height;
                updateTextSize();
                requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };
        a.setDuration(duration);
        return a;
    }

    private void updateTextSize(){
		int size = 0;
	    do {
	    	size++;
	        textPaint.setTextSize(size);
	    } while(textPaint.measureText(text) < 2*circleRadius/3);
	}
	
	public void setText(String text){
		this.text = text;
		updateTextSize();
	}
	
	public String getText(){
		return text;
	}
	
//	/**
//	 * 
//	 * @param radius in dp
//	 */
//	public void setRadius(int radius){
//		this.circleRadius = radius;
//		// See the circleRadius value as a dp value and convert it to a px value //TODO funkar det??
//        circleRadius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, circleRadius, getResources().getDisplayMetrics());
//        measure(MeasureSpec.AT_MOST, MeasureSpec.AT_MOST);
//        invalidate();
////        circleArc = new RectF(-circleRadius, 0, circleRadius, circleRadius*2);
//	}
	
	public int getRadius(){
		return circleRadius;
	}
	
	public void setFillColor(int color){
		this.circleFillColor = color;
	}
	
	public int getFillColor(){
		return circleFillColor;
	}
	
	public void setStrokeColor(int color){
		this.circleStrokeColor = color;
	}
	
	public int getStrokeColor(){
		return circleStrokeColor;
	}
	
	public void setStartAngle(int angle){
		this.circleStartAngle = angle;
	}
	
	public int getStartAngle(){
		return circleStartAngle;
	}
	
	public void setEndAngle(int angle){
		this.circleEndAngle = angle;
	}
	
	public int getEndAngle(){
		return circleEndAngle;
	}
}
