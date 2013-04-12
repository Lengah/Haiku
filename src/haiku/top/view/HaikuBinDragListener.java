package haiku.top.view;

import haiku.top.R;
import haiku.top.model.HaikuGenerator;
import haiku.top.model.Theme;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnDragListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class HaikuBinDragListener implements OnDragListener{
	private boolean inBinRange = false; // used for drag and drop
	private int inBinColor = Color.GREEN;
	private int notInBinColor = Color.WHITE;
	
	private View binView;

	public HaikuBinDragListener(View binView) {
		this.binView = binView;
	}
	
	public void updateColor(){
		if(inBinRange){
			binView.setBackgroundColor(inBinColor);
		}
		else{
			binView.setBackgroundColor(notInBinColor);
		}
	}

	@Override
	public boolean onDrag(View v, DragEvent event) {
		int action = event.getAction();
	    switch (action) {
	    	case DragEvent.ACTION_DRAG_STARTED:
	    		// Do nothing
	    		break;
	    	case DragEvent.ACTION_DRAG_ENTERED:
	    		inBinRange = true;
	    		updateColor();
	    		break;
	    	case DragEvent.ACTION_DRAG_EXITED:        
	    		inBinRange = false;
	    		updateColor();
	    		break;
	    	case DragEvent.ACTION_DROP:
	    		// Dropped, reassign View to ViewGroup
	    		View view = (View) event.getLocalState();
	    		inBinRange = false;
	    		updateColor();
	    		if(view instanceof ThemeObjectView){
	    			HaikuGenerator.addTheme(((ThemeObjectView)view).getTheme());
	    		}
	    		else{
	    			Log.i("TAG", "NOT A THEMEOBJECT!!");
	    		}
	    		Log.i("TAG", "size: " + HaikuGenerator.getThemes().size());
//	    		ViewGroup owner = (ViewGroup) view.getParent();
//	    		owner.removeView(view);
//	    		LinearLayout container = (LinearLayout) v;
//	    		container.addView(view);
//	    		view.setVisibility(View.VISIBLE);
	    		break;
	    	case DragEvent.ACTION_DRAG_ENDED:
//	    		v.setBackgroundDrawable(normalShape);
	    	default:
	    		break;
	    }
	    return true;
	}
}
