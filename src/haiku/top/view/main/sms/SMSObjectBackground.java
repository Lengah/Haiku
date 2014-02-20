package haiku.top.view.main.sms;

import haiku.top.model.Position;
import haiku.top.view.main.MainView;

import java.util.ArrayList;
import java.util.Random;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;
import android.view.View;

public class SMSObjectBackground extends View{
	private SMSObject smsObject;
	private Paint paint;
	private Path path;
	private Random random;
	private ArrayList<Position> generatedAtBot; // used to calculate where the view under this one can draw, can contain positions from the sides
	
	int xLeft;
	int xRight;
	int yTop;
	int yBot;
	
	public SMSObjectBackground(Context context, SMSObject smsObject) {
		super(context);
		this.smsObject = smsObject;
		xLeft = smsObject.getSideBoxWidth();
		xRight = xLeft + smsObject.getCenterBox().getViewWidth();
		yTop = smsObject.getHeightOfTopOrBottomBox();
		yBot = yTop + smsObject.getCenterBox().getViewHeight();
		
		paint = new Paint();
		paint.setStyle(Paint.Style.FILL_AND_STROKE); //FILL_AND_STROKE?
		path = new Path();
		path.setFillType(Path.FillType.EVEN_ODD);
		
//		random = new Random(seed);
		random = new Random();

		ArrayList<Position> generatedTop = generateTop();
		ArrayList<Position> generatedBottom = generateBottom();
		ArrayList<Position> generatedRight = generateRight(generatedTop.get(generatedTop.size()-1), generatedBottom.get(0));
		ArrayList<Position> generatedLeft = generateLeft(generatedTop.get(0), generatedBottom.get(generatedBottom.size()-1));
		
		ArrayList<Position> allPositionsClockwise = new ArrayList<Position>();
		allPositionsClockwise.addAll(generatedTop);
		allPositionsClockwise.addAll(generatedRight);
		allPositionsClockwise.addAll(generatedBottom);
		allPositionsClockwise.addAll(generatedLeft);
		
		path.moveTo(allPositionsClockwise.get(0).getXPos(), allPositionsClockwise.get(0).getYPos());
//		Log.i("TAG", "(x0, y0):  (" + generatedPositions.get(0).getXPos() + ", " +generatedPositions.get(0).getYPos() + ")");
		for(int i = 1; i < allPositionsClockwise.size(); i++){
			path.lineTo(allPositionsClockwise.get(i).getXPos(), allPositionsClockwise.get(i).getYPos());
//			Log.i("TAG", "(x" + i + ", y + " + i +"):  (" + generatedPositions.get(i).getXPos() + ", " +generatedPositions.get(i).getYPos() + ")");
		}
		path.close();
	}
	
	/**
	 * 
	 * @return From left to right.
	 */
	private ArrayList<Position> generateTop(){
		// need to take the bottom part of the above view into account
		SMSObject topView = MainView.getInstance().getLastObjectInSMSList();
		ArrayList<Position> generatedPositions = new ArrayList<Position>();
		int maxY = smsObject.getHeightOfTopOrBottomBox();
		int maxX = xLeft + xRight;
		if(topView == null){
			// First object in the list so there are no constraints on where to draw
		}
		else{
			// Not the first object -> must take the upper part into consideration when generating
		}
		//TODO
		Integer[] weights = {0, 1, 3, 5, 6, 4, 3, 5, 4, 3};
		int tw = 0;
		for(int w : weights){
			tw += w;
		}
		int amountOfDots = random.nextInt(tw)+1;
		for(int i = 0; i < weights.length; i++){
			amountOfDots -= weights[i];
			if(amountOfDots <= 0){
				amountOfDots = i;
				break;
			}
		}
		int stdX = xLeft;
		int stdY = maxY/2;
		int x;
		int y;
		int lastX = -1;
		int xOff;
		for(int i = 0; i < amountOfDots; i++){
			if(lastX == -1){
				//first
				xOff = xLeft;
			}
			else{
				if(lastX == maxX){
					break; // screw the rest of the dots, already reached max
				}
				xOff = lastX + random.nextInt(maxX - lastX);
			}
			//x
			x = (int) (stdX * random.nextGaussian() + xOff);
			if(x < 0){
				x = 0;
			}
			if(x > maxX){
				x = maxX;
			}
			//y
			y = (int) (stdY * Math.abs(random.nextGaussian()));
			if(y > maxY){
				y = maxY;
			}
			if(x < lastX){
				i--;
				continue;
			}
			generatedPositions.add(new Position(x, y));
			lastX = x;
		}
		
		return generatedPositions;
	}
	
	/**
	 * 
	 * @return From right to left
	 */
	private ArrayList<Position> generateBottom(){
		// Doesn't have to take anything into account
		ArrayList<Position> generatedPositions = new ArrayList<Position>();
		int minY = smsObject.getHeightOfTopOrBottomBox() + smsObject.getCenterBox().getViewHeight();
		int maxY = smsObject.getCenterBox().getViewHeight() + 2*smsObject.getHeightOfTopOrBottomBox();
		int maxX = xLeft + xRight;
		Integer[] weights = {0, 1, 2, 3, 5, 5, 4, 5, 4, 3};
		int tw = 0;
		for(int w : weights){
			tw += w;
		}
		int amountOfDots = random.nextInt(tw)+1;
		for(int i = 0; i < weights.length; i++){
			amountOfDots -= weights[i];
			if(amountOfDots <= 0){
				amountOfDots = i;
				break;
			}
		}
		int stdX = xLeft;
		int stdY = (maxY-minY)/2;
		int x;
		int y;
		int lastX = -1;
		int xOff;
		for(int i = 0; i < amountOfDots; i++){
			if(lastX == -1){
				//first
				xOff = xRight;
			}
			else{
				if(0 == lastX){
					break; // screw the rest of the dots, already reached max
				}
				xOff = lastX - random.nextInt(lastX);
			}
			//x
			x = (int) (stdX * random.nextGaussian() + xOff);
			if(x < 0){
				x = 0;
			}
			if(x > maxX){
				x = maxX;
			}
			//y
			y = (int) (stdY * Math.abs(random.nextGaussian()) + minY);
			if(y > maxY){
				y = maxY;
			}
			if(lastX != -1 && x > lastX){
				i--;
				continue;
			}
			generatedPositions.add(new Position(x, y));
			lastX = x;
		}
		
		
		return generatedPositions;
	}
	
	/**
	 * Top to bottom.
	 * Will find dots in between top and bottom. Will only return the dots in between (could be empty)
	 */
	private ArrayList<Position> generateRight(Position top, Position bottom){
		ArrayList<Position> generatedPositions = new ArrayList<Position>();
		int minY = yTop;
		int maxY = yBot;
		int minX = xRight;
		int maxX = xLeft + xRight;
		int r0 = 1;
		boolean topCorner = isDotLeftOfLine(new Position(xRight, yTop), top, bottom);
		boolean bottomCorner = isDotLeftOfLine(new Position(xRight, yBot), top, bottom);
		if(topCorner || bottomCorner){
			// NEEDS a dot on the right side
			r0 = 0;
		}
		Integer[] weights = {r0, 0, 3, 5, 4, 4, 5 ,4 ,3};
		int tw = 0;
		for(int w : weights){
			tw += w;
		}
		int amountOfDots = random.nextInt(tw)+1;
		for(int i = 0; i < weights.length; i++){
			amountOfDots -= weights[i];
			if(amountOfDots <= 0){
				amountOfDots = i;
				break;
			}
		}
		int stdX = xLeft/2;
		int x;
		int y;
		int lastY = -1;
		int rY;
		for(int i = 0; i < amountOfDots; i++){
			if(topCorner && xRight > top.getXPos()){ // if xRight < top.getXPos() it will sort itself out
				//TODO
				double k = ((yTop-top.getYPos())/(xRight-top.getXPos()));
				double m = top.getYPos() - top.getXPos()*k;
				
				int maxYPos = (int) (k * maxX + m);
				if(maxYPos == minY){
					y = minY;
				}
				else{
					y = random.nextInt(maxYPos-minY);
				}
				int minXPos = (int) ((y-m)/k);
				if(minXPos == maxX){
					x = maxX;
				}
				else{
					x = random.nextInt(maxX-minXPos);
				}
				topCorner = false;
				lastY = y;
				generatedPositions.add(new Position(x, y));
				continue;
			}
			if(lastY == -1){
				//first
				rY = (maxY-minY)/3;
			}
			else{
				if(lastY == maxY){
					break; // screw the rest of the dots, already reached max
				}
				rY = (maxY - lastY)/3;
			}
			//x
			x = (int) (stdX * Math.abs(random.nextGaussian()) + minX);
			if(x > maxX){
				x = maxX;
			}
			//y
			if(rY <= 0){
				break;
			}
			y = lastY + random.nextInt(rY);
			if(y < minY){
				y = minY;
			}
			if(y > maxY){
				y = maxY;
			}
			if(y < lastY){
				i--;
				continue;
			}
			generatedPositions.add(new Position(x, y));
			lastY = y;
		}
		
		return generatedPositions;
	}
	
	/**
	 * Bottom to top.
	 * Will find dots in between top and bottom. Will only return the dots in between (could be empty)
	 */
	private ArrayList<Position> generateLeft(Position top, Position bottom){
		ArrayList<Position> generatedPositions = new ArrayList<Position>();
		int minY = yTop;
		int maxY = yBot;
		int minX = 0;
		int maxX = xLeft;
		int r0 = 1;
		if(!isDotLeftOfLine(new Position(xLeft, yTop), top, bottom) || !isDotLeftOfLine(new Position(xLeft, yBot), top, bottom)){
			// NEEDS a dot on the right side
			r0 = 0;
		}
		Integer[] weights = {r0, 0, 4, 5, 4, 4, 5, 3, 4};
		int tw = 0;
		for(int w : weights){
			tw += w;
		}
		int amountOfDots = random.nextInt(tw)+1;
		for(int i = 0; i < weights.length; i++){
			amountOfDots -= weights[i];
			if(amountOfDots <= 0){
				amountOfDots = i;
				break;
			}
		}
		int stdX = xLeft/2;
		int x;
		int y;
		int lastY = maxY;
		int rY;
		for(int i = 0; i < amountOfDots; i++){
			if(lastY == maxY){
				//first
				rY = (maxY-minY)/3;
			}
			else{
				if(lastY == minY){
					break; // screw the rest of the dots, already reached max
				}
				rY = (lastY - minY)/3;
			}
			//x
			x = (int) (maxX - stdX * Math.abs(random.nextGaussian()));
			if(x < minX){
				x = minX;
			}
			//y
			if(rY <= 0){
				break;
			}
			y = lastY - random.nextInt(rY);
			if(y < minY){
				Log.i("TAG", "y:" + y);
				y = minY;
			}
			if(y > maxY){
				y = maxY;
			}
			if(y > lastY){
				i--;
				continue;
			}
			generatedPositions.add(new Position(x, y));
			lastY = y;
		}
		
		return generatedPositions;
	}
	
	private boolean isDotLeftOfLine(Position dot, Position p1, Position p2){
		if(p1.getXPos() < p2.getXPos()){
			Position temp = p1;
			p1 = p2;
			p2 = temp;
		}
		double k = ((p2.getYPos()-p1.getYPos())/(p2.getXPos()-p1.getXPos()));
		double m = p1.getYPos() - p1.getXPos()*k;
		
		float needsToBeSmallerThanX = (float) ((dot.getYPos()-m)/k);
		return dot.getXPos() <= needsToBeSmallerThanX;
	}
	
	public void setColor(int color){
		paint.setColor(color);
	}
	
	public ArrayList<Position> getGeneratedAtBot(){
		return generatedAtBot;
	}
	
	@Override
  protected void onDraw(Canvas canvas) {
//		Log.i("TAG", "onDraw! " + (bottomPath != null));
		canvas.drawPath(path, paint);
	}

}
