package haiku.top.view;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import haiku.top.HaikuActivity;
import haiku.top.R;
import haiku.top.model.HaikuGenerator;
import haiku.top.model.Theme;
import haiku.top.model.sql.DatabaseHandler;
import haiku.top.view.adapters.ContactListAdapter;

public class MainView extends LinearLayout implements OnClickListener{
	private Context context;
	private static final int ANIMATION_TIME_THEME = 300;
	private static final int ANIMATION_TIME_DATE = 300;
	
	private Button themeButton;
	private ScrollView themeView;
	
	private ListView contactList;
	
	private boolean choosingThemes = false;
	private boolean inBinRange = false; // used for drag and drop
	
	public MainView(Context context) {
		super(context);
		this.context = context;
		
		LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutInflater.inflate(R.layout.mainview,this);
		
		contactList = (ListView)findViewById(R.id.listofcontacts);
		themeButton = (Button)findViewById(R.id.themebutton);
		themeView = (ScrollView)findViewById(R.id.themeview);
		
		contactList.setAdapter(new ContactListAdapter(context, HaikuActivity.getThreads(context), true));
		

		themeButton.bringToFront();
		themeView.bringToFront();
		themeView.setVisibility(View.GONE);
		
		themeButton.setOnClickListener(this);
		themeView.setOnClickListener(this);
	}
	
	private TranslateAnimation translateAnimation;
	
	public void openThemeView(){
		updateThemeView();
		choosingThemes = true;
		themeButton.setEnabled(false);
		themeView.setVisibility(View.VISIBLE);
		translateAnimation = new TranslateAnimation(Animation.ABSOLUTE, (float)(100-25), Animation.RELATIVE_TO_SELF, (float)0, Animation.RELATIVE_TO_SELF, (float)0, Animation.RELATIVE_TO_SELF, 0);
		translateAnimation.setDuration(ANIMATION_TIME_THEME);
		themeView.startAnimation(translateAnimation);
		translateAnimation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationStart(Animation animation) {
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				
			}
		});
	}
	
	public void closeThemeView(){
		choosingThemes = false;
		translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.ABSOLUTE, (float)(100-25), Animation.RELATIVE_TO_SELF, (float)0, Animation.RELATIVE_TO_SELF, 0);
		translateAnimation.setDuration(ANIMATION_TIME_THEME);
		themeView.startAnimation(translateAnimation);
		translateAnimation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationStart(Animation animation) {
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				themeView.setVisibility(View.GONE);
				themeButton.setEnabled(true);
			}
		});
	}
	
	public void updateThemeView(){
		themeView.removeAllViews();
		ArrayList<Theme> themes;
//		themes = DatabaseHandler.getAllThemes();
		themes = new ArrayList<Theme>();
		themes.add(Theme.happy);
		themes.add(Theme.sad);
		themes.add(Theme.summer);
		themes.add(Theme.time);
		
		themes.removeAll(HaikuGenerator.getThemes()); // remove the themes that already are in the bin
		
		LinearLayout list = new LinearLayout(context);
		list.setOrientation(VERTICAL);
		list.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
		TextView themeText;
		for(int i = 0; i < themes.size(); i++){
			themeText = new TextView(context);
			themeText.setText(themes.get(i).toString());
			themeText.setTextColor(Color.BLACK);
			themeText.setTextSize(15);
			themeText.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
			list.addView(themeText);
		}
		themeView.addView(list);
	}

	@Override
	public void onClick(View v) {
		if(v.equals(themeButton)){
			openThemeView();
		}
		if(v.equals(themeView)){
			closeThemeView();
		}
	}
	
//	@Override
//	public boolean onTouch(View view, MotionEvent motionEvent) {
//		if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
//			ClipData clipData = ClipData.newPlainText("", "");
//			View.DragShadowBuilder dsb = new View.DragShadowBuilder(view);
//			view.startDrag(clipData, dsb, view, 0);
//			view.setVisibility(View.INVISIBLE);
//			return true;
//		} 
//		else {
//			return false;
//		}
//	}
}
