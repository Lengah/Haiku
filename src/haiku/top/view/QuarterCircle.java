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

    private int drawOffset = 0;
    
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
	
	public QuarterCircle(Context context, String text, int radius, int startAngle, int endAngle, int offset) {
		super(context);
		circleRadius = radius;
		this.drawOffset = offset;
        init(startAngle, endAngle); // Read all attributes

        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setColor(circleFillColor);
//        Log.i("TAG", "circleRadius: " + circleRadius + ", drawOffset: " + drawOffset + ", circleRadius-drawOffset: " + (circleRadius-drawOffset));
        circlePaint.setStrokeWidth(circleRadius-drawOffset);
//        circlePaint.setStrokeWidth(20);
        circleStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circleStrokePaint.setStyle(Paint.Style.STROKE);
        circleStrokePaint.setStrokeWidth(2);
        circleStrokePaint.setColor(circleStrokeColor);
        
        textPaint = new Paint();
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(Color.BLACK);
        textPaint.setTypeface(Typeface.DEFAULT); //TODO Adobe Garamond Pro
        
        this.text = text;
        
        if(this.text.length() > 0){
        	updateTextSize();
        }
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
	
	public void init(int startAngle, int endAngle){
        circleFillColor = Color.rgb(234, 52, 147);
        circleStrokeColor = Color.WHITE;
        circleStartAngle = startAngle;
        circleEndAngle = endAngle;
        text = "";
        
        // See the circleRadius value as a dp value and convert it to a px value //TODO funkar det??
        circleRadius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, circleRadius, getResources().getDisplayMetrics());
        circleArc = new RectF(-circleRadius, 0, circleRadius, circleRadius*2);
	}

    @Override
    protected void onDraw(Canvas canvas) {
    	if(circleStartAngle < -90 || circleEndAngle > 0){
    		// Out of view
    		return;
    	}
    	if(drawOffset == 0){// Year view
    		canvas.translate(-1, 1);
            canvas.drawArc(circleArc, circleStartAngle, circleEndAngle, true, circlePaint);
//            canvas.drawArc(circleArc, circleStartAngle, circleEndAngle, true, circleStrokePaint);
            canvas.drawText(text, circleRadius/10, 3*circleRadius/4, textPaint);
    	}
    	else{ // Month view
    		canvas.translate(-1, 1);
    		
            canvas.drawArc(circleArc, circleStartAngle, circleEndAngle, false, circlePaint);
    		
    		// Rotate the text
        	canvas.save();
        	double angle = ((-circleEndAngle)*Math.PI/180+(-circleStartAngle)*Math.PI/180)/2;
        	float xPos = (float)(drawOffset*Math.cos(angle));
        	float yPos = (float)(drawOffset*Math.sin(angle));
//            canvas.rotate(circleEndAngle - circleStartAngle, 0, circleRadius);
        	Log.i("TAG", text + ": " + "Offset: " + drawOffset + ", X: " + xPos + ", Y: " + yPos + ", Angle: " + (angle*180/Math.PI));
        	canvas.rotate((circleEndAngle + circleStartAngle)/2, xPos, yPos);
        	canvas.drawText(text, 0, 0, textPaint);
            canvas.restore();
    	}
    }
    
    @Override 
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        int measuredWidth = measureWidth(widthMeasureSpec);
        if(drawOffset == 0){
        	circleArc.set(-circleRadius, 0, circleRadius, circleRadius*2);
        }
        else{
        	// smaller height because the drawArc method with a stroke paint paints half on the inside and half on the outside.
        	// Since I want everything on the inside I have to adjust the height so the center of the arc is in the center of the slice.
        	// The width also has to be adjusted so it remains a circle.
        	circleArc.set(-circleRadius + (circleRadius-drawOffset)/2, (circleRadius-drawOffset)/2, circleRadius - (circleRadius-drawOffset)/2, circleRadius*2 - (circleRadius-drawOffset)/2);
        }
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
    
    private Animation a;
  
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
        a = new Animation(){
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
            	if(startRadius > targetRadius){
            		getLayoutParams().height = interpolatedTime == 1
                            ? targetRadius
                            : Math.max(targetRadius, startRadius-(int)((startRadius - targetRadius) * interpolatedTime));
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

    public void changeAngle(int angleChange){
    	circleStartAngle += angleChange;
    	circleEndAngle += angleChange;
    	requestLayout();
    }
    
    private void updateTextSize(){
		int size = 0;
	    do {
	    	size++;
	        textPaint.setTextSize(size);
	    } while(textPaint.measureText(text) < 2*(circleRadius - drawOffset)/3);
	}
	
	public void setText(String text){
		this.text = text;
		updateTextSize();
	}
	
	public String getText(){
		return text;
	}
	
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
