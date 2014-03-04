package haiku.top.view.main;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.DuplicateFormatFlagsException;
import java.util.Random;

import haiku.top.HaikuActivity;
import haiku.top.R;
import haiku.top.model.Theme;
import android.content.ContentUris;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class ConversationObjectView extends LinearLayout{
	private ImageView image;
	private ImageView imageForeground;
	private TextView nameView;
	private int threadID;
	private Cursor cursor;
	private Context context;
	private String name;
	private Bitmap picture;
	
	public ConversationObjectView(Context context, int threadID, String address) {
		super(context);
		this.context = context;
		this.threadID = threadID;
		this.name = HaikuActivity.getContactName(context, address);
		
		LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutInflater.inflate(R.layout.item_contact,this);
		
		setBackgroundResource(android.R.drawable.list_selector_background);
		
		
		image = (ImageView)findViewById(R.id.contactPic);
		imageForeground = (ImageView)findViewById(R.id.contact_foreground);
		nameView = (TextView)findViewById(R.id.contactname);
//		cursor = HaikuActivity.getThread(context, threadID);
//		name = HaikuActivity.getContactName(context, cursor.getString(cursor.getColumnIndexOrThrow("address")));
		nameView.setText(name);
		// Adobe Garamond Pro looks weird for contact names.
//        Typeface adobeGaramondProRegular = Typeface.createFromAsset(context.getAssets(), "fonts/AGARAMONDPRO-REGULAR.OTF");
//        nameView.setTypeface(adobeGaramondProRegular);
		
		int paddingTopAndBottom = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics());
		int paddingLeftAndRight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, getResources().getDisplayMetrics());
		
		setPadding(paddingLeftAndRight, paddingTopAndBottom, paddingLeftAndRight, paddingTopAndBottom);
		
		picture = HaikuActivity.getContactPhoto(context, name); // this method works
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
	
	public String getName(){
		return name;
	}
	
	public Bitmap getPicture(){
		return picture;
	}
}
