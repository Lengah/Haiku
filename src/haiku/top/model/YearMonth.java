package haiku.top.model;

public class YearMonth {
	private int year;
	private Month month;
	
	public YearMonth(int year, Month month){
		this.year = year;
		this.month = month;
	}
	
	public int getYear(){
		return year;
	}
	
	public Month getMonth(){
		return month;
	}
}
