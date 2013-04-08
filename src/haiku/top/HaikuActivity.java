package haiku.top;

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
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ha = this;
        mainView = new MainView(this);
        setContentView(mainView, new LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
    }
    
    public static HaikuActivity getInstance(){
    	return ha;
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
