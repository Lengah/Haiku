package haiku.top.model;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsMessage;

public class SmsListener extends BroadcastReceiver{

    private SharedPreferences preferences;

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){
            Bundle bundle = intent.getExtras();           //---get the SMS message passed in---
            SmsMessage[] msgs = null;
            String msg_from;
            long date;
            if (bundle != null){
                //---retrieve the SMS message received---
                try{
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    msgs = new SmsMessage[pdus.length];
                    for(int i = 0; i < msgs.length; i++){
                        msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                        msg_from = msgs[i].getOriginatingAddress(); // contactID
                        String msgBody = msgs[i].getMessageBody(); // message
                        date = msgs[i].getTimestampMillis(); // date
                        // add to database
                        // TODO
                    }
                }catch(Exception e){}
            }
        }
    }
}
