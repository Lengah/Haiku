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
    	String[] contactProjection = new String[] {
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.HAS_PHONE_NUMBER,
       };
       String contactSelection = ContactsContract.Contacts.HAS_PHONE_NUMBER + "='1'";
       Cursor c = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, contactProjection, contactSelection, null, null);
       String name;
       String contactId;
       
       while(c.moveToNext()){
       		 name = c.getString(c.getColumnIndex(PhoneLookup.DISPLAY_NAME));
       		 contactId = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
       		 Log.i("Tag", "Name: " + name + ", ID: " + contactId);
       		 contacts.add(new Contact(Integer.parseInt(contactId), name));
       	}
       
       	c.close();
    	
    	Uri mSmsinboxQueryUri = Uri.parse(ALLBOXES);
//    	String[] projection = new String[] {ContactsContract.Contacts._ID, "_id", "date", "body"};
//    	String[] projection = new String[] {"_id", "thread_id", "address", "person", "date", "body", "type" };
    	String[] projection = new String[] {"_id", "address", "date", "body"};
//    	String selection = ContactsContract.Contacts._ID + "='" + contact.getID() + "'";
        Cursor cursor1 = context.getContentResolver().query(mSmsinboxQueryUri,
                    projection, null, null, null);
//        String[] columns = new String[] { "address", "person", "date", "body","type" };
        String message;
        String date;
        String address;
        String _id;
        boolean found;
        String count = Integer.toString(cursor1.getCount());
        Log.i("TAG", "Count: " + count);
        
        int c1 = 0;
        int c2 = 0;
        double time1;
        double totalTime = 0;
        double startTime = System.currentTimeMillis();
        while (cursor1.moveToNext()){
        	time1 = System.currentTimeMillis();
        	found = false;
        	date = cursor1.getString(cursor1.getColumnIndexOrThrow("date"));
            message = cursor1.getString(cursor1.getColumnIndexOrThrow("body"));
            address = cursor1.getString(cursor1.getColumnIndexOrThrow("address"));
            _id = cursor1.getString(cursor1.getColumnIndexOrThrow("_id"));
            name = getContactName(context, address);
        	for(int i = 0; i < contacts.size(); i++){
        		if(name != null && name.equals(contacts.get(i).getName())){
        			contacts.get(i).addSMS(new SMS(Integer.parseInt(_id), message, contacts.get(i)));
        			found = true;
        			c1++;
        			break;
        		}
        	}
        	if(found){
        		totalTime += System.currentTimeMillis()-time1;
        		continue;
        	}
        	c2++;
        	totalTime += System.currentTimeMillis()-time1;
//            Log.i("TAG", "Contact not found for message: " + message);
        }
        cursor1.close();
        Log.i("TAG", "c1: " + c1 + ", c2: " + c2);
        Log.i("TAG", "Contacts size: " + contacts.size());
        Log.i("TAG","Total time: "+ (System.currentTimeMillis()-startTime)/1000 + " s" + ", average time: " + totalTime/(c1+c2) + " ms" + ", loops: " + (c1+c2));
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
