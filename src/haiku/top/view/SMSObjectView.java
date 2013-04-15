package haiku.top.view;

import java.util.DuplicateFormatFlagsException;

import haiku.top.HaikuActivity;
import haiku.top.R;
import haiku.top.model.Theme;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class SMSObjectView extends LinearLayout{
	private TextView message;
	private TextView date;
	private Context context;
	private boolean sent;
	private int id;
	
	
	public SMSObjectView(Context context, boolean sent, SMS sms) {
		super(context);
		this.context = context;
		this.sent = sent;
		this.message = message;
		this.date = date;
		this.id = id;
		
		LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutInflater.inflate(R.layout.item_contact,this);
		
		setBackgroundResource(android.R.drawable.list_selector_background);
		
		image = (TextView)findViewById(R.id.contactPic);
		nameView = (TextView)findViewById(R.id.contactname);
		nameView.setText(name);
	}
	
	public int getSMSID(){
		return id;
	}
	
	public String getName(){
		return name;
	}
}
