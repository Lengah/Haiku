package haiku.top.view.share;

import haiku.top.HaikuActivity;
import haiku.top.model.ShareApp;
import haiku.top.view.main.MainView;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;

public class ShareView extends LinearLayout implements OnClickListener{
	private static final String SHARE_TITLE = " Share via";
	private static final String SHARE_SUBJECT = "Subject Here";
	private String shareMessage = "";
	public static final int COLOR_BACKGROUND = Color.rgb(54, 55, 53);
	public static final int COLOR_TITLE = Color.rgb(165, 209, 125);
	
	private ArrayList<ShareElementView> elements = new ArrayList<ShareElementView>();

	public ShareView(Context context) {
		super(context);
		setOrientation(VERTICAL);
		setBackgroundColor(COLOR_BACKGROUND);
		
		TextView title = new TextView(context);
		title.setText(SHARE_TITLE);
		title.setTextColor(COLOR_TITLE);
		title.setTextSize(22);
		title.setGravity(Gravity.CENTER_VERTICAL);
		
		addView(title);
		
		LinearLayout underTitle = new LinearLayout(context);
		LayoutParams paramsl1 = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int) HaikuActivity.convertDpToPixel(2));
		paramsl1.setMargins(0, (int) HaikuActivity.convertDpToPixel(2), 0, (int) HaikuActivity.convertDpToPixel(2));
		underTitle.setBackgroundColor(COLOR_TITLE);
		
		addView(underTitle, paramsl1);
		
		LinearLayout ll = new LinearLayout(context);
		ll.setOrientation(LinearLayout.VERTICAL);
		ScrollView sv = new ScrollView(context);
		
		LayoutParams params1 = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		LayoutParams params2 = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		
		sv.addView(ll, params1);
		addView(sv, params2);
		
		ArrayList<ShareApp> shareApps = HaikuActivity.getInstance().getAllShareApplications();
		if(shareApps.isEmpty()){
			Toast.makeText(HaikuActivity.getInstance().getApplicationContext(), "Could not find any applications to share with",Toast.LENGTH_LONG).show();
			MainView.getInstance().closeShareView();
		}
		ShareElementView sev;
		for(ShareApp sa : shareApps){
			sev = new ShareElementView(context, sa);
			elements.add(sev);
			ll.addView(sev);
			sev.setOnClickListener(this);
		}
	}
	
	public void onOpen(String message){
		shareMessage = message + "\n";
	    int spaces = message.length()/3;
	    for(int i = 0; i < spaces; i++){
	    	shareMessage += " ";
	    }
	    shareMessage += "-Haiku";
		for(ShareElementView sev : elements){
			sev.resetPressed();
		}
	}

	@Override
	public void onClick(View v) {
		if(v instanceof ShareElementView){
			Intent intent = new Intent(Intent.ACTION_SEND);
			intent.setType("text/plain");
		    intent.putExtra(android.content.Intent.EXTRA_SUBJECT, SHARE_SUBJECT);
		    intent.putExtra(android.content.Intent.EXTRA_TEXT, shareMessage);
		    intent.setClassName(((ShareElementView)v).getShareApp().getPackageName(), ((ShareElementView)v).getShareApp().getActivityInfoName());
		    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		    HaikuActivity.getInstance().startActivity(intent);
		    
		    MainView.getInstance().closeShareView();
		}
	}
	
	public void faceBookShare(ShareApp shareApp){
		
	}

}
