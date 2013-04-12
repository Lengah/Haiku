package haiku.top.view;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;

import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.View.OnDragListener;
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

public class MainView extends LinearLayout implements OnClickListener, OnLongClickListener, OnDragListener{
	private Context context;
	private static final int ANIMATION_TIME_THEME = 300;
	private static final int ANIMATION_TIME_DATE = 300;
	
	private Button themeButton;
	private ScrollView themeView;
	
	private ListView contactList;
	
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
	}
	
	private TranslateAnimation translateAnimation;
	
	public void openThemeView(){
		updateThemeView();
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
		list.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		ThemeObjectView themeObject;
		for(int i = 0; i < themes.size(); i++){
			themeObject = new ThemeObjectView(context, themes.get(i));
			list.addView(themeObject);
			themeObject.setOnLongClickListener(this);
			themeObject.setOnDragListener(this);
		}
		themeView.addView(list);
		list.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				closeThemeView();
			}
		});
	}

	@Override
	public void onClick(View v) {
		if(v.equals(themeButton)){
			openThemeView();
		}
	}

	@Override
	public boolean onLongClick(View v) {
		v.setVisibility(GONE);

		    // Starts the drag

		            v.startDrag(null,  // the data to be dragged
		            		new DragShadowBuilder(v),  // the drag shadow builder
		                        null,      // no need to use local data
		                        0          // flags (not currently used, set to 0)
		            );

		return false;
	}

	@Override
	public boolean onDrag(View v, DragEvent event) {
		final int action = event.getAction();
		if(action == DragEvent.ACTION_DRAG_STARTED){
			Log.i("TAG", "Started");
			return true;
		}
		else if(action == DragEvent.ACTION_DRAG_ENTERED){
			Log.i("TAG", "Entered");
			return true;
		}
		else if(action == DragEvent.ACTION_DRAG_LOCATION){
			Log.i("TAG", "Location");
			return true;
		}
		else if(action == DragEvent.ACTION_DRAG_EXITED){
			Log.i("TAG", "Exited");
			return true;
		}
		else if(action == DragEvent.ACTION_DROP){
			Log.i("TAG", "Drop");
//			// Gets the item containing the dragged data
//            ClipData.Item item = event.getClipData().getItemAt(0);
//
//            // Gets the text data from the item.
//            dragData = item.getText();
//
//            // Displays a message containing the dragged data.
//            Toast.makeText(this, "Dragged data is " + dragData, Toast.LENGTH_LONG);
//
//            // Turns off any color tints
//            v.clearColorFilter();
//
//            // Invalidates the view to force a redraw
//            v.invalidate();
			return true;
		}
		else if(action == DragEvent.ACTION_DRAG_ENDED){
			Log.i("TAG", "Ended");
//			updateThemeView();
//		    // Turns off any color tinting
//            v.clearColorFilter();
//
//            // Invalidates the view to force a redraw
//            v.invalidate();
//
//            // Does a getResult(), and displays what happened.
//            if (event.getResult()) {
//                Toast.makeText(this, "The drop was handled.", Toast.LENGTH_LONG);
//
//            } else {
//                Toast.makeText(this, "The drop didn't work.", Toast.LENGTH_LONG);
//
//            };

			return true;
		}
		return false;
	}
}
