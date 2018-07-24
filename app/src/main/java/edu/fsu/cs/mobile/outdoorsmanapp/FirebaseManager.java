package edu.fsu.cs.mobile.outdoorsmanapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.User;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseManager {

    private static String TAG = FirebaseManager.class.getCanonicalName() + "ErrorChecking";
    private static String ANON = "anon";
    private static String USER_TABLE = "Users";
    private static String RECORDS_TABLE  = "Records";
    private static String LOBBY = "lobby";

    private MainActivity mActivity;
    private boolean firebaseSignedIn;
    private FirebaseUser mUser;
    private DatabaseReference mDatabase;
    private DatabaseReference mTable;
    private String mSessionName;

    public FirebaseManager(MainActivity m) {

        this(m, ANON + Calendar.getInstance().getTimeInMillis());

    }

    public FirebaseManager(MainActivity m, String sessionName){

        this.mActivity = m;
        this.firebaseSignedIn = false;
        this.mUser = null;
        this.mDatabase = FirebaseDatabase.getInstance().getReference();
        this.mSessionName = sessionName;

    }

    public boolean handleActivityResult(int requestCode, int resultCode, Intent data){

        if (requestCode == MainActivity.RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == MainActivity.RESULT_OK) {
                // Successfully signed in
                mUser = FirebaseAuth.getInstance().getCurrentUser();
                firebaseSignedIn = true;
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

    public void setSessionName(String s){

        this.mSessionName = s;

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

        firebaseSignedIn = false;
        mDatabase = null;
        mUser = null;
        mTable = null;
        mSessionName = null;

    }

    //not tested yet
    public void deleteUser(){

        AuthUI.getInstance()
                .delete(mActivity)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });

    }

    public FirebaseUser getCurrentUser(){

        return mUser;

    }

    public String getCurrentUserEmail(){

        return mUser.getEmail();

    }

    private void checkUserRecord(){

        final String key = UserRecord.getKeyFromEmail(getCurrentUserEmail());

        mDatabase.child(USER_TABLE).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
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
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }

        });

    }

    public void addToDatabase(HarvestRecord hr){
        String key = mDatabase.child(RECORDS_TABLE).push().getKey();

        Map<String, Object> postValues = hr.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/" + RECORDS_TABLE + "/" + key, postValues);
        mDatabase.updateChildren(childUpdates);


        //mTable = mDatabase.child(MAIN_TABLE).child(mSessionName);

        //mTable.setValue(loc);

    }

    public void addToDatabase(UserRecord ur){
        //String key = mDatabase.child(MAIN_TABLE).push().getKey();
        String key = ur.getKeyFromEmail();

        Map<String, Object> postValues = ur.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/" + USER_TABLE + "/" + key, postValues);
        mDatabase.updateChildren(childUpdates);


        //mTable = mDatabase.child(MAIN_TABLE).child(mSessionName);

        //mTable.setValue(loc);

    }
}
