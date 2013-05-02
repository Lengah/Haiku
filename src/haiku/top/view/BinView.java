package haiku.top.view;

import java.util.ArrayList;

import haiku.top.R;
import haiku.top.model.HaikuGenerator;
import haiku.top.model.SMS;
import haiku.top.model.Theme;
import haiku.top.model.YearMonth;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class BinView extends RelativeLayout implements OnClickListener, OnLongClickListener, OnTouchListener{
	private Context context;
	
	private HaikuProgressBar progressBar;
	
	private ScrollView dateScroll;
	private LinearLayout dateList;
	
	private ScrollView themeScroll;
	private LinearLayout themeList;
	
	private TextView contactName;
	private ScrollView textScroll;
	private LinearLayout textList;
	
	private ImageButton saveButton; //TODO
	
	private ArrayList<YearMonth> dates;
	private ArrayList<SMS> sms;
	private ArrayList<Theme> themes;
	
	private ArrayList<YearMonthView> datesView = new ArrayList<YearMonthView>();
	//TODO sms view, extenda textview?
	private ArrayList<ThemeObjectView> themesView = new ArrayList<ThemeObjectView>();
	
	private static final int DATE_WIDTH = 75; //in dp
	private static final int DATE_HEIGHT = 50; //in dp

	public BinView(Context context) {
		super(context);
		this.context = context;
		LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutInflater.inflate(R.layout.bin_view,this);
		
		dateScroll = (ScrollView)findViewById(R.id.dateScroll);
		dateList = (LinearLayout)findViewById(R.id.listofdates);
		
		themeScroll = (ScrollView)findViewById(R.id.themeScroll);
		themeList = (LinearLayout)findViewById(R.id.listofthemes);
		
		contactName = (TextView)findViewById(R.id.bincontactname);
		textScroll = (ScrollView)findViewById(R.id.textScroll);
		textList = (LinearLayout)findViewById(R.id.listoftext);
		
		saveButton = (ImageButton)findViewById(R.id.saveButton);
		
		saveButton.setImageResource(R.drawable.save_button);
		saveButton.setVisibility(GONE);
	}
	
	public void update(){
		dates = HaikuGenerator.getDates();
		sms = HaikuGenerator.getAllAddedSMS();
		themes = HaikuGenerator.getThemes();
		
		boolean onlyOneContact = true; // if there only is one contact in the bin its name will be shown
		
		dateList.removeAllViews();
		textList.removeAllViews();
		themeList.removeAllViews();
		
		datesView.clear();
		themesView.clear();
		
		YearMonthView ymv;
		for(int i = 0; i < dates.size(); i++){
			ymv = new YearMonthView(context, dates.get(i), DATE_WIDTH, DATE_HEIGHT);
			datesView.add(ymv);
		}
	}
	
	public void haikuReady(){
		//TODO
		saveButton.setVisibility(VISIBLE);
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		return false;
	}

	@Override
	public boolean onLongClick(View arg0) {
		return false;
	}

	@Override
	public void onClick(View v) {
		
	}

}
