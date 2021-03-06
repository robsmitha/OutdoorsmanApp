package edu.fsu.cs.mobile.outdoorsmanapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseManager {

    private static String TAG = FirebaseManager.class.getCanonicalName() + "ErrorChecking";
    private static String ANON = "anon";
    private static String USER_TABLE = "Users";
    private static String RECORDS_TABLE  = "Records";

    private MainActivity mActivity;
    private FirebaseUser mUser;
    private DatabaseReference mDatabase;

    FirebaseManager(MainActivity m) {

        this.mActivity = m;
        this.mUser = null;
        this.mDatabase = FirebaseDatabase.getInstance().getReference();

    }

    public boolean handleActivityResult(int requestCode, int resultCode, Intent data){

        if (requestCode == MainActivity.RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == MainActivity.RESULT_OK) {
                // Successfully signed in
                mUser = FirebaseAuth.getInstance().getCurrentUser();
                mDatabase = FirebaseDatabase.getInstance().getReference();
                checkUserRecord();
                return true;
                // ...
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...

                if(response == null){

                    Log.e(TAG, "Sign In failed: User canceled");

                }else{

                    Log.e(TAG, "Sign In failed:" + response.getError().getErrorCode());

                }

                return false;

            }
        }

        return false;

    }

    public void startAuth(){

        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        // Create and launch sign-in intent
        mActivity.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setIsSmartLockEnabled(false)
                        .setAvailableProviders(providers)
                        .build(),
                MainActivity.RC_SIGN_IN);

    }

    public void endSession(){

        AuthUI.getInstance()
                .signOut(mActivity)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                        Log.i(TAG, "Firebase Session Ended");
                    }
                });

        mDatabase = null;
        mUser = null;

    }

    public FirebaseUser getCurrentUser(){

        return mUser;

    }

    private String getCurrentUserEmail(){

        return mUser.getEmail();

    }

    private void checkUserRecord(){

        final String key = UserRecord.getKeyFromEmail(getCurrentUserEmail());

        mDatabase.child(USER_TABLE).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(key)) {
                    Log.i(TAG, "User with key: "+key+" exists");
                    mActivity.setMyUserRecord(UserRecord.fromDataSnapshot(snapshot.child(key)));
                }else{

                    Log.i(TAG, "User with key: "+key+" does not exist. Creating record...");

                    UserRecord tempUserRecord = new UserRecord(getCurrentUser());

                    addToDatabase(tempUserRecord);

                    mActivity.setMyUserRecord(tempUserRecord);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
                mActivity.setMyUserRecord(new UserRecord(getCurrentUser()));
            }

        });

    }

    public void getCurrentUserHarvestRecordsForMap(){

        mDatabase.child(RECORDS_TABLE).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot hRSnapshot : snapshot.getChildren()) {

                    HarvestRecord newHR = HarvestRecord.fromDataSnapshot(hRSnapshot);

                    if (newHR.getKeyFromEmail().equals(HarvestRecord.getKeyFromEmail(getCurrentUserEmail()))){
                        //records belong to this user
                        mActivity.addHarvestRecordArrayListItem(newHR);

                    }

                }
                mActivity.onFinishPopulateHRForMap();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
                mActivity.onFinishPopulateHRForMap();
            }

        });

    }

    public void getCurrentUserHarvestRecords(){

        mDatabase.child(RECORDS_TABLE).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot hRSnapshot : snapshot.getChildren()) {

                    HarvestRecord newHR = HarvestRecord.fromDataSnapshot(hRSnapshot);

                    if (newHR.getKeyFromEmail().equals(HarvestRecord.getKeyFromEmail(getCurrentUserEmail()))){
                        //records belong to this user
                        mActivity.addHarvestRecordArrayListItem(newHR);

                    }

                }
                mActivity.onFinishPopulateHR();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
                mActivity.onFinishPopulateHR();
            }

        });

    }

    public void getFeedHarvestRecords(){

        mDatabase.child(RECORDS_TABLE).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot hRSnapshot : snapshot.getChildren()) {

                    HarvestRecord newHR = HarvestRecord.fromDataSnapshot(hRSnapshot);

                    if (newHR.getKeyFromEmail().equals(HarvestRecord.getKeyFromEmail(getCurrentUserEmail()))){
                        //records belong to this user
                        mActivity.addHarvestRecordArrayListItem(newHR);

                    }
                    mActivity.addFeedHRArrayListItem(newHR);

                }
                mActivity.onFinishPopulateFeedHR();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
                mActivity.onFinishPopulateFeedHR();
            }

        });

    }

    private void addToDatabase(HarvestRecord hr){
        String key = mDatabase.child(RECORDS_TABLE).push().getKey();

        hr.setId(key.hashCode());

        Map<String, Object> postValues = hr.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/" + RECORDS_TABLE + "/" + key, postValues);
        mDatabase.updateChildren(childUpdates);
    }

    private void addToDatabase(UserRecord ur){

        String key = ur.getKeyFromEmail();

        Map<String, Object> postValues = ur.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/" + USER_TABLE + "/" + key, postValues);
        mDatabase.updateChildren(childUpdates);
    }

    public void updateRecords(HarvestRecord hr){

        addToDatabase(hr);

        final int typeId = hr.getTypeId();

        final String key = UserRecord.getKeyFromEmail(getCurrentUserEmail());

        mDatabase.child(USER_TABLE).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(key)) {
                    Log.i(TAG, "User with key: "+key+" exists. Updating...");

                    UserRecord tempUserRecord = UserRecord.fromDataSnapshot(snapshot.child(key));

                    switch(typeId){
                        //is fish
                        case 0: tempUserRecord.setNumFish(tempUserRecord.getNumFish()+1);
                                break;
                        //is fowl
                        case 1: tempUserRecord.setNumFowl(tempUserRecord.getNumFowl()+1);
                                break;
                        //is deer
                        case 2: tempUserRecord.setNumDeer(tempUserRecord.getNumDeer()+1);
                                break;
                        //unknown
                        default:Log.i(TAG, "Unknown typeId");

                    }

                    addToDatabase(tempUserRecord);
                    mActivity.setMyUserRecord(tempUserRecord);

                }else{

                    Log.i(TAG, "User with key: "+key+" does not exist. Could not update record");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }

        });

    }
}
