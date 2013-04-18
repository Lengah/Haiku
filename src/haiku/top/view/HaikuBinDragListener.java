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
	private int notInBinColor = Color.rgb(255, 236, 142);
	
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
		View view;
	    switch (action) {
	    	case DragEvent.ACTION_DRAG_STARTED:
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
	    		inBinRange = false;
	    		updateColor();
	    		view = MainView.getInstance().getDraggedView();
	    		if(view instanceof ThemeObjectView){
	    			HaikuGenerator.addTheme(((ThemeObjectView)view).getTheme());
	    		}
	    		if(view instanceof ConversationObjectView){
	    			HaikuGenerator.addThread(((ConversationObjectView)view).getThreadID());
	    		}
	    		if(view instanceof SMSObjectView){
	    			HaikuGenerator.addSMS(((SMSObjectView)view).getSMS());
	    		}
	    		break;
	    	case DragEvent.ACTION_DRAG_ENDED:
	    		view = MainView.getInstance().getDraggedView();
	    		if(view instanceof ThemeObjectView){
	    			MainView.getInstance().updateThemeView();
	    		}
	    		if(view instanceof ConversationObjectView){
	    			MainView.getInstance().updateConversations();
	    		}
	    		if(view instanceof SMSObjectView){
	    			MainView.getInstance().updateSMSView();
	    		}
	    	default:
	    		break;
	    }
	    return true;
	}
}