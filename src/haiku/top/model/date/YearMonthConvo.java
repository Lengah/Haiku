package haiku.top.model.date;

public class YearMonthConvo {
	private YearMonth yearMonth;
	private int threadID;
	
	public YearMonthConvo(YearMonth yearMonth, int threadID){
		this.yearMonth = yearMonth;
		this.threadID = threadID;
	}
	
	public YearMonth getYearMonth(){
		return yearMonth;
	}
	
	public int getThreadID(){
		return threadID;
	}
	
	@Override
	public boolean equals(Object yearMonthConvo){
		return (yearMonthConvo instanceof YearMonthConvo 
				&& this.yearMonth.equals(((YearMonthConvo)yearMonthConvo).yearMonth) 
				&& this.threadID == ((YearMonthConvo)yearMonthConvo).threadID);
	}
}
