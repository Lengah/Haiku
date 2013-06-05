package haiku.top;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import haiku.top.model.CreateSamplesContact;
import haiku.top.model.Theme;
import haiku.top.model.generator.HaikuGenerator;
import haiku.top.model.sql.DatabaseHandler;
import haiku.top.view.CreateSamplesView;
import haiku.top.view.main.MainView;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds;
import android.provider.ContactsContract.PhoneLookup;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;

public class HaikuActivity extends Activity {
	private static HaikuActivity ha;
	private View mainView;
	private View createSamplesView;
	private boolean inCreateSamplesView;
	public static final String ALLBOXES = "content://sms/";
	private static final String SORT_ORDER = "date DESC";
    private static final String SORT_ORDER_INV = "date ASC";

	public static Vibrator vibe;
	
	private SharedPreferences mPrefs;
//	public boolean smsWordTableExist; //has SMSWORD table been loaded
//	public static final String SMSWORD_EXIST_KEY = "DeleteByHaiku_smsWordTableExist";
	private boolean safeMode; //safe mode enabled?
	public static final String SAFE_MODE_KEY = "DeleteByHaiku_safeMode";
	
	public static DatabaseHandler databaseHandler;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ha = this;
        createSamplesView = new CreateSamplesView(this);
//        initContactsAndSMS(this);
        vibe = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        
		//import saved data
		mPrefs = getPreferences(Context.MODE_PRIVATE);
		((CreateSamplesView)createSamplesView).samplesExist = mPrefs.getBoolean(CreateSamplesView.SAMPLES_EXIST_KEY, false); //has contacts/SMS been loaded in a previous session?
//		smsWordTableExist =  mPrefs.getBoolean(SMSWORD_EXIST_KEY, false);
		safeMode =  mPrefs.getBoolean(SAFE_MODE_KEY, false);
		
        //if so, load contacts
        if (((CreateSamplesView)createSamplesView).samplesExist) {
        	Set<String> importContact = new HashSet<String>();
        	importContact = mPrefs.getStringSet(CreateSamplesView.EXPORT_CONTACT_KEY, null);

	        ArrayList<String> contactsToArray = new ArrayList<String>(importContact);
	        for (int i=0; i < contactsToArray.size(); i++)
	        	((CreateSamplesView)createSamplesView).contacts.add(new CreateSamplesContact(contactsToArray.get(i).
	        		substring(0, contactsToArray.get(i).indexOf("·*$")), contactsToArray.get(i). 
	        		substring(contactsToArray.get(i).indexOf("·*$") + 3, contactsToArray.get(i).length())));
	        
	        Set<String> importSMS = new HashSet<String>();
	        importSMS = mPrefs.getStringSet(CreateSamplesView.EXPORT_SMS_KEY, null);
	        ((CreateSamplesView)createSamplesView).sms.addAll(importSMS);
        }
        ((CreateSamplesView)createSamplesView).updateAfterImport();
        
        
        //create and open deltebyhaiku_db
        databaseHandler = new DatabaseHandler(this);
        try { databaseHandler.createDataBase(); } catch (IOException ioe) {  throw new Error("Unable to create database"); }
        try { databaseHandler.openDataBase(); } catch(SQLException sqle){ throw sqle; }
//        if (!smsWordTableExist)
//        {
//        	databaseHandler.setupSMSTables(); //connect words in sms with dictionary
//        	smsWordTableExist = true;
//        }
		HaikuGenerator.init();
		mainView = new MainView(this);
		setContentView(mainView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }
    
    public boolean isSafeMode(){
    	return safeMode;
    }
    
    public int getStatusBarHeight() {
    	int result = 0;
    	int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
    	if (resourceId > 0) {
    		result = getResources().getDimensionPixelSize(resourceId);
    	}
    	return result;
      }
    
    protected void onPause() { //save data between sessions
        super.onPause();
        SharedPreferences.Editor ed = mPrefs.edit();
        ed.putBoolean(CreateSamplesView.SAMPLES_EXIST_KEY, ((CreateSamplesView)createSamplesView).samplesExist);
//        ed.putBoolean(SMSWORD_EXIST_KEY, smsWordTableExist);
        ed.putBoolean(SAFE_MODE_KEY, safeMode);

        if (((CreateSamplesView)createSamplesView).samplesExist) { //if samples were created during session, save contacts
	        Set<String> exportContact = new HashSet<String>();
	        for (CreateSamplesContact contact : ((CreateSamplesView)createSamplesView).contacts)
	        	exportContact.add(contact.name + "·*$" + contact.phoneNumber); //token separator    
	        ed.putStringSet(CreateSamplesView.EXPORT_CONTACT_KEY, exportContact);
	        
	        Set<String> exportSMS = new HashSet<String>(((CreateSamplesView)createSamplesView).sms);
	        ed.putStringSet(CreateSamplesView.EXPORT_SMS_KEY, exportSMS);
        }

        ed.commit();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.samplecontent:
       	 setContentView(createSamplesView);
       	 inCreateSamplesView = true;
        return true;
        case R.id.safemode:
        
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setTitle("Safe mode");
			
		if (safeMode) {
		alertDialogBuilder
			.setMessage("Safe mode is ON. Turn OFF? SMS deleted in this app WILL be deleted in phone too!")
			.setCancelable(false)
			.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					safeMode = false;
					dialog.cancel();
				}
			  })
			.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						dialog.cancel();
					}
				});
			}
		else {
			alertDialogBuilder
			.setMessage("Safe mode is OFF. Turn ON? SMS deleted in this app WILL NOT be deleted in phone.")
			.setCancelable(false)
			.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					safeMode = true;
					dialog.cancel();
				}
			  })
			.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						dialog.cancel();
					}
				});	
		}
 
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
        return true;
        //----------------------------------------------------------------------------------------------------------------------
       	 //test database
/*       	 if (databaseHandler.getWord("upplands-väsb") != null) 
           	 Log.i("test", databaseHandler.getWord("upplands-väsb").getNumberOfSyllables() + "");
       	 else
       		 Log.i("test", "upplands-väsb not found in dictionary"); //this happens
       	 
       	 if (databaseHandler.getWord("upplands-väsby") != null)
           	 Log.i("test", databaseHandler.getWord("upplands-väsby").getNumberOfSyllables() + ""); //this happens
       	 else
       		 Log.i("test", "upplands-väsby not found in dictionary");
       	 
       	 if (databaseHandler.getWord("asks") != null)
       	 {
       		Log.i("test", "partofspeech in word \"asks\":");
           	ArrayList<String> wordtypes = new ArrayList<String>(databaseHandler.getWord("asks").getwordTypes()); //get partofspeechs from a word
           	for (String s : wordtypes)
           		Log.i("test", s);
       	 }
       	 	 
          	ArrayList<String> wordsids = databaseHandler.getWordsInSMS("2327");
          	Log.i("wordinsms", wordsids.size() + "");
          	for (String id : wordsids)
          		Log.i("wordsinsms", databaseHandler.getWordTextFromID(id));
          	
          	ArrayList<Theme> themes = databaseHandler.getAllThemes();
          	for (Theme t : themes)
          		Log.i("themes", t.getName());
          	 
   //----------------------------------------------------------------------------------------------------------------------	
        */
        default:
        return super.onOptionsItemSelected(item);
        }
    }
    
    @Override
    public boolean onKeyDown(int keycode, KeyEvent event ) {
    if(keycode == KeyEvent.KEYCODE_BACK){
			if(inCreateSamplesView){
				setContentView(mainView);
				inCreateSamplesView = false;
				return true;
			}
    	 
		ArrayList<Integer> states = MainView.getInstance().getViewsShown();
		if (states.isEmpty()) {
		 	finish();	
		}
		else {
			if (states.get(states.size()-1) == MainView.VIEW_SHOWN_SMS) {
				MainView.getInstance().closeSMSView();
			}
			else if (states.get(states.size()-1) == MainView.VIEW_SHOWN_BIN) {
				MainView.getInstance().closeBinView();
			}
			else if (states.get(states.size()-1) == MainView.VIEW_SHOWN_DATE) {
		     	MainView.getInstance().closeDateView();
			}
			
		}
     	return true;
     }
     else
    	 return false;
    }
    
    public static HaikuActivity getInstance(){
    	return ha;
    }
    
    public int getWindowHeight(){
		Display display = getWindowManager().getDefaultDisplay();
		android.graphics.Point size = new android.graphics.Point();
		display.getSize(size);
		return size.y;
	}
    
    public int getWindowWidth(){
    	Display display = getWindowManager().getDefaultDisplay();
		android.graphics.Point size = new android.graphics.Point();
		display.getSize(size);
		return size.x;
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
    
    public static Bitmap getContactPhoto(Context ctx, String displayName){
//    	Log.i("TAG", "get profile picture from contact: " + displayName);
    	String[] projection = new String[] {
                ContactsContract.Contacts.PHOTO_ID,
    			ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.CommonDataKinds.Photo.PHOTO_URI,
//                ContactsContract.CommonDataKinds.Phone.NUMBER,
              ContactsContract.CommonDataKinds.Photo.PHOTO
        };
        ContentResolver cr = ctx.getContentResolver();
        Cursor contactsCursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + "='" + displayName + "'", null, null);
       try{ 
	       // Try to find a picture by using the contact id
	       long id;
	       InputStream input = null;
	       if(contactsCursor.moveToFirst()){
			   id = contactsCursor.getLong(contactsCursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
		        Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, id);
		        input = ContactsContract.Contacts.openContactPhotoInputStream(cr, uri);
	       }
	       if (input != null) {
	    	   Log.i("TAG","Got pic from contact (contact_id): " + displayName);
	           return BitmapFactory.decodeStream(input);
	       }
	//        long photo_id = cursor.getLong(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.PHOTO_ID));
	//        Uri uri = ContentUris.withAppendedId(ContactsContract.Data.CONTENT_URI, photo_id);
	//        Cursor cursor1 = cr.query(uri, new String[] {ContactsContract.CommonDataKinds.Photo.PHOTO}, null, null, null);
	//
	//        try {
	//            Bitmap thumbnail = null;
	//            if (cursor1.moveToFirst()) {
	//                final byte[] thumbnailBytes = cursor1.getBlob(0);
	//                if (thumbnailBytes != null) {
	//                    thumbnail = BitmapFactory.decodeByteArray(thumbnailBytes, 0, thumbnailBytes.length);
	//                }
	//            }
	//            return thumbnail;
	//        }
	//        finally {
	//            cursor1.close();
	//        }
	//      
	       byte[] photoBytes = null;
	        // try to find a picture by using the picture id
	       if(contactsCursor.moveToFirst()){
	    	   long photo_id = contactsCursor.getLong(contactsCursor.getColumnIndexOrThrow(ContactsContract.Contacts.PHOTO_ID));
	    	   Uri photoUri = ContentUris.withAppendedId(ContactsContract.Data.CONTENT_URI, photo_id);
	           Cursor c = cr.query(photoUri, new String[] {ContactsContract.CommonDataKinds.Photo.PHOTO}, null, null, null);
	           try {
	               if (c.moveToFirst()){
	            	   photoBytes = c.getBlob(0);
	//            	   Log.i("TAG", "photobytes! " + (photoBytes==null));
	               }
	           } catch (Exception e) {
	               e.printStackTrace();
	           } finally {
	               c.close();
	           }
	           if (photoBytes != null){
	        	   Log.i("TAG","Got pic from contact (photo id): " + displayName);
	        	   return BitmapFactory.decodeByteArray(photoBytes,0,photoBytes.length);
	           }
	       }
	        
	        // Try to find a picutre using the picture blob
	//        byte[] photoBytes = null;
	        if (contactsCursor.moveToFirst()){
	        	photoBytes = contactsCursor.getBlob(contactsCursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Photo.PHOTO));
	//            	photoBytes = c.getBlob(0);
	//            	Log.i("TAG", "photobytes! " + (photoBytes==null));
	        }
	        if (photoBytes != null){
	        	Log.i("TAG","Got pic from contact (photo): " + displayName);
	            return BitmapFactory.decodeByteArray(photoBytes,0,photoBytes.length);
	        }
	        
	        // facebook
//	        Uri photoUri = null;
//	        if (contactsCursor.moveToFirst()) {
////	            long userId = contactsCursor.getLong(contactsCursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
//	        	long photo_id = contactsCursor.getLong(contactsCursor.getColumnIndexOrThrow(ContactsContract.Contacts.PHOTO_ID));
//	            photoUri = ContentUris.withAppendedId(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, photo_id);
//	        }
//	        if (photoUri != null) {
//	            input = ContactsContract.Contacts.openContactPhotoInputStream(cr, photoUri); // här är felet
//	            if (input != null) {
//	                return BitmapFactory.decodeStream(input);
//	            }
//	        }
	//      Log.i("TAG","Failed to get pic from contact: " + displayName);
	        return null;
       }finally{
    	   contactsCursor.close();    	   
       }
    }
    
    public static Uri getURIFromContact(Context context, String displayName){
    	String[] projection = new String[] {
                ContactsContract.Data.PHOTO_ID,
    			ContactsContract.Data.DISPLAY_NAME,
                ContactsContract.Data.CONTACT_ID,
//                ContactsContract.Data.PHOTO_URI,
//                ContactsContract.CommonDataKinds.Phone.NUMBER,
//              ContactsContract.CommonDataKinds.Photo.PHOTO
        };
        ContentResolver cr = context.getContentResolver();
        Cursor contactsCursor = cr.query(ContactsContract.Data.CONTENT_URI,
                projection, ContactsContract.Data.DISPLAY_NAME + "='" + displayName + "'", null, null);
       try{ 
    	   	if(contactsCursor.moveToFirst()){
//	    	   	long photo_id = contactsCursor.getLong(contactsCursor.getColumnIndexOrThrow(ContactsContract.Data.PHOTO_ID));
	    	   	long userId = contactsCursor.getLong(contactsCursor.getColumnIndexOrThrow(ContactsContract.Data.CONTACT_ID));
		    	Uri photoUri = ContentUris.withAppendedId(ContactsContract.Data.CONTENT_URI, userId);
	//	    	Uri photoUri = null;
	//	        if (contactsCursor.moveToFirst()) {
	//	//            long userId = contactsCursor.getLong(contactsCursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
	//	        	long photo_id = contactsCursor.getLong(contactsCursor.getColumnIndexOrThrow(ContactsContract.Contacts.PHOTO_ID));
	//	            photoUri = ContentUris.withAppendedId(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, photo_id);
	//	        }
		        return photoUri;
    	   	}
    	   	return null;
       }
       finally{
    	   contactsCursor.close();
       }
    }
    
    public static Bitmap getImage(Context ctx, String displayName){
    	String[] projection = new String[] {
//              ContactsContract.Data.PHOTO_ID,
  			ContactsContract.Data.DISPLAY_NAME,
              ContactsContract.Data.CONTACT_ID,
//              ContactsContract.Data.PHOTO_URI,
//              ContactsContract.CommonDataKinds.Phone.NUMBER,
//            ContactsContract.CommonDataKinds.Photo.PHOTO
      };
      ContentResolver cr = ctx.getContentResolver();
      Cursor contactsCursor = cr.query(ContactsContract.Data.CONTENT_URI,
              projection, ContactsContract.Data.DISPLAY_NAME + "='" + displayName + "'", null, null);
      Bitmap my_btmp = null;
      	try{
      		if(contactsCursor.moveToFirst()){
      			long contactID = contactsCursor.getLong(contactsCursor.getColumnIndexOrThrow(ContactsContract.Data.CONTACT_ID));
//      			Uri my_contact_Uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, String.valueOf(contactID));
      			Uri my_contact_Uri =  ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactID);
      	        InputStream photo_stream = ContactsContract.Contacts.openContactPhotoInputStream(cr,my_contact_Uri);            
//      	        BufferedInputStream buf = new BufferedInputStream(photo_stream);
//      	        Bitmap my_btmp = BitmapFactory.decodeStream(buf);
      	        if(photo_stream != null){
      	        	my_btmp = BitmapFactory.decodeStream(photo_stream);
      	        	photo_stream.close();
      	        }
      		}
      	}catch (IOException e) {
			e.printStackTrace();
		}finally{
      		contactsCursor.close();
      	}
      	return my_btmp;
    }
    
    public static long getIDFromName(Context ctx, String displayName){
    	String[] projection = new String[] {
//              ContactsContract.Data.PHOTO_ID,
  			ContactsContract.Data.DISPLAY_NAME,
              ContactsContract.Data.CONTACT_ID,
//              ContactsContract.Data.PHOTO_URI,
//              ContactsContract.CommonDataKinds.Phone.NUMBER,
//            ContactsContract.CommonDataKinds.Photo.PHOTO
      };
      ContentResolver cr = ctx.getContentResolver();
      Cursor contactsCursor = cr.query(ContactsContract.Data.CONTENT_URI,
              projection, ContactsContract.Data.DISPLAY_NAME + "='" + displayName + "'", null, null);
      long contactID = -1;
      if(contactsCursor.moveToFirst()){
    	  contactID = contactsCursor.getLong(contactsCursor.getColumnIndexOrThrow(ContactsContract.Data.CONTACT_ID));
      }
      contactsCursor.close();
      return contactID;
    }
    
    public static Uri getPhotoUri(Context ctx, String displayName) {
    	String[] projection = new String[] {
//                ContactsContract.Data.PHOTO_ID,
    			ContactsContract.Data.DISPLAY_NAME,
                ContactsContract.Data.CONTACT_ID,
//                ContactsContract.Data.PHOTO_URI,
//                ContactsContract.CommonDataKinds.Phone.NUMBER,
//              ContactsContract.CommonDataKinds.Photo.PHOTO
        };
        ContentResolver cr = ctx.getContentResolver();
        Cursor contactsCursor = cr.query(ContactsContract.Data.CONTENT_URI,
                projection, ContactsContract.Data.DISPLAY_NAME + "='" + displayName + "'", null, null);
        
        try {
        	if(contactsCursor.moveToFirst()){
        		long contactID = contactsCursor.getLong(contactsCursor.getColumnIndexOrThrow(ContactsContract.Data.CONTACT_ID));
        		Cursor cur = cr.query(ContactsContract.Data.CONTENT_URI,
                        null,
                        ContactsContract.Data.CONTACT_ID + "=" + contactID + " AND "
                                + ContactsContract.Data.MIMETYPE + "='"
                                + ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE + "'", null, null);
        		try{
        			Uri person = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactID);
                    return Uri.withAppendedPath(person, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
        		}finally{
        			cur.close();
        		}
        	}
        	return null;
        } finally{
        	contactsCursor.close();
        }
    }
    
    public Bitmap getFacebookPhoto(String phoneNumber) {
        Uri phoneUri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Uri photoUri = null;
        ContentResolver cr = this.getContentResolver();
        Cursor contact = cr.query(phoneUri,
                new String[] { ContactsContract.Contacts._ID }, null, null, null);

        if (contact.moveToFirst()) {
            long userId = contact.getLong(contact.getColumnIndex(ContactsContract.Contacts._ID));
            photoUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, userId);

        }
        else {
            Bitmap defaultPhoto = BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_menu_report_image);
            return defaultPhoto;
        }
        if (photoUri != null) {
            InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(
                    cr, photoUri);
            if (input != null) {
                return BitmapFactory.decodeStream(input);
            }
        } else {
            Bitmap defaultPhoto = BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_menu_report_image);
            return defaultPhoto;
        }
        Bitmap defaultPhoto = BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_menu_report_image);
        return defaultPhoto;
    }
}
