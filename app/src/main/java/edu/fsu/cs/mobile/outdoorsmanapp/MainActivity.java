package edu.fsu.cs.mobile.outdoorsmanapp;


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
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
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public static final int RC_SIGN_IN = 1234;
    public static final int RC_LOCATION_ON = 3345;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private static final String TAG = MainActivity.class.getCanonicalName()+"ErrorChecking";

    private FirebaseManager mFirebase;
    private Location currentLocation;
    private UserRecord myUserRecord;
    private boolean lastLocationAvailable;

    private ArrayList<HarvestRecord> HarvestRecordArrayList;
    private ArrayList<HarvestRecord> feedHRArrayList;
    private MapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar mToolbar = findViewById(R.id.action_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ic_public_black_24dp);

        MainFragment mainFragment = new MainFragment();
        OnFragmentReplaced(mainFragment);

        myUserRecord = new UserRecord();

        HarvestRecordArrayList = new ArrayList<>();
        feedHRArrayList = new ArrayList<>();

        mapFragment = null;

        currentLocation = (new Location(LocationManager.GPS_PROVIDER));

        lastLocationAvailable = false;

        checkLocationPermission();

    }

    @Override
    protected void onDestroy(){

        super.onDestroy();

        //firebase tests

        if(mFirebase != null){

            mFirebase.endSession();

        }

    }

    private void startPopulateHR(){

        HarvestRecordArrayList = new ArrayList<>();

        mFirebase.getCurrentUserHarvestRecords();

    }

    public void onFinishPopulateHR(){

        internalOnFragmentChanged(new RecordListFragment());

    }

    private void startPopulateHRForMap(MapFragment mf){

        mapFragment = mf;

        HarvestRecordArrayList = new ArrayList<>();

        mFirebase.getCurrentUserHarvestRecordsForMap();

    }

    public void onFinishPopulateHRForMap(){

        if (mapFragment != null){

            if(mapFragment.getArguments() == null){
            //originated from MainActivity

                if(getHarvestRecordArrayList().size() > 0){

                    Bundle bundle = new Bundle();
                    bundle.putInt(MapFragment.ARG_PARAM_HARVEST_ID, getHarvestRecordArrayList().get(0).getId());
                    mapFragment.setArguments(bundle);
                    internalOnFragmentChanged(mapFragment);

                }else{

                    Log.i(TAG, "No HarvestRecords available. Did not open MapFragment.");
                    //No harvest records available, don't open fragment
                    //TODO: display message to user?
                }

            }else{
            //coming from RecordListFragment
                internalOnFragmentChanged(mapFragment);

            }

        }else{

            //no mapFragment available, unknown error
            Log.i(TAG, "mapFragment was null: unknown cause");

        }

    }

    private void startPopulateFeedHR(){

        feedHRArrayList = new ArrayList<>();

        mFirebase.getFeedHarvestRecords();

    }

    public void onFinishPopulateFeedHR(){

        internalOnFragmentChanged(new FeedFragment());

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

    public ArrayList<HarvestRecord> getFeedHRArrayList() {
        if(feedHRArrayList == null){
            feedHRArrayList = new ArrayList<>();
        }
        return feedHRArrayList;
    }

    public void addFeedHRArrayListItem(HarvestRecord harvestRecord){
        if(feedHRArrayList == null){
            feedHRArrayList = new ArrayList<>();
        }
        feedHRArrayList.add(harvestRecord);
    }

    //END Temporary ArrayList methods for testing

    private void internalOnFragmentChanged(Fragment fragment){

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame,fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }

    public void OnFragmentReplaced(Fragment fragment){

        if(fragment != null){

            if (fragment instanceof FormFragment){

                getLastLocationFormFrag();

            }else if (fragment instanceof RecordListFragment){

                startPopulateHR();

            }else if (fragment instanceof MapFragment){

                startPopulateHRForMap((MapFragment) fragment);

            }else if (fragment instanceof FeedFragment){

                startPopulateFeedHR();

            }else{

                internalOnFragmentChanged(fragment);

            }

        }

    }

    public void checkLocationPermission() {
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
        }
    }

    public boolean isLocPermissionGranted(){

        return (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);

    }

    public void checkLocationEnabled(){

        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {
            ex.printStackTrace();
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {
            ex.printStackTrace();
        }

        if(!gps_enabled && !network_enabled) {
            // notify user
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage(getResources().getString(R.string.gps_network_not_enabled));
            dialog.setPositiveButton(getResources().getString(R.string.open_location_settings), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(myIntent, RC_LOCATION_ON);
                    //get gps
                }
            });
            dialog.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    Toast.makeText(MainActivity.this, "Location Services Disabled", Toast.LENGTH_LONG).show();
                    Log.i(TAG, "User hit cancel: location services still disabled");
                }
            });
            dialog.show();
        }

    }

    public void getLastLocationFormFrag(){

        lastLocationAvailable = false;

        checkLocationPermission();

        checkLocationEnabled();

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
                                    Log.i(TAG, "Location returned non null: "+location.toString());
                                    lastLocationAvailable = true;
                                    currentLocation = location;
                                    internalOnFragmentChanged(new FormFragment());
                                }else{
                                    Log.i(TAG, "location returned null");
                                    lastLocationAvailable = false;
                                    currentLocation = (new Location(LocationManager.GPS_PROVIDER));
                                    currentLocation.reset();
                                    internalOnFragmentChanged(new FormFragment());

                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "Error trying to get last GPS location");
                                e.printStackTrace();

                                lastLocationAvailable = false;
                                currentLocation = (new Location(LocationManager.GPS_PROVIDER));
                                currentLocation.reset();

                                internalOnFragmentChanged(new FormFragment());
                            }
                        });

            }else{

                Log.e(TAG, "Location Permission not granted");
                lastLocationAvailable = false;
                currentLocation = (new Location(LocationManager.GPS_PROVIDER));
                currentLocation.reset();
                internalOnFragmentChanged(new FormFragment());

            }

        }catch(SecurityException s){

            Log.e(TAG, "Security Exception: Permission probably not found");
            lastLocationAvailable = false;
            currentLocation = (new Location(LocationManager.GPS_PROVIDER));
            currentLocation.reset();

            internalOnFragmentChanged(new FormFragment());
        }

    }

    public void getLastLocation(){

        lastLocationAvailable = false;

        checkLocationPermission();

        checkLocationEnabled();

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
                                    Log.i(TAG, "Location returned non null: "+location.toString());
                                    lastLocationAvailable = true;
                                    currentLocation = location;

                                }else{
                                    Log.i(TAG, "location returned null");
                                    lastLocationAvailable = false;
                                    currentLocation = (new Location(LocationManager.GPS_PROVIDER));
                                    currentLocation.reset();

                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "Error trying to get last GPS location");
                                e.printStackTrace();

                                lastLocationAvailable = false;
                                currentLocation = (new Location(LocationManager.GPS_PROVIDER));
                                currentLocation.reset();

                            }
                        });

            }else{

                Log.e(TAG, "Location Permission not granted");
                lastLocationAvailable = false;
                currentLocation = (new Location(LocationManager.GPS_PROVIDER));
                currentLocation.reset();

            }

        }catch(SecurityException s){

            Log.e(TAG, "Security Exception: Permission probably not found");
            lastLocationAvailable = false;
            currentLocation = (new Location(LocationManager.GPS_PROVIDER));
            currentLocation.reset();

        }

    }

    public void updateRecord(HarvestRecord hr){

        addHarvestRecordArrayListItem(hr);
        if (mFirebase != null){

            mFirebase.updateRecords(hr);

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

    public UserRecord getMyUserRecord() {
        return myUserRecord;
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public FirebaseUser getCurrentUser() {
        return mFirebase.getCurrentUser();
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
                //test
                //OnFragmentReplaced(new FeedFragment());
                break;
            case R.id.action_map:
                OnFragmentReplaced(new MapFragment());

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
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
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
            }

        }
    }

    @Override
    public void onBackPressed() {

        //disable back button

    }

}
