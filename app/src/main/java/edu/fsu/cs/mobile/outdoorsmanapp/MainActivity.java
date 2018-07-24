package edu.fsu.cs.mobile.outdoorsmanapp;


import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    public static final int RC_SIGN_IN = 1234;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private static final String TAG = MainActivity.class.getCanonicalName()+"ErrorChecking";

    private FirebaseManager mFirebase;
    private Location currentLocation;
    private UserRecord myUserRecord;

    private Toolbar mToolbar;
    private ArrayList<HarvestRecord> HarvestRecordArrayList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.action_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ic_public_black_24dp);

        //AccountFragment accountFragment = new AccountFragment();
        //OnFragmentReplaced(accountFragment);

        MainFragment mainFragment = new MainFragment();
        OnFragmentReplaced(mainFragment);

        myUserRecord = new UserRecord();

        currentLocation = (new Location(LocationManager.GPS_PROVIDER));

        checkLocationPermission();

    }
    //Temporary ArrayList methods for testing
    public ArrayList<HarvestRecord> getHarvestRecordArrayList() {
        if(HarvestRecordArrayList == null){
            HarvestRecordArrayList = new ArrayList<>();
        }
        return HarvestRecordArrayList;
    }

    public void addHarvestRecordArrayListItem(HarvestRecord harvestRecord){
        if(HarvestRecordArrayList == null){
            HarvestRecordArrayList = new ArrayList<>();
        }
        HarvestRecordArrayList.add(harvestRecord);
    }
    //END Temporary ArrayList methods for testing

    public void OnFragmentReplaced(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame,fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle(R.string.locPermTitle)
                        .setMessage(R.string.locPermBody)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    public boolean isLocPermissionGranted(){

        return (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);

    }

    private void getLastLocation(){

        checkLocationPermission();

        FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        try{

            if(isLocPermissionGranted()){

                mFusedLocationClient.getLastLocation()
                        .addOnSuccessListener(new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // GPS location can be null if GPS is switched off
                                if (location != null) {
                                    //getAddress(location);
                                    currentLocation = location;
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "Error trying to get last GPS location");
                                e.printStackTrace();
                            }
                        });

            }else{

                Log.e(TAG, "Location Permission not granted");

            }

        }catch(SecurityException s){

            Log.e(TAG, "Security Exception: Permission probably not found");

        }

    }

    public void firebaseSignIn(){

        if(mFirebase == null){

            //firebase tests
            //initialize manager
            mFirebase = new FirebaseManager(this);

            //start firebase session with login
            mFirebase.startAuth();

        }

    }

    public void setMyUserRecord(UserRecord userRecord){
        myUserRecord = userRecord;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_home:
                AccountFragment accountFragment = new AccountFragment();
                OnFragmentReplaced(accountFragment);
                break;
            case R.id.action_form:
                FormFragment formFragment = new FormFragment();
                OnFragmentReplaced(formFragment);
                break;
            case R.id.action_record_list:
                RecordListFragment recordListFragment = new RecordListFragment();
                OnFragmentReplaced(recordListFragment);
                break;
            case R.id.action_map:
                if(!getHarvestRecordArrayList().isEmpty()){
                    MapFragment mapFragment = new MapFragment();
                    Bundle bundle = new Bundle();
                    bundle.putInt(MapFragment.ARG_PARAM_HARVEST_ID, getHarvestRecordArrayList().get(0).getId());
                    mapFragment.setArguments(bundle);
                    OnFragmentReplaced(mapFragment);
                }


                break;
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //firebase tests
        //send result to FirebaseManager
        if(mFirebase != null){

            if (mFirebase.handleActivityResult(requestCode, resultCode, data)){

                AccountFragment accountFragment = new AccountFragment();
                OnFragmentReplaced(accountFragment);

            }

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        Log.i(TAG, "Location Permission Granted");
                    }

                } else {

                    //denied
                    //locationPermissionGranted = false;
                    Log.i(TAG, "Location Permission Denied");

                }
                return;
            }

        }
    }

}
