package edu.fsu.cs.mobile.outdoorsmanapp;

import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class HarvestRecord {

    private static final String USER_NAME = "userName";
    private static final String EMAIL = "email";
    private static final String TYPE_ID = "typeId";
    private static final String TYPE = "type";
    private static final String DATE = "date";
    private static final String LAT = "lat";
    private static final String LNG = "lng";
    private static final String ID = "id";

    private int id;
    private int typeId;
    private long date;
    private double lat;
    private double lng;
    private String userName;
    private String email;
    private String type;
    //private LatLng latLng;


    public HarvestRecord(@NonNull FirebaseUser mUser){

        if(mUser != null){

            userName = mUser.getDisplayName();
            email = mUser.getEmail();

        }else{

            userName = null;
            email = null;

        }
        id = 0;
        typeId = 0;
        type = "";
        date = Calendar.getInstance().getTimeInMillis();
        lat = 0.0;
        lng = 0.0;

    }

    public HarvestRecord(){

        this(null);

    }


    @Override
    public String toString() {
        return "HarvestRecord{" +
                "id=" + id +
                ", typeId=" + typeId +
                ", date=" + date +
                ", lat=" + lat +
                ", lng=" + lng +
                ", userName='" + userName + '\'' +
                ", email='" + email + '\'' +
                ", type='" + type + '\'' +
                '}';
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put(ID, id);
        result.put(TYPE_ID, typeId);
        result.put(TYPE, type);
        result.put(USER_NAME, userName);
        result.put(EMAIL, email);
        result.put(DATE, date);
        result.put(LAT, lat);
        result.put(LNG, lng);

        return result;
    }

    public static HarvestRecord fromDataSnapshot(DataSnapshot userSnapshot) {
        String key = (String) userSnapshot.getKey();
        String username = (String) userSnapshot.child(USER_NAME).getValue();
        String email = (String) userSnapshot.child(EMAIL).getValue();
        String type = (String) userSnapshot.child(TYPE).getValue();
        int typeId = (int) userSnapshot.child(TYPE_ID).getValue();
        long date = (long) userSnapshot.child(DATE).getValue();
        double lat = (double) userSnapshot.child(LAT).getValue();
        double lng = (double) userSnapshot.child(LNG).getValue();
        int id = (int) userSnapshot.child(ID).getValue();

        return (new HarvestRecord(null)).setUserName(username).setEmail(email)
                .setType(type).setTypeId(typeId).setDate(date).setLat(lat).setLng(lng)
                .setId(id);
    }

    public Calendar convertMillisToCalendar(long ms){

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(ms);

        return calendar;
    }

    public String getDateString() {

        Calendar calendar = convertMillisToCalendar(this.date);  //get self date

        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);

        int mHour = calendar.get(Calendar.HOUR);
        int mMinute = calendar.get(Calendar.MINUTE);
        int mSecond = calendar.get(Calendar.SECOND);


        CharSequence AM_OR_PM = calendar.get(Calendar.AM_PM) == 1 ? "PM" : "AM";


        return mMonth + "/" + mDay+"/" + mYear + " " + mHour + ":" + mMinute + " "+ AM_OR_PM;

    }

    public String getKeyFromEmail(){

        return email.replace("@", "_a_t_").replace(".", "_d_o_t_");

    }

    public static String getKeyFromEmail(String email){

        return email.replace("@", "_a_t_").replace(".", "_d_o_t_");

    }

    //getters
    public String getUserName() {
        return userName;
    }

    public String getEmail() {
        return email;
    }

    public int getTypeId() {
        return typeId;
    }

    public String getType() {
        return type;
    }

    public long getDate() {
        return date;
    }

    public int getId() {
        return id;
    }

    public LatLng getLatLng() {
        return new LatLng(lat, lng);
    }

    //setters
    public HarvestRecord setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public HarvestRecord setEmail(String email) {
        this.email = email;
        return this;
    }

    public HarvestRecord setTypeId(int typeId) {
        this.typeId = typeId;
        return this;
    }

    public HarvestRecord setType(String type) {
        this.type = type;
        return this;
    }

    public HarvestRecord setDate(long date) {
        this.date = date;
        return this;
    }

    public HarvestRecord setLat(double lat){

        this.lat = lat;
        return this;

    }

    public HarvestRecord setLng(double lng){

        this.lng = lng;
        return this;

    }

    public HarvestRecord setId(int id) {
        this.id = id;
        return this;
    }

    public void setLatLng(LatLng latLng) {
        this.lat = latLng.latitude;
        this.lng = latLng.longitude;
    }

}
