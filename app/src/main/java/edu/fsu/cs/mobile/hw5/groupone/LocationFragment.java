package edu.fsu.cs.mobile.hw5.groupone;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class LocationFragment extends Fragment implements ActivityCompat.OnRequestPermissionsResultCallback{

    private static final int PERMISSION_REQUEST_LOCATIONS = 3;
    private TextView place;
    private LocationManager mLocationManager;
    private boolean mLocationPermissionGranted = false;
    private View root;
    private FirebaseAuth mAuth=FirebaseAuth.getInstance();
    private FirebaseFirestore db=FirebaseFirestore.getInstance();
    private Map<String, Object> locDatabase=new HashMap<>();


    double latitude;
    double longitude;
    ImageView img;

    public LocationFragment() {
        Log.i("this", "works");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("dis", "works");

        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_location, container, false);

        mLocationManager = (LocationManager) getActivity().getSystemService(
                Context.LOCATION_SERVICE);

        img = root.findViewById(R.id.imageView);
        img.setImageResource(R.mipmap.nowherephoto);




       // setUpMap();

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

        requestLocationUpdates(root);
        showLastKnownLocation();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLocationManager.removeUpdates(mLocationListener);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        place = root.findViewById(R.id.placeTV);
    }


    public void setSilent(){
        AudioManager am;
        am= (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        if(am.getRingerMode() != 0) {
            am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            Toast.makeText(getContext(), "Silencing the phone", Toast.LENGTH_LONG).show();
        }
    }

    public void setLoud(){
        AudioManager am;
        am= (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        if(am.getRingerMode() == 0) {
            am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            Toast.makeText(getContext(), "Unsilencing the phone", Toast.LENGTH_LONG).show();
        }
    }


    public void requestLocationUpdates(View v) {
        if(!mLocationPermissionGranted) {
            requestLocationsPermission();
        }
        try {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        0, 0, mLocationListener);
        } catch (SecurityException e) {
            requestLocationsPermission();
        }
    }

    private void requestLocationsPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) ||
                ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                        Manifest.permission.ACCESS_COARSE_LOCATION)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Requesting internet permissions");
            builder.setMessage("This application requires internet. Accept to continue");
            builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION},
                            PERMISSION_REQUEST_LOCATIONS);
                }
            });
            builder.show();
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSION_REQUEST_LOCATIONS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_LOCATIONS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getActivity(),
                        "Location permissions were granted.",
                        Toast.LENGTH_SHORT).show();
                showLastKnownLocation();
            } else {
                Toast.makeText(getActivity(), "Location permission request was denied.",
                                      Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showLastKnownLocation() {
        if (!mLocationPermissionGranted) {
            requestLocationsPermission();
        }
        Location lastLoc = null;
        //double latitude;
        //double longitude;
        try {
            lastLoc = mLocationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        } catch (SecurityException e) {
            requestLocationsPermission();
        }
        if(lastLoc == null) {
            place.setText(R.string.no_location);
            requestLocationUpdates(root);
            return;
        }

        latitude = lastLoc.getLatitude();
        longitude = lastLoc.getLongitude();

        final FirebaseUser currentUser=FirebaseAuth.getInstance().getCurrentUser();

        //comment
        if (latitude < 30.4437 && latitude > 30.4428
                && longitude > -84.2955 && longitude < -84.2945) {

           // Toast.makeText(getActivity(), "In Strozier", Toast.LENGTH_LONG).show();
            locDatabase.put("location", "Strozier");
            db.collection("Users").document(currentUser.getUid()).update(locDatabase)
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    place.setText(R.string.strozier_library);
                    img.setImageResource(R.mipmap.strozphoto);
                    setSilent();
                }
            });

        } else if (latitude < 30.4453 && latitude > 30.4447
                && longitude > -84.3002 && longitude < -84.2995){

            //Toast.makeText(getActivity(), "In Dirac", Toast.LENGTH_LONG).show();
            locDatabase.put("location", "Dirac");
            db.collection("Users").document(currentUser.getUid()).update(locDatabase)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            place.setText(R.string.dirac_science_library);
                            img.setImageResource(R.mipmap.diracphoto);
                            setSilent();
                        }
                    });
        } else if(latitude < 30.4435 && latitude >  30.4430
                && longitude > -84.2976 && longitude < -84.2966){
            locDatabase.put("location", "HCB");
            db.collection("Users").document(currentUser.getUid()).update(locDatabase)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            img.setImageResource(R.mipmap.hcb);
                            //Toast.makeText(getActivity(), "In Classroom", Toast.LENGTH_LONG).show();
                            place.setText(R.string.hcb_classroom);
                            setSilent();
                        }
                    });
        }
        else {
            img.setImageResource(R.mipmap.nowherephoto);
            place.setText(R.string.not_in_study_place);
            setLoud();
        }

    }


    private LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {
            Toast.makeText(getContext(), "Waiting for location",
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderDisabled(String provider) {
            Toast.makeText(getContext(), "Connection Lost",
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onLocationChanged(Location location) {
            showLastKnownLocation();
        }
    };



}