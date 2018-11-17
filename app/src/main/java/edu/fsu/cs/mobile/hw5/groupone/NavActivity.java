package edu.fsu.cs.mobile.hw5.groupone;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import edu.fsu.cs.mobile.hw5.groupone.HomeFragment;
import edu.fsu.cs.mobile.hw5.groupone.LocationFragment;
import edu.fsu.cs.mobile.hw5.groupone.R;
import edu.fsu.cs.mobile.hw5.groupone.SocialFragment;

public class NavActivity extends AppCompatActivity {

    private BottomNavigationView mMainNav;
    private FrameLayout mMainFrame;
    private HomeFragment homeFragment;
    private SocialFragment socialFragment;
    private LocationFragment locationFragment;


    private static final int PERMISSION_SEND_SMS = 123;
    private int MY_PERMISSIONS_REQUEST_SMS_RECEIVE = 10;

    private Context context;
    private String phoneNum;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav);
        homeFragment=new HomeFragment();
        socialFragment=new SocialFragment();
        locationFragment=new LocationFragment();
        mMainFrame=(FrameLayout) findViewById(R.id.user_frame);
        mMainNav=(BottomNavigationView) findViewById(R.id.user_nav);
        setFragment(homeFragment);
        mMainNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){

                    case R.id.nav_home:
                        mMainNav.setItemBackgroundResource(R.color.colorPrimary);
                        setFragment(homeFragment);
                        return true;

                    case R.id.nav_soc:
                        mMainNav.setItemBackgroundResource(R.color.colorPrimary);
                        setFragment(socialFragment);
                        return true;

                    case R.id.nav_loc:
                        mMainNav.setItemBackgroundResource(R.color.colorPrimary);
                        setFragment(locationFragment);
                        return true;

                    default:
                        return false;
                }
            }
        });

        //code below for texting portion of app
        //request permissions for receiving SMS, must be done at runtime
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.RECEIVE_SMS},
                MY_PERMISSIONS_REQUEST_SMS_RECEIVE);


        //things get sticky, so store current activity inside context variable
        this.context = this;
        Uri uri = Uri.parse("smsto:" + phoneNum);
        Intent smsText = new Intent(Intent.ACTION_SENDTO, uri);

        //next check to see that AutoReply (Receiver) isn't currently active
        boolean smsReceiverRunning =(PendingIntent
                .getBroadcast(this.context, 0, smsText,
                        PendingIntent.FLAG_NO_CREATE) != null);

        if (smsReceiverRunning == false){
            PendingIntent pendingIntent = PendingIntent
                    .getBroadcast(this.context, 0, smsText, 0);

            requestSmsPermission(smsText);
        }


        Intent background = new Intent(context, BackgroundService.class);
        Toast.makeText(getApplicationContext(), "about to start Service", Toast.LENGTH_LONG)
                .show();
        context.startService(background);


    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.user_frame, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }


    //this method necessary to request permission at runtime
    private void requestSmsPermission(Intent smsText){
        // check permission is given
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            // request permission (see result in onRequestPermissionsResult() method)
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS}, PERMISSION_SEND_SMS);
            Toast toast = Toast.makeText(getApplicationContext(),
                    "permission granted", Toast.LENGTH_LONG);
            toast.show();

        } else {
            // permission already granted run sms send
            SmsManager smsManager =  SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNum, null, "Sorry, busy",
                    null, null);

            Toast toast = Toast.makeText(getApplicationContext(),
                    "permission already granted, running smsText activity", Toast.LENGTH_LONG);
            toast.show();

            /*
            Uri uri = Uri.parse("smsto:" + phoneNum);
            Intent smsText = new Intent(Intent.ACTION_SENDTO, uri);
            */
            smsText.putExtra("sms_body", "Sorry, I'm busy right now studying.");
            startActivity(smsText);
        }
    }

    //for some reason, this method also needs to be overwritten
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_SMS_RECEIVE) {
            // YES!!
            Log.i("TAG", "MY_PERMISSIONS_REQUEST_SMS_RECEIVE --> YES");
        }
    }

}