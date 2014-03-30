package haiku.top.view.main;

import java.util.ArrayList;

import haiku.top.HaikuActivity;
import haiku.top.R;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ConversationObjectView extends LinearLayout{
	private ImageView image;
	private ImageView imageForeground;
	private int threadID;
	private Bitmap picture;
	private ArrayList<String> names;
	private ArrayList<String> addresses;
	
	public ConversationObjectView(Context context, int threadID){//, String address) {
		super(context);
		this.threadID = threadID;
		addresses = HaikuActivity.getConversationNumbers(context, threadID);
		names = new ArrayList<String>();
		for(int i = 0; i < addresses.size(); i++){
			names.add(HaikuActivity.getContactName(context, addresses.get(i)));
		}
//		this.names = HaikuActivity.getContactName(context, address);
		
		LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutInflater.inflate(R.layout.item_contact,this);
		
		setBackgroundResource(android.R.drawable.list_selector_background);
		
		
		image = (ImageView)findViewById(R.id.contactPic);
		imageForeground = (ImageView)findViewById(R.id.contact_foreground);
		TextView nameView1 = (TextView)findViewById(R.id.contactname1);
		TextView nameView2 = (TextView)findViewById(R.id.contactname2);
		TextView nameView3 = (TextView)findViewById(R.id.contactname3);
		TextView plus = (TextView)findViewById(R.id.contactnameplus);
//		cursor = HaikuActivity.getThread(context, threadID);
//		name = HaikuActivity.getContactName(context, cursor.getString(cursor.getColumnIndexOrThrow("address")));
		if(names.size() == 1){
			nameView1.setText(names.get(0));
			nameView1.setTypeface(MainView.getInstance().getContactsTypeface());
			
			nameView2.setVisibility(View.GONE);
			nameView3.setVisibility(View.GONE);
		}
		if(names.size() == 2){
			nameView1.setText(names.get(0));
			nameView1.setTypeface(MainView.getInstance().getContactsTypeface());
			
			nameView2.setText(names.get(1));
			nameView2.setTypeface(MainView.getInstance().getContactsTypeface());
			
			nameView3.setVisibility(View.GONE);
		}
		if(names.size() == 3){
			nameView1.setText(names.get(0));
			nameView1.setTypeface(MainView.getInstance().getContactsTypeface());
			
			nameView2.setText(names.get(1));
			nameView2.setTypeface(MainView.getInstance().getContactsTypeface());

			nameView3.setText(names.get(2));
			nameView3.setTypeface(MainView.getInstance().getContactsTypeface());
		}
		if(names.size() > 3){
			nameView1.setText(names.get(0));
			nameView1.setTypeface(MainView.getInstance().getContactsTypeface());
			
			nameView2.setText(names.get(1));
			nameView2.setTypeface(MainView.getInstance().getContactsTypeface());

			nameView3.setText(names.get(2));
			nameView3.setTypeface(MainView.getInstance().getContactsTypeface());
		}
		else{
			plus.setVisibility(View.GONE);
		}
		// Adobe Garamond Pro looks weird for contact names.
//        Typeface adobeGaramondProRegular = Typeface.createFromAsset(context.getAssets(), "fonts/AGARAMONDPRO-REGULAR.OTF");
//        nameView.setTypeface(adobeGaramondProRegular);
		
		int paddingTopAndBottom = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics());
		int paddingLeftAndRight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, getResources().getDisplayMetrics());
		
		setPadding(paddingLeftAndRight, paddingTopAndBottom, paddingLeftAndRight, paddingTopAndBottom);
		if(names.size() == 1){
			picture = HaikuActivity.getContactPhoto(context, names.get(0)); // this method works
		}
		else{
			picture = null;
		}
//		picture = HaikuActivity.getImage(context, name);
//	    InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(context.getContentResolver(), ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, threadID));
//	    if (input != null) {
//	    	picture = BitmapFactory.decodeStream(input);
		
		if(picture != null){ //TODO
	    	image.setImageBitmap(picture);
		}
		calculateForegroundAlpha();
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
	}
	
//	public Cursor getCursor(){
//		return cursor;
//	}
	
	private static final double FULLY_VISIBLE = 50.0;
	
	public void calculateForegroundAlpha(){
		int smsCount = HaikuActivity.getSMSCount(threadID);
		float alpha = (float) Math.max(0, (1.0 - smsCount/FULLY_VISIBLE));
		imageForeground.setAlpha(alpha);
	}
	
	public int getThreadID(){
		return threadID;
	}
	
	public ArrayList<String> getNames(){
		return names;
	}
	
	public Bitmap getPicture(){
		return picture;
	}
	
	public boolean isHaikuConversation(){
		return names.size() == 1 && names.get(0).equals("Haiku");
	}
}
