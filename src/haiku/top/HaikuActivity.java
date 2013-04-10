package haiku.top;

import java.util.ArrayList;

import haiku.top.R;
import haiku.top.model.Contact;
import haiku.top.model.SMS;
import haiku.top.view.AboutView;
import haiku.top.view.MainView;
import haiku.top.view.SavedHaikusView;
import haiku.top.view.ShareView;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds;
import android.provider.ContactsContract.PhoneLookup;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

public class HaikuActivity extends Activity {
	private static HaikuActivity ha;
	private View mainView;
	private View savedHaikusView;
	private View aboutView;
	private View shareView;
	private static final String ALLBOXES = "content://sms/";
	private static final String SORT_ORDER = "date DESC";
    private static final String SORT_ORDER_INV = "date ASC";
    private static ArrayList<Contact> contacts = new ArrayList<Contact>();
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ha = this;
        mainView = new MainView(this);
        setContentView(mainView, new LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
        initContactsAndSMS(this);
    }
    
    public static HaikuActivity getInstance(){
    	return ha;
    }
    
    public static void initContactsAndSMS(Context context){
    	
//    	String[] contactProjection = new String[] {
//                ContactsContract.Contacts._ID,
//                ContactsContract.Contacts.DISPLAY_NAME,
//                ContactsContract.Contacts.HAS_PHONE_NUMBER,
//                ContactsContract.CommonDataKinds.Phone.NUMBER
//       };
//       String contactSelection = ContactsContract.Contacts.HAS_PHONE_NUMBER + "='1'";
//       Cursor c = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, contactProjection, contactSelection, null, null);
       String name;
       String contactId;
       String number;
       
       Cursor phones = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);
	   while (phones.moveToNext()){
	   	  name=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
	   	  number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
	   	  contactId = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
	   	  Log.i("Tag", "Name: " + name + ", number: " + number + ", ID: " + contactId);
	   	  contacts.add(new Contact(Integer.parseInt(contactId), name, number));
	   }
	   phones.close();
       
//       while(c.moveToNext()){
//       		 name = c.getString(c.getColumnIndexOrThrow(PhoneLookup.DISPLAY_NAME));
//       		 contactId = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
//       		 number = c.getString(c.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
//       		 Log.i("Tag", "Name: " + name + ", ID: " + contactId);
//       		 contacts.add(new Contact(Integer.parseInt(contactId), name, number));
//       	}
//       
//       	c.close();
    	
    	Uri mSmsinboxQueryUri = Uri.parse(ALLBOXES);
    	String[] projection = new String[] {"_id", "address", "date", "body"};
        Cursor cursor1 = context.getContentResolver().query(mSmsinboxQueryUri, projection, null, null, null);
        String message;
        String date;
        String address;
        String _id;
        
        int c1 = 0;
        int c2 = cursor1.getCount();
        double time1;
        double totalTime = 0;
        double startTime = System.currentTimeMillis();
//        boolean found;
        
        while (cursor1.moveToNext()){
        	time1 = System.currentTimeMillis();
//        	found = false;
        	date = cursor1.getString(cursor1.getColumnIndexOrThrow("date"));
            message = cursor1.getString(cursor1.getColumnIndexOrThrow("body"));
            address = cursor1.getString(cursor1.getColumnIndexOrThrow("address"));
            if(address.contains("+")){
            	address = "0" + address.substring(3);
    		}
            _id = cursor1.getString(cursor1.getColumnIndexOrThrow("_id"));
        	for(int i = 0; i < contacts.size(); i++){
        		if(address.equals(contacts.get(i).getPhoneNumber())){
//        			contacts.get(i).addSMS(new SMS(Integer.parseInt(_id), message, contacts.get(i)));
        			c1++;
//        			found = true;
        			break;
        		}
        	}
//        	if(!found){
//        		name = getContactName(context, address);
//        		Log.i("TAG","Name: " + name + ", Address: " + address);
//        	}
        	totalTime += System.currentTimeMillis()-time1;
        }
        cursor1.close();
        for(int i = contacts.size()-1; i >= 0; i--){
//        	if(contacts.get(i).getSMS().isEmpty()){
//        		contacts.remove(i);
//        	}
        }
        Log.i("TAG", "found: " + c1 + ", not found: " + (c2-c1) + ", total: " + c2);
        Log.i("TAG", "Contacts size: " + contacts.size());
        Log.i("TAG","Total time: "+ (System.currentTimeMillis()-startTime) + " ms" + ", average time: " + totalTime/(c2) + " ms" + ", loops: " + (c2));    
   }
    
    public static Cursor getThreads(Context context){
		Uri uri = Uri.parse(ALLBOXES);
		Cursor cursor = context.getContentResolver().query(uri, null, "1) GROUP BY (thread_id", null, SORT_ORDER);
		if(cursor!=null){
			return cursor;
		}
		return null;
	} 
	
	public static Cursor getThread(Context context, int thread_id){
		Uri uri = Uri.parse(ALLBOXES);
		Cursor cursor = context.getContentResolver().query(uri, null, "thread_id = '" + thread_id + "'", null, SORT_ORDER_INV);
		if(cursor!=null){
			return cursor;
		}
		return null;
	} 

    /**
     * Tries to get the contact's display name of the specified phone number.
     * If not found, returns the argument. If there is an error or phoneNumber
     * is null, R.string.chat_call_hidden will be returned.
     *
     */
    public static String getContactName (Context ctx, String phoneNumber) {
        String res;
        if (phoneNumber != null) {
            try {
                res = phoneNumber;
                ContentResolver resolver = ctx.getContentResolver();
                Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
                Cursor c = resolver.query(uri, new String[]{PhoneLookup.DISPLAY_NAME}, null, null, null);
   
                if (c != null && c.moveToFirst()) {
                    res = c.getString(c.getColumnIndex(CommonDataKinds.Phone.DISPLAY_NAME));
                    c.close();
                }
            } catch (Exception ex) {
              Log.e("ssssss", "getContactName error: Phone number = " + phoneNumber, ex);  
              res = null;
            }
        } else {
            res = null;
        }
        return res;
    }
    
    
}
