package haiku.top.view.adapters;

import haiku.top.HaikuActivity;
import haiku.top.R;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class ContactListAdapter extends CursorAdapter {

	public ContactListAdapter(Context context, Cursor c, boolean autoRequery) {
		super(context, c, autoRequery);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		TextView contactName = (TextView) view.findViewById(R.id.contactname);
		TextView lastMsgContent = (TextView) view.findViewById(R.id.lastmsgcontent);

		contactName.setText(HaikuActivity.getContactName(context ,cursor.getString(cursor.getColumnIndex("address"))));
		lastMsgContent.setText(cursor.getString(cursor.getColumnIndex("body")));
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.item_contact, parent, false);
		bindView(v, context, cursor);
		return v;
	}

}
