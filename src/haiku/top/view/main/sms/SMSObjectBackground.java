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
	
	int xLeft;
	int xRight;
	int yTop;
	int yBot;
	
	int xMax;
	int xMin;
	int yMax;
	int yMin;
	
	public SMSObjectBackground(Context context, SMSObject smsObject) {
		super(context);
		this.smsObject = smsObject;
		xLeft = smsObject.getSideBoxWidth();
		xRight = xLeft + smsObject.getCenterBox().getViewWidth();
		yTop = smsObject.getHeightOfTopOrBottomBox();
		yBot = yTop + smsObject.getCenterBox().getViewHeight();
		
		xMax = xRight + xLeft;
		xMin = 0;
		yMax = yTop + yBot;
		yMin = 0;
		
		paint = new Paint();
		paint.setStyle(Paint.Style.FILL_AND_STROKE); //FILL_AND_STROKE?
		path = new Path();
		path.setFillType(Path.FillType.EVEN_ODD);
		
		random = new Random(smsObject.getSeed());
//		random = new Random();

		ArrayList<Position> allPositionsClockwise = generate();
		
		path.moveTo(allPositionsClockwise.get(0).getXPos(), allPositionsClockwise.get(0).getYPos());
//		Log.i("TAG", "(x0, y0):  (" + generatedPositions.get(0).getXPos() + ", " +generatedPositions.get(0).getYPos() + ")");
		for(int i = 1; i < allPositionsClockwise.size(); i++){
			path.lineTo(allPositionsClockwise.get(i).getXPos(), allPositionsClockwise.get(i).getYPos());
//			Log.i("TAG", "(x" + i + ", y + " + i +"):  (" + generatedPositions.get(i).getXPos() + ", " +generatedPositions.get(i).getYPos() + ")");
		}
		path.close();
	}
	
	private ArrayList<Position> generate(){
		ArrayList<Position> generatedPositions = new ArrayList<Position>();
		
		// how many corners should be moved?
		Integer[] cornerWeights = {0, 5, 5, 2, 1}; //0,1,2,3,4 corners
		int tcw = 0;
		for(int w : cornerWeights){
			tcw += w;
		}
		int amountOfCorners = random.nextInt(tcw)+1;
		for(int i = 0; i < cornerWeights.length; i++){
			amountOfCorners -= cornerWeights[i];
			if(amountOfCorners <= 0){
				amountOfCorners = i;
				break;
			}
		}
		// which corners?
		boolean topLeft = false;
		boolean topRight = false;
		boolean bottomRight = false;
		boolean bottomLeft = false;
		
		Integer[] cornerIndexes = {0,1,2,3}; // clockwise starting with topLeft
		while(amountOfCorners > 0){
			int cornerIndex = cornerIndexes[random.nextInt(cornerIndexes.length)];
			if(cornerIndex == 0){
				topLeft = true;
			}
			if(cornerIndex == 1){
				topRight = true;
			}
			if(cornerIndex == 2){
				bottomRight = true;
			}
			if(cornerIndex == 3){
				bottomLeft = true;
			}
			amountOfCorners--;
		}
		
		Position topLeftPos = new Position(xLeft, yTop);
		Position topRightPos = new Position(xRight, yTop);
		Position bottomRightPos = new Position(xRight, yBot);
		Position bottomLeftPos = new Position(xLeft, yBot);
		int stdx = xLeft/2;
		int stdy = stdx;
		if(topLeft){
			int x = (int) (stdx * random.nextGaussian() + xLeft);
			int y = (int) (stdy * random.nextGaussian() + yTop);
			if(x > xLeft && y > yTop){
				int r = random.nextInt(2); // 0 or 1
				if(r == 0){
					x = (int) (stdx * -Math.abs(random.nextGaussian()) + xLeft);
				}
				else{
					y = (int) (stdy * -Math.abs(random.nextGaussian()) + yTop);
				}
			}
			if(x < xMin){
				x = xMin;
			}
			if(x > xMax){
				x = xMax;
			}
			if(y < yMin){
				y = yMin;
			}
			if(y > yMax){
				y = yMax;
			}
			topLeftPos = new Position(x, y);
		}
		if(topRight){
			int x = (int) (stdx * random.nextGaussian() + xRight);
			int y = (int) (stdy * random.nextGaussian() + yTop);
			if(x < xRight && y > yTop){
				int r = random.nextInt(2); // 0 or 1
				if(r == 0){
					x = (int) (stdx * Math.abs(random.nextGaussian()) + xRight);
				}
				else{
					y = (int) (stdy * -Math.abs(random.nextGaussian()) + yTop);
				}
			}
			if(x < xMin){
				x = xMin;
			}
			if(x > xMax){
				x = xMax;
			}
			if(y < yMin){
				y = yMin;
			}
			if(y > yMax){
				y = yMax;
			}
			topRightPos = new Position(x, y);
		}
		if(bottomRight){
			int x = (int) (stdx * random.nextGaussian() + xRight);
			int y = (int) (stdy * random.nextGaussian() + yBot);
			if(x < xRight && y < yBot){
				int r = random.nextInt(2); // 0 or 1
				if(r == 0){
					x = (int) (stdx * Math.abs(random.nextGaussian()) + xRight);
				}
				else{
					y = (int) (stdy * Math.abs(random.nextGaussian()) + yBot);
				}
			}
			if(x < xMin){
				x = xMin;
			}
			if(x > xMax){
				x = xMax;
			}
			if(y < yMin){
				y = yMin;
			}
			if(y > yMax){
				y = yMax;
			}
			bottomRightPos = new Position(x, y);
		}
		if(bottomLeft){
			int x = (int) (stdx * random.nextGaussian() + xLeft);
			int y = (int) (stdy * random.nextGaussian() + yBot);
			if(x > xLeft && y < yBot){
				int r = random.nextInt(2); // 0 or 1
				if(r == 0){
					x = (int) (stdx * -Math.abs(random.nextGaussian()) + xLeft);
				}
				else{
					y = (int) (stdy * Math.abs(random.nextGaussian()) + yBot);
				}
			}
			if(x < xMin){
				x = xMin;
			}
			if(x > xMax){
				x = xMax;
			}
			if(y < yMin){
				y = yMin;
			}
			if(y > yMax){
				y = yMax;
			}
			bottomLeftPos = new Position(x, y);
		}
		// how many new dots?
		Integer[] dotsWeights = {1, 2, 2}; // minimum amount. It might add more dots depending on where the corners are
		int tdw = 0;
		for(int w : dotsWeights){
			tdw += w;
		}
		int amountOfDots = random.nextInt(tdw)+1;
		for(int i = 0; i < dotsWeights.length; i++){
			amountOfDots -= dotsWeights[i];
			if(amountOfDots <= 0){
				amountOfDots = i;
				break;
			}
		}
		
		// TOP
		if(!(topLeftPos.getYPos() == yTop && topRightPos.getYPos() == yTop)){
			// should probably add a dot top
			if(topLeftPos.getYPos() <= yTop && topLeftPos.getXPos() <= xLeft){
				if(topRightPos.getYPos() <= yTop && topRightPos.getXPos() >= xRight){
					int x = random.nextInt(xRight-xLeft) + xLeft;
					int y = yTop;
					generatedPositions.add(new Position(x, y));
				}
				else if(topRightPos.getYPos() <= yTop && topRightPos.getXPos() <= xRight){
					int x = random.nextInt(xRight-xLeft) + xLeft;
					int y = yTop;
					generatedPositions.add(new Position(x, y));
					if(topRightPos.getXPos() != xRight && x != xRight){
						x = xRight;
						generatedPositions.add(new Position(x, y));
					}
				}
				else if(topRightPos.getYPos() >= yTop && topRightPos.getXPos() > xRight){
					int x = random.nextInt(xRight-xLeft) + xLeft;
					int y = yTop;
					generatedPositions.add(new Position(x, y));
					if(topRightPos.getYPos() != yTop){
						x = random.nextInt(xMax-xRight) + xRight;
						generatedPositions.add(new Position(x, y));
					}
				}
			}
			else if(topLeftPos.getYPos() <= yTop && topLeftPos.getXPos() >= xLeft){
				if(topRightPos.getYPos() <= yTop && topRightPos.getXPos() >= xRight){
					int x = random.nextInt(xRight-xLeft) + xLeft;
					int y = yTop;
					generatedPositions.add(new Position(x, y));
					if(topLeftPos.getXPos() != xLeft && x != xLeft){
						x = xLeft;
						generatedPositions.add(new Position(x, y));
					}
				}
				else if(topRightPos.getYPos() <= yTop && topRightPos.getXPos() <= xRight){
					if(topLeftPos.getYPos() != yTop && topLeftPos.getXPos() != xLeft){
						int y = yTop;
//						int x = random.nextInt(xLeft-xMin) + xMin;
						int x = xLeft;
						generatedPositions.add(new Position(x, y));
					}
					if(topRightPos.getYPos() != yTop && topRightPos.getXPos() != xRight){
						int y = yTop;
//						int x = random.nextInt(xMax-xRight) + xRight;
						int x = xRight;
						generatedPositions.add(new Position(x, y));
					}
				}
				else if(topRightPos.getYPos() >= yTop && topRightPos.getXPos() > xRight){
					if(topLeftPos.getYPos() != yTop && topLeftPos.getXPos() != xLeft){
						int y = yTop;
//						int x = random.nextInt(xLeft-xMin) + xMin;
						int x = xLeft;
						generatedPositions.add(new Position(x, y));
					}
					if(topRightPos.getYPos() != yTop && topRightPos.getXPos() != xRight){
						int y = yTop;
//						int x = random.nextInt(xMax-xRight) + xRight;
						int x = xRight;
						generatedPositions.add(new Position(x, y));
					}
				}
			}
			else if(topLeftPos.getYPos() >= yTop && topLeftPos.getXPos() <= xLeft){
				if(topRightPos.getYPos() <= yTop && topRightPos.getXPos() >= xRight){
					int x = random.nextInt(xRight-xLeft) + xLeft;
					int y = yTop;
					generatedPositions.add(new Position(x, y));
					if(topLeftPos.getYPos() != yTop){
						x = random.nextInt(xLeft-xMin) + xMin;
						generatedPositions.add(new Position(x, y));
					}
				}
				else if(topRightPos.getYPos() <= yTop && topRightPos.getXPos() <= xRight){
					if(topLeftPos.getYPos() != yTop && topLeftPos.getXPos() != xLeft){
						int y = yTop;
//						int x = random.nextInt(xLeft-xMin) + xMin;
						int x = xLeft;
						generatedPositions.add(new Position(x, y));
					}
					if(topRightPos.getYPos() != yTop && topRightPos.getXPos() != xRight){
						int y = yTop;
//						int x = random.nextInt(xMax-xRight) + xRight;
						int x = xRight;
						generatedPositions.add(new Position(x, y));
					}
				}
				else if(topRightPos.getYPos() >= yTop && topRightPos.getXPos() > xRight){
					if(topLeftPos.getYPos() != yTop && topRightPos.getYPos() != yTop && topLeftPos.getXPos() != xLeft && topRightPos.getXPos() != xRight){
						double k1 = ((yTop-topLeftPos.getYPos())/(xLeft-topLeftPos.getXPos()));
						double m1 = topLeftPos.getYPos() - topLeftPos.getXPos()*k1;
						
						double k2 = ((topRightPos.getYPos() - yTop)/(topRightPos.getXPos() - xRight));
						double m2 = topRightPos.getYPos() - topRightPos.getXPos()*k2;
						
						int x = (int) ((m2-m1)/(k1-k2));
						int y = (int) (k1 * x + m1);
						if(y < yMin){
							// the lines are not crossed within the drawing area
							y = yMin;
							int x1 = (int) ((y-m1)/k1);
							int x2 = (int) ((y-m2)/k2);
							if(x1 < xMin){
								x1 = xMin;
							}
							if(x1 > xMax){
								x1 = xMax;
							}
							if(x2 < xMin){
								x2 = xMin;
							}
							if(x2 > xMax){
								x2 = xMax;
							}
							if(y < yMin){
								y = yMin;
							}
							if(y > yMax){
								y = yMax;
							}
							generatedPositions.add(new Position(x1, y));
							generatedPositions.add(new Position(x2, y));
						}
						else{
							if(x < xMin){
								x = xMin;
							}
							if(x > xMax){
								x = xMax;
							}
							if(y < yMin){
								y = yMin;
							}
							if(y > yMax){
								y = yMax;
							}
							generatedPositions.add(new Position(x, y));
						}
					}
					else if(topLeftPos.getYPos() != yTop && topLeftPos.getXPos() != xLeft){
						double k = ((yTop-topLeftPos.getYPos())/(xLeft-topLeftPos.getXPos()));
						double m = topLeftPos.getYPos() - topLeftPos.getXPos()*k;
						
						int y = random.nextInt(yTop-yMin) + yMin;
						int x = (int) ((y-m)/k);
						if(x < xMin){
							x = xMin;
						}
						if(x > xMax){
							x = xMax;
						}
						if(y < yMin){
							y = yMin;
						}
						if(y > yMax){
							y = yMax;
						}
						generatedPositions.add(new Position(x, y));
					}
					else if(topRightPos.getYPos() != yTop && topRightPos.getXPos() != xRight){
						double k = ((topRightPos.getYPos() - yTop)/(topRightPos.getXPos() - xRight));
						double m = topRightPos.getYPos() - topRightPos.getXPos()*k;
						
						int y = random.nextInt(yTop-yMin) + yMin;
						int x = (int) ((y-m)/k);
						if(x < xMin){
							x = xMin;
						}
						if(x > xMax){
							x = xMax;
						}
						if(y < yMin){
							y = yMin;
						}
						if(y > yMax){
							y = yMax;
						}
						generatedPositions.add(new Position(x, y));
					}
				}
			}
		}
		// /TOP
		// RIGHT
		Position actualTopRight = topRightPos;
		if(!generatedPositions.isEmpty()){
			ArrayList<Position> temp = sortClockwise(generatedPositions); 
			actualTopRight = temp.get(generatedPositions.size()-1);
		}
		
		if(!(actualTopRight.getXPos() == xRight && bottomRightPos.getXPos() == xRight)){
			if(actualTopRight.getXPos() <= xRight && actualTopRight.getYPos() <= yTop){
				if(bottomRightPos.getXPos() <= xRight && bottomRightPos.getYPos() >= yBot){
					if(actualTopRight.getXPos() != xRight && actualTopRight.getYPos() != yTop && bottomRightPos.getXPos() != xRight && bottomRightPos.getYPos() != yBot){
						double k1 = ((bottomRightPos.getYPos() - yBot)/(bottomRightPos.getXPos() - xRight));
						double m1 = bottomRightPos.getYPos() - bottomRightPos.getXPos()*k1;
						
						double k2 = ((actualTopRight.getYPos() - yTop)/(actualTopRight.getXPos() - xRight));
						double m2 = actualTopRight.getYPos() - actualTopRight.getXPos()*k2;
						
						int x = (int) ((m2-m1)/(k1-k2)); //TODO borde dubbelkolla detta
						if(x > xMax){
							// outside of the drawing area
							x = xMax;
							int y1 = (int) (k1 * x + m1);
							int y2 = (int) (k2 * x + m2);
							if(x < xMin){
								x = xMin;
							}
							if(x > xMax){
								x = xMax;
							}
							if(y1 < yMin){
								y1 = yMin;
							}
							if(y1 > yMax){
								y1 = yMax;
							}
							if(y2 < yMin){
								y2 = yMin;
							}
							if(y2 > yMax){
								y2 = yMax;
							}
							generatedPositions.add(new Position(x, y1));
							generatedPositions.add(new Position(x, y2));
						}
						else{
							int y = (int) (k1 * x + m1);
							if(x < xMin){
								x = xMin;
							}
							if(x > xMax){
								x = xMax;
							}
							if(y < yMin){
								y = yMin;
							}
							if(y > yMax){
								y = yMax;
							}
							generatedPositions.add(new Position(x, y));
						}
					}
					else if(actualTopRight.getXPos() != xRight && actualTopRight.getYPos() != yTop){
						double k = ((actualTopRight.getYPos() - yTop)/(actualTopRight.getXPos() - xRight));
						double m = actualTopRight.getYPos() - actualTopRight.getXPos()*k;
						
						int y = random.nextInt(yTop-yMin) + yMin;
						int x = (int) ((y-m)/k);
						if(x < xMin){
							x = xMin;
						}
						if(x > xMax){
							x = xMax;
						}
						if(y < yMin){
							y = yMin;
						}
						if(y > yMax){
							y = yMax;
						}
						generatedPositions.add(new Position(x, y));
					}
					else if(bottomRightPos.getXPos() != xRight && bottomRightPos.getYPos() != yBot){
						double k = ((bottomRightPos.getYPos() - yBot)/(bottomRightPos.getXPos() - xRight));
						double m = bottomRightPos.getYPos() - bottomRightPos.getXPos()*k;
						
						int y = random.nextInt(yTop-yMin) + yMin;
						int x = (int) ((y-m)/k);
						if(x < xMin){
							x = xMin;
						}
						if(x > xMax){
							x = xMax;
						}
						if(y < yMin){
							y = yMin;
						}
						if(y > yMax){
							y = yMax;
						}
						generatedPositions.add(new Position(x, y));
					}
				}
				else if(bottomRightPos.getXPos() >= xRight && bottomRightPos.getYPos() >= yBot){
					int y = random.nextInt(yBot-yTop) + yTop;
					int x = xRight;
					generatedPositions.add(new Position(x, y));
					if(actualTopRight.getXPos() != xRight){
						y = random.nextInt(yTop-yMin) + yMin;
						generatedPositions.add(new Position(x, y));
					}
				}
				else if(bottomRightPos.getXPos() >= xRight && bottomRightPos.getYPos() <= yBot){
					if(topRightPos.getYPos() != yTop && topRightPos.getXPos() != xRight){
						int x = xRight;
						int y = yTop;
						generatedPositions.add(new Position(x, y));
					}
					if(bottomRightPos.getYPos() != yBot && bottomRightPos.getXPos() != xRight){
						int x = xRight;
						int y = yBot;
						generatedPositions.add(new Position(x, y));
					}
				}
			}
			else if(actualTopRight.getXPos() >= xRight && actualTopRight.getYPos() <= yTop){
				if(bottomRightPos.getXPos() <= xRight && bottomRightPos.getYPos() >= yBot){
					int y = random.nextInt(yBot-yTop) + yTop;
					int x = xRight;
					generatedPositions.add(new Position(x, y));
					if(bottomRightPos.getXPos() != xRight){
						y = random.nextInt(yMax-yBot) + yBot;
						generatedPositions.add(new Position(x, y));
					}
				}
				else if(bottomRightPos.getXPos() >= xRight && bottomRightPos.getYPos() >= yBot){
					int y = random.nextInt(yBot-yTop) + yTop;
					int x = xRight;
					generatedPositions.add(new Position(x, y));
				}
				else if(bottomRightPos.getXPos() >= xRight && bottomRightPos.getYPos() <= yBot){
					int y = random.nextInt(yBot-yTop) + yTop;
					int x = xRight;
					generatedPositions.add(new Position(x, y));
//					if(bottomRightPos.getYPos() != yBot && y != yBot){ //TODO borde väl lösas av nästa koll? (bottom kollen alltså)
//						y = yBot;
//						generatedPositions.add(new Position(x, y));
//					}
				}
			}
			else if(actualTopRight.getXPos() >= xRight && actualTopRight.getYPos() >= yTop){
				if(bottomRightPos.getXPos() <= xRight && bottomRightPos.getYPos() >= yBot){
					if(topRightPos.getYPos() != yTop && topRightPos.getXPos() != xRight){
						int x = xRight;
						int y = yTop;
						generatedPositions.add(new Position(x, y));
					}
					if(bottomRightPos.getYPos() != yBot && bottomRightPos.getXPos() != xRight){
						int x = xRight;
						int y = yBot;
						generatedPositions.add(new Position(x, y));
					}
				}
				else if(bottomRightPos.getXPos() >= xRight && bottomRightPos.getYPos() >= yBot){
					int y = random.nextInt(yBot-yTop) + yTop;
					int x = xRight;
					generatedPositions.add(new Position(x, y));
					if(actualTopRight.getYPos() != yTop && y != yTop){
						y = yTop;
						generatedPositions.add(new Position(x, y));
					}
				}
				else if(bottomRightPos.getXPos() >= xRight && bottomRightPos.getYPos() <= yBot){
					if(topRightPos.getYPos() != yTop && topRightPos.getXPos() != xRight){
						int x = xRight;
						int y = yTop;
						generatedPositions.add(new Position(x, y));
					}
					if(bottomRightPos.getYPos() != yBot && bottomRightPos.getXPos() != xRight){
						int x = xRight;
						int y = yBot;
						generatedPositions.add(new Position(x, y));
					}
				}
			}
		}
		// /RIGHT
		// BOTTOM
		Position actualBottomRight = bottomRightPos;
		if(!generatedPositions.isEmpty()){
			ArrayList<Position> temp = new ArrayList<Position>(generatedPositions);
			temp.add(bottomRightPos);
			temp = sortClockwise(temp);
			actualBottomRight = temp.get(temp.size()-1);
		}
		
		if(!(bottomLeftPos.getYPos() == yBot && actualBottomRight.getYPos() == yBot)){
			if(bottomLeftPos.getYPos() >= yBot && bottomLeftPos.getXPos() <= xLeft){
				if(actualBottomRight.getYPos() >= yBot && actualBottomRight.getXPos() >= xRight){
					int x = random.nextInt(xRight-xLeft) + xLeft;
					int y = yBot;
					generatedPositions.add(new Position(x, y));
				}
				else if(actualBottomRight.getYPos() >= yBot && actualBottomRight.getXPos() <= xRight){
					int x = random.nextInt(xRight-xLeft) + xLeft;
					int y = yBot;
					generatedPositions.add(new Position(x, y));
					if(actualBottomRight.getXPos() != xRight && x != xRight){
						x = xRight;
						generatedPositions.add(new Position(x, y));
					}
				}
				else if(actualBottomRight.getYPos() <= yBot && actualBottomRight.getXPos() > xRight){
					int x = random.nextInt(xRight-xLeft) + xLeft;
					int y = yBot;
					generatedPositions.add(new Position(x, y));
					if(actualBottomRight.getYPos() != yBot){
						x = random.nextInt(xMax-xRight) + xRight;
						generatedPositions.add(new Position(x, y));
					}
				}
			}
			else if(bottomLeftPos.getYPos() >= yBot && bottomLeftPos.getXPos() >= xLeft){
				if(actualBottomRight.getYPos() >= yBot && actualBottomRight.getXPos() >= xRight){
					int x = random.nextInt(xRight-xLeft) + xLeft;
					int y = yBot;
					generatedPositions.add(new Position(x, y));
					if(bottomLeftPos.getXPos() != xLeft && x != xLeft){
						x = xLeft;
						generatedPositions.add(new Position(x, y));
					}
				}
				else if(actualBottomRight.getYPos() >= yBot && actualBottomRight.getXPos() <= xRight){
					if(bottomLeftPos.getYPos() != yBot && bottomLeftPos.getXPos() != xLeft){
						int y = yBot;
//						int x = random.nextInt(xLeft-xMin) + xMin;
						int x = xLeft;
						generatedPositions.add(new Position(x, y));
					}
					if(actualBottomRight.getYPos() != yBot && actualBottomRight.getXPos() != xRight){
						int y = yBot;
//						int x = random.nextInt(xMax-xRight) + xRight;
						int x = xRight;
						generatedPositions.add(new Position(x, y));
					}
				}
				else if(actualBottomRight.getYPos() <= yBot && actualBottomRight.getXPos() > xRight){
					if(bottomLeftPos.getYPos() != yBot && bottomLeftPos.getXPos() != xLeft){
						int y = yBot;
//						int x = random.nextInt(xLeft-xMin) + xMin;
						int x = xLeft;
						generatedPositions.add(new Position(x, y));
					}
					if(actualBottomRight.getYPos() != yBot && actualBottomRight.getXPos() != xRight){
						int y = yBot;
//						int x = random.nextInt(xMax-xRight) + xRight;
						int x = xRight;
						generatedPositions.add(new Position(x, y));
					}
				}
			}
			else if(bottomLeftPos.getYPos() <= yBot && bottomLeftPos.getXPos() <= xLeft){
				if(actualBottomRight.getYPos() >= yBot && actualBottomRight.getXPos() >= xRight){
					int x = random.nextInt(xRight-xLeft) + xLeft;
					int y = yBot;
					generatedPositions.add(new Position(x, y));
					if(bottomLeftPos.getYPos() != yBot){
						x = random.nextInt(xLeft-xMin) + xMin;
						generatedPositions.add(new Position(x, y));
					}
				}
				else if(actualBottomRight.getYPos() >= yBot && actualBottomRight.getXPos() <= xRight){
					if(bottomLeftPos.getYPos() != yBot && bottomLeftPos.getXPos() != xLeft){
						int y = yBot;
//						int x = random.nextInt(xLeft-xMin) + xMin;
						int x = xLeft;
						generatedPositions.add(new Position(x, y));
					}
					if(actualBottomRight.getYPos() != yBot && actualBottomRight.getXPos() != xRight){
						int y = yBot;
//						int x = random.nextInt(xMax-xRight) + xRight;
						int x = xRight;
						generatedPositions.add(new Position(x, y));
					}
				}
				else if(actualBottomRight.getYPos() <= yBot && actualBottomRight.getXPos() > xRight){
					if(bottomLeftPos.getYPos() != yBot && actualBottomRight.getYPos() != yBot && bottomLeftPos.getXPos() != xLeft && actualBottomRight.getXPos() != xRight){
						double k1 = ((yBot-bottomLeftPos.getYPos())/(xLeft-bottomLeftPos.getXPos()));
						double m1 = bottomLeftPos.getYPos() - bottomLeftPos.getXPos()*k1;
						
						double k2 = ((actualBottomRight.getYPos() - yBot)/(actualBottomRight.getXPos() - xRight));
						double m2 = actualBottomRight.getYPos() - actualBottomRight.getXPos()*k2;
						
						int x = (int) ((m2-m1)/(k1-k2)); //TODO borde dubbelkolla detta
						int y = (int) (k1 * x + m1);
						if(y > yMax){
							y = yMax;
							int x1 = (int) ((y-m1)/k1);
							int x2 = (int) ((y-m2)/k2);
							if(x1 < xMin){
								x1 = xMin;
							}
							if(x1 > xMax){
								x1 = xMax;
							}
							if(x2 < xMin){
								x2 = xMin;
							}
							if(x2 > xMax){
								x2 = xMax;
							}
							if(y < yMin){
								y = yMin;
							}
							if(y > yMax){
								y = yMax;
							}
							generatedPositions.add(new Position(x1, y));
							generatedPositions.add(new Position(x2, y));
						}
						else{
							if(x < xMin){
								x = xMin;
							}
							if(x > xMax){
								x = xMax;
							}
							if(y < yMin){
								y = yMin;
							}
							if(y > yMax){
								y = yMax;
							}
							generatedPositions.add(new Position(x, y));
						}
					}
					else if(bottomLeftPos.getYPos() != yBot && bottomLeftPos.getXPos() != xLeft){
						double k = ((yBot-bottomLeftPos.getYPos())/(xLeft-bottomLeftPos.getXPos()));
						double m = bottomLeftPos.getYPos() - bottomLeftPos.getXPos()*k;
						
						int y = random.nextInt(yBot-yMin) + yMin;
						int x = (int) ((y-m)/k);
						if(x < xMin){
							x = xMin;
						}
						if(x > xMax){
							x = xMax;
						}
						if(y < yMin){
							y = yMin;
						}
						if(y > yMax){
							y = yMax;
						}
						generatedPositions.add(new Position(x, y));
					}
					else if(actualBottomRight.getYPos() != yBot && actualBottomRight.getXPos() != xRight){
						double k = ((actualBottomRight.getYPos() - yBot)/(actualBottomRight.getXPos() - xRight));
						double m = actualBottomRight.getYPos() - actualBottomRight.getXPos()*k;
						
						int y = random.nextInt(yBot-yMin) + yMin;
						int x = (int) ((y-m)/k);
						if(x < xMin){
							x = xMin;
						}
						if(x > xMax){
							x = xMax;
						}
						if(y < yMin){
							y = yMin;
						}
						if(y > yMax){
							y = yMax;
						}
						generatedPositions.add(new Position(x, y));
					}
				}
			}
		}
		// /BOTTOM
		// LEFT
		Position actualBottomLeft = bottomLeftPos;
		Position actualTopLeft = topLeftPos;
		if(!generatedPositions.isEmpty()){
			ArrayList<Position> temp = new ArrayList<Position>(generatedPositions);
			temp.add(bottomLeftPos);
			if(topLeftPos.getYPos() <= yTop){
				temp.add(topLeftPos);
			}
			temp = sortClockwise(temp);
			actualBottomLeft = temp.get(temp.size()-1);
			if(topLeftPos.getYPos() <= yTop){
				actualTopLeft = temp.get(0);
			}
		}
		
		if(!(actualTopLeft.getXPos() == xLeft && actualBottomLeft.getXPos() == xLeft)){
			if(actualTopLeft.getXPos() >= xLeft && actualTopLeft.getYPos() <= yTop){
				if(actualBottomLeft.getXPos() >= xLeft && actualBottomLeft.getYPos() >= yBot){
					if(actualTopLeft.getXPos() != xLeft && actualBottomLeft.getXPos() != xLeft && actualTopLeft.getYPos() != yTop && actualBottomLeft.getYPos() != yBot){
						double k1 = ((actualBottomLeft.getYPos() - yBot)/(actualBottomLeft.getXPos() - xLeft));
						double m1 = actualBottomLeft.getYPos() - actualBottomLeft.getXPos()*k1;
						
						double k2 = ((actualTopLeft.getYPos() - yTop)/(actualTopLeft.getXPos() - xLeft));
						double m2 = actualTopLeft.getYPos() - actualTopLeft.getXPos()*k2;
						
						int x = (int) ((m2-m1)/(k1-k2)); //TODO borde dubbelkolla detta
						if(x < xMin){
							// outside of the drawing area
							x = xMin;
							int y1 = (int) (k1 * x + m1);
							int y2 = (int) (k2 * x + m2);
							if(x < xMin){
								x = xMin;
							}
							if(x > xMax){
								x = xMax;
							}
							if(y1 < yMin){
								y1 = yMin;
							}
							if(y1 > yMax){
								y1 = yMax;
							}
							if(y2 < yMin){
								y2 = yMin;
							}
							if(y2 > yMax){
								y2 = yMax;
							}
							generatedPositions.add(new Position(x, y1));
							generatedPositions.add(new Position(x, y2));
						}
						else{
							int y = (int) (k1 * x + m1);
							if(x < xMin){
								x = xMin;
							}
							if(x > xMax){
								x = xMax;
							}
							if(y < yMin){
								y = yMin;
							}
							if(y > yMax){
								y = yMax;
							}
							generatedPositions.add(new Position(x, y));
						}
					}
					else if(actualTopLeft.getXPos() != xLeft && actualTopLeft.getYPos() != yTop){
						double k = ((actualTopLeft.getYPos() - yTop)/(actualTopLeft.getXPos() - xLeft));
						double m = actualTopLeft.getYPos() - actualTopLeft.getXPos()*k;
						
						int y = random.nextInt(yTop-yMin) + yMin;
						int x = (int) ((y-m)/k);
						if(x < xMin){
							x = xMin;
						}
						if(x > xMax){
							x = xMax;
						}
						if(y < yMin){
							y = yMin;
						}
						if(y > yMax){
							y = yMax;
						}
						generatedPositions.add(new Position(x, y));
					}
					else if(actualBottomLeft.getXPos() != xLeft && actualBottomLeft.getYPos() != yBot){
						double k = ((actualBottomLeft.getYPos() - yBot)/(actualBottomLeft.getXPos() - xLeft));
						double m = actualBottomLeft.getYPos() - actualBottomLeft.getXPos()*k;
						
						int y = random.nextInt(yTop-yMin) + yMin;
						int x = (int) ((y-m)/k);
						if(x < xMin){
							x = xMin;
						}
						if(x > xMax){
							x = xMax;
						}
						if(y < yMin){
							y = yMin;
						}
						if(y > yMax){
							y = yMax;
						}
						generatedPositions.add(new Position(x, y));
					}
				}
				else if(actualBottomLeft.getXPos() <= xLeft && actualBottomLeft.getYPos() >= yBot){
					int y = random.nextInt(yBot-yTop) + yTop;
					int x = xLeft;
					generatedPositions.add(new Position(x, y));
					if(actualTopLeft.getXPos() != xLeft){
						y = random.nextInt(yTop-yMin) + yMin;
						generatedPositions.add(new Position(x, y));
					}
				}
				else if(actualBottomLeft.getXPos() <= xLeft && actualBottomLeft.getYPos() <= yBot){
					if(topRightPos.getYPos() != yTop && topRightPos.getXPos() != xLeft){
						int x = xLeft;
						int y = yTop;
						generatedPositions.add(new Position(x, y));
					}
					if(actualBottomLeft.getYPos() != yBot && actualBottomLeft.getXPos() != xLeft){
						int x = xLeft;
						int y = yBot;
						generatedPositions.add(new Position(x, y));
					}
				}
			}
			else if(actualTopLeft.getXPos() <= xLeft && actualTopLeft.getYPos() <= yTop){
				if(actualBottomLeft.getXPos() >= xLeft && actualBottomLeft.getYPos() >= yBot){
					int y = random.nextInt(yBot-yTop) + yTop;
					int x = xLeft;
					generatedPositions.add(new Position(x, y));
					if(actualBottomLeft.getXPos() != xLeft){
						y = random.nextInt(yMax-yBot) + yBot;
						generatedPositions.add(new Position(x, y));
					}
				}
				else if(actualBottomLeft.getXPos() <= xLeft && actualBottomLeft.getYPos() >= yBot){
					int y = random.nextInt(yBot-yTop) + yTop;
					int x = xLeft;
					generatedPositions.add(new Position(x, y));
				}
				else if(actualBottomLeft.getXPos() <= xLeft && actualBottomLeft.getYPos() <= yBot){
					int y = random.nextInt(yBot-yTop) + yTop;
					int x = xLeft;
					generatedPositions.add(new Position(x, y));
					if(actualBottomLeft.getYPos() != yBot && y != yBot){
						y = yBot;
						generatedPositions.add(new Position(x, y));
					}
				}
			}
			else if(actualTopLeft.getXPos() <= xLeft && actualTopLeft.getYPos() >= yTop){
				if(actualBottomLeft.getXPos() >= xLeft && actualBottomLeft.getYPos() >= yBot){
					if(topRightPos.getYPos() != yTop && topRightPos.getXPos() != xLeft){
						int x = xLeft;
						int y = yTop;
						generatedPositions.add(new Position(x, y));
					}
					if(actualBottomLeft.getYPos() != yBot && actualBottomLeft.getXPos() != xLeft){
						int x = xLeft;
						int y = yBot;
						generatedPositions.add(new Position(x, y));
					}
				}
				else if(actualBottomLeft.getXPos() <= xLeft && actualBottomLeft.getYPos() >= yBot){
					int y = random.nextInt(yBot-yTop) + yTop;
					int x = xLeft;
					generatedPositions.add(new Position(x, y));
					if(actualTopLeft.getYPos() != yTop && y != yTop){
						y = yTop;
						generatedPositions.add(new Position(x, y));
					}
				}
				else if(actualBottomLeft.getXPos() <= xLeft && actualBottomLeft.getYPos() <= yBot){
					if(topRightPos.getYPos() != yTop && topRightPos.getXPos() != xLeft){
						int x = xLeft;
						int y = yTop;
						generatedPositions.add(new Position(x, y));
					}
					if(actualBottomLeft.getYPos() != yBot && actualBottomLeft.getXPos() != xLeft){
						int x = xLeft;
						int y = yBot;
						generatedPositions.add(new Position(x, y));
					}
				}
			}
		}
		// /LEFT
		
		while(amountOfDots > 0){
			int ri = random.nextInt(4);
			if(ri == 0){
				//top
				int x = random.nextInt(xMax-xMin) + xMin;
				int y = random.nextInt(yTop-yMin) + yMin;
				generatedPositions.add(new Position(x, y));
			}
			else if(ri == 1){
				//right
				int x = random.nextInt(xMax-xRight) + xRight;
				int y = random.nextInt(yMax-yMin) + yMin;
				generatedPositions.add(new Position(x, y));
			}
			else if(ri == 2){
				//bottom
				int x = random.nextInt(xMax-xMin) + xMin;
				int y = random.nextInt(yMax-yBot) + yBot;
				generatedPositions.add(new Position(x, y));
			}
			else if(ri == 3){
				//left
				int x = random.nextInt(xLeft-xMin) + xMin;
				int y = random.nextInt(yMax-yMin) + yMin;
				generatedPositions.add(new Position(x, y));
			}
			amountOfDots--;
		}
		generatedPositions.add(topLeftPos);
		generatedPositions.add(topRightPos);
		generatedPositions.add(bottomRightPos);
		generatedPositions.add(bottomLeftPos);
		return sortClockwise(generatedPositions);
	}
	
	public ArrayList<Position> sortClockwise(ArrayList<Position> unsorted){
		ArrayList<Position> sorted = new ArrayList<Position>();
		
		ArrayList<Position> topLeftCorner = new ArrayList<Position>();
		ArrayList<Position> topLeftCornerSorted = new ArrayList<Position>();
		
		ArrayList<Position> top = new ArrayList<Position>();
		ArrayList<Position> topSorted = new ArrayList<Position>();
		
		ArrayList<Position> topRightCorner = new ArrayList<Position>();
		ArrayList<Position> topRightCornerSorted = new ArrayList<Position>();
		
		ArrayList<Position> right = new ArrayList<Position>();
		ArrayList<Position> rightSorted = new ArrayList<Position>();
		
		ArrayList<Position> bottomRightCorner = new ArrayList<Position>();
		ArrayList<Position> bottomRightCornerSorted = new ArrayList<Position>();
		
		ArrayList<Position> bottom = new ArrayList<Position>();
		ArrayList<Position> bottomSorted = new ArrayList<Position>();
		
		ArrayList<Position> bottomLeftCorner = new ArrayList<Position>();
		ArrayList<Position> bottomLeftCornerSorted = new ArrayList<Position>();
		
		ArrayList<Position> left = new ArrayList<Position>();
		ArrayList<Position> leftSorted = new ArrayList<Position>();
		
		for(Position p: unsorted){
			if(p.getXPos() <= xLeft){
				if(p.getYPos() <= yTop){
					topLeftCorner.add(p);
				}
				else if(p.getYPos() >= yBot){
					bottomLeftCorner.add(p);
				}
				else{
					left.add(p);
				}
			}
			else if(p.getXPos() >= xRight){
				if(p.getYPos() <= yTop){
					topRightCorner.add(p);
				}
				else if(p.getYPos() >= yBot){
					bottomRightCorner.add(p);
				}
				else{
					right.add(p);
				}
			}
			else{
				if(p.getYPos() <= yTop){
					top.add(p);
				}
				else{
					bottom.add(p);
				}
			}
		}
		//sort top-left
		Position temp;
		while(!topLeftCorner.isEmpty()){
			temp = null;
			for(Position p : topLeftCorner){
				if(topLeftCornerSorted.contains(p)){
					topLeftCorner.remove(p);
					temp = null;
					break;
				}
				if(temp == null){
					temp = p;
				}
				else{
					if(p.getYPos() > temp.getYPos()){
						temp = p;
					}
					else if(p.getYPos() == temp.getYPos() && p.getXPos() < temp.getXPos()){
						temp = p;
					}
				}
			}
			if(temp != null){
				topLeftCornerSorted.add(temp);
				topLeftCorner.remove(temp);
			}
		}
		
		while(!top.isEmpty()){
			temp = null;
			for(Position p : top){
				if(topSorted.contains(p)){
					top.remove(p);
					temp = null;
					break;
				}
				if(temp == null){
					temp = p;
				}
				else{
					if(p.getXPos() < temp.getXPos()){
						temp = p;
					}
					else if(p.getXPos() == temp.getXPos() && p.getYPos() > temp.getYPos()){
						temp = p;
					}
				}
			}
			if(temp != null){
				topSorted.add(temp);
				top.remove(temp);
			}
		}
		
		while(!topRightCorner.isEmpty()){
			temp = null;
			for(Position p : topRightCorner){
				if(topRightCornerSorted.contains(p)){
					topRightCorner.remove(p);
					temp = null;
					break;
				}
				if(temp == null){
					temp = p;
				}
				else{
					if(p.getYPos() < temp.getYPos()){
						temp = p;
					}
					else if(p.getYPos() == temp.getYPos() && p.getXPos() < temp.getXPos()){
						temp = p;
					}
				}
			}
			if(temp != null){
				topRightCornerSorted.add(temp);
				topRightCorner.remove(temp);
			}
		}
		
		while(!right.isEmpty()){
			temp = null;
			for(Position p : right){
				if(rightSorted.contains(p)){
					right.remove(p);
					temp = null;
					break;
				}
				if(temp == null){
					temp = p;
				}
				else{
					if(p.getYPos() < temp.getYPos()){
						temp = p;
					}
					else if(p.getYPos() == temp.getYPos() && p.getXPos() < temp.getXPos()){
						temp = p;
					}
				}
			}
			if(temp != null){
				rightSorted.add(temp);
				right.remove(temp);
			}
		}
		
		while(!bottomRightCorner.isEmpty()){
			temp = null;
			for(Position p : bottomRightCorner){
				if(bottomRightCornerSorted.contains(p)){
					bottomRightCorner.remove(p);
					temp = null;
					break;
				}
				if(temp == null){
					temp = p;
				}
				else{
					if(p.getYPos() < temp.getYPos()){
						temp = p;
					}
					else if(p.getYPos() == temp.getYPos() && p.getXPos() > temp.getXPos()){
						temp = p;
					}
				}
			}
			if(temp != null){
				bottomRightCornerSorted.add(temp);
				bottomRightCorner.remove(temp);
			}
		}
		
		while(!bottom.isEmpty()){
			temp = null;
			for(Position p : bottom){
				if(bottomSorted.contains(p)){
					bottom.remove(p);
					temp = null;
					break;
				}
				if(temp == null){
					temp = p;
				}
				else{
					if(p.getXPos() > temp.getXPos()){
						temp = p;
					}
					else if(p.getXPos() == temp.getXPos() && p.getYPos() < temp.getYPos()){
						temp = p;
					}
				}
			}
			if(temp != null){
				bottomSorted.add(temp);
				bottom.remove(temp);
			}
		}
		
		while(!bottomLeftCorner.isEmpty()){
			temp = null;
			for(Position p : bottomLeftCorner){
				if(bottomLeftCornerSorted.contains(p)){
					bottomLeftCorner.remove(p);
					temp = null;
					break;
				}
				if(temp == null){
					temp = p;
				}
				else{
					if(p.getYPos() > temp.getYPos()){
						temp = p;
					}
					else if(p.getYPos() == temp.getYPos() && p.getXPos() > temp.getXPos()){
						temp = p;
					}
				}
			}
			if(temp != null){
				bottomLeftCornerSorted.add(temp);
				bottomLeftCorner.remove(temp);
			}
		}
		
		while(!left.isEmpty()){
			temp = null;
			for(Position p : left){
				if(leftSorted.contains(p)){
					left.remove(p);
					temp = null;
					break;
				}
				if(temp == null){
					temp = p;
				}
				else{
					if(p.getYPos() > temp.getYPos()){
						temp = p;
					}
					else if(p.getYPos() == temp.getYPos() && p.getXPos() > temp.getXPos()){
						temp = p;
					}
				}
			}
			if(temp != null){
				leftSorted.add(temp);
				left.remove(temp);
			}
		}
		
		sorted.addAll(topLeftCornerSorted);
		sorted.addAll(topSorted);
		sorted.addAll(topRightCornerSorted);
		sorted.addAll(rightSorted);
		sorted.addAll(bottomRightCornerSorted);
		sorted.addAll(bottomSorted);
		sorted.addAll(bottomLeftCornerSorted);
		sorted.addAll(leftSorted);
//		Log.i("TAG", "sort output: " + sorted.size());
//		for(int i = 0; i < sorted.size(); i++){
//			Log.i("TAG", "     " + i + ": (" + sorted.get(i).getXPos() + ", " + sorted.get(i).getYPos() + ")");
//		}
		return sorted;
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
	
	@Override
  protected void onDraw(Canvas canvas) {
//		Log.i("TAG", "onDraw! " + (bottomPath != null));
		canvas.drawPath(path, paint);
	}

}
