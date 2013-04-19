package haiku.top.view;

import android.content.Context;
import android.widget.LinearLayout;

public class DateView extends LinearLayout{
	private QuarterCircle yearView;
	
	public DateView(Context context) {
		super(context);
		yearView = new QuarterCircle(context, 150);
		yearView.setText("2013");
	}
	
	public QuarterCircle getYearView(){
		return yearView;
	}
}
