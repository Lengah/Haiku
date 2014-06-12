package haiku.top.view.main;

import java.util.ArrayList;

import haiku.top.HaikuActivity;
import haiku.top.R;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;

public class ConversationObjectView extends LinearLayout{
	private static final int TEXT_SIZE = 15;

	private int threadID;
//	private Bitmap picture;
	private ArrayList<String> names;
	private ArrayList<String> addresses;
	
	private LetterSpacingTextView nameView1;
	private LetterSpacingTextView nameView2;
	private LetterSpacingTextView nameView3;
	private TextView plus;

	private int smsCount;
	
	public ConversationObjectView(Context context, int threadID, boolean recent){//, String address) {
		super(context);
		this.threadID = threadID;
		addresses = HaikuActivity.getConversationNumbers(context, threadID);
		names = new ArrayList<String>();
		String temp;
		for(int i = 0; i < addresses.size(); i++){
			temp = HaikuActivity.getContactName(context, addresses.get(i));
			for(int a = 0; a <= names.size(); a++){
				if(a < names.size() && names.get(a).equals(temp)){
					break; // duplicate
				}
				if(a == names.size() || HaikuActivity.compareIgnoreCase(names.get(a), temp) < 0){
					names.add(a, temp);
					break;
				}
			}
		}
//		this.names = HaikuActivity.getContactName(context, address);
		
		LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutInflater.inflate(R.layout.item_contact,this);
		
		setBackgroundResource(android.R.drawable.list_selector_background);
		
		LinearLayout l1 = (LinearLayout)findViewById(R.id.contactname1);
		LinearLayout l2 = (LinearLayout)findViewById(R.id.contactname2);
		LinearLayout l3 = (LinearLayout)findViewById(R.id.contactname3);
		plus = (TextView)findViewById(R.id.contact_plus);
		nameView1 = new LetterSpacingTextView(context);
		nameView2 = new LetterSpacingTextView(context);
		nameView3 = new LetterSpacingTextView(context);
		
		if(recent && !isHaikuConversation()){
			nameView1.setTypeface(MainView.getInstance().getContactsTypeface(), Typeface.BOLD);
			nameView2.setTypeface(MainView.getInstance().getContactsTypeface(), Typeface.BOLD);
			nameView3.setTypeface(MainView.getInstance().getContactsTypeface(), Typeface.BOLD);
			plus.setTypeface(MainView.getInstance().getContactsTypeface(), Typeface.BOLD);
		}
		else{
			nameView1.setTypeface(MainView.getInstance().getContactsTypeface());
			nameView2.setTypeface(MainView.getInstance().getContactsTypeface());
			nameView3.setTypeface(MainView.getInstance().getContactsTypeface());
			plus.setTypeface(MainView.getInstance().getContactsTypeface());
		}
		
		nameView1.setTextSize(TypedValue.COMPLEX_UNIT_SP, TEXT_SIZE);
		nameView2.setTextSize(TypedValue.COMPLEX_UNIT_SP, TEXT_SIZE);
		nameView3.setTextSize(TypedValue.COMPLEX_UNIT_SP, TEXT_SIZE);
		plus.setTextSize(TypedValue.COMPLEX_UNIT_SP, TEXT_SIZE);
		
		nameView1.setTextColor(Color.BLACK);
		nameView2.setTextColor(Color.BLACK);
		nameView3.setTextColor(Color.BLACK);
		plus.setTextColor(Color.BLACK);
		
		l1.addView(nameView1, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		l2.addView(nameView2, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		l3.addView(nameView3, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		
//		LetterSpacingTextView nameView1 = (LetterSpacingTextView)findViewById(R.id.contactname1);
//		LetterSpacingTextView nameView2 = (LetterSpacingTextView)findViewById(R.id.contactname2);
//		LetterSpacingTextView nameView3 = (LetterSpacingTextView)findViewById(R.id.contactname3);
//		LetterSpacingTextView nameView4 = (LetterSpacingTextView)findViewById(R.id.contactname4);
//		LetterSpacingTextView nameView5 = (LetterSpacingTextView)findViewById(R.id.contactname5);
//		cursor = HaikuActivity.getThread(context, threadID);
//		name = HaikuActivity.getContactName(context, cursor.getString(cursor.getColumnIndexOrThrow("address")));
		if(isHaikuConversation()){
			LinearLayout l = (LinearLayout)findViewById(R.id.ContactLayout);
			ImageView image = new ImageView(context);
			image.setImageResource(R.drawable.haiku_contact_image);
			image.setLayoutParams(new LayoutParams((int)HaikuActivity.convertDpToPixel(50), (int)HaikuActivity.convertDpToPixel(50)));
			l.addView(image, 0);
		}
		if(names.size() == 1){
			nameView1.setText(names.get(0));
			
			nameView2.setVisibility(View.GONE);
			nameView3.setVisibility(View.GONE);
			plus.setVisibility(View.GONE);
		}
		else if(names.size() == 2){
			nameView1.setText(names.get(0));
			nameView2.setText(names.get(1));
			
			nameView3.setVisibility(View.GONE);
			plus.setVisibility(View.GONE);
		}
		else if(names.size() == 3){
			nameView1.setText(names.get(0));
			nameView2.setText(names.get(1));
			nameView3.setText(names.get(2));
			
			plus.setVisibility(View.GONE);
		}
		else if(names.size() >= 4){
			nameView1.setText(names.get(0));
			nameView2.setText(names.get(1));
			nameView3.setText(names.get(2));
			plus.setVisibility(VISIBLE);
		}
		// Adobe Garamond Pro looks weird for contact names.
//        Typeface adobeGaramondProRegular = Typeface.createFromAsset(context.getAssets(), "fonts/AGARAMONDPRO-REGULAR.OTF");
//        nameView.setTypeface(adobeGaramondProRegular);
		
		if(isHaikuConversation()){
			nameView1.setVisibility(View.GONE);
			TextView haikuTextView = new TextView(context);
			haikuTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, TEXT_SIZE);
			haikuTextView.setTypeface(MainView.getInstance().getContactsTypeface());
			haikuTextView.setGravity(Gravity.CENTER_VERTICAL);
			haikuTextView.setTextColor(Color.BLACK);
			haikuTextView.setText(names.get(0));
			LinearLayout l = (LinearLayout)findViewById(R.id.layout1);
			LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			params.setMargins((int) HaikuActivity.convertDpToPixel(10), 0, 0, 0);
			l.addView(haikuTextView, params);
		}
		
		int paddingTopAndBottom = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics());
		int paddingLeftAndRight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, getResources().getDisplayMetrics());
		
		setPadding(paddingLeftAndRight, paddingTopAndBottom, paddingLeftAndRight, paddingTopAndBottom);
		// funkar
//		if(names.size() == 1){
//			picture = HaikuActivity.getContactPhoto(context, names.get(0)); // this method works
//		}
//		if(picture == null){
//			picture = BitmapFactory.decodeResource(context.getResources(), R.drawable.contact_bg_default);
//		}
		// /funkar
		
//		picture = HaikuActivity.getImage(context, name);
//	    InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(context.getContentResolver(), ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, threadID));
//	    if (input != null) {
//	    	picture = BitmapFactory.decodeStream(input);
		
//		if(picture != null){ //TODO works!!!
//	    	image.setImageBitmap(picture);
//		}
//		calculateForegroundAlpha();
//		else{
//			long id = HaikuActivity.getIDFromName(context, name);
//			if(id != -1){
//				try {
//				URL img_value = null;
//				img_value = new URL("http://graph.facebook.com/"+id+"/picture?type=large");
//				picture = BitmapFactory.decodeStream(img_value.openConnection().getInputStream());
//				if(picture != null){
//			    	image.setImageBitmap(picture);
//				}
//				} catch (MalformedURLException e) {
//					e.printStackTrace();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//		else{
//			Uri uri = HaikuActivity.getPhotoUri(context, name);
//			if(uri != null){
//				image.setImageURI(uri);
//			}
//		}
//	    }
//	    try {
//			input.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		calculateSMSCount();
	}
	
	public void calculateAndSetSpacing(int biggestSMSCount){
		if(isHaikuConversation()){
			setSpacing(LetterSpacingTextView.NORMAL);
			return;
		}
		if(smsCount == 0){
			setSpacing(LetterSpacingTextView.MAX_SPACING);
		}
		else{
			double p = ((double)smsCount)/((double)biggestSMSCount);
			if(smsCount > biggestSMSCount/2){
				//smaller than 1
				// 0.5 < p <= 1
				// turn it around so that 1->0.5, 0.5->1, 0.6->0.9 etc.
				p = p-0.5;
				double left = 0.5 - p;
				p = p + left*2.0;
				setSpacing((float) p);
			}
			else{
				// bigger than 1
				// 0.0 <= p <= 0.5
				float max = LetterSpacingTextView.MAX_SPACING - 1;
				p = p*2.0; // 0.0 <= p <= 1.0
				float x = ((float)p) * max; // 0 <= x <= LetterSpacingTextView.MAX_SPACING - 1
				// turn it around
				x = max - x + 1; // 1 <= x <= LetterSpacingTextView.MAX_SPACING
				setSpacing(x);
			}
		}
	}
	
	private void setSpacing(float spacing){
		nameView1.setLetterSpacing(spacing);
		nameView2.setLetterSpacing(spacing);
		nameView3.setLetterSpacing(spacing);
	}
	
//	public Cursor getCursor(){
//		return cursor;
//	}
	
//	private static final double FULLY_VISIBLE = 50.0; // the number of sms needed
	
//	public void calculateForegroundAlpha(){
////		int smsCount = HaikuActivity.getSMSCount(threadID);
////		float alpha = (float) Math.max(0, (1.0 - smsCount/FULLY_VISIBLE));
//		float alpha = 0; //TODO 
//		imageForeground.setAlpha(alpha);
//	}
	
	/**
	 * Does a database query!
	 */
	public void calculateSMSCount(){
		if(isHaikuConversation()){
			return; //skip the haiku contact
		}
		smsCount = HaikuActivity.getSMSCount(threadID);
//		smsCount = smsCount/names.size();
	}
	
	public int getSMSCount(){
		return smsCount;
	}
	
	public int getThreadID(){
		return threadID;
	}
	
	public ArrayList<String> getNames(){
		return names;
	}
	
//	public Bitmap getPicture(){
//		return picture;
//	}
	
	public boolean isHaikuConversation(){
		return names.size() == 1 && names.get(0).equals("Haiku");
	}
}
