package haiku.top.sql;

import java.util.ArrayList;

import haiku.top.model.Contact;
import haiku.top.model.SMS;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper{
	
	 // Database Version
    private static final int DATABASE_VERSION = 1;
 
    // Database Name
    private static final String DATABASE_NAME = "Database";
 
    // table names
    private static final String TABLE_CONTACTS = "contact";
    private static final String TABLE_SMS = "sms";
 
    // Contacts Table Columns names
    private static final String KEY_CONTACT_ID = "id";
    private static final String KEY_CONTACT_NAME = "name";
    private static final String KEY_CONTACT_PH_NO = "phone number";
    
    // SMS table column names
    private static final String KEY_SMS_ID = "id";
    private static final String KEY_SMS_MESSAGE = "message";
    private static final String KEY_SMS_DATE = "date";
    private static final String KEY_SMS_CONTACT = "contact"; 

	public DatabaseHandler(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "("
                + KEY_CONTACT_ID + " INTEGER PRIMARY KEY," + KEY_CONTACT_NAME + " TEXT,"
                + KEY_CONTACT_PH_NO + " TEXT" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
 
        // Create tables again
        onCreate(db);
	}
	
	// Adding new contact
	public void addContact(Contact contact) {
		SQLiteDatabase db = this.getWritableDatabase();
		 
	    ContentValues values = new ContentValues();
	    values.put(KEY_CONTACT_NAME, contact.getName()); // Contact Name
	    values.put(KEY_CONTACT_PH_NO, contact.getPhoneNumber()); // Contact Phone Number
	 
	    // Inserting Row
	    db.insert(TABLE_CONTACTS, null, values);
	    db.close(); // Closing database connection
	}
	 
	// Getting single contact
	public Contact getContact(int id) {
		SQLiteDatabase db = this.getReadableDatabase();
		 
	    Cursor cursor = db.query(TABLE_CONTACTS, new String[] { KEY_CONTACT_ID,
	            KEY_CONTACT_NAME, KEY_CONTACT_PH_NO }, KEY_CONTACT_ID + "=?",
	            new String[] { String.valueOf(id) }, null, null, null, null);
	    if (cursor != null)
	        cursor.moveToFirst();
	 
	    Contact contact = new Contact(Integer.parseInt(cursor.getString(0)),
	            cursor.getString(1), cursor.getString(2));
	    // return contact
	    return contact;
	}
	 
	// Getting All Contacts
	public ArrayList<Contact> getAllContacts() {
		ArrayList<Contact> contactList = new ArrayList<Contact>();
	    // Select All Query
	    String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS;
	 
	    SQLiteDatabase db = this.getWritableDatabase();
	    Cursor cursor = db.rawQuery(selectQuery, null);
	 
	    // looping through all rows and adding to list
	    if (cursor.moveToFirst()) {
	        do {
	            Contact contact = new Contact();
	            contact.setID(Integer.parseInt(cursor.getString(0)));
	            contact.setName(cursor.getString(1));
	            contact.setNumber(cursor.getString(2));
	            // Adding contact to list
	            contactList.add(contact);
	        } while (cursor.moveToNext());
	    }
	 
	    // return contact list
	    return contactList;
	}
	 
	// Getting contacts Count
	public int getContactsCount() {
		String countQuery = "SELECT  * FROM " + TABLE_CONTACTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();
 
        // return count
        return cursor.getCount();
	}
	
	// Updating single contact
	public int updateContact(Contact contact) {
		SQLiteDatabase db = this.getWritableDatabase();
		 
	    ContentValues values = new ContentValues();
	    values.put(KEY_CONTACT_NAME, contact.getName());
	    values.put(KEY_CONTACT_PH_NO, contact.getPhoneNumber());
	 
	    // updating row
	    return db.update(TABLE_CONTACTS, values, KEY_CONTACT_ID + " = ?",
	            new String[] { String.valueOf(contact.getID()) });
	}
	 
	// Deleting single contact
	public void deleteContact(Contact contact) {
		SQLiteDatabase db = this.getWritableDatabase();
	    db.delete(TABLE_CONTACTS, KEY_CONTACT_ID + " = ?",
	            new String[] { String.valueOf(contact.getID()) });
	    db.close();
	}

	
	
	
	
	
	
	
	
	
	
	
	
	// Adding new sms
	public void addSMS(SMS sms) {
		SQLiteDatabase db = this.getWritableDatabase();
		 
	    ContentValues values = new ContentValues();
	    values.put(KEY_SMS_MESSAGE, sms.getMessage());
	    values.put(KEY_SMS_DATE, sms.getDate());
	    values.put(KEY_SMS_CONTACT, sms.getContactID());
	 
	    // Inserting Row
	    db.insert(TABLE_SMS, null, values);
	    db.close(); // Closing database connection
	}
	 
	// Getting single contact
	public SMS getSMS(int id) {
		SQLiteDatabase db = this.getReadableDatabase();
		 
	    Cursor cursor = db.query(TABLE_SMS, new String[] { KEY_SMS_ID,
	            KEY_SMS_MESSAGE, KEY_SMS_DATE, KEY_SMS_CONTACT }, KEY_SMS_ID + "=?",
	            new String[] { String.valueOf(id) }, null, null, null, null);
	    if (cursor != null)
	        cursor.moveToFirst();
	 
	    SMS sms = new SMS(Integer.parseInt(cursor.getString(0)),cursor.getString(1), cursor.getString(2), Integer.parseInt(cursor.getString(3)));
	    // return contact
	    return sms;
	}
	 
	// Getting All Contacts
	public ArrayList<SMS> getAllSMS() {
		ArrayList<SMS> smsList = new ArrayList<SMS>();
	    // Select All Query
	    String selectQuery = "SELECT  * FROM " + TABLE_SMS;
	 
	    SQLiteDatabase db = this.getWritableDatabase();
	    Cursor cursor = db.rawQuery(selectQuery, null);
	 
	    // looping through all rows and adding to list
	    if (cursor.moveToFirst()) {
	        do {
	            SMS sms = new SMS();
	            sms.setID(Integer.parseInt(cursor.getString(0)));
	            sms.setMessage(cursor.getString(1));
	            sms.setDate(cursor.getString(2));
	            sms.setContact(Integer.parseInt(cursor.getString(3)));
	            // Adding contact to list
	            smsList.add(sms);
	        } while (cursor.moveToNext());
	    }
	 
	    // return contact list
	    return smsList;
	}
	 
	// Getting contacts Count
	public int getSMSCount() {
		String countQuery = "SELECT  * FROM " + TABLE_SMS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();
 
        // return count
        return cursor.getCount();
	}
	
	// Updating single contact
	public int updateSMS(SMS sms) {
		SQLiteDatabase db = this.getWritableDatabase();
		 
	    ContentValues values = new ContentValues();
	    values.put(KEY_SMS_MESSAGE, sms.getMessage());
	    values.put(KEY_SMS_DATE, sms.getDate());
	    values.put(KEY_SMS_CONTACT, sms.getContactID());
	 
	    // updating row
	    return db.update(TABLE_SMS, values, KEY_SMS_ID + " = ?",
	            new String[] { String.valueOf(sms.getID()) });
	}
	 
	// Deleting single contact
	public void deleteSMS(SMS sms) {
		SQLiteDatabase db = this.getWritableDatabase();
	    db.delete(TABLE_SMS, KEY_SMS_ID + " = ?",
	            new String[] { String.valueOf(sms.getID()) });
	    db.close();
	}
	
}
