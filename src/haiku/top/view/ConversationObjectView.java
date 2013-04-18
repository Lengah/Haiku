package haiku.top.view;

import java.io.IOException;
import java.io.InputStream;
import java.util.DuplicateFormatFlagsException;

import haiku.top.HaikuActivity;
import haiku.top.R;
import haiku.top.model.Theme;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.provider.ContactsContract;
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
		nameView = (TextView)findViewById(R.id.contactname);
//		cursor = HaikuActivity.getThread(context, threadID);
//		name = HaikuActivity.getContactName(context, cursor.getString(cursor.getColumnIndexOrThrow("address")));
		nameView.setText(name);
		
		setPadding(5, 2, 5, 2); //TODO dp!
		
		picture = HaikuActivity.getContactPhoto(context, name);
//	    InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(context.getContentResolver(), ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, threadID));
//	    if (input != null) {
//	    	picture = BitmapFactory.decodeStream(input);
		if(picture != null){
	    	image.setImageBitmap(picture);
		}
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
	
	public int getThreadID(){
		return threadID;
	}
	
	public String getName(){
		return name;
	}
}
