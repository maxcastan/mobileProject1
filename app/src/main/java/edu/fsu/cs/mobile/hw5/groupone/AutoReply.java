package edu.fsu.cs.mobile.hw5.groupone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.telephony.SmsMessage;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AutoReply extends BroadcastReceiver {

    private FirebaseFirestore db=FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth=FirebaseAuth.getInstance();
    private Map<String, Object> phoneNumber=new HashMap<>();

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
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                phoneNumber.put("message", smsMessage);
                phoneNumber.put("number", phoneNum);
                db.collection("Users").document(currentUser.getUid())
                        .collection("Messages").document(phoneNum)
                        .set(phoneNumber);
            }
        }
    }
}
