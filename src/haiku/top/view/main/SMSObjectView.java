// Old code. It has been replaced by the class SMSObject in the packet haiku.top.view.main.sms

//package haiku.top.view.main;
//
//import java.util.Date;
//import java.util.DuplicateFormatFlagsException;
//
//import haiku.top.HaikuActivity;
//import haiku.top.R;
//import haiku.top.model.Theme;
//import haiku.top.model.smshandler.SMS;
//import android.content.Context;
//import android.database.Cursor;
//import android.graphics.Color;
//import android.graphics.Typeface;
//import android.util.Log;
//import android.view.Gravity;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.ListView;
//import android.widget.ScrollView;
//import android.widget.TextView;
//import android.widget.LinearLayout.LayoutParams;
//
//public class SMSObjectView extends LinearLayout{
//	private TextView message;
//	private TextView date;
//	private Context context;
//	private boolean sent;
//	private SMS sms;
//	
//	
//	public SMSObjectView(Context context, String sentString, SMS sms) {
//		super(context);
//		this.context = context;
//		if (sentString.equals("1")) {
//	        sent = true;
//	    }
//		else {
//	        sent = false;
//	    }
//		this.sms = sms;
//		
//		LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//		if(sent){
//			layoutInflater.inflate(R.layout.item_msglist_sent,this);
//		}
//		else{
//			layoutInflater.inflate(R.layout.item_msglist_received,this);
//		}
//		
//		message = (TextView)findViewById(R.id.msgtext);
//		date = (TextView)findViewById(R.id.txtDate);
//		
//		message.setText(sms.getMessage());
//		date.setText(sms.getFullDate());
//	}
//	
//	public SMS getSMS(){
//		return sms;
//	}
//}
