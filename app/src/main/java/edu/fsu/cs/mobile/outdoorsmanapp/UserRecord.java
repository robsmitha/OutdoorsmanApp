package edu.fsu.cs.mobile.outdoorsmanapp;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;

import java.util.HashMap;
import java.util.Map;

public class UserRecord {

    private static final String USER_NAME = "userName";
    private static final String EMAIL = "email";
    private static final String NUM_FISH = "numFish";
    private static final String NUM_DEER = "numDeer";
    private static final String NUM_FOWL = "numFowl";

    private String userName;
    private String email;
    private int numFish;
    private int numFowl;
    private int numDeer;

    UserRecord(FirebaseUser mUser){

        if(mUser != null){

            userName = mUser.getDisplayName();
            email = mUser.getEmail();

        }else{

            userName = null;
            email = null;

        }
        numFish = 0;
        numFowl = 0;
        numDeer = 0;

    }

    UserRecord(){

        this(null);

    }

    @Override
    public String toString() {
        return "UserRecord{" +
                "userName='" + userName + '\'' +
                ", email='" + email + '\'' +
                ", numFish=" + numFish +
                ", numFowl=" + numFowl +
                ", numDeer=" + numDeer +
                '}';
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put(USER_NAME, userName);
        result.put(EMAIL, email);
        result.put(NUM_FISH, numFish);
        result.put(NUM_FOWL, numFowl);
        result.put(NUM_DEER, numDeer);

        return result;
    }

    public static UserRecord fromDataSnapshot(DataSnapshot userSnapshot) {

        String username = (String) userSnapshot.child(USER_NAME).getValue();
        String email = (String) userSnapshot.child(EMAIL).getValue();
        int numFish = (int) ((long)userSnapshot.child(NUM_FISH).getValue());
        int numFowl = (int) ((long)userSnapshot.child(NUM_FOWL).getValue());
        int numDeer = (int) ((long)userSnapshot.child(NUM_DEER).getValue());

        return (new UserRecord(null)).setUserName(username).setEmail(email)
                .setNumFish(numFish).setNumFowl(numFowl).setNumDeer(numDeer);
    }

    public String getKeyFromEmail(){

        return email.replace("@", "_a_t_").replace(".", "_d_o_t_");

    }

    public static String getKeyFromEmail(String email){

        return email.replace("@", "_a_t_").replace(".", "_d_o_t_");

    }

    public String getEmail() {
        return email;
    }

    public int getNumFish() {
        return numFish;
    }

    public int getNumFowl() {
        return numFowl;
    }

    public int getNumDeer() {
        return numDeer;
    }

    //setters
    private UserRecord setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public UserRecord setEmail(String email) {
        this.email = email;
        return this;
    }

    public UserRecord setNumFish(int numFish) {
        this.numFish = numFish;
        return this;
    }

    public UserRecord setNumFowl(int numFowl) {
        this.numFowl = numFowl;
        return this;
    }

    public UserRecord setNumDeer(int numDeer) {
        this.numDeer = numDeer;
        return this;
    }
}
