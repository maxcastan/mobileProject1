package edu.fsu.cs.mobile.hw5.groupone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class AutoReply extends BroadcastReceiver {

    public static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    public String phoneNum;
    public String smsMessage;
    public String location = "Dirac";

    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.getAction().equals(SMS_RECEIVED)){
            Bundle bundleExtras = intent.getExtras();

            if(bundleExtras != null){
                Object[] pdus = (Object[]) bundleExtras.get("pdus");
                if(pdus.length == 0){
                    return;
                }

                SmsMessage[] messages =  new SmsMessage[pdus.length];
                StringBuilder sb = new StringBuilder();

                //large messages may be split into multiple messages, this manages that
                for(int i=0; i<pdus.length; i++){
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                    sb.append(messages[i].getMessageBody());
                }
                phoneNum = messages[0].getOriginatingAddress();
                smsMessage = sb.toString();

                String smsMessageStr = "SMS from: " + phoneNum + "\n";
                smsMessageStr += smsMessage = "\n";
                Toast.makeText(context, smsMessageStr, Toast.LENGTH_LONG).show();

                Uri uri = Uri.parse("smsto:" + phoneNum);
                Intent smsText = new Intent(Intent.ACTION_SENDTO, uri);

                //this code below starts messaging app but right now it crashes the whole app
                /*
                smsText.putExtra("sms_body", "Sorry, I'm busy right now studying at " +
                        location);
                context.startActivity(smsText);
                */
            }
        }
    }
}
