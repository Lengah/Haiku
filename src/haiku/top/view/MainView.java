package haiku.top.view;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import haiku.top.R;
import haiku.top.model.HaikuGenerator;
import haiku.top.model.Theme;

public class MainView extends LinearLayout implements OnClickListener{
	private Context context;
	private Button generateButton;
	private TextView haikuText;
	private TextView dateText;
	private Theme selectedTheme = Theme.time;
	
	public MainView(Context context) {
		super(context);
		this.context = context;
		
		LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutInflater.inflate(R.layout.mainview,this);
		
		haikuText = (TextView)findViewById(R.id.haikuText);
		dateText = (TextView)findViewById(R.id.dateText);
		generateButton = (Button)findViewById(R.id.generateButton);
		
		generateButton.setOnClickListener(this);
	}

	
	
	public void saveHaiku() {
		
	}

	public void rateHaiku(int rating) {
		
	}

	public void generateNew() {
		HaikuGenerator.createHaiku(selectedTheme);
		haikuText.setText(HaikuGenerator.getNewestHaiku().getHaikuPoem());
		dateText.setText(HaikuGenerator.getNewestHaiku().getDate());
	}

	public void share() {
		
	}
	
	public void changeTheme(Theme theme){
		this.selectedTheme = theme;
	}

	@Override
	public void onClick(View v) {
		if(v.equals(generateButton)){
			generateNew();
		}
	}
}
