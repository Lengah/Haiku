package haiku.top.model.date;


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
	
	@Override
	public boolean equals(Object yearMonth){
		if(yearMonth instanceof YearMonth && this.year == ((YearMonth)yearMonth).getYear() && this.month == ((YearMonth)yearMonth).getMonth()){
			return true;
		}
		return false;
	}
}
