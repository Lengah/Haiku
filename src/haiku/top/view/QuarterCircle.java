package haiku.top.view;

import haiku.top.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
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
    private RectF circleOuterArc;
    private RectF circleInnerArc;

    private int drawOffset = 0;
    
    // Specifies the view's bounds
    private Position leftBottom = new Position();
    private Position leftTop = new Position();
    private Position middleBottom = new Position();
    private Position middleTop = new Position();
    private Position rightBottom = new Position();
    private Position rightTop = new Position();
    
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
        circleOuterArc = new RectF(-circleRadius, 0, circleRadius, circleRadius*2);
        circleInnerArc = new RectF(-drawOffset, circleRadius-drawOffset, drawOffset, drawOffset*2+(circleRadius-drawOffset));
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
            canvas.drawArc(circleArc, circleStartAngle, circleEndAngle, true, circleStrokePaint);
            canvas.drawText(text, circleRadius/10, 3*circleRadius/4, textPaint);
    	}
    	else{ // Month view
    		canvas.translate(-1, 1);
    		
            canvas.drawArc(circleArc, circleStartAngle, circleEndAngle, false, circlePaint);
            
            canvas.drawArc(circleOuterArc, circleStartAngle, circleEndAngle, false, circleStrokePaint);
            canvas.drawArc(circleInnerArc, circleStartAngle, circleEndAngle, false, circleStrokePaint);
            
            canvas.drawLine(leftBottom.getXPos(), leftBottom.getYPos(), leftTop.getXPos(), leftTop.getYPos(), circleStrokePaint);
            canvas.drawLine(rightBottom.getXPos(), rightBottom.getYPos(), rightTop.getXPos(), rightTop.getYPos(), circleStrokePaint);
            
    		// Rotate the text
        	canvas.save();
        	double angle = ((-circleEndAngle)*Math.PI/180+(-circleStartAngle)*Math.PI/180)/2;
        	// Angle gives where the center of the text should be, but drawText draws bottom-up
        	// Adjust the angle so it gives where the bottom of the text is
//        	Log.i("TAG", "Angle 1: " + angle);
//        	Log.i("TAG", "l: " + Math.sqrt((x1-x3)*(x1-x3)+(y1-y3)*(y1-y3)));
        	//TODO
//        	angle = angle - ((-circleEndAngle)-(-circleStartAngle))*textPaint.getTextSize()/(2*Math.sqrt((x1-x3)*(x1-x3)+(y1-y3)*(y1-y3)));
//        	Log.i("TAG", "Angle 2: " + angle);
        	float xPos = (float)((drawOffset+(circleRadius-drawOffset)/7)*Math.cos(angle));
        	float yPos = circleRadius - (float)((drawOffset+(circleRadius-drawOffset)/7)*Math.sin(angle));
//        	Log.i("TAG", text + ": " + "Offset: " + drawOffset + ", X: " + xPos + ", Y: " + yPos + ", Angle: " + (angle*180/Math.PI));
        	canvas.rotate((circleEndAngle + circleStartAngle)/2, xPos, yPos);
        	canvas.drawText(text, xPos, yPos, textPaint);
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
        	

            circleOuterArc.set(-circleRadius, 0, circleRadius, circleRadius*2);
            circleInnerArc.set(-drawOffset, circleRadius-drawOffset, drawOffset, drawOffset*2+(circleRadius-drawOffset));
            
            // Update bounds
            // circleRadius-y because (0,0) is in the top left corner, not the bottom left
            
            float angle = (float)((-circleEndAngle)*Math.PI/180+(-circleStartAngle)*Math.PI/180)/2;
            
            leftBottom.setXPos((float)(drawOffset*Math.cos((-circleEndAngle)*Math.PI/180)));
            leftBottom.setYPos(circleRadius-(float)(drawOffset*Math.sin((-circleEndAngle)*Math.PI/180)));
            
            leftTop.setXPos((float)(circleRadius*Math.cos((-circleEndAngle)*Math.PI/180)));
            leftTop.setYPos(circleRadius-(float)(circleRadius*Math.sin((-circleEndAngle)*Math.PI/180)));
            
            middleBottom.setXPos((float) (drawOffset*Math.cos(angle)));
            middleBottom.setYPos((float) (drawOffset*Math.sin(angle)));
            
            middleTop.setXPos((float) (circleRadius*Math.cos(angle)));
            middleTop.setYPos((float) (circleRadius*Math.sin(angle)));
            
            rightBottom.setXPos((float)(drawOffset*Math.cos((-circleStartAngle)*Math.PI/180)));
            rightBottom.setYPos(circleRadius-(float)(drawOffset*Math.sin((-circleStartAngle)*Math.PI/180)));
            
            rightTop.setXPos((float)(circleRadius*Math.cos((-circleStartAngle)*Math.PI/180)));
            rightTop.setYPos(circleRadius-(float)(circleRadius*Math.sin((-circleStartAngle)*Math.PI/180)));
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
    
    public boolean isPosInView(Position pos){
//    	Log.i("TAG", getText() + ": " + "LB: (" + leftBottom.getXPos() + ", " + leftBottom.getYPos() + "), " +
//    			"LT: (" + leftTop.getXPos() + ", " + leftTop.getYPos() + "), " +
//    					"RB: (" + rightBottom.getXPos() + ", " + rightBottom.getYPos() + "), " +
//    							"RT: (" + rightTop.getXPos() + ", " + rightTop.getYPos() + "), " +
//    									"MB: (" + middleBottom.getXPos() + ", " + middleBottom.getYPos() + "), " +
//    											"MT: (" + middleTop.getXPos() + ", " + middleTop.getYPos() + ")");
    	if(pos.getXPos() >= leftBottom.getXPos()
    			&& pos.getXPos() <= Math.max(rightTop.getXPos(), middleTop.getXPos())
    			&& pos.getYPos() < rightBottom.getYPos()
    	    	&& pos.getYPos() >= Math.min(leftTop.getYPos(), middleTop.getYPos())){
    		Log.i("TAG", getText());
    		float kRight = -1/((rightTop.getYPos()-rightBottom.getYPos())/(rightTop.getXPos()-rightTop.getXPos()));
    		if(pos.getYPos() < kRight*pos.getXPos()){
    			Log.i("TAG", "false 1: KRight = " + kRight);
    			return false;
    		}
    		float kLeft = -1/((leftTop.getYPos()-leftBottom.getYPos())/(leftTop.getXPos()-leftTop.getXPos()));
    		if(pos.getYPos() > kLeft*pos.getXPos()){
    			Log.i("TAG", "false 2: KLeft = " + kLeft);
    			return false;
    		}
    		return true;
    	}
    	return false;
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
