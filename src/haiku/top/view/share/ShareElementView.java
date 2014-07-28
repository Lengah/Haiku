package haiku.top.view.share;

import haiku.top.HaikuActivity;
import haiku.top.model.ShareApp;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ShareElementView extends LinearLayout{
	private ShareApp shareApp;
	private static final int HEIGHT_IN_DP = 60;

	public ShareElementView(Context context, ShareApp shareApp) {
		super(context);
		this.shareApp = shareApp;
		setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, (int) HaikuActivity.convertDpToPixel(HEIGHT_IN_DP)));
		
		setOrientation(HORIZONTAL);
		
		ImageView icon = new ImageView(context);
		icon.setImageDrawable(shareApp.getIcon());
		
		LayoutParams iconParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
		iconParams.setMargins((int)HaikuActivity.convertDpToPixel(10), 0, (int)HaikuActivity.convertDpToPixel(5), 0);
		addView(icon, iconParams);
		
		TextView name = new TextView(context);
		name.setText(" " + shareApp.getName());
		name.setTextColor(Color.WHITE);
		name.setGravity(Gravity.CENTER_VERTICAL);
		
		LayoutParams nameParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		addView(name, nameParams);
	}

	public ShareApp getShareApp(){
		return shareApp;
	}
	
	public void setPressed(){
		setBackgroundColor(ShareView.COLOR_TITLE);
	}
	
	public void resetPressed(){
		setBackgroundColor(Color.TRANSPARENT);
	}
}
