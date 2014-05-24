package haiku.top.view.binview.haiku;

import haiku.top.model.Text;
import haiku.top.model.Word;

import java.util.ArrayList;

import android.content.Context;
import android.widget.RelativeLayout;

public abstract class Row extends RelativeLayout{
	protected ArrayList<HaikuRowWord> words = new ArrayList<HaikuRowWord>();
	
	public Row(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public ArrayList<Text> getWords(){
		ArrayList<Text> retWords = new ArrayList<Text>();
		for(HaikuRowWord ws : words){
			retWords.add(ws.getWord());
		}
		return retWords;
	}
	
	public abstract void initDrag(HaikuRowWord word);
}
