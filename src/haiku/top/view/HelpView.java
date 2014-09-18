package haiku.top.view;

import haiku.top.HaikuActivity;
import haiku.top.R;
import haiku.top.model.ShareApp;
import haiku.top.view.binview.BinView;
import haiku.top.view.main.MainView;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLayoutChangeListener;

public class HelpView extends LinearLayout implements OnClickListener{
	private static final String HELP_TITLE = "Instructions";
	public static final int COLOR_BACKGROUND = Color.rgb(54, 55, 53);
	public static final int COLOR_TITLE = Color.rgb(165, 209, 125);
	
	private static final String TEXT_1 = "Choose a conversation or messages within a conversation. Drag the chosen objects into the Haiku bin";
	private static final String TEXT_2 = "When inside a conversation, you may choose a theme. (Drag a theme into the Haiku bin). " +
			"Messages related to the theme will be added automatically to the bin. Tap to expand the Haiku bin.";
	private static final String TEXT_3 = "Adding a time span (e.g march 2013) will add all the messages written during that time.";
	private static final String TEXT_4 = "To create Haiku poem from selected texts; compress by pinching.";
	private static final String TEXT_5 = "When Haiku is generated, you can rearrange and remove words as you like. Save or share your Haiku!";
	
	private static final int I_PADDING_DP = 3;
	private static final int LINES_HEIGHT_DP = 1;
	
	private static final int PADDING_DP = 6;
	
	private Button closeB;
	private MainView mainView;
	
	private int viewWidth;
	private int viewHeight;
	
	private ScrollView sv;
	
	private static final double RATIO_1 = 1256.0/1500.0;
	private static final double RATIO_2 = 1259.0/1500.0; 
	private static final double RATIO_3 = 1260.0/1500.0; 
	private static final double RATIO_4 = 1066.0/1500.0; 
	private static final double RATIO_5 = 1248.0/1500.0; 

	public HelpView(Context context, MainView mainView) {
		super(context);
		this.mainView = mainView;
		setOrientation(VERTICAL);
		setBackgroundColor(COLOR_BACKGROUND);
		
		addOnLayoutChangeListener(new OnLayoutChangeListener() {
			
			@Override
			public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
				viewWidth = v.getWidth();
				viewHeight = v.getHeight();
				update();
			}
		});
	}
	
	public void onOpen(){
		if(sv != null){
			sv.scrollTo(0, 0);
		}
	}
	
	private boolean updated = false;
	
	public void update(){
		if(updated){
			return;
		}
		updated = true;
		
		setPadding((int) HaikuActivity.convertDpToPixel(PADDING_DP), 0, (int) HaikuActivity.convertDpToPixel(PADDING_DP), (int) HaikuActivity.convertDpToPixel(PADDING_DP));
		
		int imageWidth = viewWidth - 2 * (int) HaikuActivity.convertDpToPixel(PADDING_DP);
		
		RelativeLayout top = new RelativeLayout(getContext());
		
		TextView title = new TextView(getContext());
		title.setText(HELP_TITLE);
		title.setTextColor(COLOR_TITLE);
		title.setTextSize(22);
		title.setGravity(Gravity.CENTER_VERTICAL);
		
		closeB = new Button(getContext());
		closeB.setText("X");
		RelativeLayout.LayoutParams closeP = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		closeP.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		
		closeB.setOnClickListener(this);
		
		addView(top);
		
		top.addView(title, new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		top.addView(closeB, closeP);
		
		LinearLayout underTitle = new LinearLayout(getContext());
		LayoutParams paramsl1 = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int) HaikuActivity.convertDpToPixel(2));
		paramsl1.setMargins(0, (int) HaikuActivity.convertDpToPixel(2), 0, (int) HaikuActivity.convertDpToPixel(2));
		underTitle.setBackgroundColor(COLOR_TITLE);
		
		addView(underTitle, paramsl1);
		
		LinearLayout ll = new LinearLayout(getContext());
		ll.setOrientation(LinearLayout.VERTICAL);
		sv = new ScrollView(getContext());
		
		
		LayoutParams params1 = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		LayoutParams params2 = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		
		sv.addView(ll, params1);
		addView(sv, params2);
		
		//---------------------------------------
		// 1
		
		LinearLayout ll1 = new LinearLayout(getContext());
		ll1.setOrientation(VERTICAL);
		ll1.setPadding(0, (int) HaikuActivity.convertDpToPixel(I_PADDING_DP), 0, 0);
		
		TextView t1 = new TextView(getContext());
		t1.setText(TEXT_1);
		t1.setTextColor(Color.WHITE);
		
		ImageView i1 = new ImageView(getContext());
		i1.setImageResource(R.drawable.instructions_pic_01);
		
		View v1 = new View(getContext());
		v1.setBackgroundColor(COLOR_TITLE);
		
		View v1_2 = new View(getContext());
		
		ll1.addView(new View(getContext()), new LayoutParams(LayoutParams.MATCH_PARENT, 2 * (int) HaikuActivity.convertDpToPixel(PADDING_DP)));
		ll1.addView(t1, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		ll1.addView(i1, new LayoutParams(imageWidth, (int) (imageWidth*RATIO_1)));
		ll1.addView(v1_2, new LayoutParams(LayoutParams.MATCH_PARENT, 3 * (int) HaikuActivity.convertDpToPixel(LINES_HEIGHT_DP)));
		ll1.addView(v1, new LayoutParams(LayoutParams.MATCH_PARENT, (int) HaikuActivity.convertDpToPixel(LINES_HEIGHT_DP)));
		
		//---------------------------------------
		// 2
		
		LinearLayout ll2 = new LinearLayout(getContext());
		ll2.setOrientation(VERTICAL);
		ll2.setPadding(0, (int) HaikuActivity.convertDpToPixel(I_PADDING_DP), 0, 0);
		
		TextView t2 = new TextView(getContext());
		t2.setText(TEXT_2);
		t2.setTextColor(Color.WHITE);
		
		ImageView i2 = new ImageView(getContext());
		i2.setImageResource(R.drawable.instructions_pic_02);
		
		View v2 = new View(getContext());
		v2.setBackgroundColor(COLOR_TITLE);
		
		View v2_2 = new View(getContext());
		
		ll2.addView(new View(getContext()), new LayoutParams(LayoutParams.MATCH_PARENT, 2 * (int) HaikuActivity.convertDpToPixel(PADDING_DP)));
		ll2.addView(t2, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		ll2.addView(i2, new LayoutParams(imageWidth, (int) (imageWidth*RATIO_2)));
		ll2.addView(v2_2, new LayoutParams(LayoutParams.MATCH_PARENT, 3 * (int) HaikuActivity.convertDpToPixel(LINES_HEIGHT_DP)));
		ll2.addView(v2, new LayoutParams(LayoutParams.MATCH_PARENT, (int) HaikuActivity.convertDpToPixel(LINES_HEIGHT_DP)));
		
		//---------------------------------------
		// 3
		
		LinearLayout ll3 = new LinearLayout(getContext());
		ll3.setOrientation(VERTICAL);
		ll3.setPadding(0, (int) HaikuActivity.convertDpToPixel(I_PADDING_DP), 0, 0);
		
		TextView t3 = new TextView(getContext());
		t3.setText(TEXT_3);
		t3.setTextColor(Color.WHITE);
		
		ImageView i3 = new ImageView(getContext());
		i3.setImageResource(R.drawable.instructions_pic_03);
		
		View v3 = new View(getContext());
		v3.setBackgroundColor(COLOR_TITLE);
		
		View v3_2 = new View(getContext());
		
		ll3.addView(new View(getContext()), new LayoutParams(LayoutParams.MATCH_PARENT, 2 * (int) HaikuActivity.convertDpToPixel(PADDING_DP)));
		ll3.addView(t3, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		ll3.addView(i3, new LayoutParams(imageWidth, (int) (imageWidth*RATIO_3)));
		ll3.addView(v3_2, new LayoutParams(LayoutParams.MATCH_PARENT, 3 * (int) HaikuActivity.convertDpToPixel(LINES_HEIGHT_DP)));
		ll3.addView(v3, new LayoutParams(LayoutParams.MATCH_PARENT, (int) HaikuActivity.convertDpToPixel(LINES_HEIGHT_DP)));
		
		//---------------------------------------
		// 4
		
		LinearLayout ll4 = new LinearLayout(getContext());
		ll4.setOrientation(VERTICAL);
		ll4.setPadding(0, (int) HaikuActivity.convertDpToPixel(I_PADDING_DP), 0, 0);
		
		TextView t4 = new TextView(getContext());
		t4.setText(TEXT_4);
		t4.setTextColor(Color.WHITE);
		
		ImageView i4 = new ImageView(getContext());
		i4.setImageResource(R.drawable.instructions_pic_04);
		
		View v4 = new View(getContext());
		v4.setBackgroundColor(COLOR_TITLE);
		
		View v4_2 = new View(getContext());
		
		ll4.addView(new View(getContext()), new LayoutParams(LayoutParams.MATCH_PARENT, 2 * (int) HaikuActivity.convertDpToPixel(PADDING_DP)));
		ll4.addView(t4, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		ll4.addView(i4, new LayoutParams(imageWidth, (int) (imageWidth*RATIO_4)));
		ll4.addView(v4_2, new LayoutParams(LayoutParams.MATCH_PARENT, 3 * (int) HaikuActivity.convertDpToPixel(LINES_HEIGHT_DP)));
		ll4.addView(v4, new LayoutParams(LayoutParams.MATCH_PARENT, (int) HaikuActivity.convertDpToPixel(LINES_HEIGHT_DP)));
		
		//---------------------------------------
		// 5
		
		LinearLayout ll5 = new LinearLayout(getContext());
		ll5.setOrientation(VERTICAL);
		ll5.setPadding(0, (int) HaikuActivity.convertDpToPixel(I_PADDING_DP), 0, 0);
		
		TextView t5 = new TextView(getContext());
		t5.setText(TEXT_5);
		t5.setTextColor(Color.WHITE);
		
		ImageView i5 = new ImageView(getContext());
		i5.setImageResource(R.drawable.instructions_pic_05);
		
		View v5 = new View(getContext());
		v5.setBackgroundColor(COLOR_TITLE);
		
		View v5_2 = new View(getContext());
		
		ll5.addView(new View(getContext()), new LayoutParams(LayoutParams.MATCH_PARENT, 2 * (int) HaikuActivity.convertDpToPixel(PADDING_DP)));
		ll5.addView(t5, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		ll5.addView(i5, new LayoutParams(imageWidth, (int) (imageWidth*RATIO_5)));
		ll5.addView(v5_2, new LayoutParams(LayoutParams.MATCH_PARENT, 3 * (int) HaikuActivity.convertDpToPixel(LINES_HEIGHT_DP)));
		ll5.addView(v5, new LayoutParams(LayoutParams.MATCH_PARENT, (int) HaikuActivity.convertDpToPixel(LINES_HEIGHT_DP)));
		
		//---------------------------------------
		
		ll.addView(ll1);
		ll.addView(ll2);
		ll.addView(ll3);
		ll.addView(ll4);
		ll.addView(ll5);
	}

	@Override
	public void onClick(View v) {
		if(v.equals(closeB)){
			mainView.closeHelpView();
		}
	}
}
