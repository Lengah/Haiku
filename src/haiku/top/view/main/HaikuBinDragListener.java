package haiku.top.view.main;

import java.util.ArrayList;

import haiku.top.R;
import haiku.top.model.Theme;
import haiku.top.model.date.YearMonth;
import haiku.top.model.generator.HaikuGenerator;
import haiku.top.view.ThemeObjectView;
import haiku.top.view.binview.BinView;
import haiku.top.view.date.QuarterCircle;
import haiku.top.view.main.sms.SMSObject;
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
	
	private boolean isAddingDuringDeletion = false;
	
	public void resetDeletionAddingFlag(){
		isAddingDuringDeletion = false;
	}

	@Override
	public boolean onDrag(View v, DragEvent event) {
		if(isAddingDuringDeletion){
			return false; // The BinView is handling the event -> don't do anything here.
		}
		int action = event.getAction();
		View view;
	    switch (action) {
	    	case DragEvent.ACTION_DRAG_STARTED:
	    		break;
	    	case DragEvent.ACTION_DRAG_ENTERED:
	    		inBinRange = true;
	    		view = MainView.getInstance().getDraggedView();
	    		if(MainView.getInstance().getBinView().isDeleting() && (view instanceof ConversationObjectView || view instanceof SMSObject || view instanceof QuarterCircle)){
	    			// Since deletion has started the user should be able to place the dragged object in the bin
	    			isAddingDuringDeletion = true;
	    			MainView.getInstance().getBinView().setAddingObjectDuringDeletion(view);
	    			MainView.getInstance().openBinViewToAdd();
	    		}
	    		break;
	    	case DragEvent.ACTION_DRAG_EXITED:        
	    		inBinRange = false;
	    		break;
	    	case DragEvent.ACTION_DROP:
	    		inBinRange = false;
	    		if(BinView.getInstance().isShowingHaiku()){
	    			return true;
	    		}
	    		view = MainView.getInstance().getDraggedView();
	    		if(view instanceof ThemeObjectView){
	    			HaikuGenerator.addTheme(((ThemeObjectView)view).getTheme());
	    		}
	    		if(view instanceof ConversationObjectView){
	    			HaikuGenerator.addThread(((ConversationObjectView)view).getThreadID());
	    		}
	    		if(view instanceof SMSObject){
	    			HaikuGenerator.calculateSMS(((SMSObject)view).getSMS());
	    		}
	    		if(view instanceof QuarterCircle){
	    			if(((QuarterCircle) view).isYearView()){
//	    				ArrayList<YearMonth> yearMonths = HaikuGenerator.getDates();
//	    				YearMonth yearMonth;
//	    				for(int i = 0; i < DateView.MONTHS_NAME.length; i++){
//	    					yearMonth = new YearMonth(MainView.getInstance().getSelectedYear(), DateView.MONTHS_NAME[i]);
//	    					if(!yearMonths.contains(yearMonth)){
//	    						HaikuGenerator.addDate(yearMonth);
//	    					}
//	    				}
	    				if(MainView.getInstance().isShowingSMS()){
	    					HaikuGenerator.addYearFromSMSes(MainView.getInstance().getSelectedYear(), MainView.getInstance().getSelectedConvoThreadID());
	    				}
	    				else{
	    					HaikuGenerator.addYear(MainView.getInstance().getSelectedYear());
	    				}
	    			}
	    			else{
	    				if(MainView.getInstance().isShowingSMS()){
	    					HaikuGenerator.addDateFromSMSes(new YearMonth(MainView.getInstance().getSelectedYear(), ((QuarterCircle)view).getMonth()), MainView.getInstance().getSelectedConvoThreadID());
	    				}
	    				else{
	    					HaikuGenerator.addDate(new YearMonth(MainView.getInstance().getSelectedYear(), ((QuarterCircle)view).getMonth()));
	    				}
		    		}
	    		}
	    		break;
	    	case DragEvent.ACTION_DRAG_ENDED:
	    		view = MainView.getInstance().getDraggedView();
	    		if(view instanceof ThemeObjectView){
	    			MainView.getInstance().updateThemeView();
	    		}
	    		if(view instanceof ConversationObjectView){
	    			MainView.getInstance().updateConversationsVisibility();
	    		}
	    		if(view instanceof SMSObject){
	    			MainView.getInstance().updateSMSView();
	    		}
	    		if(view instanceof QuarterCircle){
	    			MainView.getInstance().updateDateView();
	    		}
	    	default:
	    		break;
	    }
	    return true;
	}
}
