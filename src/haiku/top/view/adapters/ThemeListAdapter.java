package haiku.top.view.adapters;

import haiku.top.model.Theme;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.widget.ArrayAdapter;

public class ThemeListAdapter extends ArrayAdapter<Theme>{

	public ThemeListAdapter(Context context, int textViewResourceId, List<Theme> themes) {
		super(context, textViewResourceId, themes);
	}
	
	public void removeAll(ArrayList<Theme> themes) {
		for(int i = 0; i < themes.size(); i++){
			remove(themes.get(i));
		}
	}

}
