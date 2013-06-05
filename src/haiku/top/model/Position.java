package haiku.top.model;

public class Position {
	private float xPos;
	private float yPos;
	
	public Position(){}
	
	public Position(float xPos, float yPos){
		this.xPos = xPos;
		this.yPos = yPos;
	}
	
	public float getXPos(){
		return xPos;
	}
	
	public float getYPos(){
		return yPos;
	}
	
	public void setXPos(float xPos){
		this.xPos = xPos;
	}
	
	public void setYPos(float yPos){
		this.yPos = yPos;
	}
}
