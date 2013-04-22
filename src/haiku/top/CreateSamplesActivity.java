package haiku.top;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.Vibrator;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class CreateSamplesActivity extends Activity implements SeekBar.OnSeekBarChangeListener, OnClickListener {
	private ProgressDialog progressDialog;
	private Vibrator vibe;
	
	private Button loadButton;
	private Button removeButton;
	private TextView numberOfContacts;
	private TextView numberOfSMS;
	private TextView log;
	private TextView warning;
	private SeekBar contactSlide;
	private SeekBar smsSlide;
	
	private int numberOfContactsToAdd = 10; //min 10, max 100
	private int numberOfSMSToAdd = 10; //min 10, max 23
	
	private ArrayList<Contact> contacts = new ArrayList<Contact>();
	private ArrayList<String> sms = new ArrayList<String>();
	private boolean samplesExist; //has samples been generated/loaded
	
	private SharedPreferences mPrefs;
	private final String SAMPLES_EXIST_KEY = "SampleContactSMS_samplesExist";
	private final String EXPORT_CONTACT_KEY = "SampleContactSMS_exportContact";
	private final String EXPORT_SMS_KEY = "SampleContactSMS_exportSMS";
	
	private final long ONE_MINUTE = 60000L;
	private final long ONE_HOUR = 60*ONE_MINUTE;
	private final long ONE_DAY = 24*ONE_HOUR;
	private final long ONE_MONTH = 30*ONE_DAY;
	private final long ONE_YEAR = 31557600000L;
	private final long NOW = Calendar.getInstance().getTimeInMillis();
	private final long TWO_YEARS_BACK = (long)(NOW - (ONE_YEAR*2));
	
    protected void onPause() { //save data between sessions
        super.onPause();
        SharedPreferences.Editor ed = mPrefs.edit();
        ed.putBoolean(SAMPLES_EXIST_KEY, samplesExist);
        
        if (samplesExist) { //if samples were created during session, save contacts
	        Set<String> exportContact = new HashSet<String>();
	        for (Contact contact : contacts)
	        	exportContact.add(contact.name + "�*$" + contact.phoneNumber); //token separator    
	        ed.putStringSet(EXPORT_CONTACT_KEY, exportContact);
	        
	        Set<String> exportSMS = new HashSet<String>(sms);
	        ed.putStringSet(EXPORT_SMS_KEY, exportSMS);
        }
        ed.commit();
    }

    @Override 
    public void onBackPressed() {
    	startActivity(new Intent(this, HaikuActivity.class));
    }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_samples);
		
		//import saved data
		mPrefs = getPreferences(MODE_PRIVATE);
        samplesExist = mPrefs.getBoolean(SAMPLES_EXIST_KEY, false); //has contacts/SMS been loaded in a previous session?
        
        //if so, load contacts
        if (samplesExist) {
        	Set<String> importContact = new HashSet<String>();
        	importContact = mPrefs.getStringSet(EXPORT_CONTACT_KEY, null);

	        ArrayList<String> contactsToArray = new ArrayList<String>(importContact);
	        for (int i=0; i < contactsToArray.size(); i++)
	        		contacts.add(new Contact(contactsToArray.get(i).
	        		substring(0, contactsToArray.get(i).indexOf("�*$")), contactsToArray.get(i). 
	        		substring(contactsToArray.get(i).indexOf("�*$") + 3, contactsToArray.get(i).length())));
	        
	        Set<String> importSMS = new HashSet<String>();
	        importSMS = mPrefs.getStringSet(EXPORT_SMS_KEY, null);
	        sms.addAll(importSMS);
        }
        
        vibe = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        
        Typeface adobeGaramondProRegular = Typeface.createFromAsset(getAssets(), "fonts/AGARAMONDPRO-REGULAR.OTF");
        Typeface adobeGaramondProBold = Typeface.createFromAsset(getAssets(), "fonts/AGARAMONDPRO-BOLD.OTF");
        Typeface adobeGaramondProItalic = Typeface.createFromAsset(getAssets(), "fonts/AGARAMONDPRO-ITALIC.OTF");
        Typeface adobeGaramondProBoldItalic = Typeface.createFromAsset(getAssets(), "fonts/AGARAMONDPRO-BOLDITALIC.OTF");
        
        ((TextView)findViewById(R.id.text1)).setTypeface(adobeGaramondProBold);
        ((TextView)findViewById(R.id.text2)).setTypeface(adobeGaramondProRegular);
        ((TextView)findViewById(R.id.text3)).setTypeface(adobeGaramondProBold);
        ((TextView)findViewById(R.id.text4)).setTypeface(adobeGaramondProBold);
        
		log = (TextView)findViewById(R.id.log);
		log.setTypeface(adobeGaramondProBoldItalic);
		warning = (TextView)findViewById(R.id.warning);
		warning.setTypeface(adobeGaramondProItalic);
		numberOfContacts = (TextView)findViewById(R.id.numberOfContacts2);
		numberOfContacts.setTypeface(adobeGaramondProRegular);
		numberOfSMS = (TextView)findViewById(R.id.numberOfSMS);
		numberOfSMS.setTypeface(adobeGaramondProRegular);
		loadButton = (Button)findViewById(R.id.loadButton);
		loadButton.setTypeface(adobeGaramondProRegular);
		removeButton = (Button)findViewById(R.id.removeButton);
		removeButton.setTypeface(adobeGaramondProRegular);
		contactSlide = (SeekBar)findViewById(R.id.contactSlide2);
		smsSlide = (SeekBar)findViewById(R.id.smsSlide);
		
		if (samplesExist) {
			loadButton.setEnabled(false);
			log.setText("Samples already loaded");
		}
		else {
			removeButton.setEnabled(false);
			log.setText("No samples loaded");
			warning.setVisibility(View.INVISIBLE);
		}

		loadButton.setOnClickListener(this);
		removeButton.setOnClickListener(this);
		contactSlide.setOnSeekBarChangeListener(this);
		smsSlide.setOnSeekBarChangeListener(this);
	}
	
	@Override
	public void onClick(View v) {
		if(v.equals(loadButton)){
			vibe.vibrate(70);
			new LoadTask(true).execute(); 		
			samplesExist = true;
			loadButton.setEnabled(false);
			removeButton.setEnabled(true);
			log.setText("Samples loaded");
			warning.setVisibility(View.VISIBLE);
		}
		
		if(v.equals(removeButton)) {
			vibe.vibrate(70);
			new LoadTask(false).execute(); 			
			samplesExist = false;
			loadButton.setEnabled(true);
			removeButton.setEnabled(false);
			log.setText("Samples removed");
			warning.setVisibility(View.INVISIBLE);
		}
	}
	
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        //  Notify that the progress level has changed.
    	if (seekBar.getId() == R.id.contactSlide2)
    	{
    		numberOfContactsToAdd = (progress < 10) ? 10 : progress;
    		numberOfContacts.setText("" + numberOfContactsToAdd);
    	}
    	else if (seekBar.getId() == R.id.smsSlide)
    	{
    		numberOfSMSToAdd = (progress < 10) ? 10 : progress;
    		numberOfSMS.setText("" + numberOfSMSToAdd);
    	}
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // Notify that the user has started a touch gesture.
    }
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // Notify that the user has finished a touch gesture.
    }
	
    private void addContact(Context context, String name, String phoneNumber) {
		contacts.add(new Contact(name, phoneNumber));
		//add contact to phone's database
		 ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
		 int rawContactInsertIndex = ops.size();
		 
		 ops.add(ContentProviderOperation.newInsert(RawContacts.CONTENT_URI)
		          .withValue(RawContacts.ACCOUNT_TYPE, null)
		          .withValue(RawContacts.ACCOUNT_NAME, null).build());
		 
		 ops.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
		          .withValueBackReference(Data.RAW_CONTACT_ID, rawContactInsertIndex)
		          .withValue(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE)
		          .withValue(StructuredName.DISPLAY_NAME, name).build()); //name of person
		 
		 ops.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                .withValue(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
                .withValue(Phone.NUMBER, phoneNumber) //phone number
                .withValue(Phone.TYPE, Phone.TYPE_MOBILE).build()); //type of mobile number
		 
		 AssetManager assetManager = context.getAssets();
		 InputStream istr;
		 Bitmap bitmap = null;
		 try { istr = assetManager.open("sample-avatar.png"); bitmap = BitmapFactory.decodeStream(istr); } catch (IOException e) {}
         ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
         bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);   
         byte[] b = baos.toByteArray();

         ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                 .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                 .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
                 .withValue(ContactsContract.CommonDataKinds.Photo.DATA15, b).build());
		 
		 try { getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops); } 
		 catch (RemoteException e) {} catch (OperationApplicationException e) {}
	}
	
    private void deleteContact(Context ctx, String name, String phone) { //delete by using name and phone number. Could be done with keeping track of contact IDs, but this works as well but a bit tediously
		String[] projection = new String[] { ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.Contacts.LOOKUP_KEY };
        ContentResolver cr = ctx.getContentResolver();
        Cursor cur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + "='" + name + "'" 
        + " AND " + ContactsContract.CommonDataKinds.Phone.NUMBER + "='" + phone + "'", null, null);
	    try {
	    	if (cur.moveToFirst()) {
	            do {
	            	String lookupKey = cur.getString(cur.getColumnIndexOrThrow(ContactsContract.Contacts.LOOKUP_KEY));
	            	Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey);
                    ctx.getContentResolver().delete(uri, null, null);
	            } while (cur.moveToNext());
	        }
	    } catch (Exception e) {} 
	      finally { cur.close(); }
	}
	
    private void addSMS(String address, String date, String text, boolean inbox) {
		ContentValues values = new ContentValues();
		values.put("address", address);
		values.put("date", date);
		values.put("read", 1);
		values.put("body", text);
		if (inbox) //add to inbox
			sms.add(Long.toString(ContentUris.parseId(getContentResolver().insert(Uri.parse("content://sms/inbox"), values))));
		else //else add to outbox
			sms.add(Long.toString(ContentUris.parseId(getContentResolver().insert(Uri.parse("content://sms/sent"), values))));
	}
 
    private void deleteSMS(Context context, String id) {
    	context.getContentResolver().delete(Uri.parse("content://sms/" + id), null, null);
	}
    
    private class LoadTask extends AsyncTask<Void, Integer, Void> {
    	private boolean loadOrRemove;
    	public LoadTask(boolean loadOrRemove){
    		super();
    		this.loadOrRemove = loadOrRemove;
    	}
    	
		@Override
		protected void onPreExecute() {	
            progressDialog = new ProgressDialog(CreateSamplesActivity.this);  
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);    
            progressDialog.setTitle("Processing...");
            if (loadOrRemove) //load
            {
            	progressDialog.setMessage("Generating and loading sample contacts and SMS, please wait...");
                progressDialog.setMax(numberOfContactsToAdd + numberOfSMSToAdd);
            }
            else { //remove
            	progressDialog.setMessage("Removing sample contacts and SMS, please wait...");
            	progressDialog.setMax(contacts.size() + sms.size());
            }
            progressDialog.setCancelable(false);  
            progressDialog.setIndeterminate(false);   

            progressDialog.setProgress(0);  
            progressDialog.show(); 
		}

		@Override
		protected Void doInBackground(Void... params) {
			if (loadOrRemove) { //load
				final double progressValue = 100.0/(numberOfContactsToAdd + numberOfSMSToAdd); 
				int progress = 0;
							
				try {
					//addSampleContacts
					BufferedReader reader = new BufferedReader(new InputStreamReader(getAssets().open("namelist.txt")));
					for (int i=0; i<(numberOfContactsToAdd-5); i++) { //5 manual entries
						addContact(CreateSamplesActivity.this, reader.readLine(), "+4670000" + (1000 + i)); //fictional persons
						publishProgress((int)((++progress)*progressValue));
					}
					reader.close();
					//manual entries (real persons) = 5
					addContact(CreateSamplesActivity.this, "Fredrik Hagnell", "+41736000000");
					publishProgress((int)((++progress)*progressValue));
					addContact(CreateSamplesActivity.this, "Karl-Axel Zander", "+41736000001");
					publishProgress((int)((++progress)*progressValue));
					addContact(CreateSamplesActivity.this, "Pedro Ferreria", "+41736000002");
					publishProgress((int)((++progress)*progressValue));
					addContact(CreateSamplesActivity.this, "Vygandas Simbelis", "+41736000003");
					publishProgress((int)((++progress)*progressValue));
					addContact(CreateSamplesActivity.this, "Elsa Vaara", "+41736000004");
					publishProgress((int)((++progress)*progressValue));
					
					//addSampleSMS
					reader = new BufferedReader(new InputStreamReader(getAssets().open("sms-conversations.txt")));		
					String address = "";
					String date = "";
					String text = "";
					boolean inboxOrSent = false;	
					String input = reader.readLine();
					int i = 0;
					howmany:
					while (true)
					{
						if (input.length() > 1)
							address = input.substring(2, input.length()); //put sms to given phone number
						else
							address = contacts.get((int)(Math.random()*contacts.size())).phoneNumber; //phone number from random contact
						
						boolean sameDate = false;
						while (true) {			
							if (i >= numberOfSMSToAdd)
								break howmany;
							if ((input = reader.readLine()) == null) //not enough of conversation to create desired amount, shouldn't happen
								break howmany;
							if (input.equals("")) //empty line in file
								input = reader.readLine();
							if (input.substring(0,1).equals("$"))
								break;
							if (!(input.substring(0,1).equals("-") || input.substring(0,1).equals("+"))) { //date for following SMS exist
								DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
								Date d;
								try { d = format.parse(input); date = Long.toString(d.getTime()); } catch (ParseException e) {}
								sameDate = true;
								input = reader.readLine();
							}
							else {
								if (!sameDate) { //first sms in thread without set date
									long randomTime = (long)(Math.random()*(NOW-TWO_YEARS_BACK)); //random date between now and four years back
									Date d = new Date(NOW-randomTime);
									date = Long.toString(d.getTime());
									sameDate = true;
								}
								else { //randomize SMS date to something close after last SMS in this thread 
									long randomTime = Long.parseLong(date);
									Date d = new Date(randomTime + 10000 + ((long)(Math.random()*(ONE_HOUR/2)))); //some time between 10 seconds and half an hour
									date = Long.toString(d.getTime());
								}
							}
							if (input.substring(0,1).equals("+"))
								inboxOrSent = true; //received sms
							else if (input.substring(0,1).equals("-"))
								inboxOrSent = false; //sent sms
							
							text = input.substring(1, input.length());
							addSMS(address, date, text, inboxOrSent);
							publishProgress((int)((++progress)*progressValue));
							i++;
						}		
					}
					reader.close();
				} catch (IOException e) {}
			}
			
			else { //remove
				final double progressValue = 100.0/(contacts.size() + sms.size()); 
				int progress = 0;
				
				for (Contact contact : contacts) { //delete all loaded contacts from this app
					deleteContact(CreateSamplesActivity.this, contact.name, contact.phoneNumber);
					publishProgress((int)((++progress)*progressValue));
				}
				contacts.clear();
				for (int i=0; i<sms.size(); i++) { //delete all loaded sms from this app
					deleteSMS(CreateSamplesActivity.this, sms.get(i));
				}
				sms.clear();
			}		
			return null;
		}
		
        @Override  
        protected void onProgressUpdate(Integer... values)  
        {  
            //set the current progress of the progress dialog  
            progressDialog.setProgress(values[0]);  
        }  
		
		@Override
		protected void onPostExecute(Void result) {
			progressDialog.dismiss();
		} 	
    }
}